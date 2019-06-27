package myCharts;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

import myclass.cpudata;

public class Cpuchart {
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
	String basePath = "/root";
	public Cpuchart() {
	}
	public void get_image(List<cpudata> datalist,String uuid){
		JFreeChart linechart=ChartFactory.createTimeSeriesChart("CPU",
				"time","usage/%", createDataset(datalist),
				true,true,true);
	    String uploadPath =basePath+File.separator+"cpuimages";
	    String filePath =uploadPath+File.separator+uuid+".jpeg";
	    System.out.println(filePath);
	    File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        int width = 640; /* Width of the image */
        int height = 480; /* Height of the image */ 
        File cpufile = new File(filePath); 
        try{
        	if(!cpufile.exists())cpufile.createNewFile();
        	ChartUtilities.saveChartAsJPEG(cpufile ,linechart, width ,height);
        }catch (Exception e) {
			e.printStackTrace();
		} 
	}
	private XYDataset createDataset(List<cpudata> datalist) {

        // 生成数据序列  
		TimeSeriesCollection dataset = new TimeSeriesCollection();  
		TimeSeries series = new TimeSeries("usage_user");  
		TimeSeries series1 = new TimeSeries("usage_system");  
		TimeSeries series2 = new TimeSeries("usage_iowait");  
		TimeSeries series3 = new TimeSeries("usage_softirq");  
		for(int i=datalist.size()-1;i>=0;i--){
			cpudata temp=datalist.get(i);
			Second stime=new Second(utc_to_beijing(temp.time));
			series.add(stime, temp.usage_user);
			series1.add(stime, temp.usage_system);
			series2.add(stime, temp.usage_iowait);
			series3.add(stime, temp.usage_softirq);
		}
		dataset.addSeries(series);
		dataset.addSeries(series1);
		dataset.addSeries(series2);
		dataset.addSeries(series3);
        return dataset;  

    }  




	
	Date utc_to_beijing(String timestr){
		Date beijing_time=null;
		try{
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
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
	String getDateStr(Date t_date){
		return df1.format(t_date);
	}
}
