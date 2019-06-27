package myclass;

import java.util.Date;

public class cpudata {
	public String time;
	public String cpu;
	public String host;
	public double usage_guest;
	public double usage_guest_nice;
	public double usage_idle;
	public double usage_iowait;
	public double usage_irq;
	public double usage_nice;
	public double usage_softirq;
	public double usage_steal;
	public double usage_system;
	public double usage_user;
	public cpudata() {
		// TODO Auto-generated constructor stub
	}
	public cpudata(String time, String cpu, String host, double usage_guest, double usage_guest_nice, double usage_idle,
			double usage_iowait, double usage_irq, double usage_nice, double usage_softirq, double usage_steal,
			double usage_system, double usage_user) {
		super();
		this.time = time;
		this.cpu = cpu;
		this.host = host;
		this.usage_guest = usage_guest;
		this.usage_guest_nice = usage_guest_nice;
		this.usage_idle = usage_idle;
		this.usage_iowait = usage_iowait;
		this.usage_irq = usage_irq;
		this.usage_nice = usage_nice;
		this.usage_softirq = usage_softirq;
		this.usage_steal = usage_steal;
		this.usage_system = usage_system;
		this.usage_user = usage_user;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getCpuName() {
		return cpu;
	}
	public void setCpuName(String cpuName) {
		this.cpu = cpu;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public double getUsage_guest() {
		return usage_guest;
	}
	public void setUsage_guest(double usage_guest) {
		this.usage_guest = usage_guest;
	}
	public double getUsage_guest_nice() {
		return usage_guest_nice;
	}
	public void setUsage_guest_nice(double usage_guest_nice) {
		this.usage_guest_nice = usage_guest_nice;
	}
	public double getUsage_idle() {
		return usage_idle;
	}
	public void setUsage_idle(double usage_idle) {
		this.usage_idle = usage_idle;
	}
	public double getUsage_iowait() {
		return usage_iowait;
	}
	public void setUsage_iowait(double usage_iowait) {
		this.usage_iowait = usage_iowait;
	}
	public double getUsage_irq() {
		return usage_irq;
	}
	public void setUsage_irq(double usage_irq) {
		this.usage_irq = usage_irq;
	}
	public double getUsage_nice() {
		return usage_nice;
	}
	public void setUsage_nice(double usage_nice) {
		this.usage_nice = usage_nice;
	}
	public double getUsage_softirq() {
		return usage_softirq;
	}
	public void setUsage_softirq(double usage_softirq) {
		this.usage_softirq = usage_softirq;
	}
	public double getUsage_steal() {
		return usage_steal;
	}
	public void setUsage_steal(double usage_steal) {
		this.usage_steal = usage_steal;
	}
	public double getUsage_system() {
		return usage_system;
	}
	public void setUsage_system(double usage_system) {
		this.usage_system = usage_system;
	}
	public double getUsage_user() {
		return usage_user;
	}
	public void setUsage_user(double usage_user) {
		this.usage_user = usage_user;
	}
	
	
}
