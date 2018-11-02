package http;

import java.util.logging.Logger;

import dao.Dao;
import dao.Data;
import data.BaseData;
import main.Global;

public class Html_channelAndPrice extends Html{
	private static final Logger log = Logger.getLogger(Html_channelAndPrice.class.getName());
	@Override
	public String getHtml(String content) {
		String html = "";
		String 渠道们 = "";
		log.info(content);
		if(content.isEmpty()){
			BaseData.clearMap();//清空缓存，不然改BaseData时更新不了数据
			for(String cha:new String[]{"华为平台","苹果平台","乐视电视","其它平台"}){
				String 内容 = "";
				Data data = BaseData.getPriceData(cha);//data做了缓存
				//{价格:{1:12,2:13,,,,折扣:95},内容:{1:"",2"",,,,}}
				int 折扣=data.get("价格").get("折扣").asInt();
				for(int i=2;i<=16;i++){//单课价格
					int price = data.get("价格").get(i).asInt();
					String msg = data.get("内容").get(i).asString();
					String sid = cha+i;
					内容 +="<tr>\n"
							+ "<td>"+i+"课</td>\n"
							+ "<td><a href='#' data-role='button' onclick=\"udp('"+cha+"','"+i+"');\"><span id=\""
							+sid+"p\">"+price+"</span></a></td>\n"//价格输入框
							+ "<td><textarea id=\""+sid+"c\" onchange=\"udc('"+cha+"',"+i+");\""
									+ " valb=\""+msg+"\">"+msg+"</textarea>\n</td>\n"//内容输入框
						+ "</tr>\n";
				}
				
				String 支付方式选择 = Data.fromMap(BaseData.getContent("可用支付方式")).get(cha).asString();
				String 支付方式 = "<div data-role=\"controlgroup\" data-type=\"horizontal\" data-mini='true' >\n";
				for(String str:支付方式选择.split("#")) {
					支付方式 +="\t<a href=\"#\" data-role=\"button\" >"+str+"</a>\n";
				}
				支付方式 +="</div>\n";
				
				渠道们 +="<div id=\""+cha+"\" class=\"channels\">\n<table data-role=\"table\""
						+ " data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1'>\n"
					+ "<thead>"
						+ "<tr>"
							+ "<th width=\"45px\">课程</th>\n"
							+ "<th width=\"45px\">价格</th>\n"
							+ "<th width=\"80%\">支付说明</th>\n"
						+ "</tr>"
					+ "</thead>\n"
					+ "<tbody>\n"
					+ "<tr><td>折扣</th>\n"
					+ "<td><a href='#' data-role='button' onclick=\"udp('"+cha+"',0);\">"
							+ "<span id=\""+cha+"0p\">"+折扣+"</span></a></td>\n"//折扣输入框
					+ "<td>"+支付方式+"</td>"
					+ "</tr>"
					+ 内容
					+ "</tbody>"
				+ "</table></div>";
			}
			html += "	<style>\r\n" + 
					"		.channels {\r\n" + 
					"			display: none;\r\n" + 
					"		}\r\n" + 
					"	</style>"
					+ "<script>\n"
				+ "function udc(cha,lesson){"//update content
					+ "var r=confirm(\"是否更新支付信息？\");"
					+ "var ele=$('#'+cha+lesson+'c');"
					+ "if(r==true){"
						+ "$.post('/channels',cha+'&内容&'+lesson+'&'+ele.val(),"
						+ "function(msg,status){\n"
						+ "if(status=='success'){ele.attr('valb',ele.val());alert(msg);}"
						+ "else{alert(\"提交失败\")} });\n"
					+ "}else{ele.val(ele.attr('valb'));}"
				+ "}\n"
				+ ""
				+ ""
				+ "		function cut(id, iss) {\r\n" + 
				"			var itmA=$(\"[but]\");\r\n" + 
				"			var itmB=$('#' + id);\r\n" + 
				"			$('#head').text(iss); \r\n" + 
				"			itmA.css(\"width\", \"\"); \r\n" + 
				"			itmA.css(\"color\", \"\"); \r\n" + 
				"			itmA.css(\"border\", \"\");\r\n" + 
				"			\r\n" + 
				"			itmB.css(\"width\", \"110px\");\r\n" + 
				"			itmB.css(\"color\", \"red\"); \r\n" + 
				"			itmB.css(\"border\", \"2px solid #4CAF50\"); \r\n" + 
				"			$('.channels').css(\"display\", \"none\");\r\n" + 
				"			$('#' + iss).css(\"display\", \"block\");\r\n" + 
				"		}\n"
				+ "function udp(cha,lesson){\n"//udp:upDatePrice
					+ "var input=prompt('请输入'+cha+'第'+lesson+'课的价格');"
					+ "if(/^\\d+$/.test(input)==false){alert(\"只能输入数字\");return;}\n"
					+ "var ele=$('#'+cha+lesson+'p');"
					+ "ele.text(input);\n"
					+ "$.post('/channels',cha+'&价格&'+lesson+'&'+input,"
					+ "function(msg,status){\n"
						+ "if(status==\"success\"){\n"
							+ "alert(msg);}else{alert(\"提交失败\");}\n"
						+ "	});\n"
				+ "}\n"			
				+ "</script>\n"
						
				+ "<div id=\"container\">\n"//整个框架
					+ "<div><h1><span id=\"head\"></span>支付信息管理</h1></div>\n"
					+ "<div data-role=\"controlgroup\" data-type=\"horizontal\" data-mini='true' >\n"
						+"\t<a id=\"hwpt\" but=\"\" href=\"#\" data-role=\"button\" onclick=\"cut(id,'华为平台');\">华为平台</a>\n"
						+"\t<a id=\"pgpt\" but=\"\" href=\"#\" data-role=\"button\" onclick=\"cut(id,'苹果平台');\">苹果平台</a>\n"
						+"\t<a id=\"lspt\" but=\"\" href=\"#\" data-role=\"button\" onclick=\"cut(id,'乐视电视');\">乐视电视</a>\n"
						+"\t<a id=\"qtpt\" but=\"\" href=\"#\" data-role=\"button\" onclick=\"cut(id,'其它平台');\">其它平台</a>\n"
					+ "</div>\n"//菜单
					+ "<div id=\"channels\">"+渠道们+"</div>"
					+ "<div id=\"footer\" style=\"clear:both;text-align:center;\">\n"
					+ "成都迈瑞科后台管理系统@miralce-cn.com\n"
					+ "</div>\n"//底部
				+ "</div>\n";//整个框架
				
			html = Http.getManageHtml(html);
		}else if(content.contains("&") && content.split("&").length >= 4){
			// 华为平台&价格&3&12
			String cha = content.split("&")[0];
			String type = content.split("&")[1];
			int lesson = Global.getInt(content.split("&")[2]);
			String value = content.split("&")[3];
			BaseData bd = BaseData.getByName(cha);
			if(bd==null){
				return "未知渠道";
			}
			Data data = BaseData.getPriceData(cha);//调用缓存了data
			//{价格:{1:12,2:13,,,,折扣:95},内容:{1:"",2"",,,,}}
			if("价格".equals(type)){
				if(lesson==0){
					data.getMap("价格").put("折扣", Global.getInt(value));
				}else{
					data.getMap("价格").put(lesson, Global.getInt(value));
				}
			}else if("内容".equals(type)){
				data.getMap("内容").put(lesson, value);
			}
			bd.setContent(data.asString());
			Dao.save(bd);
			log.info(content);
			return "更新成功";
		}else{
			html="你发的什么鬼";
		}
		return html;
	}
}
