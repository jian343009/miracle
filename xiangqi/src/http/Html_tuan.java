package http;

import java.lang.reflect.Field;
import java.util.List;

import main.Global;

import dao.Dao;
import data.*;

public class Html_tuan extends Html {

	@Override
	public String getHtml(String content) {
		String html = "";
		
		String token = "";
		String number = "";
		String url = "";
		String[] conts = content.split("&");
		for(int m=0;m<conts.length;m++){
			if(conts[m].startsWith("token=")){
				token = conts[m].replace("token=", "");
			}else if(conts[m].startsWith("number=")){
				number = conts[m].replace("number=", "");
			}
		}
		Tuan tuan = Tuan.getByToken(token);
		if(token.isEmpty() || tuan == null){
			html = "参数错误，请确认链接地址是否完整。";
		}else{
			html = tuan.getTitle()+"#"+tuan.getInfo();
		}
		return html;
	}

}
