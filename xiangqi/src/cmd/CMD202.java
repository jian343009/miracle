package cmd;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import main.Global;
import main.HelloServer;
import main.ServerTimer;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dao.Dao;
import data.*;
/**
 * 启动应用
 */
public class CMD202 implements ICMD {
	private static final Logger log = Logger.getLogger(CMD202.class.getName());
	
	public static final int agent_id = 1982416;
	@Override
	public ChannelBuffer getBytes(int cmd, ChannelBuffer data) {
		int deviceID = data.readInt();log.info("deviceID:"+deviceID);
		String imei = Global.readUTF(data);log.info("imei:"+imei);
		int lesson = data.readByte();log.info("lesson:"+lesson);
		String channel = Global.readUTF(data).split("#")[0];
		int total = data.readInt();
		int money = data.readInt();
		String goods_name = Global.readUTF(data); log.info("goods_name:"+goods_name+",len:"+goods_name.length());
		if(goods_name.length() >25){
			goods_name = goods_name.substring(0, 25)+"...";
			log.info("goods_name:"+goods_name+",len:"+goods_name.length());
		}
		String goods_note = Global.readUTF(data);log.info("goods_note:"+goods_note);
		
		Device device = Dao.getDevice(deviceID, imei, "汇付宝微信支付");
        
        int user_identity = device.getId();
        String agent_bill_id = "xiangqi"+ServerTimer.getTotalWithS();
        Element root = CMD202.getToken(money, user_identity, agent_bill_id, device.getId()+"-"+lesson+"-"+total+"-"+channel, goods_name, goods_note);

		String name = "";
        String value = "";
        if(root != null){
	        name = root.getNodeName();
	        value = root.getTextContent();
        }
        log.info("name:"+name);
        log.info("value:"+value);
        
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(cmd);
		if(name.equals("token_id")){
        	buf.writeByte(1);
        	buf.writeInt(device.getId());
        	buf.writeBytes(Global.getUTF(value));
        	buf.writeBytes(Global.getUTF(agent_bill_id));
        	buf.writeInt(agent_id);
        }else if(name.equals("error")){
        	buf.writeByte(2);
        	buf.writeInt(device.getId());
        	buf.writeBytes(Global.getUTF(value));
        }
		return buf;
	}
	public static Element getToken(int money, int user_identity, String agent_bill_id, String remark, String goods_name, String goods_note){
		String version = "1";
		String pay_type = "30";
		String pay_amt = money+".00";
		String notify_url = "http://xiangqipay.miracle-cn.com:"+HelloServer.httpPort+"/heepay";
		String return_url = "http://www.miracle-cn.com";
		String user_ip = "127_0_0_1";
		String agent_bill_time = ServerTimer.getTotal();
		int goods_num = 1;
		String key = "57D0E7D84D8547AE84C31D34";
		String sign = Global.md5("version="+version+"&agent_id="+agent_id+"&agent_bill_id="+agent_bill_id+"&agent_bill_time="+agent_bill_time+"&pay_type="+pay_type+"&pay_amt="+pay_amt+"&notify_url="+notify_url+"&user_ip="+user_ip+"&user_identity="+user_identity+"&key="+key);
		
        String meta = "[{\"s\":\"Android\",\"n\":\"少儿象棋教学\",\"id\":\"air.com.miracle.xiangqi\"},{\"s\":\"IOS\",\"n\":\"\",\"id\":\"\"}]";
		try{
			goods_name = URLEncoder.encode(goods_name, "gb2312");
			goods_note = URLEncoder.encode(goods_note, "gb2312");
			remark = URLEncoder.encode(remark, "gb2312");
			meta = URLEncoder.encode(Base64.encodeBase64String(meta.getBytes("gb2312")), "utf8");
			
			URL url = new URL("https://pay.heepay.com/Phone/SDK/PayInit.aspx");  
	        URLConnection urlConnection = url.openConnection();
	        urlConnection.setDoOutput(true);  
	        OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
	        String param = 
	        		"version="+version+
	        		"&pay_type="+pay_type+
	        		"&agent_id="+agent_id+
	        		"&agent_bill_id="+agent_bill_id+
	        		"&pay_amt="+pay_amt+
	        		"&notify_url="+notify_url+
	        		"&return_url="+return_url+
	        		"&user_ip="+user_ip+
	        		"&user_identity="+user_identity+
	        		"&agent_bill_time="+agent_bill_time+
	        		"&goods_name="+goods_name+
	        		"&goods_num="+goods_num+
	        		"&remark="+remark+
	        		"&goods_note="+goods_note+
	        		"&sign="+sign+
	        		"&meta_option="+meta;
	        log.info("param:"+param);
	        out.write(param);  
	        out.flush();
	        out.close();
	        
	        // 从服务器读取响应  
	        InputStream inputStream = urlConnection.getInputStream();  
	        String body = IOUtils.toString(inputStream,Charset.forName("utf8"));
	        
	        log.info("body:"+body);
	        
	        Document doc = Global.xmlParser(body);
	        Element root = doc.getDocumentElement();
	        
	        return root;
		}catch(Exception e){
			log.warning("Exception:"+e.getLocalizedMessage());
		}
        return null;
	}
}
