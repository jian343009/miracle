package http;

import java.lang.reflect.Field;
import java.util.List;

import org.w3c.dom.Element;

import cmd.CMD202;
import cmd.CMD203;

import main.Global;
import main.ServerTimer;

import dao.Dao;
import data.*;

public class Html_nextTuan extends Html {

	@Override
	public String getHtml(String content) {
		String html = "";
		
		String token = "";
		String number = "";
		String code = "";
		String[] conts = content.split("&");
		for(int m=0;m<conts.length;m++){
			if(conts[m].startsWith("token=")){
				token = conts[m].replace("token=", "");
			}else if(conts[m].startsWith("number=")){
				number = conts[m].replace("number=", "");
			}else if(conts[m].startsWith("code=")){
				code = conts[m].replace("code=", "");
			}
		}
		Tuan tuan = Tuan.getByToken(token);
		if(token.isEmpty() || tuan == null){
			html = "参数错误，请确认链接地址是否完整。";
		}if(number.isEmpty()){
			html = "手机号不能为空";
		}else if(!number.matches("^1[\\d]{10}")){
			html = "手机号应为1开头的11位数字";
		}else if(code.isEmpty()){
			html = "验证码不能为空";
		}else if(!code.matches("^[\\d]{4}")){
			html = "验证码位数错误，请确认手机短信中收到的验证码";
		}else{
			Mobile mobile = Mobile.getByNumber(number);
			if(mobile.getReceiveCode().split("#").length >3){
				html = "验证码输入错误次数过多，请于30分钟后重试。";
			}else if(!code.equals(mobile.getCode())){
				html = "验证码输入错误，请确认后重试。";
				mobile.setReceiveCode(mobile.getReceiveCode() + "#"+code);
				Dao.save(mobile);
			}else{
				mobile.setReceiveCode("");
				tuan.setOpen(tuan.getOpen()+1);//打开人数加1。
				Dao.save(mobile);//验证成功清除错误记录
				Dao.save(tuan);
		        String url = CMD203.getPayUrl(144, "xiangqituan"+ServerTimer.getTotalWithS(), tuan.getId()+"-"+mobile.getId()+"-"+131068+"-团购", "少儿围棋团购", "参与团购");
		        html = "url="+url;
			}
		}
		return html;
	}

}
