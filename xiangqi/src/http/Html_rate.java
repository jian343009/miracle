package http;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import net.sf.json.JSONArray;

import dao.Dao;
import dao.Data;
import data.*;

import main.Global;
import main.ServerTimer;


public class Html_rate implements IHtml {
	private static final Logger log = Logger.getLogger(Html_rate.class.getName());

	@Override
	public String getHtml(String content) {
		
		StringBuilder tr=new StringBuilder();
		List<ChannelEveryday> list = Dao.getChannelEverydayByChannel(content);
		for(int m=0;m<list.size();m++){
			ChannelEveryday ce = list.get(m);
			tr.append( "<tr>" +
					"<td>"+ce.getChannel()+"</td>" +
					"<td>"+ce.getDayStr()+"</td>" +
					"<td>"+ce.getOpen()+"</td>" +
					"<td>"+ce.getNewDevice()+"</td>" +
					"<td>"+ce.getReturnNum(1)+"("+(ce.getYesterday()==0 ? 0 : ce.getReturnNum(1)*100/ce.getYesterday())+"%)</td>" +
					"<td>"+ce.getReturnNum(2)+"</td>" +
					"<td>"+ce.getReturnNum(7)+"</td>" +
					"<td>"+ce.getPay()+"</td>"+
					"<td>"+ce.getNewPay()+"</td>" +
					"<td>"+ce.getTotalPay()+"</td>" +
					"<td>"+ce.getAliPay()+"</td>" +
					"<td>"+ce.getWxPay()+"</td>" +
					"<td>"+ce.getApplePay()+"</td>" +
					"<td>"+ce.getHwPay()+"</td>" +
					"<td>"+ce.getWiiPay()+"</td>" +
					"</tr>");
		}
		
		String html = "";
		if(!content.isEmpty()){
			html = tr.toString();
		}else{
			String body =
					"<script>" +
						"function update(){" +
							"var name = $('#channelName').val();" +
							"if(name){" +
								"$.post('/rate', name, function(data){$('#detail').html(data);});" +
							"}" +
						"}" +
					"</script>"+
					"<div align=\"center\" data-role=\"collapsible\">"+
						"<h3 align=\"center\">渠道统计</h3>" +
						"<div>" +
							"渠道<input type='text' id='channelName' onchange='update();' />"+
							"<table data-role=\"table\" id='t1' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >" +
								"<thead>" +
									"<tr>" +
										"<th>渠道</th>" +
										"<th data-priority=\"2\">时间</th>" +
										"<th data-priority=\"3\">打开设备</th>" +
										"<th data-priority=\"4\">新增设备</th>" +
										"<th data-priority=\"4\">次日返回</th>" +
										"<th data-priority=\"4\">2~6返回</th>" +
										"<th data-priority=\"4\">7~返回</th>" +
										"<th data-priority=\"5\">支付次数</th>" +
										"<th data-priority=\"6\">新增支付次数</th>" +
										"<th>支付金额</th>" +
										"<th data-priority=\"8\">支付宝</th>" +
										"<th data-priority=\"9\">微信支付</th>" +
										"<th data-priority=\"9\">苹果支付</th>" +
										"<th data-priority=\"9\">华为支付</th>" +
										"<th data-priority=\"9\">其他支付</th>" +
									"</tr>"+
								"</thead>" +
								"<tbody id='detail'>" + 
									tr.toString() + 
								"</tbody>" +
							"</table>" +
						"</div>" +
					"</div>";
			
			String tr2 = "";
			StringBuilder sb=new StringBuilder();
			List<Count> list2 = Dao.getAllDayCount();
			for(int m=0;m<list2.size()-1;m++){
				Count count = list2.get(m);
				Count next = list2.get(m+1);
				Data data = list2.get(m).getData();
				int open = 0 , otherOpen = 0;
				int[] iday={0,1,2,4,8,15};				
				for(int i:iday){
					 open += data.get("返回").get(i).get("共计").asInt()-data.get("返回").get(i).get("详细").get("其它版本").asInt();
					 otherOpen +=data.get("返回").get(i).get("详细").get("其它版本").asInt();
				}
				String day2,day7;
				day2 = "<br>偶"+(data.get("返回").get(2).get("详细").get("0").asInt()+data.get("返回").get(4).get("详细").get("0").asInt())
						+"<br>奇"+(data.get("返回").get(2).get("详细").get("1").asInt()+data.get("返回").get(4).get("详细").get("1").asInt());
				day7 = "<br>偶"+(data.get("返回").get(8).get("详细").get("0").asInt()+data.get("返回").get(15).get("详细").get("0").asInt())
						+"<br>奇"+(data.get("返回").get(8).get("详细").get("1").asInt()+data.get("返回").get(15).get("详细").get("1").asInt());
				
				sb.append("<tr>" +
						"<td>"+count.getDayStr()+"</td>" +
						"<td>"+count.getOpen()+"<br>是7:"+open+"<br>非7:"+otherOpen+"</td>" +
						"<td>"+count.getNewDevice()+"<br>是7:"+data.get("新增用户").get(7).asInt()+"<br>非7:"+data.get("新增用户").get(0).asInt()+"</td>" +
						"<td>"+count.getReturnNum(1)+"("+(next.getNewDevice() == 0 ? 0 : count.getReturnNum(1)*100/next.getNewDevice())+"%)"
							+this.show奇偶(data.get("返回").get(1).get("详细"))+
						"<td>"+count.getReturnNum(2)+day2+"</td>" +
						"<td>"+count.getReturnNum(7)+day7+"</td>" +
						"<td>"+count.getPay()+this.show奇偶(data.get("支付").get("总计次数"))+"</td>"+
						"<td>"+count.getNewPay()+"</td>" +
						"<td>"+count.getTotalPay()+this.show奇偶(data.get("支付").get("总计金额"))+"</td>" +
						"<td>"+count.getAliPay()+this.show奇偶(data.get("支付").get("详细金额").get("支付宝"))+"</td>" +
						"<td>"+count.getWxPay()+this.show奇偶(data.get("支付").get("详细金额").get("微信支付"))+"</td>" +
						"<td>"+count.getApplePay()+this.show奇偶(data.get("支付").get("详细金额").get("苹果支付"))+"</td>" +
						"<td>"+count.getHwPay()+this.show奇偶(data.get("支付").get("详细金额").get("华为支付"))+"</td>" +
						"<td>"+count.getWiiPay()+this.show奇偶(data.get("支付").get("详细金额").get("其它支付"))+"</td>" +
						"</tr>");
			}
			
			body +=
				"<div align=\"center\" data-role=\"collapsible\">"+
					"<h3 align=\"center\">每天统计</h3>" +
					"<div>" +
						"<table data-role=\"table\" id='t2' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >" +
							"<thead>" +
								"<tr>" +
									"<th>时间</th>" +
									"<th data-priority=\"3\">打开设备</th>" +
									"<th data-priority=\"4\">新增设备</th>" +
									"<th data-priority=\"4\">次日返回</th>" +
									"<th data-priority=\"4\">2~6返回</th>" +
									"<th data-priority=\"4\">7~返回</th>" +
									"<th data-priority=\"5\">支付次数</th>" +
									"<th data-priority=\"6\">新增支付次数</th>" +
									"<th>支付金额</th>" +
									"<th data-priority=\"8\">支付宝</th>" +
									"<th data-priority=\"9\">微信支付</th>" +
									"<th data-priority=\"9\">苹果支付</th>" +
									"<th data-priority=\"9\">华为支付</th>" +
									"<th data-priority=\"9\">其他支付</th>" +
								"</tr>"+
							"</thead>" +
							"<tbody>" + 
								sb.toString() + 
							"</tbody>" +
						"</table>" +
					"</div>" +
				"</div>";
			tr2 = "";
			list2 = Dao.getAllMonthCount();
			for(int m=0;m<list2.size();m++){
				Count count = list2.get(m);
				tr2 += "<tr>" +
						"<td>"+count.getDayStr()+"</td>" +
						"<td>"+count.getOpen()+"</td>" +
						"<td>"+count.getNewDevice()+"</td>" +
						"<td>"+count.getPay()+"</td>"+
						"<td>"+count.getNewPay()+"</td>" +
						"<td>"+count.getTotalPay()+"</td>" +
						"<td>"+count.getAliPay()+"</td>" +
						"<td>"+count.getWxPay()+"</td>" +
						"<td>"+count.getApplePay()+"</td>" +
						"<td>"+count.getHwPay()+"</td>" +
						"<td>"+count.getWiiPay()+"</td>" +
						"</tr>";
			}
			
			
			body +=
				"<div align=\"center\" data-role=\"collapsible\">"+
					"<h3 align=\"center\">每月统计</h3>" +
					"<div>" +
						"<table data-role=\"table\" id='t2' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >" +
							"<thead>" +
								"<tr>" +
									"<th>时间</th>" +
									"<th data-priority=\"3\">打开设备</th>" +
									"<th data-priority=\"4\">新增设备</th>" +
									"<th data-priority=\"5\">支付次数</th>" +
									"<th data-priority=\"6\">新增支付次数</th>" +
									"<th>支付金额</th>" +
									"<th data-priority=\"8\">支付宝</th>" +
									"<th data-priority=\"9\">微信支付</th>" +
									"<th data-priority=\"9\">苹果支付</th>" +
									"<th data-priority=\"9\">华为支付</th>" +
									"<th data-priority=\"9\">其他支付</th>" +
								"</tr>"+
							"</thead>" +
							"<tbody>" + 
								tr2 + 
							"</tbody>" +
						"</table>" +
					"</div>" +
				"</div>";
			html = Http.getHtml(body);
		}

		return html;
	}
	public String show奇偶(Data data){
		StringBuilder str = new StringBuilder();
		if(data._Value()!=null){
			if(data.get("0").size()>0||data.get("1").size()>0){
				str.append("<br>偶:").append(data.get("0").asInt())
				.append("<br>奇:").append(data.get("1").asInt());
			}else if(data.get(0).size()>0||data.get(1).size()>0){
				str.append("<br>偶:").append(data.get(0).asInt())
				.append("<br>奇:").append(data.get(1).asInt());
			}
		}		
		return str.toString();
	}	
}