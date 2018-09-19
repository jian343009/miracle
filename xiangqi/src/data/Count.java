package data;

import dao.Data;
import main.Global;

public class Count {

	private int id;
	private int day = 0;
	private String dayStr = "";
	private int open = 0;
	private int newDevice = 0;
	private int pay = 0;
	private int newPay = 0;
	private double totalPay = 0;
	private double aliPay = 0;
	private double wiiPay = 0;
	private double wxPay = 0;
	private double applePay = 0;
	private double hwPay = 0;
	private String detail = "";
	private String dataStr = "";
	private String reward = "";
	private Data data = null;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getDayStr() {
		return dayStr;
	}

	public void setDayStr(String dayStr) {
		this.dayStr = dayStr;
	}

	public int getOpen() {
		return open;
	}

	public void setOpen(int open) {
		this.open = open;
	}

	public int getNewDevice() {
		return newDevice;
	}

	public void setNewDevice(int newDevice) {
		this.newDevice = newDevice;
	}

	public int getPay() {
		return pay;
	}

	public void setPay(int pay) {
		this.pay = pay;
	}

	public int getNewPay() {
		return newPay;
	}

	public void setNewPay(int newPay) {
		this.newPay = newPay;
	}

	public double getTotalPay() {
		return totalPay;
	}

	public void setTotalPay(double totalPay) {
		this.totalPay = totalPay;
	}

	public double getAliPay() {
		return aliPay;
	}

	public void setAliPay(double aliPay) {
		this.aliPay = aliPay;
	}

	public double getWiiPay() {
		return wiiPay;
	}

	public void setWiiPay(double wiiPay) {
		this.wiiPay = wiiPay;
	}

	public double getWxPay() {
		return wxPay;
	}

	public void setWxPay(double wxPay) {
		this.wxPay = wxPay;
	}

	public double getApplePay() {
		return applePay;
	}

	public void setApplePay(double applePay) {
		this.applePay = applePay;
	}

	public double getHwPay() {
		return hwPay;
	}

	public void setHwPay(double hwPay) {
		this.hwPay = hwPay;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public void addReturnNum(int day) {
		int[] nums = Global.getArray(Global.splitArray(this.detail), 4);
		if (day <= 0) {
			nums[0]++;
		} else if (day == 1) {
			nums[1]++;
		} else if (2 <= day && day <= 6) {
			nums[2]++;
		} else {
			nums[3]++;
		}
		this.detail = Global.concatArray(nums);
	}

	public int getReturnNum(int day) {
		int num = 0;
		int[] nums = Global.getArray(Global.splitArray(this.detail), 4);
		if (day <= 0) {
			num = nums[0];
		} else if (day == 1) {
			num = nums[1];
		} else if (2 <= day && day <= 6) {
			num = nums[2];
		} else {
			num = nums[3];
		}
		return num;
	}

	public String getDataStr() {
		return dataStr;
	}

	public void setDataStr(String dataStr) {
		this.dataStr = dataStr;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	public Data getData() {
		if (this.data == null) {
			this.data = Data.fromMap(this.dataStr);
		}
		return this.data;
	}

	/**
	 * @param days
	 * @param deviceID  为0代表7以下版本
	 */
	public void add奇偶返回(int days, int deviceID) {
		int[] array={15,8,4,2,1,0};
		for(int i:array){
			if(days >= i){
				days = i;	break;
			}
		}
		String dev = "";
		if (deviceID == 0) {dev = "其它版本";			
		} else {dev += deviceID % 2;	}
		Data dat = this.getData().getMap("返回").getMap(days);
		dat.put("共计", dat.get("共计").asInt() + 1);
		dat.getMap("详细").put(dev, dat.get("详细").get(dev).asInt() + 1);
		this.dataStr = this.getData().toString();
	}

	/**
	 * 
	 * @param cash       金额
	 * @param deviceID   如果为0代表7以下版本
	 * @param channel
	 */
	public void add奇偶付费(int cash, int deviceID, String channel) {
		String dev = "";
		if (deviceID == 0) {
			dev = "其它版本";
		} else {
			dev += deviceID % 2;
		}
		Data dat = this.getData().getMap("支付");
		dat.getMap("总计次数").put(dev, dat.get("总计次数").get(dev).asInt() + 1);
		dat.getMap("总计金额").put(dev, dat.get("总计金额").get(dev).asInt() + cash);
		dat.getMap("详细次数").getMap(channel).put(dev, dat.get("详细次数").getMap(channel).get(dev).asInt() + 1);
		dat.getMap("详细金额").getMap(channel).put(dev, dat.get("详细金额").getMap(channel).get(dev).asInt() + cash);
		this.dataStr = this.getData().toString();
	}
	
	public void add新增用户(int version) {
		Data dat = this.getData().getMap("新增用户");
		dat.put(version, dat.get(version).asInt() + 1);
		this.dataStr = this.getData().toString();
	}
//	public void add返回用户(int version) {
//		Data dat = this.getData().getMap("返回用户");
//		dat.put(version, dat.get(version).asInt() + 1);
//		this.dataStr = this.getData().toString();
//	}

}
