package http;

import dao.Dao;

public class Http {
	
	public static String getHtml(String body){
		String html = 
			"<html>"+
				"<head>"+
					"<meta content=\"notranslate\" name=\"google\">"+
					"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"+
					"<meta name='viewport' content='width=device-width, initial-scale=1'>"+
					"<title>少儿象棋后台管理</title>"+
					"<link rel=\"stylesheet\" href=\"http://main.miracle-cn.com/jquery.mobile-1.3.2/jquery.mobile-1.3.2.min.css\" />"+
			        "<script src=\"http://main.miracle-cn.com/jquery/jquery-1.11.1.min.js\"></script>"+
			        "<script src=\"http://main.miracle-cn.com/jquery.mobile-1.3.2/jquery.mobile-1.3.2.min.js\"></script>"+
				"</head>"+
				"<body>" +
					"<div data-role=\"controlgroup\" data-type=\"horizontal\" data-mini=\"true\">"+
						"<a href=\"/rate\" data-role=\"button\" data-icon=\"\" data-theme=\"b\" rel=\"external\"  data-ajax=\"false\">支付统计</a>"+
						"<a href=\"/record\" data-role=\"button\" data-icon=\"\" data-theme=\"b\" rel=\"external\"  data-ajax=\"false\">行为记录</a>"+
						"<a href=\"/comment\" data-role=\"button\" data-icon=\"\" data-theme=\"b\" rel=\"external\"  data-ajax=\"false\">审核评论</a>"+
					"</div>"+
					body+
				"</body >" +
			"</html>";
		return html;
	}
	    
}
