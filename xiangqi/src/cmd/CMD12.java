package cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import dao.Dao;
import data.Comment;
import data.Device;
import main.Global;
import main.ServerTimer;

/**
 * 评论功能
 * 
 * @author 周超 2018/8/25
 */
public class CMD12 implements ICMD {
	private static final Logger log = Logger.getLogger(CMD12.class.getName());
	private static final Map<Integer, Integer> praiseMap = new HashMap<Integer, Integer>();
	private static final List<Integer> pralist = new ArrayList<Integer>();
	private static final Map<Integer, String> comMap = new HashMap<Integer, String>();
	private static final List<Integer> list = new ArrayList<Integer>();

	@Override
	public ChannelBuffer getBytes(int code, ChannelBuffer data) {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(code);
		String html = "";
		String name = Global.readUTF(data);
		int deviceID = data.readInt();
		String channel = Global.readUTF(data);
		int boardNum = data.readInt();
		log.info(name + "评论" + ",davice = " + deviceID + ",boardNum=" + boardNum);
		Device device = Dao.getDeviceExist(deviceID, "");
		if (device == null) {
			return backBuffer(buf, 2, "找不到用户");
		} else if (!"获取".equals(name) && device.getBuy() == 0) {
			String msg = "提交".equals(name) ? "您还未购买任何课程，不能评论。" : "您还未购买任何课程，不能点赞。";
			return backBuffer(buf, 2, msg);
		}
		if ("提交".equals(name)) {
			String userName = Global.readUTF(data);
			String userAge = Global.readUTF(data);
			String userMail = Global.readUTF(data);
			String userContent = Global.readUTF(data).trim();
			log.info("name=" + userName + ",age=" + userAge + ",联系方式=" + userMail);
			if (userContent.equals(comMap.get(deviceID))) {
				return backBuffer(buf, 2, "请勿重复提交。");
			}
			Comment com = new Comment();
			userName = ("用户昵称".equals(userName) ? "ID:" + deviceID : userName);
			userMail = ("联系方式".equals(userMail) ? "未填" : userMail);
			userAge = (Global.getInt(userAge) == 0 ? "未填" : userAge + "岁");
			com.setTimeStr(ServerTimer.getFull());// 用于显示的时间
			com.setDevice(deviceID);
			com.setChannel(channel);
			com.setUserName(userName);
			com.setUserAge(userAge);
			com.setUserMail(userMail);
			com.setContent(userContent);
			Dao.save(com);
			limitMapSize(comMap, list, deviceID);
			comMap.put(deviceID, userContent);
		} else if ("点赞".equals(name)) {
			if (praiseMap.get(-1) == null || praiseMap.get(-1) != ServerTimer.distOfDay()) {
				praiseMap.clear();
				praiseMap.put(-1, ServerTimer.distOfDay());
			}
			int num = praiseMap.get(deviceID) == null ? 0 : praiseMap.get(deviceID);
			if (num <= 4) {
				Comment comment = Dao.getCommentByID(boardNum);// 当是点赞请求时，boardNum值是DeviceID
				if (comment != null) {
					comment.setPraise(comment.getPraise() + 1);
					limitMapSize(praiseMap, pralist, deviceID);
					praiseMap.put(deviceID, num + 1);
					Dao.save(comment);
					return backBuffer(buf, 3, comment.getId() + "");
				} else {
					return backBuffer(buf, 2, "点赞的评论不存在。");
				}
			} else {
				return backBuffer(buf, 2, "你已经超过了次数，每天限5次。谢谢你的参与!");
			}
		}
		List<Comment> list = Dao.getComments(deviceID, boardNum);
		StringBuilder sb = new StringBuilder();
		int step = 0;
		for (Comment comm : list) {
			if (comm.isDisplay()) {
				sb.append("<Comment user=\"" + comm.getUserName() + "\" id=\"" + comm.getId() + "\" age=\""
						+ comm.getUserAge() + "\" laud=\"" + comm.getPraise() + "\" time=\"" + comm.getTimeStr()
						+ "\" >" + comm.getContent() + "</Comment>");
			} else if (comm.getDevice() == deviceID && step == 0) {// 最多只能看到一条自己的不展示评论
				sb.append("<Comment user=\"" + comm.getUserName() + "\" id=\"" + comm.getId() + "\" age=\""
						+ comm.getUserAge() + "\" laud=\"" + comm.getPraise() + "\" time=\"" + comm.getTimeStr()
						+ "\" >" + comm.getContent() + "</Comment>");
				step++;
			}
		}
		html = "<Comments>" + sb.toString() + "</Comments>";
		String buy = device.getBuy() == 0 ? "未购" : "已购";
		buf.writeByte(1);
		buf.writeBytes(Global.getUTF(html));
		buf.writeBytes(Global.getUTF(buy));
		return buf;
	}

	/**
	 * 限定map的长度。
	 */
	private void limitMapSize(Map map, List list, int deviceID) {
		if (!map.containsKey(deviceID)) {
			list.add(deviceID);// 控制map长度
			if (list.size() > 999) {
				int j = (Integer) list.remove(0);
				map.remove(j);
				log.warning(map.toString());
			}
		}
	}

	/**
	 * 返回buf快捷方法。
	 */
	private ChannelBuffer backBuffer(ChannelBuffer buf, int _状态码, String msg) {
		buf.writeByte(_状态码);
		buf.writeBytes(Global.getUTF(msg));
		return buf;
	}
}
