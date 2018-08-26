package http;

import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.List;
import java.util.logging.*;

import main.Global;
import dao.*;
import data.*;

public class Html_basedata extends Html {
	private static final Logger log = Logger.getLogger(Html_basedata.class.getName());

	@Override
	public String getHtml(String content) {
		log.info(content);
		
		String html = "";
		
		if(content.isEmpty()){
			String[] params = "id,name,content".split(","); 
			String[] intras = "id,名称,内容".split(",");
			String body = 
			"<script type=\"text/javascript\">"+
				"function updaterule(column, id){" +
					"var value = $('#'+column+id).val();" +
					"value = encodeURIComponent(value);" +
					"$.post('/basedata','id='+id+'&'+column+'='+value,function(data,status){alert(data)});"+
				"}" +
				"function add(){" +
					"var name = $('#addname').val();" +
					"$.post('/basedata', 'id=0&name='+name, function(data){alert(data);});" +
				"}"+
			"</script>"+
			"<div>" +
				"<input type='text' id='addname'/><button onclick='add();'>新增</button>" +
				"<table data-role=\"table\" id=\"table-column-toggle\" data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border=\"1\">"+
				     "<thead>"+
				     	"<tr>" ;
				         for(int m=0;m<intras.length;m++){
				        	 body +=
				        	"<th data-priority=\""+m+"\">"+intras[m]+"</th>";
				         }
				         body +=
				       "</tr>"+
				     "</thead>"+
				     "<tbody>";
			List<BaseData> list = Dao.getAllBaseData();
			for(int m=0;m<list.size();m++){
				BaseData bd = list.get(m);
				body +=
						"<tr>" +
						"<td>"+bd.getId()+"</td>" +
						"<td>"+bd.getName()+"</td>" ;
							for(int n=2;n<params.length;n++){
								try {
									Field fd = BaseData.class.getDeclaredField(params[n]);
									fd.setAccessible(true);
									String idStr = params[n]+bd.getId();
									body += "<td><textarea id=\""+idStr+"\" onchange=\"$('#btn"+idStr+"').show();\" >"+fd.get(bd)+"</textarea><a href=\"#\"  id=\"btn"+idStr+"\" onclick=\"updaterule('"+params[n]+"', '"+bd.getId()+"');$(this).hide();\" data-role=\"button\" data-icon=\"check\" data-iconpos=\"notext\" data-theme=\"c\" data-inline=\"true\" style=\"display:none;\"></a></td>" ;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						body +=
						"</tr>";
			}
			body += 
					"</tbody>" +
				"</table>" +
			"</div>";
			html = Http.getHtml(body);
		}else{
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

}
