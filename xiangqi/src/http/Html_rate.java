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
		String bgcolor="#F5F5F5";//默认背景色
		//模糊查询功能↓
		if(!content.isEmpty()){
			String[] 全部渠道={"oppo平台","vivo平台","华为平台","苹果商城",				
				"小米平台","三星平台","金立平台","联想平台","阿里应用","安智市场","机锋市场","百度商城",
				"360商城","pconline","搜狗助手","应用宝","应用汇","网页下载","软件互推","联通商城",				
				"乐视平台","乐视电视","视频教育","视频儿童"};
			for(String str:全部渠道){
				content = str.contains(content)?str:content;
							}		}
		//模糊查询功能↑
		StringBuilder 每日渠道记录 = new StringBuilder();
		List<ChannelEveryday> list = Dao.getChannelEverydayByChannel(content);
		for(int m=0;m<list.size();m++){
			ChannelEveryday ce = list.get(m);
			bgcolor = (ce.getDay()%2==1)?"#F5F5F5":"#FFFFFF";//不同日期背景色不同
			每日渠道记录.append( "<tr bgcolor=\""+bgcolor+"\">" +
					"<td>"+ce.getChannel()+"</td>" +
					"<td>"+简化日期(ce.getDayStr())+"</td>" +
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
					"</tr>\n");
		}
		
		String html = "";
		if(!content.isEmpty()){
			html = 每日渠道记录.toString();
		}else{
			String body ="<style type=\"text/css\">table,th,td{	border:1px solid #888888;}</style>"+//表格边框属性
					"<script>\n" +
						"function update(){\n" +
							"var name = $('#channelName').val();\n" +
							"if(name){\n" +
								"$.post('/rate', name, function(data){$('#detail').html(data);});\n" +
							"} }" +
					"</script>\n"+
					"<div align=\"center\" data-role=\"collapsible\">\n"+
						"<h3 align=\"center\">渠道统计</h3>\n" +
						"<div>\n" +						
							"<input type='text' placeholder=\"渠道\" id='channelName' onchange='update();' />\n"+
							"<table data-role=\"table\" id='t1' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >\n" +
								"<thead>\n" +
									"<tr>\n" +
										"<th data-priority=\"2\" width=\"45\"><br>渠道</th>\n" +
										"<th data-priority=\"2\" width=\"45\"><br>时间</th>\n" +
										"<th data-priority=\"1\">打开<br>设备</th>\n" +
										"<th data-priority=\"4\"><br>新增</th>\n" +
										"<th data-priority=\"3\">次日<br>返回</th>\n" +
										"<th data-priority=\"5\">2~6<br>返回</th>\n" +
										"<th data-priority=\"6\">7~<br>返回</th>\n" +
										"<th data-priority=\"4\">支付<br>次数</th>\n" +
										"<th data-priority=\"4\">新增<br>支付</th>\n" +
										"<th>支付<br>金额</th>\n" +
										"<th data-priority=\"5\">支付宝<br>支付</th>\n" +
										"<th data-priority=\"5\">微信<br>支付</th>\n" +
										"<th data-priority=\"6\">苹果<br>支付</th>\n" +
										"<th data-priority=\"5\">华为<br>支付</th>\n" +
										"<th data-priority=\"6\">其他<br>支付</th>\n" +
									"</tr>\n"+
								"</thead>\n" +
								"<tbody id='detail'>\n" + 
									每日渠道记录.toString() + 
								"</tbody>\n" +
							"</table>" +
						"</div>" +
					"</div>\n";
			
			String 每月统计 = "";
			StringBuilder 每天统计=new StringBuilder();
			StringBuilder 红包=new StringBuilder();
			List<Count> list2 = Dao.getAllDayCount();
			for(int m=0;m<list2.size()-1;m++){
				Count count = list2.get(m);
				Count next = list2.get(m+1);
				Data data = list2.get(m).getData();
				//验证返回记录
				int open = 0 , otherOpen = 0;
				for(int i:new int[]{0,1,2,4,8,15}){					 
					 otherOpen += data.get("返回").get(i).get("详细").get("其它版本").asInt();
					 open += data.get("返回").get(i).get("共计").asInt();
				}
				String day2,day7;
				day2 = "<br>偶:"+(返回(data,2,0)+返回(data,4,0)) +"<br>奇:"+(返回(data,2,1)+返回(data,4,1));
				day7 = "<br>偶:"+(返回(data,8,0)+返回(data,15,0)) +"<br>奇:"+(返回(data,8,1)+返回(data,15,1));
				//计算付费率
				String 平均支付率 = "",平均支付额 = "";
				if(count.getOpen() > 0){
					平均支付率 = new DecimalFormat("0.0").format((float)count.getPay()*100/count.getOpen());
					平均支付额 = new DecimalFormat("0.000").format((float)count.getTotalPay()/count.getOpen());					
				}
				//取星期
				String[] weeks = {"没有","周日","周一","周二","周三","周四","周五","周六"};
				String week =weeks[ServerTimer.getCalendarFromString(count.getDayStr()).get(Calendar.DAY_OF_WEEK)];
				//多课支付
				String 多课次="",多课额="";			
				if(data.get("支付").get("总计次数").containsKey("多课支付")){
					多课次 = data.get("支付").get("总计次数").get("多课支付").asInt()+"次<br>";
				    多课额 = data.get("支付").get("总计金额").get("多课支付").asInt()+"元";
				}
				bgcolor = ("周日".equals(week) || "周六".equals(week))?"#FFFFFF":"#F5F5F5";//改周末背景色
				Data paydata = data.get("支付").get("详细金额");//简化后面的data
				每天统计.append("<tr class=\""+week+"\" bgcolor=\""+ bgcolor+"\">"+
						"<td>"+简化日期(count.getDayStr())+"<br>"+week+"</td>" +
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
						"<td>"+count.getAliPay()+this.show奇偶(paydata.get("支付宝"))+"</td>" +
						"<td>"+count.getWxPay()+this.show奇偶(paydata.get("微信支付"))+"</td>" +
						"<td>"+count.getApplePay()+this.show奇偶(paydata.get("苹果支付"))+"</td>" +
						"<td>"+count.getHwPay()+this.show奇偶(paydata.get("华为支付"))+"</td>" +
						"<td>"+count.getWiiPay()+this.show奇偶(paydata.get("其它支付"))+"</td>" +
						"</tr>\n");
				
				Data reward = Data.fromMap(count.getReward());
				if(reward.containsKey("红包生成")||reward.containsKey("红包使用")){
					Data rew生 =reward.get("红包生成"),rew用=reward.get("红包使用");
					红包.append("<tr>"+
						"<td>"+简化日期(count.getDayStr())+week+"</td>"+//时间
						"<td>"+rew生.get(1).get("错过").asInt()+"</td>"+
						"<td>"+rew生.get(1).get("次数").asInt()+"</td>"+
						"<td>"+rew生.get(1).get("金额").asInt()+"</td>"+
						
						"<td>"+rew生.get(2).get("错过").asInt()+"</td>"+
						"<td>"+rew生.get(2).get("次数").asInt()+"</td>"+
						"<td>"+rew生.get(2).get("金额").asInt()+"</td>"+
						
						"<td>"+rew用.get("次数").asInt()+"</td>"+
						"<td>"+rew用.get("金额").asInt()+"</td>"+
						"<td>"+rew用.get("单课次数").asInt()+"</td>"+
						"<td>"+rew用.get("单课金额").asInt()+"</td>"+
						"<td>"+rew用.get("多课次数").asInt()+"</td>"+
						"<td>"+rew用.get("多课金额").asInt()+"</td>"+
						"</tr>\n");
				}				
			}
			
			body +=	"<div align=\"center\" data-role=\"collapsible\">\n"+
					"<h3 align=\"center\">每天统计</h3>\n" +					
					"<div>\n" +
					"<div data-role=\"controlgroup\" data-type=\"horizontal\" data-mini='true' >\n" +
				  	"<label for='checkbox1'>星期一</label><input type='checkbox' id='checkbox1' checked=\"checked\" onclick=\"$('.周一').toggle();\" />\n" +
				  	"<label for='checkbox2'>星期二</label><input type='checkbox' id='checkbox2' checked=\"checked\" onclick=\"$('.周二').toggle();\" />\n" +
				  	"<label for='checkbox3'>星期三</label><input type='checkbox' id='checkbox3' checked=\"checked\" onclick=\"$('.周三').toggle();\" />\n" +
				  	"<label for='checkbox4'>星期四</label><input type='checkbox' id='checkbox4' checked=\"checked\" onclick=\"$('.周四').toggle();\" />\n" +
				  	"<label for='checkbox5'>星期五</label><input type='checkbox' id='checkbox5' checked=\"checked\" onclick=\"$('.周五').toggle();\" />\n" +
				  	"<label for='checkbox6'>星期六</label><input type='checkbox' id='checkbox6' checked=\"checked\" onclick=\"$('.周六').toggle();\" />\n" +
				  	"<label for='checkbox0'>星期天</label><input type='checkbox' id='checkbox0' checked=\"checked\" onclick=\"$('.周日').toggle();\" />\n" +
				  	 "</div>\n"+
						"<table data-role=\"table\" id='t2' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >\n" +
							"<thead>\n" +
								"<tr>\n" +
									"<th data-priority=\"1\" width=\"55px\"><br>时间</th>\n" +
									"<th data-priority=\"2\">打开<br>设备</th>\n" +
									"<th data-priority=\"3\">新增<br>设备</th>\n" +
									"<th data-priority=\"4\">返回<br>次日</th>\n" +
									"<th data-priority=\"5\">返回<br>2~6</th>\n" +
									"<th data-priority=\"6\">返回<br>7~∞</th>\n" +
									"<th data-priority=\"1\">支付<br>次数</th>\n" +
									"<th data-priority=\"1\">支付<br>金额</th>\n" +
									"<th data-priority=\"3\">多课<br>支付</th>\n" +	
									"<th data-priority=\"3\">日均<br>支付率</th>\n" +
									"<th data-priority=\"3\">平均<br>支付额</th>\n" +
									"<th data-priority=\"6\">新增<br>支付</th>\n" +
									"<th data-priority=\"5\">支付宝<br>支付</th>\n" +
									"<th data-priority=\"5\">微信<br>支付</th>\n" +
									"<th data-priority=\"6\">苹果<br>支付</th>\n" +
									"<th data-priority=\"4\">华为<br>支付</th>\n" +
									"<th data-priority=\"6\">其他<br>支付</th>\n" +
								"</tr>\n"+
							"</thead>" +
							"<tbody>\n" + 
								每天统计.toString() + 
							"</tbody>" +
						"</table>" +
					"</div>" +
				"</div>";
			每月统计 = "";
			list2 = Dao.getAllMonthCount();
			for(int m=0;m<list2.size();m++){
				Count count = list2.get(m);
				String 付费率 = (count.getOpen() > 0)?new DecimalFormat("0.0").format((float)count.getPay()*100/count.getOpen()):"";			
				每月统计 += "<tr>" +
						"<td>"+count.getDayStr()+"</td>" +
						"<td>"+count.getOpen()+"</td>" +
						"<td>"+count.getNewDevice()+"</td>" +
						"<td>"+count.getPay()+"</td>"+
						"<td>"+count.getNewPay()+"</td>" +
						"<td>"+付费率+"%"+"</td>" +
						"<td>"+count.getTotalPay()+"</td>" +
						"<td>"+count.getAliPay()+"</td>" +
						"<td>"+count.getWxPay()+"</td>" +
						"<td>"+count.getApplePay()+"</td>" +
						"<td>"+count.getHwPay()+"</td>" +
						"<td>"+count.getWiiPay()+"</td>" +
						"</tr>\n";
			}
			body +=
				"<div align=\"center\" data-role=\"collapsible\">\n"+
					"<h3 align=\"center\">每月统计</h3>\n" +
					"<div>\n" +
						"<table data-role=\"table\" id='t-month' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >\n" +
							"<thead>\n" +
								"<tr>\n" +
									"<th><br>时间</th>\n" +
									"<th data-priority=\"1\">打开<br>设备</th>\n" +
									"<th data-priority=\"3\">新增<br>设备</th>\n" +
									"<th data-priority=\"2\">支付<br>次数</th>\n" +
									"<th data-priority=\"6\">新增<br>支付</th>\n" +
									"<th>月均<br>付费率</th>\n" +
									"<th>支付<br>金额</th>\n" +
									"<th data-priority=\"5\">支付宝<br>支付</th>\n" +
									"<th data-priority=\"5\">微信<br>支付</th>\n" +
									"<th data-priority=\"6\">苹果<br>支付</th>\n" +
									"<th data-priority=\"5\">华为<br>支付</th>\n" +
									"<th data-priority=\"6\">其他<br>支付</th>\n" +
								"</tr>\n"+
							"</thead>\n" +
							"<tbody>\n" + 
								每月统计 + 
							"</tbody>\n" +
						"</table>\n" +
					"</div>\n" +
				"</div>\n";
			//红包记录
			body +="<div align=\"center\" data-role=\"collapsible\">\n"+
						"<h3 align=\"center\">红包统计</h3>\n" +
						"<div>\n" +
							"<table data-role=\"table\" id='reward' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >\n" +
								"<thead>\n" +
									"<tr>\n" +
										"<th rowspan=\"2\" ><br>时间</th>\n" + 
										"<th colspan=\"3\" data-priority=\"1\" style=\"text-align:center;\" >第一课</th>\n" +
										"<th colspan=\"3\" data-priority=\"1\" style=\"text-align:center;\" >第二课</th>\n" +
										"<th colspan=\"2\" data-priority=\"1\" style=\"text-align:center;\" >红包使用</th>\n" +
										"<th colspan=\"2\" data-priority=\"1\" style=\"text-align:center;\" >单课使用</th>\n" +
										"<th colspan=\"2\" data-priority=\"1\" style=\"text-align:center;\" >多课使用</th>\n" +
									"</tr>\n"+
									"<tr>\n" +
										"<th>错过</th>\n" +
										"<th>生成</th>\n" +
										"<th>金额</th>\n" +
										
										"<th>错过</th>\n" +
										"<th>生成</th>\n" +
										"<th>金额</th>\n" +
									
										"<th>次数</th>\n" +//总计
										"<th>金额</th>\n" +
										"<th>次数</th>\n" +//单课
										"<th>金额</th>\n" +
										"<th>次数</th>\n" +//多课
										"<th>金额</th>\n" +
									"</tr>\n"+
								"</thead>\n" +
								"<tbody>\n" + 
									红包.toString() + 
								"</tbody>\n" +
							"</table>" +
						"</div>\n" +
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
	public String 简化日期(String 日期){
		if(日期 != null && 日期.length() >7){
			return 日期.substring(5, 日期.length());
		}else{ log.warning("日期格式有误");
			return "";
		}		
	}
}