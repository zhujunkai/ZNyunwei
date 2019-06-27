package myDB;
import java.util.ArrayList;
import java.util.List;

import org.apache.jasper.tagplugins.jstl.core.If;
import org.influxdb.*;
import org.influxdb.dto.*;
import org.influxdb.dto.QueryResult.*;

import myclass.cpudata;
public class Influxdbservice {
	private InfluxDBConnect influxDB;
	private String username = "root";//用户名
	private String password = "root";//密码
	private String openurl = "http://203.195.152.23:8086";//连接地址
	private String database = "telegraf";//数据库
	private String measurement = "cpu";
	public void setUp(){
		//创建 连接
		influxDB = new InfluxDBConnect(username, password, openurl, database);
		
		influxDB.influxDbBuild();
		
		influxDB.createRetentionPolicy();
		
//		influxDB.deleteDB(database);
//		influxDB.createDB(database);
	}
	public List<cpudata> Query(){//测试数据查询
		String command = "select * from cpu order by time desc limit 9000;";
		QueryResult results = influxDB.query(command);
		List<cpudata> lists = new ArrayList<cpudata>();
		if(results.getResults() == null){
			return lists;
		}
		for (Result result : results.getResults()) {
			List<Series> series= result.getSeries();
			for (Series serie : series) {
//				Map<String, String> tags = serie.getTags();
				List<List<Object>>  values = serie.getValues();
				List<String> columns = serie.getColumns();
				
				lists.addAll(getQueryData(columns, values));
			}
		}
		return lists;
	}
	private List<cpudata> getQueryData(List<String> columns, List<List<Object>>  values){
		List<cpudata> lists = new ArrayList<cpudata>();
		for (List<Object> list : values) {
			if(((String)list.get(1)).equals("cpu-total")){
				cpudata info = new cpudata((String)list.get(0),(String)list.get(1),(String)list.get(2),
						(double)list.get(3),(double)list.get(4),(double)list.get(5),
						(double)list.get(6),(double)list.get(7),(double)list.get(8),
						(double)list.get(9),(double)list.get(10),(double)list.get(11),(double)list.get(12));
				lists.add(info);
			}
		}
		
		return lists;
	}

	private String setColumns(String column){
		String[] cols = column.split("_");
		StringBuffer sb = new StringBuffer();
		for(int i=0; i< cols.length; i++){
			String col = cols[i].toLowerCase();
			if(i != 0){
				String start = col.substring(0, 1).toUpperCase();
				String end = col.substring(1).toLowerCase();
				col = start + end;
			}
			sb.append(col);
		}
		return sb.toString();
	}
	

}
