package http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import net.sf.json.JSONArray;

import dao.Dao;
import dao.Data;
import data.*;

import main.Global;


public class Html_record implements IHtml {
	private static final Logger log = Logger.getLogger("Html_allstep");

	@Override
	public String getHtml(String content) {
		
		String tr="";		
		List<StepRecord> list = StepRecord.getRecentRecord(content);
		for(StepRecord re : list){
			tr += "<tr>" +
					"<td>"+re.getDeviceID()+"</td>" +
					"<td>"+re.getImei()+"</td>" +
					"<td>"+re.getStep()+"</td>" +
					"<td>"+re.getInfo()+"</td>" +
					"<td>"+re.getTimeStr()+"</td>"+
					"</tr>\n";
		}
		String html = "";
		if(!content.isEmpty()){
			html = tr;
		}else{
			String body =
					"<script>\n" +
						"function update(){\n" +
							"$.post('/record', $('#imei').val(), function(data){$('#detail').html(data);});\n" +
						"}" +
					"</script>\n"+
					  "<div align=\"center\" data-role=\"collapsible\">\n"+
			              "<h3 align=\"center\">行为记录</h3>\n" +
			              "<div>\n" +
			              //	"设备<input type='text' id='imei' onchange='update();' />"+
				              "<table data-role=\"table\" id='t1' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >\n" +
					              "<thead>\n" +
						              "<tr>\n" +
						              	  "<th data-priority=\"1\">设备ID</th>\n" +
							              "<th data-priority=\"1\">imei</th>\n" +
							              "<th data-priority=\"2\">行为</th>\n" +
							              "<th data-priority=\"3\">信息</th>\n" +
							              "<th data-priority=\"4\">时间</th>\n" +
						              "</tr>\n"+
					              "</thead>\n" +
					              "<tbody id='detail'>\n" + 
					              		tr + 
								  "</tbody>\n" +
							  "</table>\n" +
						  "</div>\n" +
					  "</div>\n";
			
			String tr2 = "";
			String str_退出原因 = "";
			List<StepCount> scList = StepCount.getAll();	
			for(StepCount sc : scList){
				tr2 += "<tr>" +
						"<td>"+sc.getDayStr()+"</td>" +
						"<td>"+sc.getChannel()+"</td>" +
						"<td>"+sc.getData().get("打开设备").asInt()+"</td>" +
						"<td>"+sc.getData().get("新增设备").asInt()+"</td>" +
						"<td>\n";	
//				if(sc.getData().get("退出测试").size()>0){
//					Data data_out = sc.getData().get("退出测试");				
//					str_退出原因 += "<tr>" + 
//					"<td>"+sc.getDayStr()+" "+sc.getChannel()+"</td>" +
//					"<td>"+data_out.get("不吸引人").asInt()+"</td>" +
//					"<td>"+data_out.get("想学但价格太高").asInt()+"</td>" +
//					"<td>"+data_out.get("习题太少缺少练习").asInt()+"</td>" +
//					"<td>"+data_out.get("孩子看不懂").asInt()+"</td>" +
//					"<td>"+data_out.get("操作不方便").asInt()+"</td>" +
//					"</tr>\n";		
//				}
										
				for(int m=0;m<24;m++){
					int num = sc.getData().get("打开设备时段").get(m).asInt();
					if(num >0){
						tr2 += m+":"+sc.getData().get("打开设备时段").get(m).asInt()+"<br/>";
					}
				}
				tr2 +="</td>" +
						"<td>"+sc.getData().get("打开支付单课").asInt()+"#"+sc.getData().get("单课支付成功").asInt()+"</td>" +
						"<td>"+sc.getData().get("打开支付多课").asInt()+"#"+sc.getData().get("多课支付成功").asInt()+"</td>";
				for(int m=1;m<=16;m++){
					String _完成第一课的奇偶用户 = "";
					if(m == 1){
						_完成第一课的奇偶用户 = "<br>奇看完:"+sc.getData().get(m).get("1完成学习").asInt()+
								"<br>偶看完:"+sc.getData().get(m).get("0完成学习").asInt();
					}
					tr2 +=
						"<td>"+sc.getData().get(m).get("开始学习").asInt()+"#"+sc.getData().get(m).get("结束学习").asInt()+"#"+sc.getData().get(m).get("完成学习").asInt()+"<br/>"+
						sc.getData().get(m).get("开始练习").asInt()+"#"+sc.getData().get(m).get("结束练习").asInt()+"#"+sc.getData().get(m).get("完成练习").asInt()+
						_完成第一课的奇偶用户+"</td>";
				}
				tr2 += "</tr>\n";
			}
			body +=
					"<div align=\"center\" data-role=\"collapsible\">\n"+
				              "<h3 align=\"center\">行为统计</h3>\n" +
				              "<div>\n" +
					              "<table data-role=\"table\" id='t2' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >\n" +
						              "<thead>\n" +
							              "<tr>\n" +
							              	  "<th>日期</th>\n" +
								              "<th>渠道</th>\n" +
								              "<th data-priority=\"1\">打开设备</th>\n" +
								              "<th data-priority=\"2\">新增设备</th>\n" +
								              "<th data-priority=\"2\">设备打开时段</th>\n" +
								              "<th data-priority=\"2\">单课支付(打开#成功)</th>\n" +
								              "<th data-priority=\"2\">多课支付(打开#成功)</th>\n" +
								              "<th data-priority=\"3\">第1课学习和练习(开始#结束#完成)</th>\n" +								              
								              "<th data-priority=\"3\">第2课学习和练习(开始#结束#完成)</th>\n" +
												"<th data-priority=\"5\">第3课学习和练习(开始#结束#完成)</th>\n" +
												"<th data-priority=\"6\">第4课学习和练习(开始#结束#完成)</th>\n" +
												"<th data-priority=\"6\">第5课学习和练习(开始#结束#完成)</th>\n" +
												"<th data-priority=\"6\">第6课学习和练习(开始#结束#完成)</th>\n" +
												"<th data-priority=\"6\">第7课学习和练习(开始#结束#完成)</th>\n" +
												"<th data-priority=\"6\">第8课学习和练习(开始#结束#完成)</th>\n" +
												"<th data-priority=\"6\">第9课学习和练习(开始#结束#完成)</th>\n" +
												"<th data-priority=\"6\">第10课学习和练习(开始#结束#完成)</th>\n" +
												"<th data-priority=\"6\">第11课学习和练习(开始#结束#完成)</th>\n" +
												"<th data-priority=\"6\">第12课学习和练习(开始#结束#完成)</th>\n" +
												"<th data-priority=\"6\">第13课学习和练习(开始#结束#完成)</th>\n" +
												"<th data-priority=\"6\">第14课学习和练习(开始#结束#完成)</th>\n" +
												"<th data-priority=\"6\">第15课学习和练习(开始#结束#完成)</th>\n" +
												"<th data-priority=\"6\">第16课学习和练习(开始#结束#完成)</th>\n" +
							              "</tr>\n"+
						              "</thead>\n" +
						              "<tbody id='tb2'>\n" + 
						              		tr2 + 
									  "</tbody>\n" +
								  "</table>\n" +
							  "</div>\n" +
						  "</div>\n";
			
//			body += "<div align=\"center\" data-role=\"collapsible\">\n"+
//	              "<h3 align=\"center\">退出测试</h3>\n" +
//	              "<div>\n" +
//		              "<table data-role=\"table\" id='t3' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >\n" +
//			              "<thead>\n" +
//				              "<tr>\n" +
//				              	  "<th data-priority=\"1\">日期</th>\n" +				              	
//				              	  "<th data-priority=\"1\">不吸引人</th>\n" +
//					              "<th data-priority=\"1\">想学但价格太高</th>\n" +
//					              "<th data-priority=\"2\">习题太少缺少练习</th>\n" +
//					              "<th data-priority=\"3\">孩子看不懂</th>\n" +
//					              "<th data-priority=\"4\">操作不方便</th>\n" +
//				              "</tr>\n"+
//			              "</thead>\n" +
//			              "<tbody id='str_退出原因'>\n" + 
//			              	str_退出原因 + 
//						  "</tbody>\n" +
//					  "</table>\n" +
//				  "</div>\n" +
//			  "</div>\n";
		
			
			html = Http.getHtml(body);
		}

		return html;
	}
}
