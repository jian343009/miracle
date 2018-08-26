package data;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import dao.Dao;
import dao.HSF;
import main.Global;
import main.ServerTimer;

@Entity
public class Mobile extends Database{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String number = "";
	private String imei = "";
	private String enter = "";
	private String firstTime = "";
	private String sex = "";
	private int age = 0;
	
	private int state = 0;
	private String status = "";
	private String bizId = "";
	private String code = "";
	private String receiveCode = "";
	private int lastTime = 0;
	private String lastTimeStr = "";
	
	private int openState = 0;
	private int buyState = 0;
	private int open = 0;
	private int buy = 0;
	private int offbuy = 0;
	@Column(length=60000)
	private String regChannel = "";
	private String channel="";
	private String version = "";
	private String extra = "";
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getEnter() {
		return enter;
	}
	public void setEnter(String enter) {
		this.enter = enter;
	}
	public String getFirstTime() {
		return firstTime;
	}
	public void setFirstTime(String firstTime) {
		this.firstTime = firstTime;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getBizId() {
		return bizId;
	}
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getReceiveCode() {
		return receiveCode;
	}
	public void setReceiveCode(String receiveCode) {
		this.receiveCode = receiveCode;
	}
	public int getLastTime() {
		return lastTime;
	}
	public void setLastTime(int lastTime) {
		this.lastTime = lastTime;
	}
	public String getLastTimeStr() {
		return lastTimeStr;
	}
	public void setLastTimeStr(String lastTimeStr) {
		this.lastTimeStr = lastTimeStr;
	}
	public int getOpenState() {
		return openState;
	}
	public void setOpenState(int openState) {
		this.openState = openState;
	}
	public int getBuyState() {
		return buyState;
	}
	public void setBuyState(int buyState) {
		this.buyState = buyState;
	}
	public int getOpen() {
		return open;
	}
	public void setOpen(int open) {
		this.open = open;
	}
	public int getBuy() {
		return buy;
	}
	public void setBuy(int buy) {
		this.buy = buy;
	}
	public int getOffbuy() {
		return offbuy;
	}
	public void setOffbuy(int offbuy) {
		this.offbuy = offbuy;
	}
	public String getRegChannel() {
		return regChannel;
	}
	public void setRegChannel(String regChannel) {
		this.regChannel = regChannel;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getExtra() {
		return extra;
	}
	public void setExtra(String extra) {
		this.extra = extra;
	}
	
	public int getUnlockNum(int lesson){
		return Global.getInt(Global.getArrayValue(this.extra, lesson));
	}
	public void setUnlockNum(int lesson, int num){
		this.extra = Global.setArrayValue(this.extra, lesson, ""+num);
	}
	public void modUnlockNum(int lesson, int num){
		int m = this.getUnlockNum(lesson);
		m += num;
		this.extra = Global.setArrayValue(this.extra, lesson, ""+m);
	}
	@Override
	public String toString() {
		return this.id+"#"+this.imei;
	}
	
//	private static Hashtable<String, Mobile> dicNumberMobile = new Hashtable<String, Mobile>();
//	private static ArrayList<Mobile> listMobile = new ArrayList<Mobile>();
//	private static void addMobile(Mobile mo){
//		if(mo != null){
//			synchronized (listMobile) {
//				dicNumberMobile.put(mo.getNumber(), mo);
//				listMobile.add(mo);
//				if(listMobile.size() >1000){
//					Mobile m = listMobile.get(0);
//					dicNumberMobile.remove(m.getNumber());
//				}
//			}
//		}
//	}
//	private static void orderMobile(Mobile mo){
//		if(mo != null){
//			synchronized (listMobile) {
//				listMobile.remove(mo);
//				listMobile.add(mo);
//			}
//		}
//	}
//	public static synchronized Mobile getByNumber(String number){
//		Mobile mo = dicNumberMobile.get(number);
//		if(mo == null){
//			Session ss = HSF.getSession();
//			List<Mobile> list = ss.createCriteria(Mobile.class).add(Restrictions.eq("number", number)).list();
//			ss.close();
//			if(list.size() >0){
//				mo = list.get(0);
//			}else{
//				mo = new Mobile();
//				mo.setNumber(number);
//				mo.setFirstTime(ServerTimer.getFull());
//				Dao.save(mo);
//			}
//			addMobile(mo);
//		}else{
//			orderMobile(mo);
//		}
//		return mo;
//	}
	public static synchronized Mobile getByNumber(String number){
		Mobile mo = null;
		Session ss = HSF.getSession();
		List<Mobile> list = ss.createCriteria(Mobile.class).add(Restrictions.eq("number", number)).list();
		ss.close();
		if(list.size() >0){
			mo = list.get(0);
		}else{
			mo = new Mobile();
			mo.setNumber(number);
			mo.setFirstTime(ServerTimer.getFull());
			Dao.save(mo);
		}
		return mo;
	}
	public static synchronized Mobile getByID(int id){
		Session ss = HSF.getSession();
		List<Mobile> list = ss.createCriteria(Mobile.class).add(Restrictions.eq("id", id)).list();
		ss.close();
		if(list.size() >0){
			return list.get(0);
		}
		return null;
	}
}
