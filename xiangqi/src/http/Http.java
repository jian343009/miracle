package http;

import dao.Dao;

public class Http {
	
	public static String getHtml(String body){
		String html = 
			"<html>\n"+
				"\t<head>\n"+
					"\t\t<meta content=\"notranslate\" name=\"google\">\n"+
					"\t\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"+
					"\t\t<meta name='viewport' content='width=device-width, initial-scale=1'>\n"+
					"\t\t<title>少儿象棋后台管理</title>\n"+
					"\t\t<link rel=\"stylesheet\" href=\"http://main.miracle-cn.com/jquery.mobile-1.3.2/jquery.mobile-1.3.2.min.css\" />\n"+
			        "\t\t<script src=\"http://main.miracle-cn.com/jquery/jquery-1.11.1.min.js\"></script>\n"+
			        "\t\t<script src=\"http://main.miracle-cn.com/jquery.mobile-1.3.2/jquery.mobile-1.3.2.min.js\"></script>\n"+
				"\t</head>\n"+
				"\t<body>\n" +
					"\t\t<div data-role=\"controlgroup\" data-type=\"horizontal\" data-mini=\"true\">\n"+
					"\t\t<a href=\"/rate\" data-role=\"button\" data-icon=\"\" data-theme=\"b\" rel=\"external\"  data-ajax=\"false\">支付统计</a>\n"+
					"\t\t<a href=\"/record\" data-role=\"button\" data-icon=\"\" data-theme=\"b\" rel=\"external\"  data-ajax=\"false\">行为记录</a>\n"+
					"\t\t<a href=\"/comment\" data-role=\"button\" data-icon=\"\" data-theme=\"b\" rel=\"external\"  data-ajax=\"false\">审核评论</a>\n"+
					"\t</div>\n"+
					body+
				"\t</body >\n" +
			"</html>\n";
		return html;
	}
	public static String getManageHtml(String body){
		String html = 
			"<html>\n"+
				"\t<head>\n"+
					"\t\t<meta content=\"notranslate\" name=\"google\">\n"+
					"\t\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"+
					"\t\t<meta name='viewport' content='width=device-width, initial-scale=1'>\n"+
					"\t\t<title>少儿象棋后台管理</title>\n"+
					"\t\t<link rel=\"stylesheet\" href=\"http://main.miracle-cn.com/jquery.mobile-1.3.2/jquery.mobile-1.3.2.min.css\" />\n"+
			        "\t\t<script src=\"http://main.miracle-cn.com/jquery/jquery-1.11.1.min.js\"></script>\n"+
			        "\t\t<script src=\"http://main.miracle-cn.com/jquery.mobile-1.3.2/jquery.mobile-1.3.2.min.js\"></script>\n"+
				"\t</head>\n"+
				"\t<body>\n" +
					"\t\t<div data-role=\"controlgroup\" data-type=\"horizontal\" data-mini=\"true\">\n"+
					"\t\t<a href=\"/manage_device\" data-role=\"button\" data-icon=\"\" data-theme=\"b\" rel=\"external\"  data-ajax=\"false\">device</a>\n"+
					"\t\t<a href=\"/basedata\" data-role=\"button\" data-icon=\"\" data-theme=\"b\" rel=\"external\"  data-ajax=\"false\">baseDate</a>\n"+
					"\t\t<a href=\"/channels\" data-role=\"button\" data-icon=\"\" data-theme=\"b\" rel=\"external\"  data-ajax=\"false\">渠道信息</a>\n"+
					"\t</div>\n"+
					body+
				"\t</body >\n" +
			"</html>\n";
		return html;
	}
	    
}
