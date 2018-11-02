package cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * 
 * @author 周超
 */
public class CMD14 implements ICMD {
	private static final Logger log = Logger.getLogger(CMD14.class.getName());

	@Override
	public ChannelBuffer getBytes(int code, ChannelBuffer receive) {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(code);

		String name = Global.readUTF(receive);
		int deviceID = receive.readInt();
		String channelName = Global.readUTF(receive);
		int lesson = receive.readInt();// 支付多课为0

		Device device = Dao.getDeviceExist(deviceID, "");
		if (device == null) {
			return reBuf(buf, 2, "用户ID有误");
		}
		if ("hwei".equals(channelName)) {
			channelName = "华为平台";
		} else if ("leds".equals(channelName)) {
			channelName = "乐视电视";		return reBuf(buf, 2, "暂不处理");
		} else if ("appl".equals(channelName)) {
			channelName = "苹果平台";		return reBuf(buf, 2, "暂不处理");
		} else {
			channelName = "其它平台";
		}
		//log.info("name:" + name + ",ID:" + deviceID + ",channel:" + channelName + ",lesson:" + lesson);
		if (lesson > 16 || (lesson < 2 && lesson != 0)) {
			return reBuf(buf, 2, "未知的课程");
		} else if ((device.getBuyState() & (1 << lesson)) == (1 << lesson)) {
			return reBuf(buf, 3, "该课已解锁");
		}

		int price = getPrice(device, lesson);
		int 红包 = 可用红包额(device);

		String 红包限制 = BaseData.getContent(BaseData.红包限制);// 红包使用条件
		String 红包抵扣了 = "";// 红包抵扣后的提示信息
		if (("通用".equals(红包限制) || ("多课".equals(红包限制) && lesson == 0)) && 红包 > 0) {
			红包抵扣了 = "(原价" + (price + 红包) + "元,红包抵扣" + 红包 + "元)";
		}
		String payMsg = "";// 返回的提示信息
		Data data = BaseData.getPriceData(channelName);// 渠道价格信息
		if (data == null) {
			return reBuf(buf, 3, "没找到对应渠道");
		}
		String 支付提示 = data.get("内容").get(lesson).asString();// 单课的支付提示
		if ("单课".equals(name) && lesson != 0 && 支付提示.contains("#") ) {
			String[] arr=支付提示.split("#");
			payMsg = arr[0] + price + "元" + 红包抵扣了 + arr[1];
		} else if ("多课".equals(name) && lesson == 0) {
			List<Integer> list = new ArrayList<Integer>();
			for (int i = 2; i <= 16; i++) {
				if ((device.getBuyState() & (1 << i)) != (1 << i)) {// 取出所有未购买的课程
					list.add(i);
				}
			}
			String 未解锁课程 = list.toString().replace("[", "").replace("]", "");
			payMsg = "花" + price + "元" + 红包抵扣了 + "解锁" + 未解锁课程 + "课,共" + list.size() + "课。";
		}
		// 回返信息
		String payChannel = BaseData.getContent(BaseData.可用支付方式);
		payChannel = Data.fromMap(payChannel).get(channelName).asString();
		// 把价格存在device里，支付回调时用于比对
		device.setMoney(price);
		Dao.save(device);
		log.info("price:" + price);
		buf.writeByte(1);
		buf.writeBytes(Global.getUTF(payChannel));
		buf.writeInt(price);
		buf.writeBytes(Global.getUTF(payMsg));// 提示信息
		return buf;
	}

	public static int getPrice(Device device, int lesson) {
		// {价格:{1:12,2:13,,,,折扣:95},内容:{1:"",2"",,,,}}
		int price = 0;
		String channel = device.getChannel();

		if (!"华为平台".equals(channel)) {// 确定的渠道才有对应的价格
			channel = "其它平台";
		}

		Data data = BaseData.getPriceData(channel);
		if (data == null) {
			return 0;
		}
		if (lesson == 0) {// 多课价格
			int total = 0;
			for (int i = 2; i <= 16; i++) {
				int pow = 1 << i;
				if ((device.getBuyState() & pow) != pow) {// 取出所有未购买的课程
					total += data.get("价格").get(i).asInt();
				}
			}
			int 折扣 = data.get("价格").get("折扣").asInt();
			price = (total * 折扣) / 100;
		} else {// 单课价格
			price = data.get("价格").get(lesson).asInt();
		}
		String 红包限制 = BaseData.getContent(BaseData.红包限制);
		if ("通用".equals(红包限制) || ("多课".equals(红包限制) && lesson == 0)) {
			price -= 可用红包额(device);
		}
		return price;
	}

	public static int 可用红包额(Device device) {
		int 红包 = 0;
		if (device.getReward() == null || !device.getReward().contains("未使用")) {
			return 0;
		}
		Data data = Data.fromMap(device.getReward());
		for (int les : new int[] { 1, 2 }) {
			if ("未使用".equals(data.get(les).get("状态").asString())) {
				红包 += data.get(les).get("金额").asInt();
			}
		}
		return 红包;
	}

	// 快来返回buffer方法
	private ChannelBuffer reBuf(ChannelBuffer buf, int code, String msg) {
		buf.writeByte(code);
		buf.writeBytes(Global.getUTF(msg));
		return buf;
	}
}
