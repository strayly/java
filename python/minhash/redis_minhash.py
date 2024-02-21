 #改造RedisListStorage
	class RedisListStorage(OrderedStorage, RedisStorage):
        def __init__(self, config, name=None):
            RedisStorage.__init__(self, config, name=name)

        def keys(self):
            return self._redis.hkeys(self._name)

        def redis_keys(self):
            return self._redis.hvals(self._name)

        def status(self):
            status = self._parse_config(self.config['redis'])
            status.update(Storage.status(self))
            return status

        def get(self, key):
            return self._get_items(self._redis, self.redis_key(key))

        def getmany(self, *keys):
            pipe = self._redis.pipeline()
            pipe.multi()
            for key in keys:
                self._get_items(pipe, self.redis_key(key))
            return pipe.execute()

        @staticmethod
        def _get_items(r, k):
            return r.lrange(k, 0, -1)

        def remove(self, *keys):
            self._redis.hdel(self._name, *keys)
            self._redis.delete(*[self.redis_key(key) for key in keys])

        def remove_val(self, key, val):
            redis_key = self.redis_key(key)
            self._redis.lrem(redis_key, val)
            if not self._redis.exists(redis_key):
                self._redis.hdel(self._name, redis_key)

        def insert(self, key, *vals, **kwargs):
            # Using buffer=True outside of an `insertion_session`
            # could lead to inconsistencies, because those
            # insertion will not be processed until the
            # buffer is cleared
            buffer = kwargs.pop('buffer', False)
            if buffer:
                self._insert(self._buffer, key, *vals)
            else:
                self._insert(self._redis, key, *vals)

        def _insert(self, r, key, *values):
            redis_key = self.redis_key(key)
            r.hset(self._name, key, redis_key)
            r.rpush(redis_key, *values)
            r.expire(redis_key, 86400 * 30)
            r.expire(self._name, 86400*20)
        def size(self):
            return self._redis.hlen(self._name)

        def itemcounts(self):
            pipe = self._redis.pipeline()
            pipe.multi()
            ks = self.keys()
            for k in ks:
                self._get_len(pipe, self.redis_key(k))
            d = dict(zip(ks, pipe.execute()))
            return d

        @staticmethod
        def _get_len(r, k):
            return r.llen(k)

        def has_key(self, key):
            return self._redis.hexists(self._name, key)

        def empty_buffer(self):
            self._buffer.execute()
            # To avoid broken pipes, recreate the connection
            # objects upon emptying the buffer
            self.__init__(self.config, name=self._name)


    class RedisSetStorage(UnorderedStorage, RedisListStorage):
        def __init__(self, config, name=None):
            RedisListStorage.__init__(self, config, name=name)

        @staticmethod
        def _get_items(r, k):
            return r.smembers(k)

        def remove_val(self, key, val):
            redis_key = self.redis_key(key)
            self._redis.srem(redis_key, val)
            if not self._redis.exists(redis_key):
                self._redis.hdel(self._name, redis_key)

        def _insert(self, r, key, *values):
            redis_key = self.redis_key(key)
            r.hset(self._name, key, redis_key)
            r.sadd(redis_key, *values)
            r.expire(redis_key, 86400 * 30)
            r.expire(self._name, 86400*20)

        @staticmethod
        def _get_len(r, k):
            return r.scard(k)


#使用方法

from datasketch import MinHash
from lsh import  MinHashLSH
import configparser
def get_minhash(item_str):
    item_str_arr = item_str.split(' ')
    temp = MinHash()
    for d in item_str_arr:
        temp.update(d.upper().encode('utf8'))
        
cp = configparser.SafeConfigParser()
cp.read("my.conf")
#从配置文件读取redis相关配置
redis_host = cp.get('myredis', 'host')
redis_port = cp.get('myredis', 'port')
redis_pass = cp.get('myredis', 'password')
redis_db = cp.get('myredis', 'dbname')
myminhash = MinHashLSH(threshold=0.7, num_perm=128, storage_config={'type': 'redis', 'basename': b'mykey:','redis': {'host': redis_host,'port': redis_port,'password': redis_pass,'db':redis_db}}

documents = []
#...
#实际业务中自己的文档数据
#存储
for i in range(len(documents)):    
    temp = get_minhash(documents[i])
    myminhash.insert(i, temp)
    
#查询
item_str = "要查询的词"    
query_minhash = get_minhash(item_str)
results = myminhash.query(query_minhash)
