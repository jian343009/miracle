package data;

import java.util.*;
import java.util.concurrent.TimeUnit;

import main.Global;

import org.jboss.logging.Logger;
import org.jboss.netty.util.*;
import org.jboss.netty.util.TimerTask;

import dao.Dao;

public class Database implements TimerTask {
	private static final Logger log = Logger.getLogger(Database.class);
	private static HashedWheelTimer timer = new HashedWheelTimer(100,TimeUnit.MILLISECONDS);

	private static boolean SaveNow = false;
	private static final ArrayList<Database> cache = new ArrayList<Database>();
	private static final ArrayList<Database> saveList = new ArrayList<Database>();
	private static Timeout timeout = null;
	
	private long time = 0;
	
	public static boolean getSaveNow(){
		return SaveNow;
	}
	public static void setSaveNow(boolean b){
		SaveNow = b;
		if(SaveNow){
			synchronized (cache) {
				for(Database db : cache){
					Dao.save(db);
				}
				cache.clear();
			}
		}
	}
	public static ArrayList<Database> getCache() {
		return cache;
	}
	public static ArrayList<Database> getSaveList() {
		return saveList;
	}
	public void save(){
		if(SaveNow){
			store();
		}else if(time ==0){
			time = System.currentTimeMillis();
			synchronized (cache) {
				if(!cache.contains(this)){
					cache.add(this);
					while(cache.size() >100){
						cache.remove(0).store();
					}
				}
			}
		}else if(System.currentTimeMillis() - time >60000){
			store();
		}
	}

	public void store(){
		time = 0;
		synchronized (cache) {
			cache.remove(this);
		}
		synchronized (saveList) {
			if(!saveList.contains(this)){
				saveList.add(this);
			}
		}
		start();
	}
	public void storeNow(){
		time = 0;
		synchronized (cache) {
			cache.remove(this);
		}
		synchronized (saveList) {
			saveList.remove(this);
		}
		Dao.save(this);
	}
	public void delete(){
		time = 0;
		synchronized (cache) {
			cache.remove(this);
		}
		synchronized (saveList) {
			saveList.remove(this);
		}
		Dao.delete(this);
	}
	private void start(){
		synchronized (timer) {
			if(timeout == null || timeout.isExpired() || timeout.isCancelled()){
				timeout = timer.newTimeout(this, 100, TimeUnit.MILLISECONDS);
			}
		}
	}
	@Override
	public void run(Timeout timeout) throws Exception {
		Database db = null;
		while(saveList.size() >0){
			synchronized (saveList) {
				db = saveList.remove(0);
			}
			if(db != null){
				Dao.save(db);
			}
		}
		timeout = null;
	}
}
