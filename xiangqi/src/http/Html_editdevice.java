package http;

import java.lang.reflect.Field;
import java.util.List;

import main.Global;

import dao.Dao;
import data.*;

public class Html_editdevice extends Html {

	@Override
	public String getHtml(String content) {
		String html = "";
		
		if(content.isEmpty()){
			String body = 
			"<script type=\"text/javascript\">"+
				"function update(column, id){" +
					"$.post('/updatedevice','id='+id+'&'+column+'='+$('#'+column+id).val(),"
					+ "function(data,status){alert(data)});"+
				"}"+
			"</script>"+
			"<div style=\"width:600px;\">"
			+ "<table data-role=\"table\" data-mode=\"column\" class=\"ui-responsive table-stroke\" border='1' >"
				+ "<tr>"//第一行
				+"<td>"	+ "<label>设备ID</label>"	+"</td>"
				+ "<td>"
					+ "<input type='text' data-clear-btn=\"true\" name=\"id\" id=\"id\" value=\"\">"
				+ "</td>"
				+"<td>"	+ "<label>每页条数</label>" + "</td>"
				+ "<td><div style=\"width:51px;\">"
					+"<input type=\"text\" name=\"base\" id=\"base\" value=\"10\">"
				+ "</div></td>"
				+ "</tr>"//第一行
				+ "<tr>"//第二行
				+"<td>"	+ "<label>设备imei</label>" + "</td>"
				+ "<td>"
					+"<input type=\"text\" data-clear-btn=\"true\" name=\"imei\" id=\"imei\" value=\"\">"
				+ "</td>"
				+ "<td>" + "<label>第几页</label>"	+ "</td>"
				+ "<td><div style=\"width:51px;\">"
					+"<input type=\"number\" name=\"page\" id=\"page\" value=\"1\">"
				+ "</div></td>"
				+ "</tr>"//第二行
				+ "<tr>"//第三行
				+ "<td></td>"
				+ "<td>"
					+"<button onclick=\"$.post('/manage_device',"
					+ "{id:$('#id').val(),imei:$('#imei').val(),base:$('#base').val(),page:$('#page').val()},"
					+ "function(data,status){$('#users').html(data).trigger('create');});\">查询"
					+ "</button>"
				+ "</td>"
				+ "<td></td><td></td>"
				+ "</tr>"//第三行
			+ "</table>"
			+"</div>" +
			"<div id='users' style=\"width:600px;\"><button disabled>设备数据管后台</button></div>";
			html = Http.getManageHtml(body);
		}else{
			int id = 0;
			String imei = "";
			int base = 10;
			int page = 1;
			String[] conts = content.split("&");
			for(int m=0;m<conts.length;m++){
				if(conts[m].startsWith("id=")){
					id = Global.getInt(conts[m].replace("id=", ""));
				}else if(conts[m].startsWith("imei=")){
					imei = conts[m].replace("imei=", "");
				}else if(conts[m].startsWith("base=")){
					base = Global.getInt(conts[m].replace("base=", ""));
				}else if(conts[m].startsWith("page=")){
					page = Global.getInt(conts[m].replace("page=", ""));
				}
			}
			int start = base * (page -1);
			if(start <0){
				start = 0;
			}
			if(base <1){
				base = 1;
			}
			List<Device> list = Dao.getDevice(id, imei, start, base);
			String param = "openState=打开课程,buyState=购买课程,extra=解锁课程,channel=打开渠道,"
					+ "firstTime=首次打开,lastTime=最近打开,reward=红包";
			String[] params = param.split(","); 
			String body = "查询结果:"+list.size();
			for(int m=0;m<list.size();m++){
				Device device = list.get(m);
				body +=
					"<div data-role=\"collapsible\" data-inset=\"false\">"+
						"<h3>"+device+"</h3>"+
						"<table data-role=\"table\" id=\"table-column-toggle\" class=\"ui-responsive table-stroke\" border='1'>"+
					     "<thead>"+
					       "<tr>"+
					         "<th>字段</th>"+
					         "<th>值</th>" +
					       "</tr>" +
					     "</thead>" +
					     "<tbody>"+
					     	"<tr><td>id</td><td>"+device.getId()+"</td></tr>" +
							"<tr><td>imei</td><td>"+device.getImei()+"</td></tr>" ;
							for(int n=0;n<params.length;n++){
								String arg = params[n];
								String argName = params[n].split("=")[0];
								try {
									Field fd = Device.class.getDeclaredField(argName);
									fd.setAccessible(true);
									String idStr = argName+device.getId();
									String value=String.valueOf(fd.get(device));
									if("reward".equals(argName)) {
										value=fd.get(device).toString().replaceAll("\"", "&quot;");
									}
									body += "<tr><td>"+arg+"</td><td><input type=\"text\" data-clear-btn=\"false\" id=\""
									+idStr+"\" value=\""+value+"\" onchange=\"$('#btn"+idStr
									+"').show();\" /><a href=\"#\"  id=\"btn"+idStr+"\" onclick=\"update('"
									+argName+"', '"+device.getId()+"');$(this).hide();\" data-role=\"button\" data-icon=\"check\""
									+ " data-iconpos=\"notext\" data-theme=\"c\" data-inline=\"true\" "
									+ "style=\"display:none;\"></a></td></tr>" ;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
				body +=
						"</tbody></table></div>";
			}
			html = body;
		}
		return html;
	}

}
