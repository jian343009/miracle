package http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import data.*;
import dao.*;
import main.Global;
import main.ServerTimer;

public class Html_newletvpay extends Html{
	private static final Logger log = Logger.getLogger(Html_newletvpay.class);

	public String getHtml(String content)
	{
		String html = "{\"code\":0,\"msg\":\"\"}";
		String key = "whnfqk2uuwllnafjcaq9qbbkmrebg8py";
		
		synchronized (log) {
			LjPay pay = new LjPay();
			pay.setFirstTime(ServerTimer.getFullWithS());
			pay.setContent(content);
			pay.setChannel("新乐视");
			
			
			HashMap<String, String> map = Global.decodeUrlParam(content);
			log.info("新乐视回调:"+map);
			String signBefore = map.remove("sign");log.info("sign:"+signBefore);
			String md5Str = Global.GetSortString(map, "&")+"&key="+key;
			log.info("md5Str:"+md5Str);
			String sign = Global.md5(md5Str);
			log.info("md5:"+sign);
			
			if(map.get("orderNo") != null){
				pay.setOrderID(map.get("orderNo"));
			}
			LjPay before = Dao.getLjPayByOrderID(pay.getOrderID());
			if(map.get("paramExt") != null){
				pay.setParam(map.get("paramExt"));
			}
			String[] params = pay.getParam().split("-");
			int deviceID = 0;
			int lesson = 0;
			int total = 0;
			String channel = "";
			if(params.length >=4){
				deviceID = Global.getInt(params[0]);
				lesson = Global.getInt(params[1]);
				total = Global.getInt(params[2]);
				channel = params[3].split("#")[0];
			}
			double money = 0;
			if(map.get("price") != null){
				money = Global.getDouble(map.get("price"));
				pay.setMoney((int)money);
			}
			Record record = Global.addRecord(deviceID, "", "html_letvpay", "乐视支付回调");
			Device wd = Dao.getDeviceExist(deviceID,"");
			log.info("wd:"+wd+",money:"+money+",before:"+before+",sign:"+sign.equals(signBefore));
			if(wd != null && money > 0 && before == null && sign.equals(signBefore)){
				pay.setUserName(wd.getImei());
				
				Count mc = Dao.getCountMonth();
				Count count = Dao.getCountToday();
				mc.setPay(mc.getPay() + 1);
				count.setPay(count.getPay() +1);
				ChannelEveryday ce = Dao.getChannelEverydayToday(channel);
				ce.setPay(ce.getPay() +1);
				if(wd.getBuyState() ==0){
					mc.setNewPay(mc.getNewPay() +1);
					count.setNewPay(count.getNewPay() +1);
					ce.setNewPay(ce.getNewPay() +1);
				}
				mc.setTotalPay(mc.getTotalPay() + money);
				mc.setWiiPay(mc.getWiiPay() + money);
				
				count.setTotalPay(count.getTotalPay() + money);
				ce.setTotalPay(ce.getTotalPay() + money);
				count.setWiiPay(count.getWiiPay() + money);
				ce.setWiiPay(ce.getWiiPay() + money);
				log.info("version = "+wd.getVersion());
				if(Global.getInt(wd.getVersion()) >= 7){
					count.add奇偶付费((int)money, wd.getId(), "其它支付");
					log.info(count.getDataStr());
				}
				Dao.save(count);
				Dao.save(ce);
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
//					AppleProduct product = Dao.getWeiqiProductByLesson(lesson);
//					if(product != null && money >= product.getPrice()){
						int pow = 1<<lesson;
						wd.setLastDay(ServerTimer.distOfDay());
						wd.setLastTime(ServerTimer.getFull());
						wd.setBuyState(wd.getBuyState() | pow);
						wd.setBuy(wd.getBuy() +1);
						wd.setUnlockNum(lesson, 0);
						Dao.save(wd);
						StepCount.getByChannelToday(wd.getChannel()).add支付统计("单课支付成功").store();
//					}
				}
				
				record.setImei(wd.getImei());
				record.setTimeStr(record.getTimeStr() + "#" +ServerTimer.getFullWithS());
				Dao.save(record);
				
				html = "SUCCESS";
			}
			pay.setUsed(pay.getUsed() +1);
			pay.setFirstTime(pay.getFirstTime() + "#"+ServerTimer.getFullWithS());
			Dao.save(pay);
		}
		
		return html;
	}
}
