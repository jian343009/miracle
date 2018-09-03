package dao;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import main.Global;
import main.ServerTimer;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import data.*;

public class Dao {

	private static final Logger log = Logger.getLogger(Dao.class.getName());
	
	public static List<Record> getRecentRecord(String imei){
		Session ss = HSF.getSession();
		Criteria ct = ss.createCriteria(Record.class);
		if(!imei.isEmpty()){
			ct.add(Restrictions.like("imei", imei));
		}
		ct.addOrder(Order.desc("id"));
		ct.setMaxResults(300);
		List<Record> list = ct.list();
		ss.close();
		return list;
	}
	public static List<BaseData> getAllBaseData(){
		Session ss = HSF.getSession();
		List<BaseData> list = ss.createCriteria(BaseData.class).addOrder(Order.desc("id")).list();
		ss.close();
		return list;
	}
	
	public static BaseData getBaseDataById(int id){
		BaseData bd = null;
		Session ss = HSF.getSession();
		List<BaseData> list = ss.createCriteria(BaseData.class).add(Restrictions.eq("id", id)).list();
		ss.close();
		if(list.size() >0){
			bd = list.get(0);
		}
		return bd;
	}
	@SuppressWarnings("unchecked")
	public static ChannelEveryday getChannelEverydayToday(String channel){
		ChannelEveryday ce = null;
		Session ss = HSF.getSession();
		List<ChannelEveryday> list = ss.createCriteria(ChannelEveryday.class).add(Restrictions.eq("channel", channel)).add(Restrictions.eq("day", ServerTimer.distOfDay())).setMaxResults(1).list();
		ss.close();
		if(list.size() >0){
			ce = list.get(0);
		}else{
			int today = ServerTimer.distOfDay();
			ce = new ChannelEveryday();
			ce.setChannel(channel);
			ce.setYesterday(getChannelNewDeviceYesterday(channel, today));
			ce.setDay(today);
			ce.setDayStr(ServerTimer.getYMD());
			Dao.save(ce);
		}
		return ce;
	}
	public static int getChannelNewDeviceYesterday(String channel, int today){
		int newDevice = 1;
		Session ss = HSF.getSession();
		Criteria ct = ss.createCriteria(ChannelEveryday.class);
		ct.add(Restrictions.eq("channel", channel));
		ct.add(Restrictions.eq("day", today -1));
		ct.setMaxResults(1);
		List<ChannelEveryday> list = ct.list();
		ss.close();
		if(list.size() >0){
			newDevice = list.get(0).getNewDevice();
		}
		return newDevice;
	}
	@SuppressWarnings("unchecked")
	public static List<ChannelEveryday> getChannelEverydayByChannel(String channel){
		List<ChannelEveryday> list = null;
		Session ss = HSF.getSession();
		Criteria ct = ss.createCriteria(ChannelEveryday.class);
		if(!channel.isEmpty()){
			ct.add(Restrictions.like("channel", channel));
		}
		list = ct.addOrder(Order.desc("id")).setMaxResults(300).list();
		ss.close();
		return list;
	}
	
	public static AliPay getAliPayByContent(String content){
		AliPay pay = null;
		Session ss = HSF.getSession();
		List<AliPay> list = ss.createCriteria(AliPay.class).add(Restrictions.eq("content", content)).setMaxResults(1).list();
		ss.close();
		if(list.size() >0){
			pay = list.get(0);
		}else{
			pay = new AliPay();
			pay.setFirstTime(ServerTimer.getFull());
			pay.setContent(content);
			Dao.save(pay);
		}
		return pay;
	}
//	public static LjPay getLjPayByContent(String content){
//		LjPay pay = null;
//		Session ss = HSF.getSession();
//		List<LjPay> list = ss.createCriteria(LjPay.class).add(Restrictions.eq("content", content)).setMaxResults(1).list();
//		ss.close();
//		if(list.size() >0){
//			pay = list.get(0);
//		}else{
//			pay = new LjPay();
//			pay.setFirstTime(ServerTimer.getFullWithS());
//			pay.setContent(content);
//			Dao.save(pay);
//		}
//		return pay;
//	}
	public static LjPay getLjPayByOrderID(String orderID){
		LjPay pay = null;
		Session ss = HSF.getSession();
		List<LjPay> list = ss.createCriteria(LjPay.class).add(Restrictions.eq("orderID", orderID)).setMaxResults(1).list();
		ss.close();
		if(list.size() >0){
			pay = list.get(0);
		}
		return pay;
	}
	public static String checkReceipt(String receipt, boolean sandbox)
	{
		JSONObject obj = new JSONObject();
		try{
			String urlStr = "https://buy.itunes.apple.com/verifyReceipt";
			if(sandbox)
			{
				urlStr = "https://sandbox.itunes.apple.com/verifyReceipt";
			}
            URL url = new URL(urlStr);  
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoOutput(true);  
            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());  
            String encodedReceipt = Global.BASE64Encod(receipt);
            out.write("{\"receipt-data\" : \""+encodedReceipt+"\"}");  
            out.flush();
            out.close();
            
            // 从服务器读取响应  
            InputStream inputStream = urlConnection.getInputStream();  
            String body = IOUtils.toString(inputStream,Charset.forName("gbk"));
            obj = JSONObject.fromObject(body);
            log.info(obj.toString());
        }catch(Exception e){
            log.warning("Exception:"+e.getMessage());
        }
		return obj.toString();
	}
	private static Count count = null;
	@SuppressWarnings("unchecked")
	public static Count getCountToday()
	{
		int today = ServerTimer.distOfDay();
		if(count == null || count.getDay() != today){
			Session ss = HSF.getSession();
			List<Count> list = ss.createCriteria(Count.class).add(Restrictions.eq("day", today)).list();
			ss.close();
			if(list.size() >0)
			{
				count = list.get(0);
			}else
			{
				count = new Count();
				count.setDay(today);
				count.setDayStr(ServerTimer.getYMD());
				Dao.save(count);
			}
		}
		return count;
	}
	private static Count monthCount = null;
	public static Count getCountMonth(){
		String month = ServerTimer.getYearMonth();
		if(monthCount == null || !monthCount.getDayStr().equals(month)){
			Session ss = HSF.getSession();
			List<Count> list = ss.createCriteria(Count.class).add(Restrictions.eq("dayStr", month)).list();
			ss.close();
			if(list.size() >0)
			{
				monthCount = list.get(0);
			}else
			{
				monthCount = new Count();
				monthCount.setDayStr(ServerTimer.getYearMonth());
				Dao.save(monthCount);
			}
		}
		return monthCount;
	}
	public static List<Count> getAllDayCount(){
		Session ss = HSF.getSession();
		List<Count> list = ss.createCriteria(Count.class).add(Restrictions.gt("day", 0)).addOrder(Order.desc("id")).setMaxResults(300).list();
		ss.close();
		return list;
	}
	public static List<Count> getAllMonthCount(){
		Session ss = HSF.getSession();
		List<Count> list = ss.createCriteria(Count.class).add(Restrictions.eq("day", 0)).addOrder(Order.desc("id")).setMaxResults(300).list();
		ss.close();
		return list;
	}
	@SuppressWarnings("unchecked")
	public static AppleProduct getWeiqiProductByLesson(int lesson)
	{
		AppleProduct product = null;
		Session ss = HSF.getSession();
		List<AppleProduct> list = ss.createCriteria(AppleProduct.class).add(Restrictions.eq("lesson", lesson)).list();
		ss.close();
		if(list.size() >0){
			product = list.get(0);
		}
		return product;
	}
	@SuppressWarnings("unchecked")
	public static AppleProduct getAppleProductByProductIdentifier(String pi)
	{
		AppleProduct product = null;
		Session ss = HSF.getSession();
		List<AppleProduct> list = ss.createCriteria(AppleProduct.class).add(Restrictions.eq("productIdentifier", pi)).list();
		ss.close();
		if(list.size() >0){
			product = list.get(0);
		}
		return product;
	}
	public static List<Device> getDevice(int id, String imei, int start, int num){
		Session ss = HSF.getSession();
		Criteria ct = ss.createCriteria(Device.class);
		if(id >0){
			ct.add(Restrictions.eq("id", id));
		}
		if(!imei.isEmpty()){
			ct.add(Restrictions.like("imei", imei));
		}
		ct.setFirstResult(start);
		ct.setMaxResults(num);
		List<Device> list = ct.list();
		ss.close();
		return list;
	}
	private static Hashtable<Integer, Device> dicIdDevice = new Hashtable<Integer, Device>();
	private static Hashtable<String, Device> dicImeiDevice = new Hashtable<String, Device>();
	private static ArrayList<Device> listDevice = new ArrayList<Device>();
	public static void addDevice(Device device){
		if(device != null){
			synchronized (listDevice) {
				dicIdDevice.put(device.getId(), device);
				dicImeiDevice.put(device.getImei(), device);
				listDevice.add(device);
				if(listDevice.size() >1000){
					Device wd = listDevice.get(0);
					dicIdDevice.remove(wd.getId());
					dicImeiDevice.remove(wd.getImei());
				}
			}
		}
	}
	public static void removeDevice(Device device){
		if(device != null){
			synchronized (listDevice) {
				dicIdDevice.remove(device.getId());
				dicImeiDevice.remove(device.getImei());
				listDevice.remove(device);
			}
		}
	}
	private static void orderDevice(Device device){
		if(device != null){
			synchronized (listDevice) {
				listDevice.remove(device);
				listDevice.add(device);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Device getDeviceExist(int id, String imei)
	{
		Device device = dicIdDevice.get(id);
		if(device == null){
			device = dicImeiDevice.get(imei);
		}
		if(device == null){
			Session ss = HSF.getSession();
			Criteria ct = ss.createCriteria(Device.class);
			if(id >0){
				ct.add(Restrictions.eq("id", id));
			}else if(!imei.isEmpty()){
				ct.add(Restrictions.eq("imei", imei));
			}
			List<Device> list = ct.setMaxResults(1).list();
			ss.close();
			if(list.size() >0)
			{
				device = list.get(0);
				Dao.addDevice(device);
			}
		}
		return device;
	}
	@SuppressWarnings("unchecked")
	public synchronized static Device getDevice(int id, String imei, String enter)
	{
		Device device = null;
		if(id >10){
			device = dicIdDevice.get(id);
			if(device == null){
				Session ss = HSF.getSession();
				List<Device> list = ss.createCriteria(Device.class).add(Restrictions.eq("id", id)).setMaxResults(1).list();
				ss.close();
				if(list.size() >0)
				{
					device = list.get(0);
					Dao.addDevice(device);
				}
			}else{
				Dao.orderDevice(device);
			}
		}else if(!imei.isEmpty()){
			device = dicImeiDevice.get(imei);
			if(device == null){
				Session ss = HSF.getSession();
				List<Device> list = ss.createCriteria(Device.class).add(Restrictions.eq("imei", imei)).setMaxResults(1).list();
				ss.close();
				if(list.size() >0)
				{
					device = list.get(0);
				}else{
					device = new Device();
					device.setImei(imei);
					device.setEnter(enter);
					device.setFirstTime(ServerTimer.getFull());
					Dao.save(device);
				}
				Dao.addDevice(device);
			}else{
				Dao.orderDevice(device);
			}
		}
		if(device == null){
			device = new Device();
			device.setEnter(enter);
			device.setFirstTime(ServerTimer.getFull());
			Dao.save(device);
			device.setImei("device"+device.getId());
			Dao.save(device);
			Dao.addDevice(device);
		}
		return device;
	}
	public static Device getDeviceByToken(String token)
	{
		if(token == null){
			return null;
		}
		Device device = null;
		Session ss = HSF.getSession();
		Criteria ct = ss.createCriteria(Device.class);
		ct.add(Restrictions.eq("token", token));
		List<Device> list = ct.setMaxResults(1).list();
		ss.close();
		if(list.size() >0)
		{
			device = list.get(0);
			Dao.addDevice(device);
		}
		return device;
	}
	@SuppressWarnings("unchecked")
	public static int getOriginalSize(String orig)
	{
		Session ss = HSF.getSession();
		Criteria ct = ss.createCriteria(ApplePay.class).add(Restrictions.eq("original", orig));
		List<ApplePay> list = ct.list();
		ss.close();
		return list.size();
	}
	@SuppressWarnings("unchecked")
	public static ApplePay getOriginalPay(String orig)
	{
		Session ss = HSF.getSession();
		Criteria ct = ss.createCriteria(ApplePay.class).add(Restrictions.eq("original", orig)).setMaxResults(1);
		List<ApplePay> list = ct.list();
		ss.close();
		
		ApplePay pay = null;
		if(list.size() >0){
			pay = list.get(0);
		}
		return pay;
	}

	public static Comment getCommentByID(int id){
		Session ss = HSF.getSession();
		Criteria ct = ss.createCriteria(Comment.class).add(Restrictions.eq("id", id)).setMaxResults(1);
		List<Comment> list = ct.list();
		ss.close();		
		if(list.size() >0){
			return list.get(0);
		}
		return null;	
	}
	/**
	 * 通过评论内容和device查找评论，防止重复评论相同内容。
	 */
	public static List<Comment> getCommentByContent(String content,int device){
		Session ss = HSF.getSession();
		Criteria ct = ss.createCriteria(Comment.class).add(Restrictions.eq("content", content)).add(Restrictions.eq("device", device));
		List<Comment> list = ct.list();
		ss.close();		
		if(list.size() >0){
			return list;
		}
		return null;	
	}
	/**
	 * 当前用户可以看到的评论。
	 */
	public static List<Comment> getComments(int deviceID,int page){
		Session ss = HSF.getSession();
		Criteria ct = ss.createCriteria(Comment.class).add(Restrictions.or(Restrictions.eq("display", true),Restrictions.eq("device", deviceID)))
				.setFirstResult(30*(page-1)).setMaxResults(30).addOrder(Order.desc("id"));
		List<Comment> list = ct.list();
		ss.close();
		return list;
	}
	/**
	 * 查找未审核评论。
	 */
	public static List<Comment> get审核Comment(boolean bool){
		Session ss = HSF.getSession();
		Criteria ct = ss.createCriteria(Comment.class).add(Restrictions.eq("checked", bool)).addOrder(Order.desc("id"));
		List<Comment> list = ct.list();
		ss.close();
		return list;
		
	}
	
	public static void save(Object obj){
	   Session s=HSF.getSession();
	   Transaction ts=s.beginTransaction();
	   try
	   {
		   s.saveOrUpdate(obj);
		   ts.commit();
	   }catch(Exception e)
	   {
		   log.warning(obj.toString()+" Exception !!!"+e.getMessage());
		   ts.rollback();
	   }finally{
		   s.close();
	   }
   }
	public static void delete(Object obj){
	   Session s=HSF.getSession();
	   Transaction ts=s.beginTransaction();
	   try
	   {
		   s.delete(obj);
		   ts.commit();
	   }catch(Exception e)
	   {
		   log.warning("Exception !!!"+e.getMessage());
		   ts.rollback();
	   }finally{
		   s.close();
	   }
	} 
}
