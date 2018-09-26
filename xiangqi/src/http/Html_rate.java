package http;

import java.text.DecimalFormat;
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
										"<th data-priority=\"1\">打开设备</th>" +
										"<th data-priority=\"4\">新增</th>" +
										"<th data-priority=\"3\">次日返回</th>" +
										"<th data-priority=\"5\">2~6返回</th>" +
										"<th data-priority=\"6\">7~返回</th>" +
										"<th data-priority=\"4\">支付次数</th>" +
										"<th data-priority=\"4\">新增支付次数</th>" +
										"<th>支付金额</th>" +
										"<th data-priority=\"5\">支付宝</th>" +
										"<th data-priority=\"5\">微信支付</th>" +
										"<th data-priority=\"6\">苹果支付</th>" +
										"<th data-priority=\"5\">华为支付</th>" +
										"<th data-priority=\"6\">其他支付</th>" +
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
			StringBuilder 红包=new StringBuilder();
			List<Count> list2 = Dao.getAllDayCount();
			for(int m=0;m<list2.size()-1;m++){
				Count count = list2.get(m);
				Count next = list2.get(m+1);
				Data data = list2.get(m).getData();
				int open = 0 , otherOpen = 0;
				for(int i:new int[]{0,1,2,4,8,15}){					 
					 otherOpen += data.get("返回").get(i).get("详细").get("其它版本").asInt();
					 open += data.get("返回").get(i).get("共计").asInt();
				}
				String day2,day7;
				day2 = "<br>偶"+( 返回(data,2,0) + 返回(data,4,0)	)
						+"<br>奇"+( 返回(data,2,1) + 返回(data,4,1) );
				day7 = "<br>偶"+( 返回(data,8,0) + 返回(data,15,0) )
						+"<br>奇"+( 返回(data,8,1) + 返回(data,15,1) );
				Data detail = data.get("支付").get("详细金额");
				String 平均支付率 = "",平均支付额 = "";
				if(count.getOpen() > 0){
					平均支付率 = new DecimalFormat("0.0").format((float)count.getPay()*100/count.getOpen());
					平均支付额 = new DecimalFormat("0.000").format((float)count.getTotalPay()/count.getOpen());					
				}
				String[] weeks = {"没有","星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
				String week =weeks[ServerTimer.getCalendarFromString(count.getDayStr()).get(Calendar.DAY_OF_WEEK)];
				String color = "<tr class=\""+week+"\">";
				if("星期日".equals(week) || "星期六".equals(week)){
					color = "<tr class=\""+week+"\" bgcolor=\"#FFFFBB\">";
				}
				String 多课次="",多课额="";			
				if(data.get("支付").get("总计次数").containsKey("多课支付")){
					多课次 = data.get("支付").get("总计次数").get("多课支付").asInt()+"次<br>";
				    多课额 = data.get("支付").get("总计金额").get("多课支付").asInt()+"元";
				}				
				sb.append(color +
						"<td>"+count.getDayStr()+"<br>"+week+"</td>" +
						"<td>"+count.getOpen()+"<br>是7:"+(open - otherOpen)+"<br>非7:"+otherOpen+"</td>" +
						"<td>"+count.getNewDevice()+"<br>是7:"+data.get("新增用户").get(7).asInt()+"<br>非7:"+data.get("新增用户").get(0).asInt()+"</td>" +
						"<td>"+count.getReturnNum(1)+"("+(next.getNewDevice() == 0 ? 0 : count.getReturnNum(1)*100/next.getNewDevice())+"%)"
							+this.show奇偶(data.get("返回").get(1).get("详细"))+"</td>" +
						"<td>"+count.getReturnNum(2)+day2+"</td>" +
						"<td>"+count.getReturnNum(7)+day7+"</td>" +
						"<td>"+count.getPay()+this.show奇偶(data.get("支付").get("总计次数"))+"</td>"+//支付次数		
						"<td>"+count.getTotalPay()+this.show奇偶(data.get("支付").get("总计金额"))+"</td>" +//支付金额
						"<td>"+多课次+多课额+"</td>" +//多课支付
						"<td>"+平均支付率+"%"+"</td>" +
						"<td>"+平均支付额+"</td>" +
						"<td>"+count.getNewPay()+"</td>" +
						"<td>"+count.getAliPay()+this.show奇偶(detail.get("支付宝"))+"</td>" +
						"<td>"+count.getWxPay()+this.show奇偶(detail.get("微信支付"))+"</td>" +
						"<td>"+count.getApplePay()+this.show奇偶(detail.get("苹果支付"))+"</td>" +
						"<td>"+count.getHwPay()+this.show奇偶(detail.get("华为支付"))+"</td>" +
						"<td>"+count.getWiiPay()+this.show奇偶(detail.get("其它支付"))+"</td>" +
						"</tr>");
				Data reward = Data.fromMap(count.getReward());
				if(reward.containsKey("红包生成")||reward.containsKey("红包使用")){
					红包.append("<tr>"+
						"<td>"+count.getDayStr()+"<br>"+week+"</td>"+
						"<td>"+reward.get("红包生成").get(1).get("次数").asInt()+"次<br>"+reward.get("红包生成").get(1).get("金额").asInt()+"元</td>"+
						"<td>"+reward.get("红包生成").get(2).get("次数").asInt()+"次<br>"+reward.get("红包生成").get(2).get("金额").asInt()+"元</td>"+
						"<td>"+reward.get("红包生成").get(1).get("错过").asInt()+"次</td>"+
						"<td>"+reward.get("红包生成").get(2).get("错过").asInt()+"次</td>"+
						"<td>"+reward.get("红包使用").get("次数").asInt()+"次<br>"+reward.get("红包使用").get("金额").asInt()+"元</td>"+
						"</tr>");
				}				
			}
			
			body +=
				"<div align=\"center\" data-role=\"collapsible\">"+
					"<h3 align=\"center\">每天统计</h3>" +					
					"<div>" +
					"<div data-role=\"controlgroup\" data-type=\"horizontal\" data-mini='true' >" +
				  	"<label for='checkbox1'>星期一</label><input type='checkbox' id='checkbox1' checked=\"checked\" onclick=\"$('.星期一').toggle();\" />" +
				  	"<label for='checkbox2'>星期二</label><input type='checkbox' id='checkbox2' checked=\"checked\" onclick=\"$('.星期二').toggle();\" />" +
				  	"<label for='checkbox3'>星期三</label><input type='checkbox' id='checkbox3' checked=\"checked\" onclick=\"$('.星期三').toggle();\" />" +
				  	"<label for='checkbox4'>星期四</label><input type='checkbox' id='checkbox4' checked=\"checked\" onclick=\"$('.星期四').toggle();\" />" +
				  	"<label for='checkbox5'>星期五</label><input type='checkbox' id='checkbox5' checked=\"checked\" onclick=\"$('.星期五').toggle();\" />" +
				  	"<label for='checkbox6'>星期六</label><input type='checkbox' id='checkbox6' checked=\"checked\" onclick=\"$('.星期六').toggle();\" />" +
				  	"<label for='checkbox0'>星期天</label><input type='checkbox' id='checkbox0' checked=\"checked\" onclick=\"$('.星期日').toggle();\" />" +
				  	 "</div>"+
						"<table data-role=\"table\" id='t2' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >" +
							"<thead>" +
								"<tr>" +
									"<th>时间</th>" +
									"<th data-priority=\"2\">打开设备</th>" +
									"<th data-priority=\"3\">新增</th>" +
									"<th data-priority=\"4\">次日返回</th>" +
									"<th data-priority=\"5\">2~6返回</th>" +
									"<th data-priority=\"6\">7~返回</th>" +
									"<th data-priority=\"1\">支付次数</th>" +									
									"<th data-priority=\"1\">支付金额</th>" +
									"<th data-priority=\"3\">多课支付</th>" +	
									"<th data-priority=\"3\">平均支付率</th>" +
									"<th data-priority=\"3\">平均支付额</th>" +
									"<th data-priority=\"6\">新增支付</th>" +
									"<th data-priority=\"5\">支付宝</th>" +
									"<th data-priority=\"5\">微信支付</th>" +
									"<th data-priority=\"6\">苹果支付</th>" +
									"<th data-priority=\"4\">华为支付</th>" +
									"<th data-priority=\"6\">其他支付</th>" +
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
									"<th data-priority=\"1\">打开设备</th>" +
									"<th data-priority=\"3\">新增设备</th>" +
									"<th data-priority=\"2\">支付次数</th>" +
									"<th data-priority=\"6\">新增支付次数</th>" +
									"<th>支付金额</th>" +
									"<th data-priority=\"5\">支付宝</th>" +
									"<th data-priority=\"5\">微信支付</th>" +
									"<th data-priority=\"6\">苹果支付</th>" +
									"<th data-priority=\"5\">华为支付</th>" +
									"<th data-priority=\"6\">其他支付</th>" +
								"</tr>"+
							"</thead>" +
							"<tbody>" + 
								tr2 + 
							"</tbody>" +
						"</table>" +
					"</div>" +
				"</div>";
			//红包记录
			body +="<div align=\"center\" data-role=\"collapsible\">"+
						"<h3 align=\"center\">红包统计</h3>" +
						"<div>" +
							"<table data-role=\"table\" id='reward' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >" +
								"<thead>" +
									"<tr>" +
										"<th>时间</th>" +
										"<th data-priority=\"2\">第一课生成</th>" +
										"<th data-priority=\"2\">第二课生成</th>" +
										"<th data-priority=\"3\">第一课错过</th>" +
										"<th data-priority=\"3\">第二课错过</th>" +
										"<th data-priority=\"1\">今日使用</th>" +										
									"</tr>"+
								"</thead>" +
								"<tbody>" + 
									红包.toString() + 
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
	private int 返回(Data data,int day,int 奇偶){
		//data.get("返回").get(2).get("详细").get("1").asInt();
		return data.get("返回").get(day).get("详细").get(奇偶).asInt();		
	}
}