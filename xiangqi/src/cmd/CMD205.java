package cmd;

import java.util.logging.Logger;

import main.Global;
import main.ServerTimer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;

import dao.Dao;
import dao.Sms;
import data.*;

public class CMD205 implements ICMD {
	private final Logger log = Logger.getLogger(CMD205.class.getName());
	
	@Override
	public ChannelBuffer getBytes(int cmd, ChannelBuffer data) {
		int deviceID = data.readInt();
		String imei = Global.readUTF(data).trim();
		String channel = Global.readUTF(data).trim().split("#")[0];
		String sex = Global.readUTF(data).trim();
		int age = data.readInt();
		String number = Global.readUTF(data).trim();
		String code = Global.readUTF(data).trim();
		
		int result = 2;
		String msg = "";
		if(number.isEmpty()){
			msg = "手机号不能为空";
		}else if(!number.matches("^1[\\d]{10}")){
			msg = "手机号应为1开头的11位数字";
		}else{
			Mobile mobile = Mobile.getByNumber(number);
			if(mobile.getState() ==2){
				msg = "该手机号已经成功绑定";
			}else if(code.isEmpty()){//发送验证码
				if(mobile.getCode().isEmpty() || ServerTimer.distOfMinute() - mobile.getLastTime() >30){
					code = ""+Global.getRandom(1000, 9999);
					mobile.setCode(code);
					mobile.setLastTime(ServerTimer.distOfMinute());
					mobile.setLastTimeStr(ServerTimer.getFull());
					try {
						SendSmsResponse response = Sms.send验证码(number, code);
						if(response.getCode() != null && response.getCode().equals("OK")){
							msg = "验证码发送成功";
							mobile.setState(1);
							mobile.setBizId(response.getBizId());
						}else{
							msg = "发送失败"+response.getCode();
						}
					} catch (ClientException e) {
						e.printStackTrace();
						msg = "发送出错"+e.getLocalizedMessage();
					}
					mobile.setStatus(msg);
					Dao.save(mobile);
				}else{
					msg = "验证码已发送，请查收验证码绑定手机。如未收到验证码，请于30分钟后重试。";
				}
			}else if(code.length() ==4){//比对验证码
				if(mobile.getReceiveCode().split("#").length >3){
					msg = "验证码输入错误次数过多，请于30分钟后重试。";
				}else if(!code.equals(mobile.getCode())){
					msg = "验证码输入错误，请确认后重试。";
					mobile.setReceiveCode(mobile.getReceiveCode() + "#"+code);
					Dao.save(mobile);
				}else{
					result  =1;
					msg = number;
					mobile.setState(2);
					mobile.setSex(sex);
					mobile.setAge(age);
					mobile.setImei(imei);
					mobile.setChannel(channel);
					mobile.setReceiveCode(mobile.getReceiveCode() + "#"+code);
					Dao.save(mobile);
					
					Device device = Dao.getDeviceExist(deviceID, imei);
					if(device != null){
						device.setMobile(number);
					} 
				}
			}else{
				msg = "验证码位数不对";
			}
		}
		
		
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(cmd);
		buf.writeByte(result);
		buf.writeBytes(Global.getUTF(msg));
		return buf;
	}

}
