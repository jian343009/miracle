import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.text.StrBuilder;
import org.jboss.logging.Logger;

import dao.Dao;
import data.Device;
import main.Global;
import main.ServerTimer;


public class Test {
	private static final Logger log = Logger.getLogger("Test");
	private static final Map<Integer,Integer> map = new HashMap<Integer, Integer>();
	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		log.info("hello git");
	}
	public static int get方位(int mySeat, int youSeat){
		return (3 - youSeat + mySeat)%4;
	}
	public static int next(int code){
		code ^= (code<<21);
		code ^= (code>>21);
		code ^= (code<<4);
		return code;
	}
}
