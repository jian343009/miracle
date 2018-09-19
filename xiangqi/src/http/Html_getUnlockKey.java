package http;

import java.util.HashMap;
import java.util.logging.Logger;

import cmd.CMD10;
import dao.Dao;
import data.BaseData;
import data.Device;
import main.Global;

public class Html_getUnlockKey extends Html {
	private static final Logger log = Logger.getLogger(Html_getUnlockKey.class.getName());

	@Override
	public String getHtml(String content) {
		String code = "";
		HashMap<String, String> map = Global.decodeUrlParam(content);
		String token = map.get("token");
		int lesson = Global.getInt(map.get("lesson"));
		//log.info("token = " + token + "---lesson = " + lesson);

		Device device = Dao.getDeviceByToken(token);
		//log.info("device:"+device);
		if(device != null && lesson >0){
			int unlockMark = device.getUnlockKey();
			log.info(device+"原始码:"+unlockMark);
			
			int bought = device.getBuyState();//取出用户购买记录
			log.info("bought:"+bought);
			int pow = 1 << lesson;
			if(lesson > 16){
				unlockMark = CMD10.next(unlockMark, lesson);
				code += unlockMark;
			}else if (BaseData.getContent(BaseData.强制全部解锁).contains("#"+device.getChannel()+device.getVersion()+"#") || (bought & pow) == pow) {//是否购买
				if (device.getUnlockNum(lesson) <= 5) {//解锁次数				
					unlockMark = CMD10.next(unlockMark, lesson);
					code += unlockMark;
					log.info("解锁：" + device.getExtra() + ",code = " + code);
					device.modUnlockNum(lesson, 1);
					Dao.save(device);
					return code;
				}
			}
		}
		return code;
	}
}
