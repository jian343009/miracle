package cmd;

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
		
		Device device = Dao.getDeviceExist(deviceID, "");
		String content = BaseData.getContent(channelName+"&价格信息");
		if(device == null){
			return reBuf(buf, 2, "用户ID有误");
		}if(content == null || content.isEmpty()){
			return reBuf(buf, 2, "未知的渠道");
		}
		
		Data data = Data.fromMap(content);
		//{价格:{1:12,2:13,,,,折扣:95},信息:{1:"",2"",,,,}}
		int price = 0;
		String payChannel = BaseData.getContent(BaseData.可用支付方式);
		
		if("单课".equals(name)){
			if(lesson > 16 || lesson <2){
				return reBuf(buf, 2, "未知的课程");
			}else if((device.getBuyState() & (1 << lesson)) > 0){
				return reBuf(buf, 3, "该课已解锁");
			}
			price = data.get("价格").get(lesson).asInt();
		}else if("多课".equals(name)){
			price = get多课价(data,device);
		}else{
			return reBuf(buf, 2, "未知功能");
		}
		//红包抵扣
		if(device.getReward() != null && device.getReward().contains("未使用")){
			int 红包 = 0; Data dat = Data.fromMap(device.getReward());
			for(int les:new int[]{1,2}){
				if("未使用".equals(dat.get(les).get("状态").asString())){
					红包 += dat.get(les).get("金额").asInt();
				}
			}
			price -= 红包;
		}
		//把价格存在device里，支付回调时用于比对
		device.setMoney(price);
		Dao.save(device);
		
		buf.writeByte(1);
		buf.writeBytes(Global.getUTF(payChannel));
		buf.writeBytes(Global.getUTF(""));//提示信息
		buf.writeInt(price);
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
				total += data.get("价格").get(lesson).asInt();
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
