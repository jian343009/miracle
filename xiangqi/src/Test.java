import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import dao.Data;
import main.Global;
import main.ServerTimer;

public class Test {
	private static final Logger log = Logger.getLogger(Test.class.getName());

	public static void main(String[] args) {
		long l1 = System.currentTimeMillis();
		int i = 0;
		while (i < 9999) {
			Data.fromMap("{22:44}").put(22, 55).toString();
			
			i++;

		}
		long l2 = System.currentTimeMillis();
		long time = 000;

		log.info(time + "," +i+ Data.fromMap("{22:44}").put(22, 56).toString());

	}
}
