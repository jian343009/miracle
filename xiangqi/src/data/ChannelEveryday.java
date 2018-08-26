package data;

import main.Global;

public class ChannelEveryday {
	
	private int id;
	private String channel = "";
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
	private int yesterday = 0;
	private String detail = "";
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
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
	public int getYesterday() {
		return yesterday;
	}
	public void setYesterday(int yesterday) {
		this.yesterday = yesterday;
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
		} else{
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
}
