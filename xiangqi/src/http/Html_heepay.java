package http;

import java.net.URLDecoder;
import java.util.*;

import org.jboss.logging.Logger;

import data.*;
import dao.*;
import main.*;

public class Html_heepay implements IHtml{
	private static final Logger log = Logger.getLogger(Html_heepay.class);

	public String getHtml(String content)
	{
		String html = "ok";
		String key = "57D0E7D84D8547AE84C31D34";
		
		try {
			content = URLDecoder.decode(content, "gb2312");
		} catch (Exception e) {
			e.printStackTrace();
		}
		synchronized (log) {
			LjPay pay = new LjPay();
			pay.setFirstTime(ServerTimer.getFullWithS());
			pay.setContent(content);
			Dao.save(pay);
			
			HashMap<String, String> map = Global.decodeUrlParam(content);
			log.info("汇付宝:"+map);
			String sign = Global.md5("result="+map.get("result")+"&agent_id="+map.get("agent_id")+"&jnet_bill_no="+map.get("jnet_bill_no")+"&agent_bill_id="+map.get("agent_bill_id")+"&pay_type="+map.get("pay_type")+"&pay_amt="+map.get("pay_amt")+"&remark="+map.get("remark")+"&key="+key);
			log.info("sign:"+sign);
			pay.setOrderID(map.get("jnet_bill_no"));
			LjPay before = Dao.getLjPayByOrderID(pay.getOrderID());
			String remark = "";
			if(map.get("remark") != null){
				remark = map.get("remark");
			}
			pay.setParam(remark);
			String[] params = remark.split("-");
			int id = 0;
			int lesson = 0;
			int total = 0;
			String channel = "汇付宝";
			if(params.length >=4){
				id = Global.getInt(params[0]);
				lesson = Global.getInt(params[1]);
				total = Global.getInt(params[2]);
				channel = params[3];
			}
			pay.setChannel(channel);
			String result = "";
			if(map.get("result") != null){
				result = map.get("result");
			}
			double money = 0;
			if(map.get("pay_amt") != null){
				money = Global.getDouble(map.get("pay_amt"));
				pay.setMoney((int)money);
			}
			
			if(channel.equals("团购功能屏蔽中")){
				//id = tuan.getId()+"-"+lesson = mobile.getId()+"-"+total = 131068 + channel = "-团购",
				Tuan tuan = Tuan.getByID(id);
				if(tuan == null){
					return "error";
				}else if(lesson == 888){//发起团购
					Device device = Dao.getDeviceExist(0, tuan.getImei());
					if(device != null){//改发起人的状态为团购。
						device.setState(1);	//改状态
						device.setBuy( device.getBuy() + 1 );
						device.setMoney(device.getMoney() + (int)money);
						Dao.save(device);
					}
					tuan.setPeoples(1);
					tuan.setStatus("已开团");
					tuan.setFirstTime(ServerTimer.getFull());// 设置开始时间
					int lastHour = 24;//团购有效期
					tuan.setLastTime(ServerTimer.distOfSecond() + lastHour * 60 * 60);
					Calendar c1 = Calendar.getInstance();
					c1.add(Calendar.HOUR, lastHour);// 设置结束时间			
					tuan.setLastTimeStr(ServerTimer.getFull(c1));// 设置结束时间
					Dao.save(tuan);
				}else if("已开团".equals(tuan.getStatus())){
					if(999 == lesson){//补差价。
						Device device = Dao.getDeviceExist(0, tuan.getImei());
						if(device != null){
							device.setBuyState(total);
							device.setBuy( device.getBuy() + 1 );
							device.setMoney(device.getMoney() + (int)money);
							Dao.save(device);
						}					
					}
					Mobile mobile = Mobile.getByID(lesson);//mobile在通过手机验证的时候生成
					if(mobile != null){
						mobile.setImei("");//这样一个手机号可以多次团
						mobile.setTuanID(tuan.getId());
						mobile.setBuy(mobile.getBuy() + 1);
						mobile.setOffbuy((int)money);
						mobile.setBuyState(total);
						mobile.setFirstTime(ServerTimer.getFull());
						Dao.save(mobile);
					}
					//有人参团，团购有效期延时
					int lastHour = 24;//团购有效期
					tuan.setLastTime(ServerTimer.distOfSecond() + lastHour * 60 * 60);
					tuan.setLastTimeStr(ServerTimer.getFull(tuan.getLastTime()));// 设置结束时间					
					tuan.setPeoples(tuan.getPeoples() + 1);//参团人数加1
					tuan.setPay(tuan.getPay() +1);
					tuan.setPayMobile(tuan.getPayMobile()+lesson+"#");
					tuan.setPayMoney(tuan.getPayMoney() + money);
					Dao.save(tuan);
					if(tuan.getPeoples() == 2){//达到参团人数，发起人解锁。
						Device device = Dao.getDeviceExist(0, tuan.getImei());
						if(device != null){
							device.setBuyState(total);
							Dao.save(device);
						}
					}
				}
				
			}else if(channel.equals("众筹屏蔽中")){
				Chou chou = Chou.getByID(id);
				if(chou != null){
					chou.setPay(chou.getPay() +1);
					chou.setPayMobile(chou.getPayMobile() + lesson + "#");
					chou.setPayMoney(chou.getPayMoney() + money);
					Dao.save(chou);
				}
			}else{
				Device wd = Dao.getDeviceExist(id, "");
				if(wd != null && money > 1 && before == null && result.equals("1") && sign.equals(map.get("sign"))){
					pay.setUserName(wd.getImei());
					
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
					count.setTotalPay(count.getTotalPay() + money);
					ce.setTotalPay(ce.getTotalPay() + money);
					if(map.get("pay_type").equals("30")){
						mc.setWxPay(mc.getWxPay() + money);
						count.setWxPay(count.getWxPay() + money);
						ce.setWxPay(ce.getWxPay() + money);
			
						if(Global.getInt(wd.getVersion()) >= 7){
							count.add奇偶付费((int)money, wd.getId(), "微信支付");						
						}else{
							count.add奇偶付费((int)money, 0, "微信支付");
						}
					}else{
						mc.setWiiPay(mc.getWiiPay() + money);
						count.setWiiPay(count.getWiiPay() + money);
						ce.setWiiPay(ce.getWiiPay() + money);
						if(Global.getInt(wd.getVersion()) >= 7){
							count.add奇偶付费((int)money, wd.getId(), "其它支付");							
						}else{
							count.add奇偶付费((int)money, 0, "其它支付");
						}
					}
					wd.使用红包((int)money, count);
					Dao.save(count);
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
			}
			pay.setUsed(pay.getUsed() +1);
			pay.setFirstTime(pay.getFirstTime() + "#"+ServerTimer.getFullWithS());
			Dao.save(pay);
		}		
		return html;
	}
}
