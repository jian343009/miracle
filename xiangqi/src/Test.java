import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import dao.Data;
import main.Global;
import main.ServerTimer;

public class Test {
	private static final Logger log = Logger.getLogger(Test.class.getName()+"aqw");	
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException  {
		Data data = Data.fromMap("");
		data.getMap("42").put(31,43);
		Data dat = data.getMap("42");
		dat.put("aaa", "aaa");
		log.info(data.containsKey(42) + ","+data.containsKey("42"));
		log.info(data.toString());
	}
	 
}
