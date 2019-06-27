package mysevice.grafanaservice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class grafanaimg
 */
@WebServlet("/grafanaimg")
public class grafanaimg extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String basePath = "/root";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public grafanaimg() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Writer out = response.getWriter();
		Date time=new Date();
		Long from=time.getTime()-10010000;
		Long to=time.getTime()-10000;
		String uuid=UUID.randomUUID().toString();
		String type=request.getParameter("q");
		String uri="";
		if(type==null){
			out.write("Need Parameter!!!!");
			out.flush();
			return;
		}else if(type.equals("cpu")){
			uri="http://www.722captain.cn:3000/render/dashboard-solo/db/telegraf-system-dashboard?refresh=1m&orgId=1&panelId=28239&from="+String.valueOf(from)
				+"&to="+ String.valueOf(to)+"&var-datasource=my_influxDB&var-inter=10s&var-server=VM_0_13_centos&var-mountpoint=All&var-cpu=All&var-disk=All&var-netif=All&width=1000&height=500&tz=UTC%2B08%3A00" ;
		}else if(type.equals("system")) {
			uri="http://www.722captain.cn:3000/render/dashboard-solo/db/telegraf-system-dashboard?refresh=1m&orgId=1&panelId=54694&from="+String.valueOf(from)
				+"&to="+String.valueOf(to)+"&var-datasource=my_influxDB&var-inter=10s&var-server=VM_0_13_centos&var-mountpoint=All&var-cpu=All&var-disk=All&var-netif=All&width=1000&height=500&tz=UTC%2B08%3A00";
		}else if(type.equals("mem")){
			uri="http://www.722captain.cn:3000/render/dashboard-solo/db/telegraf-system-dashboard?refresh=1m&orgId=1&panelId=12054&from="+String.valueOf(from)
				+"&to="+String.valueOf(to)+"&var-datasource=my_influxDB&var-inter=10s&var-server=VM_0_13_centos&var-mountpoint=All&var-cpu=All&var-disk=All&var-netif=All&width=1000&height=500&tz=UTC%2B08%3A00";
		}else{
			out.write("Parameter wrong!!!!");
			out.flush();
			return;
		}
		saveurlimg(uri,uuid);
		String imageurl="http://www.722captain.cn:8080/cpu_images/"+uuid+".jpeg";
		out.write(imageurl);
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	void saveurlimg(String strUrl,String uuid){
		try{
			//构造URL
			URL url = new URL(strUrl);
			//构造连接
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			//这个网站要模拟浏览器才行
			conn.setRequestProperty("Accept","ext/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			conn.setRequestProperty("User-Agent","Mozilla/5.0 (Linux; Android 8.0.0; Pixel 2 XL Build/OPD1.170816.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.119 Mobile Safari/537.36");
			conn.setRequestProperty("Cookie","grafana_user=admin; grafana_remember=fbd28b592a99a12e7fffa6d9cd9638c864ef911661bec59e896e197eb27c65174d; grafana_sess=1d56163d047d9009");
			//打开连接
			conn.connect();
			//打开这个网站的输入流
			InputStream inStream = conn.getInputStream();
			//用这个做中转站 ，把图片数据都放在了这里，再调用toByteArray()即可获得数据的byte数组
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte [] buf = new byte[1024];
			int len = 0;
			//读取图片数据
			while((len=inStream.read(buf))!=-1){
			System.out.println(len);
			outStream.write(buf,0,len);
			}
			inStream.close();
			outStream.close();
			//把图片数据填入文件中
			String uploadPath =basePath+File.separator+"cpuimages";
		    String filePath =uploadPath+File.separator+uuid+".jpeg";
		    File uploadDir = new File(uploadPath);
	        if (!uploadDir.exists()) {
	            uploadDir.mkdir();
	        }
			File file = new File(filePath);
			if(!file.exists())file.createNewFile();
			FileOutputStream op = new FileOutputStream(file);
			op.write(outStream.toByteArray());
			op.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
