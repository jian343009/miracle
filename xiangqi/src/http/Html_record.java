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
					"</tr>";
			
		}
		
		String html = "";
		if(!content.isEmpty()){
			html = tr;
		}else{
			String body =
					"<script>" +
						"function update(){" +
							"$.post('/record', $('#imei').val(), function(data){$('#detail').html(data);});" +
						"}" +
					"</script>"+
					  "<div align=\"center\" data-role=\"collapsible\">"+
			              "<h3 align=\"center\">行为记录</h3>" +
			              "<div>" +
			              //	"设备<input type='text' id='imei' onchange='update();' />"+
				              "<table data-role=\"table\" id='t1' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >" +
					              "<thead>" +
						              "<tr>" +
						              	  "<th data-priority=\"1\">设备ID</th>" +
							              "<th data-priority=\"1\">imei</th>" +
							              "<th data-priority=\"2\">行为</th>" +
							              "<th data-priority=\"3\">信息</th>" +
							              "<th data-priority=\"4\">时间</th>" +
						              "</tr>"+
					              "</thead>" +
					              "<tbody id='detail'>" + 
					              		tr + 
								  "</tbody>" +
							  "</table>" +
						  "</div>" +
					  "</div>";
			
			String tr2 = "";
			String str_退出原因 = "";
			List<StepCount> scList = StepCount.getAll();	
			for(StepCount sc : scList){				
				tr2 += "<tr>" +
						"<td>"+sc.getDayStr()+"</td>" +
						"<td>"+sc.getChannel()+"</td>" +
						"<td>"+sc.getData().get("打开设备").asInt()+"</td>" +
						"<td>"+sc.getData().get("新增设备").asInt()+"</td>" +
						"<td>";	
				if(sc.getData().get("退出测试").size()>0){
					Data data_out = sc.getData().get("退出测试");				
					str_退出原因 += "<tr>" + 
					"<td>"+sc.getDayStr()+" "+sc.getChannel()+"</td>" +
					"<td>"+data_out.get("不吸引人").asInt()+"</td>" +
					"<td>"+data_out.get("想学但价格太高").asInt()+"</td>" +
					"<td>"+data_out.get("习题太少缺少练习").asInt()+"</td>" +
					"<td>"+data_out.get("孩子看不懂").asInt()+"</td>" +
					"<td>"+data_out.get("操作不方便").asInt()+"</td>" +
					"</tr>";		
				}
										
				for(int m=0;m<24;m++){
					int num = sc.getData().get("打开设备时段").get(m).asInt();
					if(num >0){
						tr2 += m+":"+sc.getData().get("打开设备时段").get(m).asInt()+"<br/>";
					}
				}
				tr2 +=
						"</td>" +
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
				
				
				tr2 += "</tr>";
			}
			body +=
					"<div align=\"center\" data-role=\"collapsible\">"+
				              "<h3 align=\"center\">行为统计</h3>" +
				              "<div>" +
					              "<table data-role=\"table\" id='t2' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >" +
						              "<thead>" +
							              "<tr>" +
							              	  "<th>日期</th>" +
								              "<th>渠道</th>" +
								              "<th data-priority=\"1\">打开设备</th>" +
								              "<th data-priority=\"1\">新增设备</th>" +
								              "<th data-priority=\"1\">设备打开时段</th>" +
								              "<th data-priority=\"1\">单课支付(打开#成功)</th>" +
								              "<th data-priority=\"1\">多课支付(打开#成功)</th>" +
								              "<th data-priority=\"1\">第1课学习和练习(开始#结束#完成)</th>" +
								              
								              "<th data-priority=\"1\">第2课学习和练习(开始#结束#完成)</th>" +
								              
												"<th data-priority=\"1\">第3课学习和练习(开始#结束#完成)</th>" +
												
												"<th data-priority=\"1\">第4课学习和练习(开始#结束#完成)</th>" +
												
												"<th data-priority=\"1\">第5课学习和练习(开始#结束#完成)</th>" +
												
												"<th data-priority=\"1\">第6课学习和练习(开始#结束#完成)</th>" +
												
												"<th data-priority=\"1\">第7课学习和练习(开始#结束#完成)</th>" +
												
												"<th data-priority=\"1\">第8课学习和练习(开始#结束#完成)</th>" +
												
												"<th data-priority=\"1\">第9课学习和练习(开始#结束#完成)</th>" +
												
												"<th data-priority=\"1\">第10课学习和练习(开始#结束#完成)</th>" +
												
												"<th data-priority=\"1\">第11课学习和练习(开始#结束#完成)</th>" +
												
												"<th data-priority=\"1\">第12课学习和练习(开始#结束#完成)</th>" +
												
												"<th data-priority=\"1\">第13课学习和练习(开始#结束#完成)</th>" +
												
												"<th data-priority=\"1\">第14课学习和练习(开始#结束#完成)</th>" +
												
												"<th data-priority=\"1\">第15课学习和练习(开始#结束#完成)</th>" +
												
												"<th data-priority=\"1\">第16课学习和练习(开始#结束#完成)</th>" +
							              "</tr>"+
						              "</thead>" +
						              "<tbody id='tb2'>" + 
						              		tr2 + 
									  "</tbody>" +
								  "</table>" +
							  "</div>" +
						  "</div>";
			
			body += "<div align=\"center\" data-role=\"collapsible\">"+
	              "<h3 align=\"center\">退出测试</h3>" +
	              "<div>" +
		              "<table data-role=\"table\" id='t3' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >" +
			              "<thead>" +
				              "<tr>" +
				              	  "<th data-priority=\"1\">日期</th>" +				              	
				              	  "<th data-priority=\"1\">不吸引人</th>" +
					              "<th data-priority=\"1\">想学但价格太高</th>" +
					              "<th data-priority=\"2\">习题太少缺少练习</th>" +
					              "<th data-priority=\"3\">孩子看不懂</th>" +
					              "<th data-priority=\"4\">操作不方便</th>" +
				              "</tr>"+
			              "</thead>" +
			              "<tbody id='str_退出原因'>" + 
			              	str_退出原因 + 
						  "</tbody>" +
					  "</table>" +
				  "</div>" +
			  "</div>";
		
			
			html = Http.getHtml(body);
		}

		return html;
	}
}
