package http;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import cmd.CMD11;
import main.*;
import dao.Dao;
import dao.Data;
import data.*;

public class Html_wxpay extends Html {
	private static final Logger log = Logger.getLogger(Html_wxpay.class.getName());
	
	public static boolean verify(Map<String, String> map)
	{
		String sign = map.remove("sign");
		log.info("sign:"+sign);
		String md5Str = Global.GetSortString(map)+"&key="+CMD11.wxPayKey;
		log.info("md5Str:"+md5Str);
		String md5 = Global.md5(md5Str).toUpperCase();
		log.info("md5:"+md5);
		
		return md5.equals(sign);
	}
	@Override
	public String getHtml(String content) {
		String html = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
		
		synchronized (log) {
			HashMap<String, String> map = Global.decodeXML(content);
			log.info("map:"+map);
			String status = map.get("return_code");
			log.info("status:"+status);
			if(status.equals("SUCCESS")){
				String orderId = map.get("transaction_id");
				String userID = map.get("openid");
				int money = Global.getInt(map.get("total_fee"))/100;
				String body = map.get("attach");
				
				log.info("userID:"+userID);
				log.info("money:"+money);
				log.info("body:"+body);
			
				LjPay before = Dao.getLjPayByOrderID(orderId);
				
				LjPay pay = new LjPay();
				pay.setFirstTime(ServerTimer.getFullWithS());
				pay.setUserName(userID);
				pay.setContent(content);
				pay.setOrderID(orderId);
				pay.setParam(body);
				pay.setMoney((int)money);
				Dao.save(pay);
				
				String[] params = body.split("-");
				int deviceID = 0;
				int lesson = 0;
				int total = 0;
				String channel = "";
				if(params.length >=4){
					deviceID = Global.getInt(params[0]);
					lesson = Global.getInt(params[1]);
					total = Global.getInt(params[2]);
					channel = params[3];
				}
				pay.setChannel(channel);
				Device wd = Dao.getDeviceExist(deviceID, "");log.info("device:"+wd);
				if(wd != null && money > 0 && before == null && verify(map)){
					pay.setUserName(wd.getImei());
					
					Count mc = Dao.getCountMonth();
					Count count = Dao.getCountToday();
					mc.setPay(mc.getPay() +1);
					count.setPay(count.getPay() +1);
					ChannelEveryday ce = Dao.getChannelEverydayToday(channel);
					ce.setPay(ce.getPay() +1);
					if(wd.getBuyState() ==0){
						mc.setNewPay(mc.getNewPay() +1);
						count.setNewPay(count.getNewPay() +1);
						ce.setNewPay(ce.getNewPay() +1);
					}
					mc.setTotalPay(mc.getTotalPay() + money);
					mc.setWxPay(mc.getWxPay() + money);
					
					count.setTotalPay(count.getTotalPay() + money);
					count.setWxPay(count.getWxPay() + money);
					if(Global.getInt(wd.getVersion()) >= 7){
						count.add奇偶付费(money, wd.getId(), "微信支付");
					}else{
						count.add奇偶付费(money, 0, "微信支付");
					}
					Dao.save(count);
					ce.setTotalPay(ce.getTotalPay() + money);
					ce.setWxPay(ce.getWxPay() + money);
					Dao.save(ce);
					log.info("lesson:"+lesson);
					log.info("total:"+total);
					Data dat = Data.fromMap(wd.getReward());
					for(int les:new int[]{1,2}){
						if("未使用".equals(dat.get(les).get("状态").asString())){
							dat.getMap(les).put("状态", "已使用");	//改用户红包状态
							Data data1=Data.fromMap(count.getReward());//记录红包使用
							Data data2=data1.getMap("红包使用");
							data2.put("次数", data2.get("次数").asInt()+1);
							data2.put("金额", data2.get("金额").asInt()+dat.get(les).get("金额").asInt());
							count.setReward(data1.toString());
						}
					}
					Dao.save(mc);
					wd.setReward(dat.toString());
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
					log.info("device.buyState:"+wd.getBuyState());
				}
				pay.setUsed(pay.getUsed() +1);
				pay.setFirstTime(pay.getFirstTime() + "#"+ServerTimer.getFullWithS());
				Dao.save(pay);
			}
		}
		
		return html;
	}
}
