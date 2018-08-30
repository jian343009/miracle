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
			for(Comment co:list){
				sb.append("<tr>"+
					"<th>"+co.getId()+"</th>"+
					"<th>"+co.getUserName()+"</th>"+
					"<th>"+co.getTimeStr()+"</th>"+
					"<th>"+co.getUserAge()+"</th>"+
					"<th>"+co.getContent()+"</th>"+
					"<th><label id='"+co.getId()+"'>待审核</label></th>"+
					"<th><label id='"+co.getId()+"rrtrue' onclick='check(id)' style=\"color :green\">公开</label></th>"+
					"<th><label id='"+co.getId()+"rrfalse' onclick='check(id)' style=\"color :red\">不公开</label></th>"+
					"<th><label id='"+co.getId()+"rrdelete' onclick='check(id)' style=\"color :black\">删除</label></th>"+
					"<tr>");
			};
			
			String body="<script type=\"text/javascript\">"+
					"function check(id){ "+
			        "$.post('/comment',id,function(data){"+
			           "var arr=data.split(\"&\");"+
			           "$(\"#\" + arr[1]).text(arr[0]);"+
			           "$(\"#\" + arr[1] + \"rrtrue\").remove();"+
			           "$(\"#\" + arr[1] + \"rrfalse\").remove();"+
			           "$(\"#\" + arr[1] + \"rrdelete\").remove();"+
			        "} );}</script>";
			body +="<div align=\"center\" data-role=\"collapsible\">"+
		              "<h3 align=\"center\">评论审核</h3>" +
				"<div><table data-role=\"table\" id='t5' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >"+
		        "<thead>"+
		        "<tr>"+
		            "<th>id</th>"+
		            "<th>用户名</th>"+
		            "<th>时间</th>"+
		            " <th>年龄</th>"+
		            "<th width=\"360\">评论</th>"+
		            " <th>审核结果</th>"+
		            "<th>公开</th>"+
		            "<th>不公开</th>"+
		            "<th>删除</th>"+
		        "</tr>"+
		        "</thead>"+
		        "<tbody>";
			
			body +=sb.toString()+"</tbody></table></body></div></div>";			
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
