import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.Logger;



public class Test {
	private static final Logger log = Logger.getLogger(Test.class.getName()+"aqw");
	private static final Map<Integer, String> comMap = new HashMap<Integer, String>();
	static List<Integer> list = new ArrayList<Integer>();
	/**
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) {
		for (int i = 0; i < 1000; i++) {
			if(!comMap.containsKey(i)){
				list.add(i);
				if(list.size()>1){
					int j = list.remove(0);					
					comMap.remove(j);
				}
			}			
			comMap.put(i, "aa");			
		}
		log.info(comMap.size()+"");
		log.info(comMap.toString());
	}

}
