package dao;
import java.util.*;
import java.util.Map.Entry;

import json.*;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import main.Global;


public class Data {
	private static final Logger log = Logger.getLogger(Data.class);
	
	private Object value = null;
	private ChannelBuffer buf = null;
	
	public Data(){}
	/**
	 * 从字符串初始化json数据 或 List数据
	 * @param str
	 * @return
	 */
	public static Data fromString(String str){
		if(str == null){
			
		}else if(str.startsWith("{") && str.endsWith("}")){
			return Data.fromMap(str);
		}else if(str.startsWith("[") && str.endsWith("]")){
			return Data.fromArray(str);
		}
		return Data.fromValue(str);
	}
	/**
	 * 从字符串初始化json数据
	 * @param map json字符串
	 * @return
	 */
	public static Data fromMap(String map){
		Data data = new Data();
		try{
			data.value = JSONObject.fromObject(map);
		}catch(Exception e){
			log.warn(map + "转换JSONObject失败"+e.getLocalizedMessage());
		}
		return data;
	}
	/**
	 * 从字符串初始化array数据
	 * @param arr array字符串
	 * @return
	 */
	public static Data fromArray(String arr){
		Data data = new Data();
		try{
			data.value = JSONArray.fromObject(arr);
		}catch(Exception e){
			log.warn(arr + "转换JSONArray失败");
		}
		return data;
	}
	/**
	 * 从实例初始化Data
	 * @param obj
	 * @return
	 */
	public static Data fromValue(Object obj){
		Data data = new Data();
		data.value = obj;
		return data;
	}
	/**
	 * 从二进制数据初始化Data
	 * @param buf
	 * @return
	 */
	public static Data fromBuf(ChannelBuffer buf){
		Data data = new Data();
		data.value = data.read(buf);
		return data;
	}
	/**
	 * 解析二进制数据
	 * @param buf
	 * @return
	 */
	private Object read(ChannelBuffer buf){
		int type = buf.readByte();//log.info("type:"+type);
		if(type ==1){
//			log.info("type:byte");
			int num = buf.readByte();
			return num;
		}else if(type ==2){
//			log.info("type:short");
			int num = buf.readShort();
			return num;
		}else if(type ==3){
//			log.info("type:int");
			int num = buf.readInt();
			return num;
		}else if(type ==4){
//			log.info("type:UTF");
			String str = Global.readUTF(buf);//.replace(",", "\\,").replace("{", "\\{").replace("}", "\\}").replace("[", "\\[").replace("]", "\\]").replace("\"", "\\\"");
			return str;
		}else if(type ==5){
//			log.info("type:double");
			double num = buf.readDouble();
			return num;
		}else if(type ==6){
//			log.info("type:数组");
			return this.readArray(buf);
		}else if(type ==7){
//			log.info("type:bool");
			boolean b = buf.readByte() ==1 ? true : false;
			return b;
		}else if(type ==8){
//			log.info("type:null");
			return null;
		}else if(type ==9){
//			log.info("type:Map");
			return this.readMap(buf);
		}
		return null;
	}
	private JSONArray readArray(ChannelBuffer buf){
		JSONArray array = new JSONArray();
		int size = (Integer) this.read(buf);//log.info("size:"+size);
		for(int i=0;i<size;i++){
			array.add(this.read(buf));
		}
		return array;
	}
	private JSONObject readMap(ChannelBuffer buf){
		JSONObject map = new JSONObject();
		int size = (Integer) this.read(buf);
		for(int i=0;i<size;i++){
			map.put(this.read(buf), this.read(buf));
		}
		return map;
	}
	public Object _Value(){//获取
		return this.value;
	}
	public void setValue(Object val){
		this.value = val;
	}
	public void clear(){
		if(this.value instanceof JSONObject){
			((JSONObject) this.value).clear();
		}else if(this.value instanceof JSONArray){
			((JSONArray) this.value).clear();
		}
	}
	public boolean containsKey(Object key){
		if(this.value instanceof JSONObject){
			return ((JSONObject) this.value).containsKey(key);
		}
		return false;
	}
	public boolean containsValue(Object val){
		if(this.value instanceof JSONObject){
			return ((JSONObject) this.value).containsValue(val);
		}else if(this.value instanceof JSONArray){
			return ((JSONArray) this.value).contains(val);
		}
		return false;
	}
	/**
	 * 获取json字项
	 * @param key
	 * @return
	 */
	public Data get(Object key){
		if(value instanceof Map){
			return Data.fromValue(((Map)value).get(key));
		}else if(value instanceof JSONObject){
			return Data.fromValue(((JSONObject)value).get(key));
		}
		
		return Data.fromValue(null);
	}
	/**
	 * 获取json的Map字项，若不存在则创建
	 * @param key
	 * @return
	 */
	public Data getMap(Object key){
		if(this.value == null){
			this.value = new JSONObject();
		}
		if(this.value instanceof JSONObject){
			Object val = ((JSONObject) this.value).get(key);
			if(val == null){
				val = new JSONObject();
				((JSONObject) this.value).put(key, val);
			}
			return Data.fromValue(((JSONObject) this.value).get(key));
		}
		return Data.fromValue(null);
	}
	/**
	 * 获取json的List字项，若不存在则创建
	 * @param key
	 * @return
	 */
	public Data getList(Object key){
		if(this.value == null){
			this.value = new JSONObject();
		}
		if(this.value instanceof JSONObject){
			Object val = ((JSONObject) this.value).get(key);
			if(val == null){
				val = new JSONArray();
				((JSONObject) this.value).put(key, val);
			}
			return Data.fromValue(((JSONObject) this.value).get(key));
		}
		return Data.fromValue(null);
	}
	/**
	 * 获取List的字项
	 * @param index
	 * @return
	 */
	public Data get(int index){
		if(value instanceof Map){
			return Data.fromValue(((Map)value).get(index));
		}else if(value instanceof List){
			List list = (List)value;
			if(index < list.size()){
				return Data.fromValue(list.get(index));
			}
		}else if(value instanceof JSONArray){
			JSONArray array = (JSONArray)value;
			if(index < array.size()){
				return Data.fromValue(array.get(index));
			}
		}else if(this.value instanceof JSONObject){
			return Data.fromValue(((JSONObject) this.value).get(index));
		}
		
		return Data.fromValue(null);
	}
	/**
	 * 获取List的Map字项，若不存在则创建
	 * @param index
	 * @return
	 */
	public Data getMap(int index){
		if(this.value == null){
			this.value = new JSONArray();
		}
		if(this.value instanceof JSONArray){
			while(index >= ((JSONArray) this.value).size()){
				((JSONArray) this.value).add(null);
			}
			Object obj = ((JSONArray) this.value).get(index);
			if(obj == null || obj instanceof JSONNull){
				obj = new JSONObject();
				((JSONArray) this.value).set(index, obj);
			}
			return Data.fromValue(((JSONArray) this.value).get(index));
		}else if(this.value instanceof JSONObject){
			Object val = ((JSONObject) this.value).get(index);
			if(val == null){
				val = new JSONObject();
				((JSONObject) this.value).put(index, val);
			}
			return Data.fromValue(((JSONObject) this.value).get(index));
		}
		return Data.fromValue(null);
	}
	/**
	 * 获取List的List字项，若不存在则创建
	 * @param index
	 * @return
	 */
	public Data getList(int index){
		if(this.value == null){
			this.value = new JSONArray();
		}
		if(this.value instanceof JSONArray){
			while(index >= ((JSONArray) this.value).size()){
				((JSONArray) this.value).add(null);
			}
			Object obj = ((JSONArray) this.value).get(index);
			if(obj == null || obj instanceof JSONNull){
				obj = new JSONArray();
				((JSONArray) this.value).set(index, obj);
			}
			return Data.fromValue(((JSONArray) this.value).get(index));
		}else if(this.value instanceof JSONObject){
			Object val = ((JSONObject) this.value).get(index);
			if(val == null){
				val = new JSONArray();
				((JSONObject) this.value).put(index, val);
			}
			return Data.fromValue(((JSONObject) this.value).get(index));
		}
		return Data.fromValue(null);
	}
	/**
	 * 为List添加Map字项
	 * @return
	 */
	public Data addMap(){
		if(this.value == null){
			this.value = new JSONArray();
		}
		if(this.value instanceof JSONArray){
			int index = ((JSONArray) this.value).size();
			Object obj = new JSONObject();
			((JSONArray) this.value).add(obj);
			return Data.fromValue(((JSONArray) this.value).get(index));
		}
		return Data.fromValue(null);
	}
	/**
	 * 为List添加List字项
	 * @return
	 */
	public Data addList(){
		if(this.value == null){
			this.value = new JSONArray();
		}
		if(this.value instanceof JSONArray){
			int index = ((JSONArray) this.value).size();
			Object obj = new JSONArray();
			((JSONArray) this.value).add(obj);
			return Data.fromValue(((JSONArray) this.value).get(index));
		}
		return Data.fromValue(null);
	}
	/**
	 * 获取json或List的字项数量
	 * @return
	 */
	public int size(){
		if(this.value instanceof List){
			return ((List) this.value).size();
		}else if(this.value instanceof Map){
			return ((Map) this.value).size();
		}else if(this.value instanceof JSONObject){
			return ((JSONObject) this.value).size();
		}else if(this.value instanceof JSONArray){
			return ((JSONArray) this.value).size();
		}else if(this.value != null){
			return 1;
		}
		
		return 0;
	}
	/**
	 * 设置json字项和值
	 * @param key
	 * @param value
	 * @return
	 */
	public Data put(Object key, Object value){
		if(this.value == null){
			this.value = new JSONObject();
		}
		if(this.value instanceof JSONObject){
			((JSONObject) this.value).put(key, value);
		}
		return this;
	}
	/**
	 * 删除json字项
	 * @param key
	 * @return
	 */
	public Data remove(String key){
		if(this.value == null){
			this.value = new JSONObject();
		}
		if(this.value instanceof JSONObject){
			return Data.fromValue(((JSONObject) this.value).remove(key));
		}
		return Data.fromValue(null);
	}
	/**
	 * 添加List字项和值
	 * @param value
	 * @return
	 */
	public Data add(Object value){
		if(this.value == null){
			this.value = new JSONArray();
		}
		if(this.value instanceof JSONArray){
			((JSONArray) this.value).add(value);
		}
		return this;
	}
	/**
	 * 添加List字项和值
	 * @param value
	 * @return
	 */
	public Data remove(int index){
		if(this.value == null){
			this.value = new JSONArray();
		}
		if(this.value instanceof JSONArray){
			return Data.fromValue(((JSONArray) this.value).remove(index));
		}
		return Data.fromValue(null);
	}
	public boolean removeValue(Object val){
		if(this.value instanceof JSONArray){
			return ((JSONArray) this.value).remove(val);
		}
		return false;
	}
	
	/**
	 * 修改List字项和值
	 * @param index
	 * @param value
	 * @return
	 */
	public Data set(int index, Object value){
		if(this.value == null){
			this.value = new JSONArray();
		}
		if(this.value instanceof JSONArray){
			while(index >= ((JSONArray) this.value).size()){
				((JSONArray) this.value).add(null);
			}
			((JSONArray) this.value).set(index, value);
		}
		return this;
	}
	/**
	 * 获取字符串值，不存在则默认返回空字符串（""）
	 * @return
	 */
	public String asString(){
		return asString("");
	}
	/**
	 * 获取字符串值
	 * @param def 不存在则返回传入值
	 * @return
	 */
	public String asString(String def){
		if(value != null){
			return value.toString();
		}
		
		return def;
	}
	public int asInt(){
		return asInt(0);
	}
	public int asInt(int def){
		if(value instanceof Integer){
			return (Integer)value;
		}else if(value instanceof String){
			return Global.getInt(value.toString());
		}
		
		return def;
	}
	public long asLong(){
		return asLong(0);
	}
	public long asLong(long def){
		if(value instanceof Long){
			return (Long)value;
		}else if(value instanceof Integer){
			return (Integer)value;
		}
		return def;
	}
	public double asDouble(){
		return asDouble(0.0);
	}
	public double asDouble(double def){
		if(value instanceof Double){
			return (Double)value;
		}else if(value instanceof String){
			return Global.getDouble(value.toString());
		}
		
		return def;
	}
	public boolean asBoolean(){
		return asBoolean(false);
	}
	public boolean asBoolean(boolean def){
		if(value instanceof Boolean){
			return (Boolean)value;
		}else if(value instanceof String){
			return ((String) value).equalsIgnoreCase("true");
		}
		
		return def;
	}
	/**
	 * 获取二进制值
	 * @return
	 */
	public ChannelBuffer toBuf(){
		if(buf == null){
			buf = ChannelBuffers.dynamicBuffer();
			this.write(value);
		}
		
		return buf;
	}
	/**
	 * 组织二进制值
	 * @param obj
	 */
	private void write(Object obj){
		//log.info(obj);
		if(obj == null || obj instanceof JSONNull){
			buf.writeByte(8);//log.info("null");
		}else if(obj instanceof Long){
			buf.writeByte(5);//log.info("Long");
			buf.writeDouble((Long) obj);
		}else if(obj instanceof Float){
			buf.writeByte(5);//log.info("Double");
			buf.writeDouble((Float) obj);
		}else if(obj instanceof Double){
			buf.writeByte(5);//log.info("Double");
			buf.writeDouble((Double) obj);
		}else if(obj instanceof Integer){
			int num = (Integer)obj;
			if(num > Short.MAX_VALUE || num < Short.MIN_VALUE){
				buf.writeByte(3);//log.info("Int");
				buf.writeInt(num);
			}else if(num > Byte.MAX_VALUE || num < Byte.MIN_VALUE){
				buf.writeByte(2);//log.info("Short");
				buf.writeShort(num);
			}else{
				buf.writeByte(1);//log.info("byte");
				buf.writeByte(num);
			}
		}else if(obj instanceof Boolean){
			buf.writeByte(7);//log.info("Boolean");
			buf.writeByte((Boolean)obj ? 1 :0);
		}else if(obj instanceof String){
			buf.writeByte(4);//log.info("String");
			buf.writeBytes(Global.getUTF(obj.toString()));
		}else if(obj instanceof JSONArray){
			buf.writeByte(6);//log.info("Array");
			JSONArray array = (JSONArray) obj;
			this.write(array.size());//log.info("array.size():"+array.size());
			for(int m=0;m<array.size();m++){
				this.write(array.get(m));//log.info(m+":"+array.get(m));
			}
		}else if(obj instanceof List){
			buf.writeByte(6);//log.info("List");
			List list = (List) obj;
			this.write(list.size());//log.info("list.size():"+list.size());
			for(int m=0;m<list.size();m++){
				this.write(list.get(m));//log.info(m+":"+list.get(m));
			}
		}else if(obj instanceof JSONObject){
			buf.writeByte(9);//log.info("Map");
			JSONObject map = (JSONObject)obj;
			this.write(map.size());
			Iterator<Entry<Object, Object>> it = map.entrySet().iterator();
			while(it.hasNext()){
				Entry<Object,Object> en = it.next();
				this.write(en.getKey());
				this.write(en.getValue());
			}
		}else if(obj instanceof Map){
			buf.writeByte(9);//log.info("Map");
			Map<Object, Object> map = (Map<Object, Object>)obj;
			this.write(map.size());
			Iterator<Entry<Object, Object>> it = map.entrySet().iterator();
			while(it.hasNext()){
				Entry<Object,Object> en = it.next();
				this.write(en.getKey());
				this.write(en.getValue());
			}
		}
	}
	/**
	 * 获取字符串值
	 */
	public String toString(){
		return ""+this.value;
	}
}
