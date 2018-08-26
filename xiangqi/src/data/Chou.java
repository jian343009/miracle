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
public class Chou extends Database{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String number = "";//发起者手机号
	private String imei = "";//发起者设备号
	private String token = "";
	private String firstTime = "";//发起时间
	private int lastTime = 0;
	private String lastTimeStr = "";
	private String title = "";
	private String info = "";
	private double money = 0;
	
	private int state = 0;
	private String status = "";
	private int open = 0;
	private int pay = 0;
	@Column(length=60000)
	private String payMobile = "";
	private double payMoney = 0;
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getFirstTime() {
		return firstTime;
	}
	public void setFirstTime(String firstTime) {
		this.firstTime = firstTime;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public double getMoney() {
		return money;
	}
	public void setMoney(double money) {
		this.money = money;
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
	public int getOpen() {
		return open;
	}
	public void setOpen(int open) {
		this.open = open;
	}
	public int getPay() {
		return pay;
	}
	public void setPay(int pay) {
		this.pay = pay;
	}
	public String getPayMobile() {
		return payMobile;
	}
	public void setPayMobile(String payMobile) {
		this.payMobile = payMobile;
	}
	public double getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(double payMoney) {
		this.payMoney = payMoney;
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
	
	public static synchronized Chou getByImei(String imei){
		Chou mo = null;
		Session ss = HSF.getSession();
		List<Chou> list = ss.createCriteria(Chou.class).add(Restrictions.eq("imei", imei)).list();
		ss.close();
		if(list.size() >0){
			mo = list.get(0);
		}else{
			mo = new Chou();
			mo.setImei(imei);
			mo.setFirstTime(ServerTimer.getFull());
			Dao.save(mo);
		}
		return mo;
	}
	public static Chou getByID(int id){
		Session ss = HSF.getSession();
		List<Chou> list = ss.createCriteria(Chou.class).add(Restrictions.eq("id", id)).list();
		ss.close();
		if(list.size() >0){
			return list.get(0);
		}
		return null;
	}
	public static Chou getByToken(String token){
		Session ss = HSF.getSession();
		List<Chou> list = ss.createCriteria(Chou.class).add(Restrictions.eq("token", token)).setMaxResults(1).list();
		ss.close();
		if(list.size() >0){
			return list.get(0);
		}
		return null;
	}
}
