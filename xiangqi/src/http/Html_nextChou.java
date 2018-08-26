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

public class Html_nextChou extends Html {

	@Override
	public String getHtml(String content) {
		String html = "";
		
		String token = "";
		String number = "";
		int payMoney = 0;
		String[] conts = content.split("&");
		for(int m=0;m<conts.length;m++){
			if(conts[m].startsWith("token=")){
				token = conts[m].replace("token=", "");
			}else if(conts[m].startsWith("number=")){
				number = conts[m].replace("number=", "");
			}else if(conts[m].startsWith("payMoney=")){
				payMoney = Global.getInt(conts[m].replace("payMoney=", ""));
			}
		}
		Chou chou = Chou.getByToken(token);
		if(token.isEmpty() || chou == null){
			html = "参数错误，请确认链接地址是否完整。";
		}else if(payMoney <1){
			html = "支付金额需大于0";
		}else{
			if(!number.isEmpty()){
				chou.setPayMobile(chou.getPayMobile() + number+ "#");
			}
			if(payMoney > chou.getMoney() - chou.getPayMoney()){
				payMoney = (int) (chou.getMoney() - chou.getPayMoney());
			}
	        String url = CMD203.getPayUrl(payMoney, "xiangqichou"+ServerTimer.getTotalWithS(), chou.getId()+"-"+0+"-"+0+"-众筹", "少儿围棋众筹", "参与众筹");
	        html = "url="+url;
		}
		return html;
	}

}
