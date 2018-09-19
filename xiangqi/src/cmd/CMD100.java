package cmd;

import main.Global;
import main.ServerTimer;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import antlr.Version;
import dao.Dao;
import data.*;
/**
 * 启动应用
 */
public class CMD100 implements ICMD {
	private final Logger log = Logger.getLogger(CMD100.class);
	
	@Override
	public ChannelBuffer getBytes(int cmd, ChannelBuffer data) {
		int deviceID = data.readInt();log.info("id:"+deviceID);
		String mark = Global.readUTF(data);log.info("mark:"+mark);
		String channel = Global.readUTF(data);log.info("channel:"+channel);
		String version = Global.readUTF(data);log.info("version:"+version);
		
		Global.addStep(deviceID, mark, "CMD100启动"+version, channel);
		Device device = Dao.getDevice(deviceID, mark, "CMD100");
		if(!mark.isEmpty() && !device.getImei().equals(mark)){
			device.setImei(mark);
		}else if(device.getImei().isEmpty()){
			device.setImei("device"+device.getId());
		}
		if(!channel.isEmpty() && channel.contains("#")){
			String[] spilt=Global.splitStringArray(channel,"#");			
			if(!spilt[1].isEmpty()){
				String token=spilt[1];
				device.setToken(token);
				log.info("token ="+token);
				device.setUnlockKey(CMD10.getUnlockyByToken(token));						
			}
			channel=spilt[0];	
		}
		if(!device.getRegChannel().contains(channel)){
			device.setRegChannel(device.getRegChannel() + channel + "#");
		}
		device.setChannel(channel);
		device.setVersion(version);
		
		Count mc = Dao.getCountMonth();
		Count count = Dao.getCountToday();
		ChannelEveryday ce = Dao.getChannelEverydayToday(channel);
		StepCount sc = StepCount.getByChannelToday(channel);
		int today = ServerTimer.distOfDay();
		if(device.getLastDay() != today){
			int firstDay = ServerTimer.distOfDay(ServerTimer.getCalendarFromString(device.getFirstTime()));
			mc.setOpen(mc.getOpen() +1);
			count.setOpen(count.getOpen() +1);
			count.addReturnNum(today - firstDay);
			if(Global.getInt(version)>=7 ){
				count.add奇偶返回(today - firstDay, device.getId());
			}else{
				count.add奇偶返回(today - firstDay, 0);
			}
			ce.setOpen(ce.getOpen() +1);
			ce.addReturnNum(today - firstDay);
			sc.add打开设备().store();
			log.info("firstDay = "+firstDay+",lastDay = "+ device.getLastDay() + ", today ="+ today);
		}
		if(device.getOpen() ==0){
			if(!channel.equals("苹果商城")){
				device.setBuyState(device.getBuyState() | 4);
			}
			if(Global.getInt(version) >=7){
				count.add新增用户(7);
			}else{
				count.add新增用户(0);//0版本代表非7版本
			}
			mc.setNewDevice(mc.getNewDevice() +1);
			count.setNewDevice(count.getNewDevice() +1);
			ce.setNewDevice(ce.getNewDevice() +1);
			sc.add新增设备().store();
		}
		Dao.save(mc);
		Dao.save(count);
		Dao.save(ce);
		
		device.setOpen(device.getOpen() +1);
		device.setLastDay(ServerTimer.distOfDay());
		device.setLastTime(ServerTimer.getFull());
		Dao.save(device);
		
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(cmd);
		buf.writeInt(device.getId());
		buf.writeInt(BaseData.getContent(BaseData.强制全部解锁).contains("#"+channel+version+"#") ? Integer.MAX_VALUE : device.getBuyState());
		buf.writeBytes(Global.getUTF(device.getMobile()));
		buf.writeByte(device.getState());
		return buf;
	}

}
