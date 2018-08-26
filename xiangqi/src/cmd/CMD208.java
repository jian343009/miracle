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

public class CMD208 implements ICMD {
	private final Logger log = Logger.getLogger(CMD208.class.getName());
	
	@Override
	public ChannelBuffer getBytes(int cmd, ChannelBuffer data) {
//		device:int
//		imei:String
//		channel:String
//		phoneNumber:String(手机号)   
//		total:int
//		money:int
//		goods_name:String
//		goods_note:String

		int deviceID = data.readInt();log.info("deviceID:"+deviceID);
//		String imei = Global.readUTF(data).trim();
		String channel = Global.readUTF(data).trim().split("#")[0];
		String number = Global.readUTF(data).trim();
		int total = data.readInt();
		int money = data.readInt();
		String goods_name = Global.readUTF(data).trim();
		String goods_note = Global.readUTF(data).trim();
		
		Device device = Dao.getDeviceExist(deviceID, "");
		int result = 2;
		String msg = "";
		log.info("device:"+device);
		if(device == null){
			msg = "未正常获取设备号";
		}else{
			Chou chou = Chou.getByImei(device.getImei());
			if(chou.getState() ==0){//未开始
				chou.setState(1);
				chou.setImei(device.getImei());
				chou.setNumber(number);
				chou.setToken(Global.md5(device.getImei()+number+total+ServerTimer.getTotalWithS()));
				chou.setTitle(goods_name);
				chou.setInfo(goods_note);
				chou.setMoney(money);
				Dao.save(chou);
				
				device.setState(2);
				Dao.save(device);
				
				ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
				buf.writeShort(Cmap._208众筹);
				buf.writeByte(1);
				buf.writeBytes(Global.getUTF("http://main.miracle-cn.com/xiangqi/chou.html?token="+chou.getToken()));
				buf.writeBytes(Global.getUTF(chou.getInfo()));
				buf.writeInt((int) chou.getMoney());
				buf.writeInt((int) chou.getPayMoney());
				return buf;
			}else if(chou.getState() ==1){//进行中
				ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
				buf.writeShort(Cmap._208众筹);
				buf.writeByte(1);
				buf.writeBytes(Global.getUTF("http://main.miracle-cn.com/xiangqi/chou.html?token="+chou.getToken()));
				buf.writeBytes(Global.getUTF(chou.getInfo()));
				buf.writeInt((int) chou.getMoney());
				buf.writeInt((int)chou.getPayMoney());
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
