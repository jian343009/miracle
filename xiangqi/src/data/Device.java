package data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import cmd.CMD14;
import dao.Dao;
import dao.Data;
import main.Global;

@Entity
public class Device {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String imei = "";
	private String enter = "";
	private String firstTime = "";
	private int lastDay = 0;
	private String lastTime = "";
	private int openState = 0;
	private int buyState = 0;
	private int open = 0;
	private int buy = 0;
	private int money = 0;
	private int offbuy = 0;
	@Column(length=60000)
	private String regChannel = "";
	private String channel="";
	private String version = "";
	@Column(length=60000)
	private String extra = "";
	private String mobile = "";
	private int state = 0;//0.空闲 1.团购 2.众筹
	private String token="";//加密锁
	private int unlockKey=0;//课程解锁码
	private String reward = "";//记录红包信息，json
	private int praise = 0;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public int getLastDay() {
		return lastDay;
	}
	public void setLastDay(int lastDay) {
		this.lastDay = lastDay;
	}
	public String getLastTime() {
		return lastTime;
	}
	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
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
	public int getMoney() {
		return money;
	}
	public void setMoney(int money) {
		this.money = money;
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
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public int getUnlockKey() {
		return unlockKey;
	}
	public void setUnlockKey(int unlockKey) {
		this.unlockKey = unlockKey;
	}

	public String getReward() {
		return reward;
	}
	public void setReward(String reward) {
		this.reward = reward;
	}
	public int getPraise() {
		return praise;
	}
	public void setPraise(int praise) {
		this.praise = praise;
	}
	@Override
	public String toString() {
		return this.id+"#"+this.imei;
	}
	/**
	 * @param money 支付金额
	 */
	public void 使用红包(int money,Count count){
		if(this.reward == null || !this.reward.contains("未使用")){
			return;//无可用红包直接返回
		}
		Data dat = Data.fromMap(this.getReward());
		int 红包使用金额=0;
		for(int les:new int[]{1,2}){
			if("未使用".equals(dat.get(les).get("状态").asString())){
				dat.getMap(les).put("状态", "已使用");	//改用户红包状态
				红包使用金额 += dat.get(les).get("金额").asInt();
			}
		}
		this.setReward(dat.toString());
		if(红包使用金额 >= 1){
			Data data1=Data.fromMap(count.getReward());//记录红包使用
			Data data2=data1.getMap("红包使用");
			data2.put("次数", data2.get("次数").asInt()+1);
			data2.put("金额", data2.get("金额").asInt()+红包使用金额);
			if(money < 12){//区分单课和多课使用
				data2.put("单课次数", data2.get("单课次数").asInt()+1);
				data2.put("单课金额", data2.get("单课金额").asInt()+红包使用金额);
			}else{
				data2.put("多课次数", data2.get("多课次数").asInt()+1);
				data2.put("多课金额", data2.get("多课金额").asInt()+红包使用金额);
			}
			count.setReward(data1.toString());
		}
	}
	/**
	 * 比对支付金额和应付金额
	 */
	public void checkPrice(Device device,int lesson,int money,int ljpayID){
		if(Global.getInt(device.getVersion()) <= 8){
			return;
		}
		int price = CMD14.getPrice(device, lesson);
		if(price==money){
			device.setMoney(0);
		}else{
			device.setMoney(ljpayID);
		}
		Dao.save(device);
	}
	
}
