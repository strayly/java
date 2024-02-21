#将logging集成到kafka 实现分布式日志收集
import logging
import json
from datetime import datetime
from kafka import KafkaProducer
from utils import *


class KafkaLoggingHandler(logging.Handler):
    def __init__(self):
        logging.Handler.__init__(self)
        # 这里是用来确定使用哪一个topic
        self.config_topic = 'test'
        # 获取ip
        self.hostname = get_local_ip()
        self.hosts = get_kafka_hosts()
        self.producer = KafkaProducer(bootstrap_servers=self.hosts, compression_type='gzip', max_block_ms=1000, acks=0)
    # 重写emit方法
    def emit(self, record):
        # 获取到日志里面需要的数据
        logging_dict = getattr(record, '__dict__')
        logging_dict['message'] = self.format(record)
        #if isinstance(message, unicode):
        #    msg = message.encode("utf-8")
        # 对日志数据进行进一步处理，添加一些必要的数据
        logging_dict['TimeStamp'] = str(datetime.now().replace(microsecond=0))
        logging_dict['HOSTNAME'] = self.hostname
        # 将数据dump成json字符串
        msg = json.dumps(logging_dict)
        # 发送到kafka,partition_key生成规则，timestamp是创建消息时间，注意需要是bytes类型，因此对字符串操作都是进行encode的处理
        try:
            print('send kafka msg:', msg)
            self.producer.send(self.config_topic, key='py-logging'.encode('utf-8'), value=msg.encode('utf-8'))
            self.producer.flush()

        except Exception as e:
            logging.error('发送kafka消息失败', e)
        #self.producer.close()
 
class KafkaLoggingUtils:
 
    level = eval('logging.INFO')
    format = '%(asctime)s %(name)s[line:%(lineno)d] %(levelname)s %(message)s'
    datefmt = '%a,%d %b %Y %H:%M:%S'
    name = __name__

    def __init__(self, hosts):
        self.hosts = hosts

    @staticmethod
    def set_logging_params(params):
        level, format, datefmt = params
        if level is not None:
            KafkaLoggingUtils.level = level
        if format is not None:
            KafkaLoggingUtils.format = format
        if datefmt is not None:
            KafkaLoggingUtils.datefmt = datefmt
 
    @staticmethod
    def getLogger(name):

        #stdoutHandler = logging.StreamHandler() #stream=sys.stdout

        # logging的一些format配置
        logging.basicConfig(level=logging.INFO, format=KafkaLoggingUtils.format, datefmt=KafkaLoggingUtils.datefmt)
        logger = logging.getLogger(name)
        # 将kafkahandler加入logging的handler中
        handler = KafkaLoggingHandler()
        logger.addHandler(hdlr=handler)
           
        return logger


#使用
if __name__ == '__main__':
	project_id = 23241
	project_name = "项目1"

	#开启日志
	logging = KafkaLoggingUtils.getLogger(str(project_id)+"::"+project_name)

	logging.info("%s 任务开始" % project_name)
	logging.info("%s 任务结束" % project_name)
