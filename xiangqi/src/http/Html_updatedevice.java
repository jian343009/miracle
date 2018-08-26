package http;

import java.lang.reflect.Field;

import org.jboss.logging.*;

import main.*;
import dao.*;
import data.*;

public class Html_updatedevice extends Html {
	private static final Logger log = Logger.getLogger(Html_updatedevice.class.getName());

	@Override
	public String getHtml(String content) {
		String html = "";
		
		String[] params = content.split("&");
		int id = Global.getInt(params[0].split("=")[1]);
		Device device = Dao.getDeviceExist(id, "");
		if(device != null){
			for(int m=1;m<params.length;m++){
				String[] kv = params[m].split("=");
				String key = kv[0];
				String value = "";
				if(kv.length ==2){
					value = kv[1];
				}
				html = key+"修改"+value+";";
				try {
					Field fd = Device.class.getDeclaredField(key);
					fd.setAccessible(true);
					if(fd.getType() == int.class){
						fd.set(device, Global.getInt(value));
					}else{
						fd.set(device, value);
					}
				} catch (Exception e) {
					e.printStackTrace();
					html += e.getLocalizedMessage();
				}
			}
			Dao.save(device);
		}else{
			html = "设备不存在";
		}
		return html;
	}

}
