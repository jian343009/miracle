package data;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import dao.HSF;

public class BaseData {
	
	public static final String 微信网页支付地址 = "微信网页支付地址";
	public static final String 强制全部解锁 = "强制全部解锁";
	
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
}
