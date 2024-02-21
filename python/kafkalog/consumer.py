# -*- coding: utf-8 -*-
#读取kafka日志 根据业务条件存入数据库
#
from utils import *
from kafka import KafkaConsumer
from datetime import datetime
import json
import re

source_topic = "test"


def kf_consumer():
    while True:
        try:
            msg_list = []
            consumer = KafkaConsumer(source_topic, bootstrap_servers=bootstrap_servers)
            for msg in consumer:
                project_id = 0
                message = msg.value
                # 获取该条日志
                message = message.decode('utf-8')
                try:
                    data_dict = json.loads(message)
                    remsg = ''
                    hostip = ''
                    logtime = ''
                    proname = ''
                    levelname = ''
                    if type(data_dict) == dict:
                        if 'levelname' in data_dict.keys():
                            levelname = data_dict["levelname"]
                        if 'name' in data_dict.keys():
                            proname_str = data_dict["name"]
                            if '::' in proname_str:
                                proname_arr = proname_str.split('::')
                                project_id = int(proname_arr[0])
                        if 'HOSTNAME' in data_dict.keys():
                            hostip = data_dict["HOSTNAME"]
                        if 'TimeStamp' in data_dict.keys():
                            logtime = data_dict["TimeStamp"]
                        if 'msg' in data_dict.keys():
                            remsg = data_dict["msg"]
                        msg_tupe = (project_id,remsg,hostip,levelname,logtime,str(datetime.now().replace(microsecond=0)))

                        save_to_db(msg_tupe)

                except Exception as e:
                    print(message)
                    print(e)
        except Exception as e:
            now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
            print(now, e)
            #consumer.close()

if __name__ == '__main__':
    kf_consumer()