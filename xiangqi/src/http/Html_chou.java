package http;

import java.lang.reflect.Field;
import java.util.List;

import main.Global;

import dao.Dao;
import data.*;

public class Html_chou extends Html {

	@Override
	public String getHtml(String content) {
		String html = "";
		
		String token = "";
		String[] conts = content.split("&");
		for(int m=0;m<conts.length;m++){
			if(conts[m].startsWith("token=")){
				token = conts[m].replace("token=", "");
			}
		}
		Chou chou = Chou.getByToken(token);
		if(token.isEmpty() || chou == null){
			html = "参数错误，请确认链接地址是否完整。";
		}else{
			html = chou.getTitle()+"#"+chou.getInfo()+"#"+chou.getMoney()+"#"+chou.getPayMoney();
		}
		return html;
	}

}
