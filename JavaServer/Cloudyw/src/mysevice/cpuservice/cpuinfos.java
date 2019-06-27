package mysevice.cpuservice;


import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.org.apache.bcel.internal.generic.NEW;

import myCharts.Cpuchart;
import myDB.Influxdbservice;
import myclass.cpudata;

/**
 * Servlet implementation class cpuinfos
 */
@WebServlet("/cpuinfos")
public class cpuinfos extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Influxdbservice dbservice=null;
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    /**
     * @see HttpServlet#HttpServlet()
     */
    public cpuinfos() {
        super();
        // TODO Auto-generated constructor stub
        dbservice=new Influxdbservice();
		dbservice.setUp();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String uuid=UUID.randomUUID().toString();
		String cpu_imagestr="http://www.722captain.cn:8080/cpu_images/"+uuid+".jpeg";
		List<cpudata> result=dbservice.Query();
		Cpuchart cpuchart=new Cpuchart();
		cpuchart.get_image(result,uuid);
		Writer out = response.getWriter();
		out.write(cpu_imagestr);
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	Date utc_to_beijing(String timestr){
		Date beijing_time=null;
		try{
			Date ori_time=df.parse(timestr);
			Calendar calendar = Calendar.getInstance();
	        calendar.setTime(ori_time);
	        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 8);
	        beijing_time=calendar.getTime();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return beijing_time;
	}
}
