package http;

import java.util.List;
import java.util.logging.Logger;

import dao.Dao;
import data.Comment;
import main.Global;

public class Html_comment extends Html {
	private static final Logger log = Logger.getLogger(Html_comment.class.getName());
	@Override
	public String getHtml(String content) {
		String html = "";	
		if(content.isEmpty()){			
			List<Comment> list = Dao.get审核Comment(false);
			StringBuilder sb = new StringBuilder();
			List<Comment> list2 = Dao.get审核Comment(true);
			StringBuilder 已审核评论 = new StringBuilder();
			for(Comment co:list){
				sb.append("<tr>"+
					"<th>"+co.getId()+"</th>"+
					"<th>"+co.getUserName()+"</th>"+
					"<th>"+co.getTimeStr()+"</th>"+
					"<th>"+co.getUserAge()+"</th>"+
					"<th>"+co.getContent()+"</th>"+
					"<th><label id='"+co.getId()+"'>待审核</label></th>"+
					"<th><label id='"+co.getId()+"rrtrue' onclick='check(id)' style=\"color :green; border-style:groove\">公开</label></th>"+
					"<th><label id='"+co.getId()+"rrfalse' onclick='check(id)' style=\"color :red; border-style:groove\">不公开</label></th>"+
					"<th><label id='"+co.getId()+"rrdelete' onclick='check(id)' style=\"color :black; border-style:groove\">删除</label></th>"+
					"<tr>");
			};
			for(Comment co:list2){
				String 状态="不公开", 公开="",不公开 ="hidden";
				if(co.isDisplay()){
					状态="已公开";	公开="hidden";不公开="";
				}
				已审核评论.append("<tr>"+
					"<th>"+co.getId()+"</th>"+
					"<th>"+co.getUserName()+"</th>"+
					"<th>"+co.getTimeStr()+"</th>"+
					"<th>"+co.getUserAge()+"</th>"+
					"<th>"+co.getContent()+"</th>"+
					"<th><label id='"+co.getId()+"'>"+状态+"</label></th>"+//状态
					"<th><label id='"+co.getId()+"rrtrue' onclick='check(id)' style=\"color :green; border-style:ridge\" "+公开+">公开</label></th>"+
					"<th><label id='"+co.getId()+"rrfalse' onclick='check(id)' style=\"color :red; border-style:ridge\" "+不公开+">不公开</label></th>"+
					"<th><label id='"+co.getId()+"rrdelete' onclick='check(id)' style=\"color :black; border-style:ridge\">删除</label></th>"+
					"<tr>");
			};
			String body="<script type=\"text/javascript\">"+
					"function check(id){ "+
					"if(id.split(\"rr\")[1]==\"delete\"){"+
						"var r = confirm(\"确认删除？\");"+
						"if(r==false){return;}}"+					
			        "$.post('/comment',id,function(data){"+
			           "var arr=data.split(\"&\");"+
			           "$(\"#\" + arr[1]).text(arr[0]);"+
			           "if(arr[0]==\"已删除\"){"+//删除时移除label
			           "$(\"#\" + arr[1] + \"rrtrue\").remove();"+
			           "$(\"#\" + arr[1] + \"rrfalse\").remove();"+
			           "$(\"#\" + arr[1] + \"rrdelete\").remove();"+
			           "}"+
			           "else if(arr[0]==\"已公开\"){"+//删除时移除label			          
			           "$(\"#\" + arr[1] + \"rrtrue\").hide();"+
			            "$(\"#\" + arr[1] + \"rrfalse\").show();"+
			           "}else if(arr[0]==\"不公开\"){"+//删除时移除label 
			           "$(\"#\" + arr[1] + \"rrfalse\").hide();"+
			            "$(\"#\" + arr[1] + \"rrtrue\").show();}"+
			        "} );}</script>";
			body +="<div align=\"center\" data-role=\"collapsible\">"+
		              "<h3 align=\"center\">评论审核</h3>" +
				"<div><table data-role=\"table\" id='t1' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >"+
		        "<thead>"+
		        "<tr>"+
		            "<th>id</th>"+
		            "<th data-priority=\"2\">用户名</th>"+
		            "<th data-priority=\"2\">时间</th>"+
		            "<th data-priority=\"2\">年龄</th>"+
		            "<th data-priority=\"1\"width=\"360\">评论</th>"+
		            "<th data-priority=\"1\">审核结果</th>"+
		            "<th data-priority=\"7\">公开</th>"+
		            "<th data-priority=\"8\">不公开</th>"+
		            "<th data-priority=\"9\">删除</th>"+
		        "</tr>"+
		        "</thead>"+
		        "<tbody>";
			
			body +=sb.toString()+"<tr><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th></tr>"+
					"<tr><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th><th></th></tr>"+
					已审核评论.toString()+"</tbody></table></body></div></div>";
			html = Http.getHtml(body);
		}else if(content.trim().split("rr").length == 2){
			String[] con=content.trim().split("rr");
			int id = Global.getInt(con[0]);
			Comment com = Dao.getCommentByID(id);
			if(com!=null){
				com.setChecked(true);
				if("true".equals(con[1])){
					com.setDisplay(true);
					Dao.save(com);
					return "已公开&"+id;				
				}else if("false".equals(con[1])){				
					com.setDisplay(false);
					Dao.save(com);
					return "不公开&"+id;
				}else if("delete".equals(con[1])){
					Dao.delete(com);
					return "已删除&"+id;
				}
			}else{
				return "没找到&" + id;
			}
		}
		return html;
	}
		
}
