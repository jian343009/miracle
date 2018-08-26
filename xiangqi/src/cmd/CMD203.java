package cmd;

import java.net.URLEncoder;
import java.util.logging.Logger;

import main.Global;
import main.HelloServer;
import main.ServerTimer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.w3c.dom.Element;

import dao.Dao;
import data.*;
/**
 * 启动应用
 */
public class CMD203 implements ICMD {
	private static final Logger log = Logger.getLogger(CMD203.class.getName());
	
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

		Device device = Dao.getDevice(deviceID, imei, "汇付宝网页支付");
		
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(cmd);
		if(money >0){
			buf.writeByte(1);
			buf.writeInt(device.getId());
			
			buf.writeBytes(Global.getUTF(getPayUrl(money, "xiangqi"+ServerTimer.getTotalWithS(), device.getId()+"-"+lesson+"-"+total+"-"+channel, goods_name, goods_note)));
		}else{
			buf.writeByte(2);
			buf.writeInt(device.getId());
			buf.writeBytes(Global.getUTF("支付金额不足"));
		}
		return buf;
	}
	public static String getPayUrl(int money, String agent_bill_id, String remark, String goods_name, String goods_note){
		String version = "1";
		String pay_type = "0";
		int agent_id = 1982416;
		String pay_amt = money+".00";
		String notify_url = "http://xiangqipay.miracle-cn.com:"+HelloServer.httpPort+"/heepay";
		String return_url = "http://www.miracle-cn.com/ysz.html";
		String user_ip = "127_0_0_1";
		String agent_bill_time = ServerTimer.getTotal();
		int goods_num = 1;
		String key = "57D0E7D84D8547AE84C31D34";
		String sign = Global.md5(
				"version="+version+
				"&agent_id="+agent_id+
				"&agent_bill_id="+agent_bill_id+
				"&agent_bill_time="+agent_bill_time+
				"&pay_type="+pay_type+
				"&pay_amt="+pay_amt+
				"&notify_url="+notify_url+
				"&return_url="+return_url+
				"&user_ip="+user_ip+
//				"&is_test=1" +
				"&key="+key);

		try{
			goods_name = URLEncoder.encode(goods_name, "gb2312");
			goods_note = URLEncoder.encode(goods_note, "gb2312");
			remark = URLEncoder.encode(remark, "gb2312");
		}catch(Exception e){
			e.printStackTrace();
		}
		String param = 
				"version="+version+
				"&is_phone=1"+
				"&pay_type="+pay_type+
				"&pay_code=0"+
				"&agent_id="+agent_id+
				"&agent_bill_id="+agent_bill_id+
				"&pay_amt="+pay_amt+
				"&notify_url="+notify_url+
				"&return_url="+return_url+
				"&user_ip="+user_ip+
				"&agent_bill_time="+agent_bill_time+
				"&goods_name="+goods_name+
				"&goods_num="+goods_num+
				"&remark="+remark+
//				"&is_test=1"+
				"&goods_note="+goods_note+
				"&sign="+sign;
        log.info("param:"+param);
        
        return "https://pay.heepay.com/Payment/Index.aspx?"+param;
	}
}
