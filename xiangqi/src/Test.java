import java.io.File;
import java.io.FileFilter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;

import main.Global;

public class Test {
	private static final Logger log = Logger.getLogger("Test");
	private static final Map<Integer, Integer> praiseMap = new HashMap<Integer, Integer>();
	/**
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) {
		String str="aaa";
		str = (str=="aaa"?"bbb":str);
		boolean b=praiseMap.get(-1)>0;
		log.info(b);
	}

}