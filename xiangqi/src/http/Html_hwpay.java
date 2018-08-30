package http;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import main.*;
import dao.Dao;
import dao.Data;
import data.*;

public class Html_hwpay extends Html {
	private static final Logger log = Logger.getLogger(Html_hwpay.class.getName());
	
	public static String PublicKey =	"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAK66cNxY0mzFz2FH9swQVDBfv2cy4TO6GTdF/yDKTYsgm8DO5q9wFc/XQ3Ydt2veuCOaPKHTWRKUdWB5LyBGOjkCAwEAAQ==";
	//测试公钥
//	public static String PublicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIYSLknVbI4U2FHjOM1z5mb7+VPXCsIs5vFKuy4/xRQpF/NCx8GBOUR/6SR1YQMFqgIaLcQ+goxI/kOM2b2VjVECAwEAAQ==";

	@Override
	public String getHtml(String content) {
		String html = "{\"result\":0}";
		if(content.isEmpty()){
			return html;
		}
		synchronized (log) {
			String extReserved = "";
			String orderId = "";
			double price = 0;
			String sign = "";
			
			String contents[] = content.split("&");
			for(int i=0;i<contents.length;i++)
			{
				if(contents[i].startsWith("extReserved="))
				{
					extReserved = contents[i].replaceAll("extReserved=", "");
				}else if(contents[i].startsWith("orderId="))
				{
					orderId = contents[i].replaceAll("orderId=", "");
				}else if(contents[i].startsWith("amount="))
				{
					price = Global.getDouble(contents[i].replaceAll("amount=", ""));
				}else if(contents[i].startsWith("sign=")){
					sign = contents[i].replaceAll("sign=", "");
				}
			}
			log.info("orderId:"+orderId);
			log.info("price:"+price);
			log.info("extReserved:"+extReserved);
			log.info("sign:"+sign);
			
//			String md5 = Global.md5(orderId+price+extReserved+"58a7e728a29f48b4a35b63dea655bdb5");
//			log.info("md5:"+md5);
			HashMap<String, String> map = this.getValue(content);
			log.info("sign:"+map.get("sign"));
			String param = RSA.getSignData(map);
			log.info("param:"+param);
			boolean succ = RSA.doCheck(param, sign, PublicKey);
			log.info("succ:"+succ);
			
			LjPay before = Dao.getLjPayByOrderID(orderId);
			
			LjPay pay = new LjPay();
			pay.setFirstTime(ServerTimer.getFullWithS());
			pay.setContent(content);
			pay.setOrderID(orderId);
			pay.setParam(extReserved);
			Dao.save(pay);
			
			String[] params = extReserved.split("-");
			int deviceID = 0;
			int lesson = 0;
			int total = 0;
			String channel = "华为";
			if(params.length >=4){
				deviceID = Global.getInt(params[0]);
				lesson = Global.getInt(params[1]);
				total = Global.getInt(params[2]);
				channel = params[3].split("#")[0];
			}
			pay.setChannel(channel);
			String result = "";
			if(map.get("result") != null){
				result = map.get("result");
			}
			double money = 0;
			if(map.get("amount") != null){
				money = Global.getDouble(map.get("amount"));
				pay.setMoney((int)money);
			}
			Device wd = Dao.getDeviceExist(deviceID, "");log.info("device:"+wd);
			if(wd != null && money > 0 && before == null && result.equals("0")){
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
				mc.setHwPay(mc.getHwPay() + money);
				Dao.save(mc);
				count.setTotalPay(count.getTotalPay() + money);
				count.setHwPay(count.getHwPay() + money);
				
				if(Global.getInt(wd.getVersion()) >= 7){
					count.add奇偶付费((int)money, wd.getId(), "华为支付");
				}else{
					count.add奇偶付费((int)money, 0, "华为支付");
				}
				Dao.save(count);
				ce.setTotalPay(ce.getTotalPay() + money);
				ce.setHwPay(ce.getHwPay() + money);
				Dao.save(ce);
				log.info("lesson:"+lesson);
				log.info("total:"+total);
				Data dat = Data.fromMap(wd.getReward());
				for(int les:new int[]{1,2}){
					if("未使用".equals(dat.get(les).get("状态").asString())){
						dat.getMap(les).put("状态", "已使用");		}
				}	
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
		
		return html;
	}
	
	/**
     * @param str
     * @return
     *         本接口Content-Type是：application/x-www-form-urlencoded，对所有参数，会自动进行编码，接收端收到消息也会自动根据Content-Type进行解码。
     *         同时，接口中参数在发送端并没有进行单独的URLEncode (sign和extReserved、sysReserved参数除外)，所以，在接收端根据Content-Type解码后，即为原始的参数信息。
     *         但是HttpServletRequest的getParameter()方法会对指定参数执行隐含的URLDecoder.decode(),所以，相应参数中如果包含比如"%"，就会发生错误。
     *         因此，我们建议通过如下方法获取原始参数信息。
     * 
     *         注：使用如下方法必须在原始ServletRequest未被处理的情况下进行，否则无法获取到信息。比如，在Struts情况，由于struts层已经对参数进行若干处理，
     *         http中InputStream中其实已经没有信息，因此，本方法不适用。要获取原始信息，必须在原始的，未经处理的ServletRequest中进行。
     */
    public HashMap<String, String> getValue(String str)
    {
        
        HashMap<String, String> valueMap = new HashMap<String, String>();
        if (null == str || "".equals(str))
        {
            return valueMap;
        }
        
        String[] valueKey = str.split("&");
        for (String temp : valueKey)
        {
            String[] single = temp.split("=");
            valueMap.put(single[0], single[1]);
        }
        
        // 接口中，如下参数sign和extReserved、sysReserved是URLEncode的，所以需要decode，其他参数直接是原始信息发送，不需要decode
        try
        {
            String sign = (String)valueMap.get("sign");
            String extReserved = (String)valueMap.get("extReserved");
            String sysReserved = (String)valueMap.get("sysReserved");
            
            if (null != sign)
            {
                sign = URLDecoder.decode(sign, "utf-8");
                valueMap.put("sign", sign);
            }
            if (null != extReserved)
            {
                extReserved = URLDecoder.decode(extReserved, "utf-8");
                valueMap.put("extReserved", extReserved);
            }
            
            if (null != sysReserved)
            {
                sysReserved = URLDecoder.decode(sysReserved, "utf-8");
                valueMap.put("sysReserved", sysReserved);
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return valueMap;
        
    }

}
