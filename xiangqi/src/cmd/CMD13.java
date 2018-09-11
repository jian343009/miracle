package cmd;

import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import dao.Dao;
import dao.Data;
import data.Count;
import data.Device;
import main.Global;

/**
 * 红包功能
 *
 * @author 周超 2018/8/27
 */

public class CMD13 implements ICMD {
	private static final Logger log = Logger.getLogger(CMD13.class.getName());
	private ChannelBuffer buf = ChannelBuffers.dynamicBuffer();

	@Override
	public ChannelBuffer getBytes(int code, ChannelBuffer receive) {
		String name = Global.readUTF(receive);
		int deviceID = receive.readInt();
		String channel = Global.readUTF(receive);
		int lesson = receive.readInt();

		buf.writeShort(code);
		Device device = Dao.getDeviceExist(deviceID, "");
		log.info("name=" + name + ",deviceID=" + deviceID + ",channel = " + channel + ",lesson=" + lesson);
		if (device == null) {
			return backBuffer(2, "没找到用户");
		}
		Data data = Data.fromMap(device.getReward());// {1:{"金额":4,"状态":"已使用"},2:{"金额":7,"状态":"未获取"}}
		if ("生成红包".equals(name)) {// 全部购买有不发红包
			if (!data.get(lesson).get("状态").asString().isEmpty()) {
				return backBuffer(2, "当前课程的红包状态是："+data.get(lesson).get("状态").asString());
			}else if (device.getBuyState() == 131068 ) {
				return backBuffer(2, "已全部购买");
			}else if(lesson < 1 || lesson > 3){
				return backBuffer(2, "非法的课程");
			}
			
			int cash = 0;
			int other = (lesson == 1 ? 2 : 1);
			int cash2 = data.get(other).get("金额").asInt();
			cash = Global.getRandom((cash2 == 0 ? 10 : 11) - cash2) + 1;
			data.getMap(lesson).put("金额", cash).put("状态", "未使用");
			log.info( "device:"+deviceID+",生成的红包金额=" + cash);
			//月记录统计
			Count mc = Dao.getCountMonth();
			Data mcData = Data.fromMap(mc.getDataStr());
			Data mcData2 = mcData.getMap("红包生成").getMap(lesson);
			mcData2.put("次数", mcData2.get("次数").asInt()+1);
			mcData2.put("金额", mcData2.get("金额").asInt()+cash);
			mc.setDataStr(mcData.toString());
			Dao.save(mc);
		} else if ("错过红包".equals(name)) {
			if (!data.get(lesson).get("状态").asString().isEmpty()) {
				return backBuffer(2, "当前课程的红包状态是："+data.get(lesson).get("状态").asString());
			}
			data.getMap(lesson).put("状态", "已错过");
			log.info("device:"+deviceID+"错过红包 = " + data.toString());
			//月记录统计
			Count mc = Dao.getCountMonth();
			Data mcData = Data.fromMap(mc.getDataStr());
			Data mcData2 = mcData.getMap("红包生成").getMap(lesson);
			mcData2.put("错过", mcData2.get("错过").asInt()+1);			
			mc.setDataStr(mcData.toString());
			Dao.save(mc);
		}
		if (!"获取红包".equals(name)) {			
			device.setReward(data.toString());
			Dao.save(device);
		}
		String sta1 = data.get(1).get("状态").asString();
		String sta2 = data.get(2).get("状态").asString();
		sta1 = sta1.isEmpty() ? "未获取" : sta1;
		sta2 = sta2.isEmpty() ? "未获取" : sta2;
		String msg = "<Rewards><Reward lesson=\"1\" money=\"" + data.get(1).get("金额").asInt() + "\" status=\"" + sta1
				+ "\"/><Reward lesson=\"2\" money=\"" + data.get(2).get("金额").asInt() + "\" status=\"" + sta2
				+ "\"/></Rewards>";
		//log.info(msg);
		return backBuffer(1, msg);
	}

	/**
	 * result 返回状态码
	 *  msg 返回内容	
	 * @return 返回 buffer
	 */
	private ChannelBuffer backBuffer(int result, String msg) {
		buf.writeByte(result);
		buf.writeBytes(Global.getUTF(msg));
		return buf;
	}
}
