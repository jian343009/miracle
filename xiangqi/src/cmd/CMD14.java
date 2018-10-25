package cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import dao.Dao;
import dao.Data;
import data.BaseData;
import data.Device;
import main.Global;
/**
 * 获取支付价格
 * @author 周超
 */
public class CMD14 implements ICMD{
	private static final Logger log = Logger.getLogger(CMD14.class.getName());
	@Override
	public ChannelBuffer getBytes(int code, ChannelBuffer receive) {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(code);
		
		String name = Global.readUTF(receive);
		int deviceID = receive.readInt();
		String channelName = Global.readUTF(receive);
		int lesson = receive.readInt();//支付多课为total
		
		
		log.info("name:"+name+",ID:"+deviceID+",channel:"+channelName+",lesson:"+lesson);
		
		Device device = Dao.getDeviceExist(deviceID, "");
		if("leds".equals(channelName)) {
			channelName = "乐视电视";
		}else if("appl".equals(channelName)) {
			channelName = "苹果平台";
		}else if("hwei".equals(channelName)) {
			channelName = "华为平台";
		}else{
			channelName = "其它平台";
		}
		String content = BaseData.getContent(channelName);
		if(device == null){
			return reBuf(buf, 2, "用户ID有误");
		}if(content == null || content.isEmpty()){
			return reBuf(buf, 2, "未知的渠道");
		}
		
		Data data = Data.fromMap(content);
		//{价格:{1:12,2:13,,,,折扣:95},内容:{1:"",2"",,,,}}
		int price = 0;
		String 单课支付提示="";
		if("单课".equals(name)){
			if(lesson > 16 || lesson <2){
				return reBuf(buf, 2, "未知的课程");
			}else if((device.getBuyState() & (1 << lesson)) > 0){
				return reBuf(buf, 3, "该课已解锁");
			}
			单课支付提示 = data.get("内容").get(lesson).asString();
			price = data.get("价格").get(lesson).asInt();
		}else if("多课".equals(name)){
			price = get多课价(data,device);
		}else{
			return reBuf(buf, 2, "未知功能");
		}
		//红包抵扣
		String 红包抵扣 = "";
		if(device.getReward() != null && device.getReward().contains("未使用")){
			int 红包 = 0; Data dat = Data.fromMap(device.getReward());
			for(int les:new int[]{1,2}){
				if("未使用".equals(dat.get(les).get("状态").asString())){
					红包 += dat.get(les).get("金额").asInt();
				}
			}
			if(红包>0) {
				红包抵扣 ="(其中红包抵扣"+红包+"元)";
			}
			price -= 红包;
		}
		String payMsg="";
		if("单课".equals(name) && 单课支付提示.contains("#")) {
			payMsg=单课支付提示.split("#")[0]+price+"元"+红包抵扣+单课支付提示.split("#")[1];
		}else if("多课".equals(name)) {
			List<Integer> list = new ArrayList<Integer>();
			for(int i= 2; i<=16;i++){
				int les = 1 << i;
				if((device.getBuyState() & les) == 0){//取出所有未购买的课程
					list.add(i);
				}
			}
			String 未解锁课程 = list.toString().replace("[", "").replace("]", "");
			payMsg = "花"+price+"元"+红包抵扣+"解锁"+未解锁课程+"课,共"+list.size()+"课。";
		}
		String payChannel = BaseData.getContent(BaseData.可用支付方式);
		payChannel = Data.fromMap(payChannel).get(channelName).asString();
		//把价格存在device里，支付回调时用于比对
		device.setMoney(price);
		Dao.save(device);
		log.info("price:"+price);
		buf.writeByte(1);
		buf.writeBytes(Global.getUTF(payChannel));
		buf.writeInt(price);
		buf.writeBytes(Global.getUTF(payMsg));//提示信息
		return buf;
	}
	//快来返回buffer方法
	private ChannelBuffer reBuf(ChannelBuffer buf,int code,String msg){
		buf.writeByte(code);
		buf.writeBytes(Global.getUTF(msg));
		return buf;
	}
	
	private static int get多课价(Data data,Device device){
		int total = 0;
		for(int i= 2; i<=16;i++){
			int lesson = 1 << i;
			if((device.getBuyState() & lesson) == 0){//取出所有未购买的课程
				total += data.get("价格").get(i).asInt();
			}
		}
		if(total == 0){//只要total不为0，渠道就一定不为空
			return 0;
		}else{
			int 折扣 = data.get("价格").get("折扣").asInt();
			return Math.round(((float)total*折扣)/100);//四舍五入
		}
	}
}
