package cmd;

import main.Global;
import main.ServerTimer;

import java.util.Arrays;
import java.util.List;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.sun.org.apache.xerces.internal.impl.dv.DVFactoryException;

import dao.Dao;
import data.*;

public class CMD101 implements ICMD {
	private final Logger log = Logger.getLogger(CMD101.class);
	
	@Override
	public ChannelBuffer getBytes(int cmd, ChannelBuffer data) {
		int deviceID = data.readInt();
		String imei = Global.readUTF(data);
		int lesson = data.readByte();
		String step = Global.readUTF(data);
		String channel = "";
		String info = "";
		
		Device device = Dao.getDeviceExist(deviceID, imei);
		int version = 0;
		if(device != null){
			channel = device.getChannel();
			version = Global.getInt(device.getVersion());
			deviceID = device.getId();
		}		
		Global.addStep(deviceID, imei, lesson+"#"+step, channel);
		
		if(step.equals("打开支付单课")){
			StepCount.getByChannelToday(channel).add支付统计(step).store();
		}else if(step.equals("打开支付多课")){
			StepCount.getByChannelToday(channel).add支付统计(step).store();
		}else if(step.equals("开始学习")){
			StepCount.getByChannelToday(channel).add单课行为(lesson, step).store();
		}else if(step.equals("结束学习")){
			StepCount.getByChannelToday(channel).add单课行为(lesson, step).store();
		}else if(step.equals("完成学习")){			
			StepCount sc = StepCount.getByChannelToday(channel);				
			if(lesson == 1 && version >=7){
				sc.add单课行为(lesson, (deviceID%2)+step);//0完成学习，1完成学习，记录奇偶用用户完成学习的情况
			}	sc.add单课行为(lesson, step).store();
		}else if(step.equals("开始练习")){
			StepCount.getByChannelToday(channel).add单课行为(lesson, step).store();
		}else if(step.equals("结束练习")){
			StepCount.getByChannelToday(channel).add单课行为(lesson, step).store();
		}else if(step.equals("完成练习")){
			StepCount.getByChannelToday(channel).add单课行为(lesson, step).store();			
		}else if(step.contains("退出应用程序选择")){
			String[] list_退出 = {"不吸引人","想学但价格太高","习题太少缺少练习","孩子看不懂","操作不方便"};
			int num = Global.getInt(step.substring(step.trim().length()-1))-1;
			//取出step中位于最后的一个数字
			if(num >=0 && num <=4){//传入代号对应的退出原因
			StepCount.getByChannelToday(channel).add退出测试(list_退出[num]).store();
			}
		}
		
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(cmd);
		return buf;
	}

}
