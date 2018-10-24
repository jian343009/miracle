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
//		class ins{//内部类，用于字符串复用
//					String sub(String 链接ID,String 提交内容,String sid){
//						String str ="<a href=\"#\" id=\""+链接ID+"\" onclick=\"udp("+提交内容+","+sid+");\""
//							+ " data-role=\"button\" data-icon=\"check\" data-iconpos=\"notext\""
//							+ " data-theme=\"c\" data-inline=\"true\" style=\"display:none;\">提交</a>\n";
//						return str;
//					}
//				}
		log.info(content);
		if(content.isEmpty()){
			for(String cha:new String[]{"华为平台","苹果平台","乐视电视","其它平台"}){
				String 内容 = "";
				Data data = Data.fromMap(BaseData.getContent(cha));
				//{价格:{1:12,2:13,,,,折扣:95},内容:{1:"",2"",,,,}}
				int 折扣=data.get("价格").get("折扣").asInt();
				for(int i=2;i<=16;i++){//单课价格
					int price = data.get("价格").get(i).asInt();
					String msg = data.get("内容").get(i).asString();
					String sid = cha+i;
					内容 +="<tr>\n"
							+ "<td>"+i+"课</td>\n"
							+ "<td><a href='#' data-role='button' id=\""+sid+"p\""
									+ " onclick=\"udp('"+cha+"','"+i+"');\">"+price+"</a></td>\n"//价格输入框
							+ "<td><textarea id=\""+sid+"c\" onchange=\"udc('"+cha+"',"+i+");\""
									+ " valb=\""+msg+"\">"+msg+"</textarea>\n</td>\n"//内容输入框
						+ "</tr>\n";
				}
				渠道们 +="<div class=\"channels\"><table id=\""+cha+"\" data-role=\"table\""
						+ " class=\"ui-responsive table-stroke\" border='1'>\n"
					+ "<thead>\n"
						+ "<tr>\n"
						+ "<th>"+cha+"</th>\n"//表头名
						+ "<th colspan=\"2\">"+"</th>"//可用支付方式
						+ "</tr><tr>\n"
							+ "<th width=\"45px\">课程</th>\n"
							+ "<th width=\"75px\">价格</th>\n"
							+ "<th width=\"80%\">支付说明</th>\n"
						+ "</tr>\n"
					+ "</thead>\n"
					+ "<tbody>\n"
					+ "<tr><td>折扣</th>\n"
					+ "<td><a href='#' data-role='button' id=\""+cha+"0p\""
						+ " onclick=\"udp('"+cha+"',0);\">"+折扣+"</a></td>\n"//折扣输入框
					+ "<td></td>"
					+ "</tr>"
					+ 内容
					+ "</tbody>"
				+ "</table></div>";
			}
			html += "<script>\n"
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
				+ "function cut(iss){\n"//切换平台
					+ "$('#head').text(iss);"
					+ "$(\"table\").hide();"
					+ "$('#'+iss).show();"
					+ "}\n"

				+ "function udp(cha,lesson){\n"//udp:upDatePrice
					+ "var input=prompt('请输入'+cha+'第'+lesson+'课的价格');"
					+ "if(input==null && input==\"\"){return;}\n"
					+ "var ele=$('#'+cha+lesson+'p');"
					+ "ele.text(input);ele.height('40px');\n"
					+ "$.post('/channels',cha+'&价格&'+lesson+'&'+input,"
					+ "function(msg,status){\n"
						+ "if(status==\"success\"){\n"
							+ "alert(msg);}else{alert(\"提交失败\");}\n"
						+ "	});\n"
				+ "}\n"			
				+ "</script>\n"
						
				+ "<div id=\"container\">\n"//整个框架
					+ "<div><h1 style=\"margin-botton:0;\"><span id=\"head\"></span>支付信息管理</h1></div>\n"
					+ "<div data-role=\"controlgroup\" data-type=\"horizontal\" data-mini='true' >\n"
						+"\t<a href=\"#\" data-role=\"button\" onclick=\"cut('华为平台');\">华为平台</a>\n"
						+"\t<a href=\"#\" data-role=\"button\" onclick=\"cut('苹果平台');\">苹果平台</a>\n"
						+"\t<a href=\"#\" data-role=\"button\" onclick=\"cut('乐视电视');\">乐视电视</a>\n"
						+"\t<a href=\"#\" data-role=\"button\" onclick=\"cut('其它平台');\">其它平台</a>\n"
					+ "</div>\n"//菜单
					+ "<div id\"channels\">"+渠道们+"</div>"
					+ "<div id=\"footer\" style=\"clear:both;text-align:center;\">\n"
					+ "成都迈瑞科后台管理系统@miralce-cn.com\n"
					+ "</div>\n"//底部
				+ "</div>\n";//整个框架
				
			html = Http.getManageHtml(html);
		}else if(content.contains("&") && content.split("&").length == 4){
			// 华为平台&价格&3&12
			String cha = content.split("&")[0];
			String type = content.split("&")[1];
			int lesson = Global.getInt(content.split("&")[2]);
			String value = content.split("&")[3];
			BaseData bd = BaseData.getByName(cha);
			if(bd==null){
				return "未知渠道";
			}
			Data data = Data.fromMap(bd.getContent());
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
