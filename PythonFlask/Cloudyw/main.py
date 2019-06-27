# coding=UTF-8
from flask import Response,request,Flask
from flask import abort, redirect, url_for
import requests
from PIL import Image
from io import BytesIO
import time
import urllib
import urllib2
import uuid


app = Flask(__name__)
@app.route("/", methods=['GET', 'POST'])
def hello():
    if request.method == 'POST':
        return "NONE"
    else:
        a=request.args.get('key', '')
        return a



@app.route("/image/<path>", methods=['GET', 'POST'])
def index(path):
    
    path1 = "./cpuimage/%s" % path
    resp = Response(open(path1, 'rb'), mimetype="image/png")
    return resp


@app.route("/graimage", methods=['GET', 'POST'])
def graimage():
    t_type=request.args.get('type')
    ticks = time.time()*1000
    t_from = ticks-10010000
    t_to=ticks-10000
    t_id=uuid.uuid1()
    if t_type=='cpu':
        img_src = 'http://www.722captain.cn:3000/render/dashboard-solo/db/telegraf-system-dashboard?refresh=1m&orgId=1&panelId=28239&from=' \
                + str(t_from)+"&to=" + str(t_to) + "&var-datasource=my_influxDB&var-inter=10s&var-server=VM_0_13_centos&var-mountpoint=All&var-cpu=All&var-disk=All&var-netif=All&width=1000&height=500&tz=UTC%2B08%3A00"
        headers = {}
        headers["accept"] = "ext/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"
        headers["Cookie"] = "grafana_user=admin; grafana_remember=05253d264eb446b22765bae2b0f942bb38b502ffd65863c275fca97103c278e042; grafana_sess=2baa9317c3dd3549"
        headers[
            "User-Agent"] = "Mozilla/5.0 (Linux; Android 8.0.0; Pixel 2 XL Build/OPD1.170816.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Mobile Safari/537.36"
        request1 = urllib2.Request(img_src, headers=headers)
        response = urllib2.urlopen(request1)
        data=response.read()
        BytesIOObj = BytesIO()
        BytesIOObj.write(data)
        img = Image.open(BytesIOObj)
        img.save('./cpuimage/'+str(t_id)+".png")

        return "/image/"+str(t_id)+".png"
    else:
        return "None"


@app.errorhandler(404)
def page_not_found(error):
    return "404.........."

if __name__ == "__main__":
    app.run('127.0.0.1',5001)