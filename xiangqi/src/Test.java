import java.util.logging.Logger;

import dao.Data;
import main.Global;
import main.ServerTimer;

public class Test {
	private static final Logger log = Logger.getLogger(Test.class.getName());
	public static void main(String[] args){
		String cha = "佾华为平台工林";
		for(String str:new String[]{"华为平台","苹果平台","乐视电视","其它平台"}){
			if(cha.contains(str)){
				cha = str;
				log.info(cha);
			}
		}
		log.info("");
	}
}
