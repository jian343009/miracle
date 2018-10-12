package http;

import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Session;

import dao.Dao;
import dao.HSF;
import data.BaseData;
import data.Channels;
import main.Global;
import main.ServerTimer;

public class Html_basedata extends Html {
	private static final Logger log = Logger.getLogger(Html_basedata.class.getName());
	@Override
	public String getHtml(String content) {
		log.info(content);
		
		String html = "";
		
		if(content.isEmpty()){
			String[] params = "id,name,content".split(","); 
			String[] intras = "id,名称,内容".split(",");
			String body = this.价格与渠道("")+
			"<script type=\"text/javascript\">\n"+
				"function updaterule(column, id){\n" +
					"var value = $('#'+column+id).val();\n" +
					"value = encodeURIComponent(value);\n" +
					"$.post('/basedata','id='+id+'&'+column+'='+value,function(data,status){alert(data)});"+
				"}\n" +
				"function add(){\n" +
					"var name = $('#addname').val();\n" +
					"$.post('/basedata', 'id=0&name='+name, function(data){alert(data);});\n" +
				"}"+
			"</script>\n"+
			"<div>\n" +
				"<input type='text' id='addname'/><button onclick='add();'>新增</button>\n" +
				"<table data-role=\"table\" id=\"table-column-toggle\" data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border=\"1\">\n"+
				     "<thead>\n"+
				     	"<tr>\n" ;
				         for(int m=0;m<intras.length;m++){
				        	 body +=
				        	"<th data-priority=\""+m+"\">"+intras[m]+"</th>\n";
				         }
				         body +=
				       "</tr>\n"+
				     "</thead>\n"+
				     "<tbody>\n";
			List<BaseData> list = Dao.getAllBaseData();
			for(int m=0;m<list.size();m++){
				BaseData bd = list.get(m);
				body +=
						"<tr>\n" +
						"<td>"+bd.getId()+"</td>\n" +
						"<td>"+bd.getName()+"</td>\n" ;
							for(int n=2;n<params.length;n++){
								try {
									Field fd = BaseData.class.getDeclaredField(params[n]);
									fd.setAccessible(true);
									String idStr = params[n]+bd.getId();
									body += "<td><textarea id=\""+idStr+"\" onchange=\"$('#btn"+idStr+"').show();\" >"+fd.get(bd)+
										"</textarea><a href=\"#\"  id=\"btn"+idStr+"\" onclick=\"updaterule('"+
											params[n]+"', '"+bd.getId()+"');$(this).hide();\""+ 
										" data-role=\"button\" data-icon=\"check\" data-iconpos=\"notext\""+
										" data-theme=\"c\" data-inline=\"true\" style=\"display:none;\"></a></td>\n" ;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						body +=
						"</tr>\n";
						
						
						m += 18;//测试用，减少数据
			}
			body += 
					"</tbody>\n" +
				"</table>\n" +
			"</div>\n";
			html = Http.getHtml(body);
		}else if(content.contains("&") && content.startsWith("价格与渠道")){//改渠道价格
			if(content.split("&").length != 4){
				return "数据格式不对";
			}
			return updatePrice(content);
		}else{//改baseData
			String[] conts = content.split("&");
			int id = Global.getInt(conts[0].split("=")[1]);
			
			BaseData br = Dao.getBaseDataById(id);
			if(id ==0 && conts.length >1 && conts[1].startsWith("name=")){
				String name = conts[1].replace("name=", "");
				BaseData bd = BaseData.getByName(name);
				if(bd == null){
					bd = new BaseData();
					bd.setName(name);
					Dao.save(bd);
					html = name+" 新增成功，请刷新页面查看。";
				}else{
					html = name+" 已经存在。";
				}
			}else if(br != null){
				for(int m=1;m<conts.length;m++){
					String[] kv = conts[m].split("=");
					String key = kv[0];
					String value = "";
					if(kv.length ==2){
						value = kv[1];
					}
					try {
						Field fd = BaseData.class.getDeclaredField(key);
						fd.setAccessible(true);
						if(fd.getType() == int.class){
							fd.set(br, Global.getInt(value));
						}else{
							fd.set(br, URLDecoder.decode(value, "utf-8"));
						}
						html += key+"修改"+fd.get(br)+";";
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				Dao.save(br);
			}else{
				html = "记录不存在";
			}
		}
		return html;
	}
	
	//以下都是改渠道价格相关的方法
	private String 价格与渠道(String body){
		Session ss = HSF.getSession();
		List<Channels> list = ss.createCriteria(Channels.class).list();
		ss.close();
		StringBuffer 价格与渠道 = new StringBuffer();
		for(Channels chann:list){
			String ID = chann.getChannel()+"单课";
			String 单课 ="<a href=\"#"+ID+"\" data-rel=\"popup\" class=\"ui-btn ui-btn-inline ui-corner-all\">单课价格</a>"+
					"<div id=\""+ID+"\"data-role=\"popup\" style=\"width:60%;\">"+
					"<table data-role=\"table\" id=\""+ID+"table\" data-mode=\"columntoggle\"\n"+
					" data-column-btn-text=\""+chann.getChannel()+"单课加价\" class=\"ui-responsive table-stroke\" border=\"1\">\n"+
					//"<caption>"+chann.getChannel()+"</caption>"+
					"<tr>"+
					"<td>2课"+渠道调价(chann,"2",chann.getPrice2())+"</td>"+
					"<td>3课"+渠道调价(chann,"3",chann.getPrice3())+"</td>"+
					"<td>4课"+渠道调价(chann,"4",chann.getPrice4())+"</td>"+
					"<td>5课"+渠道调价(chann,"5",chann.getPrice5())+"</td>"+
					"<td>6课"+渠道调价(chann,"6",chann.getPrice6())+"</td>"+
					"</tr><tr>"+
					"<td>7课"+渠道调价(chann,"7",chann.getPrice7())+"</td>"+
					"<td>8课"+渠道调价(chann,"8",chann.getPrice8())+"</td>"+
					"<td>9课"+渠道调价(chann,"9",chann.getPrice9())+"</td>"+
					"<td>10课"+渠道调价(chann,"10",chann.getPrice10())+"</td>"+
					"<td>11课"+渠道调价(chann,"11",chann.getPrice11())+"</td>"+
					"</tr>"+
					"</tr><tr>"+
					"<td>12课"+渠道调价(chann,"12",chann.getPrice12())+"</td>"+
					"<td>13课"+渠道调价(chann,"13",chann.getPrice13())+"</td>"+
					"<td>14课"+渠道调价(chann,"14",chann.getPrice14())+"</td>"+
					"<td>15课"+渠道调价(chann,"15",chann.getPrice15())+"</td>"+
					"<td>16课"+渠道调价(chann,"16",chann.getPrice16())+"</td>"+
					"</tr>"+
					"</table>"+
					"</div>";
			价格与渠道.append(	"<tr>\n"+
					"<td>"+chann.getId()+ "</td>\n"+
					"<td>"+chann.getChannel()+ "</td>\n"+
					"<td>"+渠道调价(chann,"原价",chann.getBaseprice())+"</td>\n"+
					"<td>"+渠道调价(chann,"折扣",chann.getDiscount())+"</td>\n"+
					"<td>"+单课+ "</td>\n"+
					"<td>"+chann.getTime()+"</td>\n"+
					"</tr>"
					);
		}
		
		body += "<script type=\"text/javascript\">\n"+
				"function updatePrice(row,column){\n"+
					"var value=$('#'+row).val();\n"+
					//"value=encodeURIComponent(value);" +
					"$.post('/basedata','价格与渠道&'+value+'&'+row+'&'+column,function(data,status){alert(data)});\n"+
					"}</script>\n"+
				"<div align=\"center\" data-role=\"collapsible\">\n"+
				"<h3 align=\"center\">价格与渠道</h3>\n" +
				"<div>\n"+
				"<table data-role=\"table\" id=\"channelAndPrice\" data-mode=\"columntoggle\"\n"+
				" class=\"ui-responsive table-stroke\" border=\"1\">\n"+
			     "<thead><tr>\n"+
			     	"<th>ID</th>\n"+
			     	"<th>渠道 </th>\n"+
			     	"<th>原价</th>\n"+
			     	"<th>折扣</th>\n"+
			     	"<th>单课</th>\n"+
			     	"<th>修改时间</th>\n"+
			     	"</tr></thead><tbody>\n"+
			     	价格与渠道+
			     "</tbody></table>\n"+
			     "</div></div>\n";
		return body;
	}
	private String 渠道调价(Channels chann,String name,int value){//有多次调用
		String rowID = chann.getChannel()+name;//name=column,传行和列的信息过去。
		String str ="<textarea id=\""+rowID+"\" onchange=\"$('#"+rowID+"submit').show();\""+
				" onkeyup=\"this.value=this.value.replace(/\\D/g,'')\""+ //限定只能输入数字
				" onafterpaste=\"this.value=this.value.replace(/\\D/g,'')\">"+
				value+"</textarea><a href=\"#\" id=\""+rowID+"submit\""+
				" onclick=\"updatePrice('"+rowID+"','"+name+"');$(this).hide();\""+
				" data-role=\"button\" data-icon=\"check\" data-iconpos=\"notext\""+
				" data-theme=\"c\" data-inline=\"true\" style=\"display:none;\"/>\n" ;
		return str;
	}
	//处理更新数据
	private String updatePrice(String content){
		//价格与渠道&12&渠道行&列
		String pri = content.split("&")[1];
		if(!pri.matches("^\\d{1,3}$")){
			return "请输入合理的价格";
		}
		int price =  Global.getInt(pri);
		String 渠道 = content.split("&")[2];
		String column = content.split("&")[3];
		Channels chan = Channels.getChannels(渠道.replaceAll(column, ""));
		if(chan == null){
			return "没找到对应渠道";
		}
		if("原价".equals(column)){
			chan.setBaseprice(price);
		}else if("折扣".equals(column)){
			chan.setDiscount(price);
		}else{
			try {
				Field field = chan.getClass().getDeclaredField("price" + column);
				field.setAccessible(true);
				field.set(chan, price);
			} catch (Exception e) {
				log.warning(e.getClass()+","+e.getMessage());
			}
		}
		chan.setTime(ServerTimer.getFull());
		Dao.save(chan);
		return "更改成功";
	}

}
