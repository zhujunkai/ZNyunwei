# coding=UTF-8
from influxdb import InfluxDBClient
import time
from datetime import datetime,timedelta
import numpy as np
import matplotlib.pyplot as plt
import math
import random
from sklearn import datasets
from scipy.spatial import KDTree

# visitlist类用于记录访问列表
# unvisitedlist记录未访问过的点
# visitedlist记录已访问过的点
# unvisitednum记录访问过的点数量
class visitlist:
    def __init__(self, count=0):
        self.unvisitedlist=[i for i in range(count)]
        self.visitedlist=list()
        self.unvisitednum=count

    def visit(self, pointId):
        self.visitedlist.append(pointId)
        self.unvisitedlist.remove(pointId)
        self.unvisitednum -= 1

def  dist(a, b):
    # 计算a,b两个元组的欧几里得距离
    return math.sqrt(math.pow(a-b,2).sum())

def my_dbscanl(dataSet, eps, minPts):
    # numpy.ndarray的 shape属性表示矩阵的行数与列数
    # 行数即表小所有点的个数
    nPoints = dataSet.shape[0]
    # (1) 标记所有对象为unvisited
    # 在这里用一个类vPoints进行实现
    vPoints = visitlist(count=nPoints)
    # 初始化簇标记列表C，簇标记为 k
    k = -1
    C = [-1 for i in range(nPoints)]
    # 构建KD-Tree，并生成所有距离<=eps的点集合
    kd = KDTree(dataSet)
    while(vPoints.unvisitednum>0):
        # (3) 随机选择一个unvisited对象p
        p = random.choice(vPoints.unvisitedlist)
        # (4) 标t己p为visited
        vPoints.visit(p)
        # (5) if p 的$\varepsilon$-邻域至少有MinPts个对象
        # N是p的$\varepsilon$-邻域点列表
        print [dataSet[p]]
        N = kd.query_ball_point(dataSet[p], eps)
        if len(N) >= minPts:
            # (6) 创建个一个新簇C，并把p添加到C
            # 这里的C是一个标记列表，直接对第p个结点进行赋值
            k += 1
            C[p] = k
            # (7) 令N为p的$\varepsilon$-邻域中的对象的集合
            # N是p的$\varepsilon$-邻域点集合
            # (8) for N中的每个点p'
            for p1 in N:
                # (9) if p'是unvisited
                if p1 in vPoints.unvisitedlist:
                    # (10) 标记p'为visited
                    vPoints.visit(p1)
                    # (11) if p'的$\varepsilon$-邻域至少有MinPts个点，把这些点添加到N
                    # 找出p'的$\varepsilon$-邻域点，并将这些点去重新添加到N
                    M = kd.query_ball_point(dataSet[p1], eps)
                    if len(M) >= minPts:
                        for i in M:
                            if i not in N:
                                N.append(i)
                    # (12) if p'还不是任何簇的成员，把p'添加到c
                    # C是标记列表，直接把p'分到对应的簇里即可
                    if C[p1] == -1:
                        C[p1] = k
        # (15) else标记p为噪声
        else:
            C[p] = -1

    # (16) until没有标记为unvisited的对象
    return C

X1, Y1 = datasets.make_circles(n_samples=2000, factor=0.6, noise=0.05,
                               random_state=1)
X2, Y2 = datasets.make_blobs(n_samples=500, n_features=2, centers=[[1.5,1.5]],
                             cluster_std=[[0.1]], random_state=5)

query_num=800
client = InfluxDBClient('203.195.152.23', 8086, 'root', 'root', 'telegraf')
t_result = client.query('select * from cpu where cpu = \'cpu-total\' order by time desc limit %d;' % query_num)
X =[]
Y = []
for row in t_result:
    data1 = row[::-1]
    X =np.zeros((len(data1),1))
    Y =np.zeros((len(data1),1))
    for idle_index in range(0, len(data1)):
        X[idle_index, 0] = time.mktime(time.strptime(data1[idle_index]['time'],"%Y-%m-%dT%H:%M:%SZ"))
        Y[idle_index, 0] = data1[idle_index]['usage_idle']/100

#X = np.concatenate((X1, X2))
plt.figure(figsize=(12, 9), dpi=80)
#plt.scatter(X[:,0], Y[:,0],  marker='.')
C1 = my_dbscanl(Y, 0.05, 20)
print C1
plt.scatter(X[:,0], Y[:,0], c=C1, marker='.')
plt.show()


# client = InfluxDBClient('203.195.152.23', 8086, 'root', 'root', 'telegraf')
# a = client.get_list_database()
# t_result = client.query('select * from cpu where cpu = \'cpu-total\' order by time desc limit 20;')
# for row in t_result:
#     data1 = row[::-1]
#     for idle_index in range(0, len(data1)):
#         print data1[idle_index]['time']
