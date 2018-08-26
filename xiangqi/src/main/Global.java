package main;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.w3c.dom.Document;

import dao.Dao;
import data.Record;
import data.StepRecord;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public final class Global{
	private static final Logger log = Logger.getLogger(Global.class.getName());
	
	public static Record addRecord(int deviceID, String imei,String step,String info){
		Record record = new Record();
		record.setDeviceID(deviceID);
		record.setImei(imei);
		record.setStep(step);
		record.setInfo(info);
		record.setTimeStr(ServerTimer.getFullWithS());
		Dao.save(record);
		return record;
	}
	public static StepRecord addStep(int deviceID, String imei,String step,String info){
		StepRecord sr = new StepRecord();
		sr.setDeviceID(deviceID);
		sr.setImei(imei);
		sr.setStep(step);
		sr.setInfo(info);
		sr.setTimeStr(ServerTimer.getFullWithS());
		Dao.save(sr);
		return sr;
	}
	
	/*********************
	****** 常用方法 *******
	*********************/
	public static String readUTF(ChannelBuffer buf)
	{
		return buf.readBytes(buf.readShort()).toString(Charset.forName("utf-8"));
	}
	
//	public static ChannelBuffer getUTFBytes(String str)
//	{
//		if(str == null) str = "";
//		return ChannelBuffers.copiedBuffer(str, Charset.forName("UTF-8"));
//	}
	public static ChannelBuffer getUTF(String str)
	{
		if(str == null) str = "";
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		ChannelBuffer strBuf = ChannelBuffers.copiedBuffer(str, Charset.forName("UTF-8"));
		buf.writeShort(strBuf.readableBytes());
		buf.writeBytes(strBuf);
		return buf;
	}
	public static String md5(String str) {
		String result = "";
		try {
			//通过MD5计算出签名
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] b = messageDigest.digest(str.getBytes("utf-8"));
			
			String HEX_CHARS = "0123456789abcdef";
			StringBuffer sb = new StringBuffer();
			for (byte aB : b) {
				sb.append(HEX_CHARS.charAt(aB >>> 4 & 0x0F));
				sb.append(HEX_CHARS.charAt(aB & 0x0F));
			}
			result = sb.toString();
		} catch (Exception e) {
			log.warning("md5签名失败:"+str);
		}
		return result;
	}
	public static String BASE64Encod(String s) {   
		if (s == null) return "";   
		return (new BASE64Encoder()).encode( s.getBytes() );   
	}   
		  
	// 将 BASE64 编码的字符串 s 进行解码   
	public static String DecodBASE64(String s) {   
		if (s == null) return "";   
		BASE64Decoder decoder = new BASE64Decoder();   
		try {   
			byte[] b = decoder.decodeBuffer(s);   
			return new String(b);   
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}   
	}
	public static HashMap<String, String> decodeUrlParam(String param){
		HashMap<String, String> map = new HashMap<String, String>();
		String[] params = param.split("[&]");
		for(int m=0;m<params.length;m++){
			if(!params[m].isEmpty()){
				String kv = params[m];
				int pos = kv.indexOf("=");
				if(pos >0){
					map.put(kv.substring(0, pos), kv.substring(pos+1));
				}
			}
		}
		return map;
	}
	public static HashMap<String, String> decodeXML(String xmlStr){
		HashMap<String, String> map = new HashMap<String, String>();
		xmlStr = xmlStr.replace("<xml>", "").replace("</xml>", "");
		log.info(xmlStr);
		xmlStr = xmlStr.replace("<![CDATA[", "").replace("]]>", "");
		log.info(xmlStr);
		int pos = xmlStr.indexOf("<");
		while(pos != -1){
			int pre = pos;
			int suf = pos = xmlStr.indexOf(">", pos+1);
			if(pos <0){
				return map;
			}
			String key = xmlStr.substring(pre+1, suf);
			String last = "</"+key+">";
			pos = xmlStr.indexOf(last, pos);
			if(pos <0){
				return map;
			}
			String value = xmlStr.substring(suf+1, pos);
			map.put(key, value);
			pos += last.length();
			pos = xmlStr.indexOf("<", pos);
		}
		return map;
	}
	public static String GetSortString(Map<String, String> map){
		return GetSortString(map, "&");
	}
	public static String GetSortString(Map<String, String> map, String mark)
	{
		List<Map.Entry<String, String>> keyValues =
			    new ArrayList<Map.Entry<String, String>>(map.entrySet());

		Collections.sort(keyValues, new Comparator<Map.Entry<String, String>>() {   
		    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {      
		        //return (o2.getValue() - o1.getValue()); 
		        return o1.getKey().compareTo(o2.getKey());
		    }
		});
		
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<keyValues.size();i++) {	
			if(i ==0)
			{
				sb.append(keyValues.get(i).getKey()+ "=" + keyValues.get(i).getValue());
			}
			else
			{
				sb.append(mark + keyValues.get(i).getKey()+ "=" + keyValues.get(i).getValue());
			}
		}
		
		return sb.toString();
		
	}
	public static String GetSortQueryToLowerString(Map<String, String> map)
	{
		List<Map.Entry<String, String>> keyValues =
			    new ArrayList<Map.Entry<String, String>>(map.entrySet());

		Collections.sort(keyValues, new Comparator<Map.Entry<String, String>>() {   
		    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {      
		        //return (o2.getValue() - o1.getValue()); 
		        return (o1.getKey()).toString().compareTo(o2.getKey());
		    }
		}); 
		
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<keyValues.size();i++) {	
			if(keyValues.get(i).getValue()==null)
			{
				sb.append(keyValues.get(i).getKey()+ "= " );
			}
			else
			{
				sb.append(keyValues.get(i).getKey()+ "=" + keyValues.get(i).getValue().toLowerCase());
			}
			sb.append("&");
		}
		
		return sb.substring(0, sb.length()-1);
		
	}
	public static String Decrypt(String base64Data,String base64Key)
	{ 
	     try 
	     { 
	    	 byte[] data = Base64.decodeBase64(base64Data);
	    	 byte[] key = Base64.decodeBase64(base64Key); 
			 byte[] iv = Arrays.copyOf(key, 16);
			 Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding"); 
	         SecretKeySpec keyspec = new SecretKeySpec(key, "AES"); 
	         IvParameterSpec ivspec = new IvParameterSpec(iv); 
	         cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec); 
	         ArrayList<Byte> arr = new ArrayList<Byte>();
	         byte[] original = cipher.doFinal(data); 
	         for(int i=0;i<original.length;i++)
	         {
	        	 byte val = original[i];
	        	 if(val != 0)
	        	 {
	        		 arr.add(val);
	        	 }
	         }
	         
	         Object[] arrObj = arr.toArray();
	         byte[] arrByte  = new byte[arrObj.length];
	         for(int i=0;i<arrObj.length;i++)
	         {
	        	 arrByte[i] = Byte.parseByte(arrObj[i].toString());
	         }
	         
	         String originalString = new String(arrByte, Charset.forName("utf8")); 
	         return originalString; 
	     }catch (Exception e) 
	     { 
	         e.printStackTrace(); 
	         return ""; 
	     }
	}
	public static String getJSONString(String str)
	{
		String json = str;
		try{
			JSONObject obj = JSONObject.fromObject(str);
			json = obj.toString();
		}catch(Exception e)
		{
			log.warning("Exception:"+e.getMessage());
		}
		return json;
	}
	public static Document xmlParser(String xmlStr){
		Document doc = null;
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			ByteArrayInputStream in = new ByteArrayInputStream(xmlStr.getBytes());
			doc = builder.parse(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}
	public static int[] splitArray(String str){
		return splitArray(str,",");
	}
	public static int[] splitArray(String str, String mark){
		if(str == null || str.isEmpty()){
			return new int[0];
		}
		String[] array = str.split(mark);
		int[] result = new int[array.length];
		for(int m=0;m<array.length;m++)
		{
			result[m] = Global.getInt(array[m]);;
		}
		return result;
	}
	public static String concatArray(int[] array){
		return concatArray(array,",");
	}
	public static String concatArray(int[] array, String mark){
		String str = "";
		for(int m=0;m<array.length;m++)
		{
			if(str.isEmpty())
			{
				str += ""+array[m];
			}else
			{
				str += mark+array[m];
			}
		}
		return str;
	}
	public static int[] getArray(int[] array,int len)
	{
		if(len <= array.length){
			return array;
		}
		int[] result = new int[len];
		for(int m=0;m<array.length;m++)
		{
			result[m] = array[m];
		}
		return result;
	}
	public static String[] splitStringArray(String str){
		return splitStringArray(str,",");
	}
	public static String[] splitStringArray(String str, String mark){
		if(str == null || str.isEmpty()){
			return new String[0];
		}
		String[] result = str.split(mark);
		return result;
	}
	public static String getArrayValue(String str, int index){
		if(str == null || str.isEmpty()){
			return "";
		}
		String result = "";
		String[] strs = str.split(",");
		if(index < strs.length){
			result = strs[index];
		}
		return result;
	}
	public static String setArrayValue(String str, int index, String value){
		if(str == null){
			str = "";
		}
		String[] strs = str.split(",");
		if(index >= strs.length){
			String[] tmp = new String[index +1];
			for(int i=0;i<strs.length;i++){
				tmp[i] = strs[i];
			}
			strs = tmp;
		}
		strs[index] = value;
		return concatStringArray(strs, ",");
	}
	public static String concatStringArray(String[] array, String mark){
		String str = "";
		for(int m=0;m<array.length;m++)
		{
			if(m==0)
			{
				str += array[m];
			}else
			{
				str += mark+array[m];
			}
		}
		return str;
	}
	
	public static int getInt(String str){
		int num=0;
		if(str != null && !str.isEmpty()){
			try{
				num = Integer.parseInt(str.trim());
			}catch(Exception e){
				log.info(e.getMessage());
			}
		}
		return num;
	}
	public static double getDouble(String str){
		double num = 0.0;
		if(str != null && !str.isEmpty()){
			try{
				num = Double.parseDouble(str);
			}catch(Exception e){
				log.info(e.getMessage());
			}
		}
		return num;
	}
	/**
	 * 
	 * @param max
	 * @return 0<=int<max
	 */
	public static int getRandom(int max){
		return (int) (Math.random() * max);
	}
	/**
	 * 
	 * @param min
	 * @param max
	 * @return min <= int < max
	 */
	public static int getRandom(int min, int max){
		return (int) (Math.random()*(max - min) + min);
	}
}
