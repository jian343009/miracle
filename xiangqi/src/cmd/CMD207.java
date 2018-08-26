package cmd;

import java.util.logging.Logger;

import main.Global;
import main.ServerTimer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.w3c.dom.Element;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;

import dao.Dao;
import dao.Sms;
import data.*;

public class CMD207 implements ICMD {
	private final Logger log = Logger.getLogger(CMD207.class.getName());
	
	@Override
	public ChannelBuffer getBytes(int cmd, ChannelBuffer data) {
//		device:int
//		imei:String
//		channel:String
//		phoneNumber:String(手机号)   
//		total:int
//		goods_price:int(团购金额)
//		goods_name:String(团购名称)
//		goods_note:String(团购说明)

		int deviceID = data.readInt();
		String channel = Global.readUTF(data).trim().split("#")[0];
		String number = Global.readUTF(data).trim();
		int money = data.readInt();
		String goods_name = Global.readUTF(data).trim();
		String goods_note = Global.readUTF(data).trim();
		
		
		int result = 2;
		String msg = "";
		Device device = Dao.getDeviceExist(deviceID, "");
		if(device == null){
			msg = "未正常获取设备号";
		}else{
			Tuan tuan = Tuan.getByImei(device.getImei());
			if(tuan.getState() ==0){//未开始
				tuan.setImei(device.getImei());
				tuan.setNumber(number);
				tuan.setToken(Global.md5(device.getImei()+number+131068+ServerTimer.getTotalWithS()));
				tuan.setTitle(goods_name);
				tuan.setInfo(goods_note);
				Dao.save(tuan);
				
				device.setState(1);
				Dao.save(device);
				
				int user_identity = device.getId();
		        String agent_bill_id = "xiangqituan"+ServerTimer.getTotalWithS();
		        Element root = CMD202.getToken(money, user_identity, agent_bill_id, tuan.getId()+"-"+0+"-"+131068+"-团购", "少儿象棋团购", "团购解锁");

				String name = "";
		        String value = "";
		        if(root != null){
			        name = root.getNodeName();
			        value = root.getTextContent();
		        }
		        log.info("name:"+name);
		        log.info("value:"+value);
		        
				ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
				buf.writeShort(207);
				if(name.equals("token_id")){
		        	buf.writeByte(1);
		        	buf.writeInt(device.getId());
		        	buf.writeBytes(Global.getUTF(value));
		        	buf.writeBytes(Global.getUTF(agent_bill_id));
		        	buf.writeInt(CMD202.agent_id);
		        }else if(name.equals("error")){
		        	buf.writeByte(2);
		        	buf.writeBytes(Global.getUTF(value));
		        }
				return buf;
			}else if(tuan.getState() ==1){//进行中
				ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
				buf.writeShort(207);
				buf.writeByte(3);
				buf.writeBytes(Global.getUTF("http://main.miracle-cn.com/xiangqi/tuan.html?token="+tuan.getToken()));
				return buf;
			}else{//已结束
				
			}
		}
		
		
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(cmd);
		buf.writeByte(result);
		buf.writeBytes(Global.getUTF(msg));
		return buf;
	}

}
