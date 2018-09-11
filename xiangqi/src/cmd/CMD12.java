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
		if (device == null) {// 防止数据传输出错导致应用崩溃
			return backBuffer(buf, 2, "找不到用户");
		}
		if ("提交".equals(name)) {
//			if (device.getBuy() == 0 && device.getBuyState() <= 4) {
//				return backBuffer(buf, 2, "您还未购买任何课程，不能评论");
//			}
			String userName = Global.readUTF(data);
			String userAge = Global.readUTF(data);
			String userMail = Global.readUTF(data);
			String userContent = Global.readUTF(data).trim();// 去空格
			if (userContent.length() == 0) {
				return backBuffer(buf, 2, "输入的评论不能都为空格，请修改内容");
			} else if (userContent.matches("[0-9]+")) {
				return backBuffer(buf, 2, "输入的评论不能都为数字，请修改内容");
			}
			log.info("name=" + userName + ",age=" + userAge + ",联系方式=" + userMail);
			/* 防止重复评论 */
			List<Comment> list = Dao.getCommentByContent(userContent, deviceID);
			if (list != null) {
				return backBuffer(buf, 2, "请勿重复提交相同评论");
			}
			Comment com = new Comment();
			userName = ("用户昵称(选填)".equals(userName) ? "ID:" + deviceID : userName);
			userMail = ("联系方式(选填)".equals(userMail) ? "未填" : userMail);
			userAge = (Global.getInt(userAge) == 0 ? "未填" : userAge + "岁");
			com.setTimeStr(ServerTimer.getFull());// 用于显示的时间
			com.setDevice(deviceID);
			com.setChannel(channel);
			com.setUserName(userName);
			com.setUserAge(userAge);
			com.setUserMail(userMail);
			com.setContent(userContent);
			Dao.save(com);
		} else if ("点赞".equals(name)) {
//			if (device.getBuy() == 0 && device.getBuyState() <= 4) {
//				return backBuffer(buf, 2, "您还未购买任何课程，不能点赞");
//			}
			int today = ServerTimer.distOfDay();
			/* praise = 点赞日期乘100 + 点赞次数 */
			if (device.getPraise() / 100 != today) {// 去掉点赞次数比点赞日期
				device.setPraise(today * 100);
			}
			int num = device.getPraise() - (today * 100);
			if (num <= 4) {
				Comment comment = Dao.getCommentByID(boardNum);// 当是点赞请求时，boardNum值是DeviceID
				if (comment != null) {
					comment.setPraise(comment.getPraise() + 1);
					device.setPraise(device.getPraise() + 1);
					Dao.save(comment);
					Dao.save(device);
					return backBuffer(buf, 3, comment.getId() + "");
				} else {
					return backBuffer(buf, 2, "点赞的评论不存在");
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
		String buy = device.getBuy() == 0 ? "已购" : "已购";
		buf.writeByte(1);
		buf.writeBytes(Global.getUTF(html));
		buf.writeBytes(Global.getUTF(buy));
		return buf;
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
