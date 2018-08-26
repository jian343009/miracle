package cmd;

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

public class CMD12 implements ICMD {
	private static final Logger log = Logger.getLogger(CMD12.class.getName());
	private static final Map<Integer, Integer> praiseMap = new HashMap<Integer, Integer>();

	@Override
	public ChannelBuffer getBytes(int code, ChannelBuffer data) {

		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(code);
		String html = "";
		String name = Global.readUTF(data);
		int deviceID = data.readInt();
		String channel = Global.readUTF(data);
		int boardNum = data.readInt();
		log.info("接收到的评论请求：" + name + ",davice = " + deviceID + ",boardNum=" + boardNum);
		
		if ("提交".equals(name)) {
			String userName = Global.readUTF(data);
			String userAge = Global.readUTF(data);
			String userMail = Global.readUTF(data);
			String userContent = Global.readUTF(data);
			log.info("name="+userName+",age="+userAge +",联系方式="+userMail);
			Comment com = new Comment();
			if("用户昵称".equals(userName)){
				userName="ID:"+deviceID;
			}
			if("孩子年龄".equals(userAge)){
				userAge="未填";
			}else if(Global.getInt(userAge)>0){
				userAge +="岁";
			}
			if("联系方式".equals(userMail)){
				userMail="未填";
			}		
			com.setTimeStr(ServerTimer.getFull());// 用于显示的时间
			com.setDevice(deviceID);
			com.setChannel(channel);
			com.setUserName(userName);
			com.setUserAge(userAge);
			com.setUserMail(userMail);
			com.setContent(userContent);
			Dao.save(com);
		} else if ("点赞".equals(name)) {
			if (praiseMap.isEmpty()) {
				praiseMap.put(-1, ServerTimer.distOfDay());
			} else if (praiseMap.get(-1) != ServerTimer.distOfDay()) {
				praiseMap.clear();
				praiseMap.put(-1, ServerTimer.distOfDay());
			}
			int num = praiseMap.get(deviceID) == null ? 0 : praiseMap.get(deviceID);
			log.info("点赞次数=" + num + ",id=" + boardNum);
			if(num <= 4){
				Comment comment = Dao.getCommentByID(boardNum);// 当是点赞请求时，boardNum值是DeviceID
				if (comment != null) {
					comment.setPraise(comment.getPraise() + 1);
					praiseMap.put(deviceID, num + 1);
					Dao.save(comment);
					buf.writeByte(3);
					buf.writeBytes(Global.getUTF(comment.getId() + ""));
					return buf;
				}
			} else {
				log.info("你已经超过了次数，每天限5次。谢谢你的参与!");
				buf.writeByte(2);
				buf.writeBytes(Global.getUTF("你已经超过了次数，每天限5次。谢谢你的参与!"));
				return buf;
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
			} else if (comm.getDevice() == deviceID & step == 0) {// 最多只能看到一条自己的不展示评论
				sb.append("<Comment user=\"" + comm.getUserName() + "\" id=\"" + comm.getId() + "\" age=\""
						+ comm.getUserAge() + "\" laud=\"" + comm.getPraise() + "\" time=\"" + comm.getTimeStr()
						+ "\" >" + comm.getContent() + "</Comment>");
				step++;
			}
		}
		html = "<Comments>" + sb.toString() + "</Comments>";
		buf.writeByte(1);		
		buf.writeBytes(Global.getUTF(html));
		log.info(html);
		return buf;
	}

}
