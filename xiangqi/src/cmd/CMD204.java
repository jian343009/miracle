package cmd;

import java.util.logging.Logger;

import main.Global;
import main.ServerTimer;
import net.sf.json.JSONObject;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import dao.Dao;
import dao.Data;
import data.*;

public class CMD204 implements ICMD {
	private static final Logger log = Logger.getLogger(CMD204.class.getName());
	
	@Override
	public ChannelBuffer getBytes(int cmd, ChannelBuffer data) {
		int deviceID = data.readInt();log.info("deviceID:"+deviceID);
		String imei = Global.readUTF(data);log.info("imei:"+imei);
		int lesson = data.readByte();log.info("lesson:"+lesson);
		int total = data.readInt();log.info("total:"+total);
		String identifier = Global.readUTF(data);log.info("id:"+identifier);
		String productIdentifier = Global.readUTF(data);log.info("pi:"+productIdentifier);
		String receipt = Global.readUTF(data);log.info("receipt:"+receipt);
		
		ApplePay pay = new ApplePay();
		pay.setIdentifier(identifier);
		pay.setProductIdentifier(productIdentifier);
		pay.setReceipt(receipt);
		pay.setFirstTime(ServerTimer.getFullWithS());
		pay.setLesson(lesson);
		pay.setTotal(total);
		
		int originalSize = 0;
		ApplePay before = null;
		Device device = Dao.getDevice(deviceID, imei, "CMD204");
		
		JSONObject receiptJSON = null;
		try{
			receiptJSON = JSONObject.fromObject(receipt);
		}catch(Exception e){
			log.info(e.getLocalizedMessage());
		}
		if(receiptJSON != null && !receiptJSON.isEmpty())
		{
			String environment = "Apple";
			if(receiptJSON.get("environment") != null)
			{
				environment = receiptJSON.getString("environment");
			}
			pay.setEnvironment(environment);
			pay.setResult(Dao.checkReceipt(receipt, environment.equalsIgnoreCase("Sandbox")));
			JSONObject obj = JSONObject.fromObject(pay.getResult());
			JSONObject obj2 = obj.getJSONObject("receipt");
			if(!obj2.isEmpty()){
				if(obj2.get("original_transaction_id") != null)
				{
					String oti = obj2.getString("original_transaction_id");
					originalSize = Dao.getOriginalSize(oti);
					before = Dao.getOriginalPay(oti);
					pay.setOriginal(oti);
				}
				if(obj2.get("product_id") != null){
					pay.setProduct_id(obj2.getString("product_id"));
				}
			}
		}
		pay.setUsed(pay.getUsed() +1);
		pay.setDevice(pay.getDevice() + device +";");
		pay.setLastTime(ServerTimer.getFullWithS());
		Dao.save(pay);
		
		int result = 2;
		String msg = "";
		
		if(before != null){
			if(before.getLesson() >0){
				lesson = before.getLesson();
			}
			if(before.getTotal() >0){
				total = before.getTotal();
			}
		}
		
		JSONObject obj = null;
		if(!pay.getResult().isEmpty())
		{
			obj = JSONObject.fromObject(pay.getResult());
		}
		if(obj != null && obj.get("status") != null && obj.getInt("status") == 0 && originalSize <3)
		{
			JSONObject obj2 = obj.getJSONObject("receipt");
			if(!obj2.isEmpty() && obj2.get("product_id") != null)
			{
				AppleProduct apt = Dao.getAppleProductByProductIdentifier(obj2.getString("product_id"));
				if(apt != null)
				{
					result = 1;
					int money = apt.getPrice();
					
					Count mc = Dao.getCountMonth();
					Count count = Dao.getCountToday();
					mc.setPay(mc.getPay() +1);
					count.setPay(count.getPay() +1);
					ChannelEveryday ce = Dao.getChannelEverydayToday("苹果商城");
					ce.setPay(ce.getPay() +1);
					if(device.getBuyState() ==0){
						mc.setNewPay(mc.getNewPay() +1);
						count.setNewPay(count.getNewPay() +1);
						ce.setNewPay(ce.getNewPay() +1);
					}
					mc.setTotalPay(mc.getTotalPay() + money);
					mc.setApplePay(mc.getApplePay() + money);					
					count.setTotalPay(count.getTotalPay() + money);
					count.setApplePay(count.getApplePay() + money);				
					if(Global.getInt(device.getVersion()) >= 7){
						count.add奇偶付费(money, device.getId(), "苹果支付");						
					}else{
						count.add奇偶付费(money, 0, "苹果支付");
					}
					device.使用红包((int)money, count);
					ce.setTotalPay(ce.getTotalPay() + money);
					ce.setApplePay(ce.getApplePay() + money);
					Dao.save(ce);
					Dao.save(count);
					Dao.save(mc);
					if(lesson ==0){
						device.setBuyState(device.getBuyState() | total);
						device.setBuy(device.getBuy() +1);
						device.setLastDay(ServerTimer.distOfDay());
						device.setLastTime(ServerTimer.getFullWithS());
						Dao.save(device);
					}else{
		//				AppleProduct product = Dao.getWeiqiProductByLesson(lesson);
		//				if(product != null && money >= product.getPrice()){
							int pow = 1<<lesson;
							device.setBuyState(device.getBuyState() | pow);
							device.setBuy(device.getBuy() +1);
							device.setUnlockNum(lesson, 0);
							device.setLastDay(ServerTimer.distOfDay());
							device.setLastTime(ServerTimer.getFull());
							Dao.save(device);
		//				}
					}
				}else{
					msg = "未知支付商品";
				}
			}
		}else{
			msg = "验证不成功";
		}
		
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(cmd);
		buf.writeByte(result);
		buf.writeInt(device.getId());
		buf.writeBytes(Global.getUTF(identifier));
		buf.writeBytes(Global.getUTF(msg));
		return buf;
	}

}
