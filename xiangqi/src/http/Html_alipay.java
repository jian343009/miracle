package http;

import java.util.logging.Logger;

import main.Global;
import main.ServerTimer;
import dao.Dao;
import dao.Data;
import data.*;

public class Html_alipay implements IHtml {
	private static final Logger log = Logger.getLogger(Html_alipay.class.getName());

	@Override
	public String getHtml(String content) {
		String html = "success";
		
		synchronized (log) {
			String userID = "";
			int deviceID = 0;
			String imei = "";
			int lesson = 1;
			int total = 0;
			double money = 0;
			String channel = "";
			String status = "";
			String body = "";
			
			String contents[] = content.split("&");
			for(int i=0;i<contents.length;i++)
			{
				if(contents[i].startsWith("buyer_email="))
				{
					userID = contents[i].replaceAll("buyer_email=", "");
				}else if(contents[i].startsWith("total_fee="))
				{
					money = Global.getDouble(contents[i].replaceAll("total_fee=", ""));
				}else if(contents[i].startsWith("device="))
				{
					deviceID = Global.getInt(contents[i].replaceAll("device=", ""));
				}else if(contents[i].startsWith("lesson="))
				{
					lesson = Global.getInt(contents[i].replaceAll("lesson=", ""));
				}else if(contents[i].startsWith("total="))
				{
					total = Global.getInt(contents[i].replaceAll("total=", ""));
				}else if(contents[i].startsWith("channel="))
				{
					channel = contents[i].replaceAll("channel=", "").split("#")[0];
				}else if(contents[i].startsWith("trade_status="))
				{
					status = contents[i].replaceAll("trade_status=", "");
				}else if(contents[i].startsWith("body="))
				{
					body = contents[i].replaceAll("body=", "");
				}
			}
			contents = body.split("&");
			for(int i=0;i<contents.length;i++)
			{
				if(contents[i].startsWith("imei="))
				{
					imei = contents[i].replaceAll("imei=", "");
				}
			}
			
			AliPay pay = Dao.getAliPayByContent(content);
			pay.setUserName(userID);
			pay.setImei(imei);
			pay.setDevice(deviceID+"#"+imei);
			pay.setLesson(lesson);
			pay.setChannel(channel);
			pay.setTradeStatus(status);
			pay.setMoney(money);
			boolean paysuccuss = status.equals("TRADE_SUCCESS");
			if(paysuccuss && money >= 1){
				Device wd = Dao.getDevice(deviceID, imei, "Html_alipay");
				
				Count mc = Dao.getCountMonth();
				Count count = Dao.getCountToday();
				mc.setPay(mc.getPay() +1);
				count.setPay(count.getPay() +1);
				ChannelEveryday ce = Dao.getChannelEverydayToday(channel);
				ce.setPay(ce.getPay() +1);
				if(wd.getBuy() ==0){
					mc.setNewPay(mc.getNewPay() +1);
					count.setNewPay(count.getNewPay() +1);
					ce.setNewPay(ce.getNewPay() +1);
				}
				mc.setTotalPay(mc.getTotalPay() + money);
				mc.setAliPay(mc.getAliPay() + money);				
				count.setTotalPay(count.getTotalPay() + money);
				count.setAliPay(count.getAliPay() + money);
			
				if(Global.getInt(wd.getVersion()) >= 7){
					count.add奇偶付费((int)money, wd.getId(), "支付宝");		
				}else{
					count.add奇偶付费((int)money, 0, "支付宝");
				}
				wd.使用红包((int)money, count);
				wd.checkPrice(wd, (int)money, pay.getId());
				
				Dao.save(count);
				ce.setTotalPay(ce.getTotalPay() + money);
				ce.setAliPay(ce.getAliPay() + money);							
				Dao.save(ce);
				Dao.save(mc);
				if(lesson ==0){
					wd.setLastDay(ServerTimer.distOfDay());
					wd.setLastTime(ServerTimer.getFull());
					wd.setBuyState(wd.getBuyState() | total);
					wd.setBuy(wd.getBuy() +1);
					Dao.save(wd);
					StepCount.getByChannelToday(wd.getChannel()).add支付统计("多课支付成功").store();
				}else{
	//				AppleProduct product = Dao.getWeiqiProductByLesson(lesson);
	//				if(product != null && money >= product.getPrice()){
						int pow = 1<<lesson;
						wd.setLastDay(ServerTimer.distOfDay());
						wd.setLastTime(ServerTimer.getFull());
						wd.setBuyState(wd.getBuyState() | pow);
						wd.setBuy(wd.getBuy() +1);
						wd.setUnlockNum(lesson, 0);
						Dao.save(wd);
						StepCount.getByChannelToday(wd.getChannel()).add支付统计("单课支付成功").store();
	//				}
				}
			}
			pay.setUsed(pay.getUsed() +1);
			Dao.save(pay);
		}
		return html;
	}

}
