package cmd;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

import main.Global;
import main.HelloServer;
import main.ServerTimer;

import org.apache.commons.io.IOUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.w3c.dom.Element;

import dao.Dao;
import data.*;
/**
 * 启动应用
 */
public class CMD11 implements ICMD {
	private static final Logger log = Logger.getLogger(CMD11.class.getName());
	
	public static final String wxPayKey = "a85e8cfce3ba32b56b89b4d0e0d4a9d0";
	@Override
	public ChannelBuffer getBytes(int cmd, ChannelBuffer data) {
		int type = data.readByte();log.info("type:"+type);
		int deviceID = data.readInt();log.info("deviceID:"+deviceID);
		String imei = Global.readUTF(data);log.info("imei:"+imei);
		int lesson = data.readByte();log.info("lesson:"+lesson);
		String channel = Global.readUTF(data).split("#")[0];
		int total = data.readInt();
		int money = data.readInt()*100;
		String info = Global.readUTF(data);log.info("info:"+info);

		Device device = Dao.getDevice(deviceID, imei, "微信支付");
		
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(Cmap._11微信支付);
		buf.writeByte(type);
		if(type ==1){//微信app支付
			buf.writeBytes(Global.getUTF(getWxAppPayUrl(money, device.getId()+"-"+lesson+"-"+total+"-"+channel, info)));
		}else if(type ==2){//微信网页支付
			buf.writeBytes(Global.getUTF(getWxH5PayUrl(money, device.getId()+"-"+lesson+"-"+total+"-"+channel, info)));
		}
		return buf;
	}
	public static String getWxAppPayUrl(int money, String remark, String info){//自己的微信支付
		Hashtable<String, String> map = new Hashtable<String, String>();
		map.put("appid", "wx75d0f46f6b004e49");
		map.put("mch_id", "1244135102");
		map.put("nonce_str", Global.md5("少儿象棋教学微信支付"+Math.random()));
		map.put("body", info);
		map.put("attach", remark);
		map.put("out_trade_no", "xiangqi"+ServerTimer.getTotalWithS());
		map.put("total_fee", ""+money);
		map.put("spbill_create_ip", HelloServer.sip);
		map.put("notify_url", "http://"+HelloServer.cbip+":"+HelloServer.httpPort+"/wxpay");
		map.put("trade_type", "APP");
		
		String md5Str = Global.GetSortString(map)+"&key="+wxPayKey;
		log.info("md5Str:"+md5Str);
		String md5 = Global.md5(md5Str).toUpperCase();
		log.info("md5:"+md5);
		String xmlStr = "<xml>";
		Set<Entry<String, String>> ens = map.entrySet();
		for(Entry<String, String> en : ens){
			xmlStr += "<"+en.getKey()+">"+en.getValue()+"</"+en.getKey()+">";
		}
		xmlStr += "<sign>"+md5+"</sign>";
		xmlStr += "</xml>";
		log.info("xmlStr:"+xmlStr);
		
		Record re = Global.addRecord(0, "生成微信APP支付订单", xmlStr, "");
		
		String body = "";
		try{
			URL url = new URL("https://api.mch.weixin.qq.com/pay/unifiedorder");  
	        URLConnection urlConnection = url.openConnection();
	        urlConnection.setDoOutput(true);  
	        OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
	        out.write(xmlStr);  
	        out.flush();
	        out.close();
	        
	        // 从服务器读取响应  
	        InputStream inputStream = urlConnection.getInputStream();  
	        body = IOUtils.toString(inputStream,Charset.forName("utf8"));
		}catch(Exception e){
			e.printStackTrace();
		}
        
        log.info("body:"+body);
        re.setInfo(body);
        Dao.save(re);
        
        HashMap<String, String> resultMap = Global.decodeXML(body);
        
        Hashtable<String, String> map2 = new Hashtable<String, String>();
        map2.put("prepayid", resultMap.get("prepay_id"));
        map2.put("appid", "wx75d0f46f6b004e49");
        map2.put("partnerid", "1244135102");
        map2.put("package", "Sign=WXPay");
        map2.put("noncestr", Global.md5("少儿象棋教学微信支付"+Math.random()));
        map2.put("timestamp", ""+System.currentTimeMillis()/1000);
        
        md5Str = Global.GetSortString(map2)+"&key="+CMD11.wxPayKey;
		log.info("md5Str:"+md5Str);
		md5 = Global.md5(md5Str).toUpperCase();
		log.info("md5:"+md5);
		
		String value = Global.GetSortString(map2)+"&sign="+md5;
		log.info("value:"+value);
		
		return value;
	}
	public static String getWxH5PayUrl(int money, String remark, String info){
		Hashtable<String, String> map = new Hashtable<String, String>();
		map.put("appid", "wx75d0f46f6b004e49");
		map.put("mch_id", "1244135102");
		map.put("nonce_str", Global.md5("少儿象棋教学微信支付"+Math.random()));
		map.put("body", info);
		map.put("attach", remark);
		map.put("out_trade_no", "xiangqi"+ServerTimer.getTotalWithS());
		map.put("total_fee", ""+money);
		map.put("notify_url", "http://"+HelloServer.cbip+":"+HelloServer.httpPort+"/wxpay");
		
		String md5Str = Global.GetSortString(map)+"&key="+CMD11.wxPayKey;
		log.info("md5Str:"+md5Str);
		String md5 = Global.md5(md5Str).toUpperCase();
		log.info("md5:"+md5);
		String param = Global.GetSortString(map)+"&sign="+md5;
		try {
			param = URLEncoder.encode(param, "utf8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BaseData.getContent(BaseData.微信网页支付地址)+"?"+param;
	}
}
