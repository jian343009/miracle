package data;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import dao.HSF;

public class StepRecord {
	
	private int id;
	
	private int deviceID = 0;
	private String imei="";
	private String step = "";
	private String info = "";
	private String timeStr = "";
	
//	private Record(){}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(int deviceID) {
		this.deviceID = deviceID;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getTimeStr() {
		return timeStr;
	}
	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}
	
	public static List<StepRecord> getRecentRecord(String imei){
		Session ss = HSF.getSession();
		Criteria ct = ss.createCriteria(StepRecord.class);
		if(!imei.isEmpty()){
			ct.add(Restrictions.like("imei", imei));
		}
		ct.addOrder(Order.desc("id"));
		ct.setMaxResults(300);
		List<StepRecord> list = ct.list();
		ss.close();
		return list;
	}
}
