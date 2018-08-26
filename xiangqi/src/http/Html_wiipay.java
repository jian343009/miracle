package http;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import main.Global;
import main.ServerTimer;
import net.sf.json.JSONObject;
import dao.*;
import data.*;

public class Html_wiipay implements IHtml{
	private static Logger log = Logger.getLogger(Html_wiipay.class);

	public String getHtml(String content)
	{
//		operatorType=CT&operatorTypeTile=电信&channelCode=100186&appCode=03670003&payCode=0001
//		&imsi=460030495811183&tel=&state=success&price=2&bookNo=B02041532058617436&date=20150204153242
//		&devPrivate=eyJ1aWQiOiIyMTY5In0=&synType=wiipay
//		&sig=LtXIV5PDTZD0p/IxeqhSgUNFlMH8enGPl5TJVsCmDd4lZcAqgSno9DkVQ2r7lUOsUcSdCt2yX1jf
//		5/drWsRjs3IYBE0qdLdpRBoUfNtQLK1VJXJMYb0UqYUAhyLNz6FOQf9gLY45m+dzH52RZfkcjxZp
//		MNpax2thuxg0zfaZ4k1mU81KuqlF8+kfdrnmzVYyuq6rkMB8GvtB2gMgHdOqDMmuIhjOOB6YkM8J
//		S2zISwNVgCGl4DhbR0aeDRhw3mZE4OOx7WkEUs4qNuIhDrIY2HukZNAKxYTMCBboz1erk1mbQqNk
//		X3P43cPofzco80LWgvdYMfUcRxWVwhko31Ro7A==
		
		synchronized (log) {
			String payCode = "";
			String imsi = "";
			String tel = "";
			String state = "";
			int money = 0;
			String devPrivate = "";
			String synType = "";
			
			String[] conts = content.split("&");
			for(int m=0;m<conts.length;m++){
				if(conts[m].startsWith("payCode=")){
					payCode = conts[m].replace("payCode=", "");
				}else if(conts[m].startsWith("imsi=")){
					imsi = conts[m].replace("imsi=", "");
				}else if(conts[m].startsWith("tel=")){
					tel = conts[m].replace("tel=", "");
				}else if(conts[m].startsWith("state=")){
					state = conts[m].replace("state=", "");
				}else if(conts[m].startsWith("price=")){
					money = Global.getInt(conts[m].replace("price=", ""));
				}else if(conts[m].startsWith("devPrivate=")){
					devPrivate = conts[m].replace("devPrivate=", "");
				}else if(conts[m].startsWith("synType=")){
					synType = conts[m].replace("synType=", "");
				}
			}
			String imei = "";
			int deviceID = 0;
			int lesson = 0;
			String channel = "";
			String param = Global.DecodBASE64(devPrivate);
			JSONObject obj = JSONObject.fromObject(param);
			if(obj != null){
				if(obj.get("imei") != null){
					imei = obj.getString("imei");
				}
				if(obj.get("device") != null){
					deviceID = obj.getInt("device");
				}
				if(obj.get("lesson") != null){
					lesson = obj.getInt("lesson");
				}
				if(obj.get("channel") != null){
					channel = obj.getString("channel");
				}
			}
			
			AliPay pay = Dao.getAliPayByContent(content);
			pay.setUserName(imsi);
			pay.setImei(imei);
			pay.setDevice(deviceID+"#"+imei);
			pay.setLesson(lesson);
			pay.setChannel(channel);
			pay.setTradeStatus(state);
			pay.setMoney(money);
			
			boolean succ = state.equals("success");
			if(succ){
				AppleProduct product = Dao.getWeiqiProductByLesson(lesson);
				if(product != null && money >= product.getPrice()){
					int pow = 1<<lesson;
					Device wd = Dao.getDevice(deviceID, imei, "Html_wiipay");
					
					Count mc = Dao.getCountMonth();
					Count count = Dao.getCountToday();
					mc.setPay(mc.getPay() +1);
					count.setPay(count.getPay() + 1);
					ChannelEveryday ce = Dao.getChannelEverydayToday(channel);
					ce.setPay(ce.getPay() +1);
					if(wd.getBuyState() ==0){
						mc.setNewPay(mc.getNewPay() +1);
						count.setNewPay(count.getNewPay() +1);
						ce.setNewPay(ce.getNewPay() +1);
					}
					mc.setTotalPay(mc.getTotalPay() + money);
					mc.setWiiPay(mc.getWiiPay() + money);
					Dao.save(mc);
					count.setTotalPay(count.getTotalPay() + money);
					count.setWiiPay(count.getWiiPay() + money);
					if(Global.getInt(wd.getVersion()) >= 7){
						count.add奇偶付费(money, wd.getId(), "其它支付");
					}else{
						count.add奇偶付费(money, 0, "其它支付");
					}
					Dao.save(count);
					ce.setTotalPay(ce.getTotalPay() + money);
					ce.setWiiPay(ce.getWiiPay() + money);
					Dao.save(ce);
					
					wd.setLastDay(ServerTimer.distOfDay());
					wd.setLastTime(ServerTimer.getFull());
					wd.setBuyState(wd.getBuyState() | pow);
					wd.setBuy(wd.getBuy() +1);
					wd.setUnlockNum(lesson, 0);
					Dao.save(wd);
				}
			}
			pay.setUsed(pay.getUsed() +1);
			Dao.save(pay);
		}
		return "success";
	}
}
