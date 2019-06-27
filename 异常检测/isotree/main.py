# coding=UTF-8
import pandas as pd
from sklearn.ensemble import IsolationForest
from sklearn.externals import joblib
import numpy as np
from influxdb import InfluxDBClient
import time
import matplotlib.pyplot as plt
import datetime

query_num=2000
client = InfluxDBClient('203.195.152.23', 8086, 'root', 'root', 'telegraf')
t_result = client.query('select packets_recv,packets_sent from net order by time desc limit %d;' % query_num)
t_result_cpu = client.query('select * from cpu where cpu = \'cpu-total\' order by time desc limit %d;' % query_num)
t_result_mem = client.query('select active from mem order by time desc limit %d;' % query_num)
X =[]
Y =[]
for row in t_result:
    data1 = row[::-1]
    X =np.zeros((len(data1)-1,1))
    Y = np.zeros((len(data1)-1, 3))
    for idle_index in range(0, len(data1)-1):
        X[idle_index, 0] = time.mktime(time.strptime(data1[idle_index+1]['time'],"%Y-%m-%dT%H:%M:%SZ"))
        packets_recv = data1[idle_index+1]['packets_recv']-data1[idle_index]['packets_recv']
        packets_sent = data1[idle_index+1]['packets_sent']-data1[idle_index]['packets_sent']
        Y[idle_index, 0]=packets_recv - packets_sent
for row in t_result_cpu:
    data1 = row[::-1]
    for idle_index in range(0, len(data1) - 1):
        Y[idle_index, 1] = data1[idle_index+1]['usage_iowait']
for row in t_result_mem:
    data1 = row[::-1]
    for idle_index in range(0, len(data1) - 1):
        Y[idle_index, 1] = data1[idle_index+1]['active']


#训练样本
clf = IsolationForest( behaviour="new",max_samples=256,random_state=42,contamination=0.1)
clf.fit(Y)
joblib.dump(clf, "train_model.m") # 存训练好的模型
clf = joblib.load("train_model.m") # 读训练好的模型
y_pred_train = clf.predict(Y)
error_count=0
out_str=""
for y_index in range(0, len(y_pred_train)):
    if y_pred_train[y_index]<0:
        t_time = time.localtime(X[y_index,0])
        a_time=datetime.datetime.fromtimestamp(X[y_index,0])
        b=datetime.timedelta(hours=8)
        c=a_time+b
        out_str = out_str + str(c.strftime("%Y-%m-%d %H:%M:%S")) + "\n"
print(out_str)
plt.scatter(X[:,0], Y[:,0], c=y_pred_train, marker='.')
plt.save()
