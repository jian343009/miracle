package cmd;

import java.util.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.w3c.dom.Element;
import dao.Dao;
import data.Device;
import data.Mobile;
import data.Tuan;
import main.Global;
import main.ServerTimer;

/**
 * 团购功能
 */
public class CMD207 implements ICMD {
	private final Logger log = Logger.getLogger(CMD207.class.getName());

	@Override
	public ChannelBuffer getBytes(int cmd, ChannelBuffer data) {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(cmd);

		int deviceID = data.readInt();
		String channel = Global.readUTF(data).trim().split("#")[0];
		String name = Global.readUTF(data).trim();
		String number = Global.readUTF(data).trim();
		Device device = Dao.getDeviceExist(deviceID, "");
		
//		name = "功能屏蔽中";
		
		if (device == null) {
			return goBackBuffer(buf, 2, "未正常获取设备号");
		}
		
		if ("发起团购".equals(name)) {// 新开团
			if (device.getBuyState() == 131068) {
				return goBackBuffer(buf, 2, "全部课程已解锁，无需参团");
			} else if (device.getState() != 0) {
				return goBackBuffer(buf, 2, "已参与活动，不能发起");
			}
			Tuan tuan = Tuan.getByImei(device.getImei());
			if(tuan == null){
				tuan = new Tuan();	tuan.setImei(device.getImei());
			}
			if (tuan.getPeoples() == 0) {
				int price = data.readInt();
				String goods_name = Global.readUTF(data).trim();
				String goods_note = Global.readUTF(data).trim();

				tuan.setChannel(channel);	tuan.setRegChannel(channel + "#");
				tuan.setNumber(number);		tuan.setTitle(goods_name);
				tuan.setInfo(goods_note);	tuan.setVersion(device.getVersion());
				// 团购时间以收到支付结果为准。
				tuan.setToken(Global.md5(device.getImei() + "#" + number + 131068 + ServerTimer.getTotalWithS()));
				log.info("建团成功");
				Dao.save(tuan);
				String agent_bill_id = "xiangqituan" + ServerTimer.getTotalWithS();
				Element root = CMD202.getToken(price, device.getId(), agent_bill_id,
						tuan.getId() + "-" + 888 + "-" + 131068 + "-团购", "少儿象棋团购", "团购解锁");
				String name2 = "", value = "";
				if (root != null) {
					name2 = root.getNodeName();
					value = root.getTextContent();
				}
				log.info("name:" + name2 + ",value:" + value);
				if (name2.equals("token_id")) {
					buf.writeByte(1);
					buf.writeInt(device.getId());
					buf.writeBytes(Global.getUTF(value));// token验证码
					buf.writeBytes(Global.getUTF(agent_bill_id));// 订单号
					buf.writeBytes(Global.getUTF(CMD202.agent_id + ""));// 商户号
				} else if (name2.equals("error")) {
					return goBackBuffer(buf, 2, value);
				}
			} else if (tuan.getPeoples() == 1 ) {
				if( ServerTimer.distOfSecond() > tuan.getLastTime()){
					return goBackBuffer(buf, 2, "团购已截止，拼团不成功");
				}else{
					return goBackBuffer(buf, 2, "请获取团购链接并邀请他人参与团购");
				}
			} else if (tuan.getPeoples() > 1) {
				device.setBuyState(131068);		Dao.save(device);
				return goBackBuffer(buf, 2, "已参团，已成功");
			}
		} 
		
		else if ("获取链接".equals(name)) {// 获取链接
			if(device.getState() != 1){	
				return goBackBuffer(buf, 2, "您还未参与团购");	}
			Tuan tuan = Tuan.getByImei(device.getImei());
			if(tuan == null){//非自建团
				Mobile mobile = Mobile.getByImei(device.getImei());
				if(mobile == null){
					device.setState(0); Dao.save(device);
					return goBackBuffer(buf, 2, "没有找到你的moblie");
				}else{
					tuan = Tuan.getByID(mobile.getTuanID());
					if(tuan == null){
						return goBackBuffer(buf, 2, "你参与的团购查找失败");
					}
				}				
			}
			if (tuan.getPeoples() == 0) {
				return goBackBuffer(buf, 2, "你已建团但未支付成功");
			}else if ( tuan.getPeoples() >=1 ) {//已开团
				String time = "";
				if (tuan.getLastTime() < ServerTimer.distOfSecond()) {
					time = "团购已结束";
				}else{
					/* 计算截止时间 */
					long now = ServerTimer.distOfSecond();// 当前时间的long值
					long lon = tuan.getLastTime() - now;// 时间差的long值
					time = String.format("%02d", lon / (60 * 60)) + "-" + String.format("%02d", (lon / 60) % 60)
							+ "-" + String.format("%02d", lon % 60);
				}
				String url = "http://main.miracle-cn.com/xiangqi/tuan.html?token=" + tuan.getToken() + 
				"&url=xiangqipay.miracle-cn.com:20000";
				buf.writeByte(3);
				buf.writeBytes(Global.getUTF(url));
				buf.writeBytes(Global.getUTF(tuan.getInfo()));// 团购内容
				buf.writeInt(tuan.getPeoples());// 已参团人数
				buf.writeBytes(Global.getUTF(time));// 截止时间
			}
		}
		
		
		else if ("团购解锁".equals(name)) {// 将手机号作为解锁码输入。
			if(!number.matches("^1[\\d]{10}")){
				return goBackBuffer(buf, 2, "不合法的解锁码");
			}else if (device.getBuyState() == 131068) {
				return goBackBuffer(buf, 2, "本设备已全部解锁，请解锁其它设备");
			}
			Mobile mobile = Mobile.getByNumber(number);
			if(mobile.getTuanID() == 0){
				return goBackBuffer(buf, 2, "该号码未成功参与团购");
			}
			Tuan tuan = Tuan.getByID(mobile.getTuanID());
			if (tuan == null) {// 没参团且未付费
				return goBackBuffer(buf, 2, "没有找到对应的团购");
			} else if (mobile.getImei().isEmpty()) {
				device.setBuyState(131068);
				device.setState(1);
				device.setBuy(device.getBuy()+1);
				mobile.setImei(device.getImei());
				Dao.save(device);
				Dao.save(mobile);
				return goBackBuffer(buf, 4, "解锁成功");
			} else if (!mobile.getImei().equals(device.getImei())) {
				return goBackBuffer(buf, 2, "该手机号已绑定其它设备");
			}

		} 
		
		else if("补差价".equals(name)){
			int lesson = 0;
			for (int i = 0; i < 17; i++) {
				int j = 1 << i;
				if((device.getBuyState() & j) > 0){
					lesson++;//已购的买课程数量。
				}
			}
			int money = (14 - lesson) * 12 * 9 / 10  - device.getMoney();
			int tuanID = 0;
			Tuan tuan = Tuan.getByImei(device.getImei());
			if(tuan != null){
				tuanID = tuan.getId();
			}			
			String agent_bill_id = "xiangqituan" + ServerTimer.getTotalWithS();
			Element root = CMD202.getToken(money / 10, device.getId(), agent_bill_id,
					tuanID + "-" + 999 + "-" + 131068 + "-团购", "少儿象棋团购补差价", "团购解锁");
			String name2 = "", value = "";
			if (root != null) {
				name2 = root.getNodeName();
				value = root.getTextContent();
			}
			log.info("name:" + name2 + ",value:" + value);
			if (name2.equals("token_id")) {
				log.info("正常返回链接");
				buf.writeByte(1);
				buf.writeInt(device.getId());
				buf.writeBytes(Global.getUTF(value));// token验证码
				buf.writeBytes(Global.getUTF(agent_bill_id));// 订单号
				buf.writeBytes(Global.getUTF(CMD202.agent_id + ""));// 商户号
			} else if (name2.equals("error")) {
				log.info("获取链接异常");
				buf.writeByte(2);
				buf.writeBytes(Global.getUTF(value));
			}
		}		
		return buf;
	}

	private ChannelBuffer goBackBuffer(ChannelBuffer buf, int code, String msg) {
		buf.writeByte(code);
		buf.writeBytes(Global.getUTF(msg));
		return buf;
	}

}
