package cmd;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import dao.Dao;
import data.BaseData;
import data.Channels;
import data.Device;
import main.Global;
/**
 * 获取支付价格
 * @author 周超
 */
public class CMD14 implements ICMD{
	private static final Logger log = Logger.getLogger(CMD14.class.getName());
	@Override
	public ChannelBuffer getBytes(int code, ChannelBuffer data) {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(code);
		
		String name = Global.readUTF(data);
		int deviceID = data.readInt();
		String channel = Global.readUTF(data);
		int lesson = data.readInt();//支付多课为total
		
		Device device = Dao.getDeviceExist(deviceID, "");
		List<String> list = Arrays.asList(BaseData.全部渠道);
		Channels chann = Channels.getChannels(channel);
		if(device == null){
			return reBuf(buf, 2, "用户ID有误");
		}else if(chann == null){
			return reBuf(buf, 2, "未知的渠道信息");
		}
		int price = 0;
		String payChannel = BaseData.getContent(BaseData.可用支付方式);
		
		if("单课".equals(name)){
			if(lesson > 16 || lesson <2){
				return reBuf(buf, 2, "未知的课程");
			}
			price = get单课价(chann, lesson);
		}else if("多课".equals(name)){
			int total = 0;
			for(int i= 2; i<=16;i++){
				lesson = 1 << i;
				if((device.getBuyState() & lesson) == 0){
					total += get单课价(chann, i);
				}
			}
			int 折扣 = Channels.getChannels(channel).getDiscount();
			price = Math.round(((float)total*折扣)/10);//四舍五入
			log.info("total="+total+",折扣="+折扣+",price="+price);
		}else{
			return reBuf(buf, 2, "未知功能");
		}
		buf.writeByte(1);
		buf.writeBytes(Global.getUTF(payChannel));
		buf.writeInt(price);
		return buf;
	}
	private ChannelBuffer reBuf(ChannelBuffer buf,int code,String msg){
		buf.writeByte(code);
		buf.writeBytes(Global.getUTF(msg));
		return buf;
	}
	private int get单课价(Channels chann,int lesson){
		int price = 0;
		try {
			Field field = chann.getClass().getDeclaredField("price" + lesson);
			field.setAccessible(true);//可转私有属性
			Integer 单课价 = (Integer) field.get(chann);//通过反射取属性值
			price = chann.getBaseprice() + (单课价 == null ? 0 : 单课价);
		} catch (Exception e) {
			log.warning(e.getClass()+","+e.getMessage());
		}
		return price;
	}
}
