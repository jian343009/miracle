package cmd;

import java.util.logging.Logger;

import main.Global;
import main.ServerTimer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import dao.Dao;
import data.BaseData;
import data.Device;
/**
 * 启动应用
 */
public class CMD201 implements ICMD {
	private final Logger log = Logger.getLogger("CMD100");
	
	@Override
	public ChannelBuffer getBytes(int cmd, ChannelBuffer data) {
		int deviceID = data.readInt();log.info("deviceID:"+deviceID);
		String imei = Global.readUTF(data);log.info("imei:"+imei);
		int lesson = data.readByte();log.info("lesson:"+lesson);
		
		Device device = Dao.getDevice(deviceID, imei, "验证解锁");
		
		int result = 2;
		String code = "还未购买该课";
		int pow = 1<<lesson;
		if(BaseData.getContent(BaseData.强制全部解锁).contains("#"+device.getChannel()+device.getVersion()+"#")|| (device.getBuyState() & pow) == pow){
			if(device.getUnlockNum(lesson) <=5){
				result = 1;
				int x=1,y=0,z=1;
				do{
					int m = (int)(Math.random()*device.getId()) + device.getId();
					int n = (int)(Math.random()*60 +40);
					int p = device.getId()*n - m*lesson;
					log.info("m:"+m);
					log.info("n:"+n);
					log.info("p:"+p);
					x = m<<3;
					y = n<<2;
					z = p<<1;
					code = x+","+y+","+z;
				}while(x<0 || y<0 || z<0);
			}else{
				result = 2;
				code = "解锁次数超出限制";
			}
		}
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(cmd);
		buf.writeByte(result);log.info("result:"+result);
		buf.writeInt(device.getId());log.info("deviceID:"+device.getId());
		buf.writeBytes(Global.getUTF(code));log.info("code:"+code);
		return buf;
	}

}
