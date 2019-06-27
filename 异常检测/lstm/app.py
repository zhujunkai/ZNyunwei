# coding=UTF-8
from flask import Flask
import json
import pandas as pd
import numpy as np
import torch
from torch import nn
from torch.autograd import Variable
from datetime import datetime,timedelta
import matplotlib.pyplot as plt
from influxdb import InfluxDBClient

def create_dataset(dataset, look_back=2):
 dataX, dataY = [], []
 for i in range(len(dataset) - look_back):
  a=[];
  for j in range(0,look_back):
   a.append(dataset[i+j])
  dataX.append(a)
  dataY.append(dataset[i + look_back])
 return np.array(dataX), np.array(dataY)


class lstm_reg(nn.Module):
 def __init__(self, input_size, hidden_size, output_size=1, num_layers=2):
  super(lstm_reg, self).__init__()

  self.rnn = nn.LSTM(input_size, hidden_size, num_layers)
  self.reg = nn.Linear(hidden_size, output_size)

 def forward(self, x):
  x, _ = self.rnn(x)
  s, b, h = x.shape  # (seq, batch, hidden)
  x = x.view(s * b, h)  # 转化为线性层的输入方式
  x = self.reg(x)
  x = x.view(s, b, -1)
  return x

app = Flask(__name__)
@app.route("/")
def hello():
 client = InfluxDBClient('203.195.152.23', 8086, 'root', 'root', 'telegraf')
 a=client.get_list_database()
 t_result = client.query('select * from cpu order by time desc limit 20;')
 mstr = str(t_result)
 for row in t_result:
  dataset1=row[::-1]
  dataset=[];
  for idle_index in range(0,len(dataset1),2):
   print dataset1[idle_index]['time']
   dataset.append(dataset1[idle_index]['usage_idle']/100)
  dataset=dataset.float()
  data_X, data_Y=create_dataset(dataset)
  train_size = int(len(data_X) * 0.7)
  test_size = len(data_X) - train_size
  train_X = data_X[:train_size]
  train_Y = data_Y[:train_size]
  test_X = data_X[train_size:]
  test_Y = data_Y[train_size:]
  train_X = train_X.reshape(-1, 1, 2)
  train_Y = train_Y.reshape(-1, 1, 1)
  test_X = test_X.reshape(-1, 1, 2)
  train_x = torch.from_numpy(train_X)
  train_y = torch.from_numpy(train_Y)
  test_x = torch.from_numpy(test_X)

  net = lstm_reg(2, 4)
  criterion = nn.MSELoss()
  optimizer = torch.optim.Adam(net.parameters(), lr=1e-2)
  for e in range(1000):
   var_x = Variable(train_x)
   var_y = Variable(train_y)
   # 前向传播
   out = net(var_x)
   loss = criterion(out, var_y)
   # 反向传播
   optimizer.zero_grad()
   loss.backward()
   optimizer.step()
   if (e + 1) % 100 == 0:
    print('Epoch:{}, Loss:{:.5f}'.format(e + 1, loss.item()))
  net = net.eval()
  data_X = data_X.reshape(-1, 1, 2)
  data_X = torch.from_numpy(data_X)
  var_data = Variable(data_X)
  pred_test = net(var_data)
  pred_test = pred_test.view(-1).data.numpy()
  plt.plot(pred_test, 'r', label='prediction')
  plt.plot(dataset, 'b', label='real')
  plt.legend(loc='best')
 return mstr


if __name__ == "__main__":
 app.run('127.0.0.1',5001)