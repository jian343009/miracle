package http;

import java.lang.reflect.Field;
import java.util.List;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;

import main.Global;
import main.ServerTimer;

import dao.Dao;
import dao.Sms;
import data.*;

public class Html_getCode extends Html {

	@Override
	public String getHtml(String number) {
		String html = "";
		
		if(number.isEmpty()){
			html = "手机号不能为空";
		}else if(!number.matches("^1[\\d]{10}")){
			html = "手机号应为1开头的11位数字";
		}else{
			Mobile mobile = Mobile.getByNumber(number);
			if(mobile.getCode().isEmpty() || ServerTimer.distOfMinute() - mobile.getLastTime() >30){
				String code = ""+Global.getRandom(1000, 9999);
				mobile.setCode(code);
				mobile.setReceiveCode("");
				mobile.setLastTime(ServerTimer.distOfMinute());
				mobile.setLastTimeStr(ServerTimer.getFull());
				try {
					SendSmsResponse response = Sms.send验证码(number, code);
					if(response.getCode() != null && response.getCode().equals("OK")){
						html = "验证码发送成功";
						mobile.setBizId(mobile.getBizId() + "#"+response.getBizId());
					}else{
						html = "发送失败"+response.getCode();
					}
				} catch (ClientException e) {
					e.printStackTrace();
					html = "发送出错"+e.getLocalizedMessage();
				}
				mobile.setStatus(html);
				Dao.save(mobile);
			}else{
				html = "验证码已发送，请查收"+number+"手机短信。如未收到验证码，请于30分钟后重试。";
			}
		}
		return html;
	}

}
