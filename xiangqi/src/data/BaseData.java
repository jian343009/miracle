package data;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import dao.HSF;

public class BaseData {
	
	public static final String 微信网页支付地址 = "微信网页支付地址";
	public static final String 强制全部解锁 = "强制全部解锁";
	public static final String 测试开关 = "测试开关";
	public static final String 可用支付方式 = "可用支付方式";//对应数据的name
	
	
	private int id;
	private String name = "";
	private String content = "";
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public static BaseData getByName(String name){
		BaseData bd = null;
		Session ss = HSF.getSession();
		List<BaseData> list = ss.createCriteria(BaseData.class).add(Restrictions.eq("name", name)).list();
		ss.close();
		if(list.size() >0){
			bd = list.get(0);
		}
		return bd;
	}
	public static String getContent(String name){
		BaseData bd = getByName(name);
		if(bd != null){
			return bd.getContent();
		}
		return "";
	}
	//已知的渠道
	public static final String[] 全部渠道={"oppo平台","vivo平台","华为平台","苹果商城",				
			"小米平台","三星平台","金立平台","联想平台","阿里应用","安智市场","机锋市场","百度商城",
			"360商城","pconline","搜狗助手","应用宝","应用汇","网页下载","软件互推","联通商城",				
			"乐视平台","乐视电视","视频教育","视频儿童"};

}
