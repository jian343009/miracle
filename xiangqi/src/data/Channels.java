package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.Session;
import org.hibernate.annotations.Columns;
import org.hibernate.criterion.Restrictions;

import com.sun.istack.internal.Nullable;

import cmd.CMD14;
import dao.HSF;
import main.Global;
/**
 * @author 不同渠道不同价格，不同折扣
 */
@Entity
public class Channels {
	private static final Logger log = Logger.getLogger(Channels.class.getName());
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column(nullable=false,unique=true)
	private String channel = "";
	private int discount = 0;//多课支付折扣
	private int baseprice = 0;//基础价格
	@Column(nullable=true)
	private Integer price2 = 0;
	@Column(nullable=true)
	private Integer price3 = 0;
	@Column(nullable=true)
	private Integer price4 = 0;
	@Column(nullable=true)
	private Integer price5 = 0;
	@Column(nullable=true)
	private Integer price6 = 0;
	@Column(nullable=true)
	private Integer price7 = 0;
	@Column(nullable=true)
	private Integer price8 = 0;
	@Column(nullable=true)
	private Integer price9 = 0;
	@Column(nullable=true)
	private Integer price10 = 0;
	@Column(nullable=true)
	private Integer price11 = 0;
	@Column(nullable=true)
	private Integer price12 = 0;
	@Column(nullable=true)
	private Integer price13 = 0;
	@Column(nullable=true)
	private Integer price14 = 0;
	@Column(nullable=true)
	private Integer price15 = 0;
	@Column(nullable=true)
	private Integer price16 = 0;
	@Column(nullable=true)
	private String time = "";//更改时间
	public int getId() {
		return id;
	}
	public String getChannel() {
		return channel;
	}
	public int getDiscount() {
		return discount;
	}
	public int getBaseprice() {
		return baseprice;
	}
	public int getPrice2() {
		return price2==null?0:price2;
	}
	public int getPrice3() {
		return price3==null?0:price3;
	}
	public int getPrice4() {
		return price4==null?0:price4;
	}
	public int getPrice5() {
		return price5==null?0:price5;
	}
	public int getPrice6() {
		return price6==null?0:price6;
	}
	public int getPrice7() {
		return price7==null?0:price7;
	}
	public int getPrice8() {
		return price8==null?0:price8;
	}
	public int getPrice9() {
		return price9==null?0:price9;
	}
	public int getPrice10() {
		return price10==null?0:price10;
	}
	public int getPrice11() {
		return price11==null?0:price11;
	}
	public int getPrice12() {
		return price12==null?0:price12;
	}
	public int getPrice13() {
		return price13==null?0:price13;
	}
	public int getPrice14() {
		return price14==null?0:price14;
	}
	public int getPrice15() {
		return price15==null?0:price15;
	}
	public int getPrice16() {
		return price16==null?0:price16;
	}
	public String getTime() {
		return time;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public void setDiscount(int discount) {
		this.discount = discount;
	}
	public void setBaseprice(int baseprice) {
		this.baseprice = baseprice;
	}
	public void setPrice2(int price2) {
		this.price2 = price2;
	}
	public void setPrice3(int price3) {
		this.price3 = price3;
	}
	public void setPrice4(int price4) {
		this.price4 = price4;
	}
	public void setPrice5(int price5) {
		this.price5 = price5;
	}
	public void setPrice6(int price6) {
		this.price6 = price6;
	}
	public void setPrice7(int price7) {
		this.price7 = price7;
	}
	public void setPrice8(int price8) {
		this.price8 = price8;
	}
	public void setPrice9(int price9) {
		this.price9 = price9;
	}
	public void setPrice10(int price10) {
		this.price10 = price10;
	}
	public void setPrice11(int price11) {
		this.price11 = price11;
	}
	public void setPrice12(int price12) {
		this.price12 = price12;
	}
	public void setPrice13(int price13) {
		this.price13 = price13;
	}
	public void setPrice14(int price14) {
		this.price14 = price14;
	}
	public void setPrice15(int price15) {
		this.price15 = price15;
	}
	public void setPrice16(int price16) {
		this.price16 = price16;
	}
	public void setTime(String time) {
		this.time = time;
	}
	private static HashMap<String, Channels> dicChannel =  new HashMap<String, Channels>();//缓存
	public static Channels getChannels(String channel){
		if(channel == null || channel.isEmpty()){
			return null;
		}
		Channels chann = dicChannel.get(channel);
		if(chann == null){
			Session ss = HSF.getSession();
			List<Channels> list = ss.createCriteria(Channels.class).add(Restrictions.eq("channel", channel)).setMaxResults(1).list();
			ss.close();
			if(list.size()>0){
				int limit = 13;
				if(dicChannel.size() > limit){//限制缓存大小
					dicChannel.remove(dicChannel.keySet().toArray()[Global.getRandom(limit)]);
					log.info(channel);
				}
				chann = list.get(0);
				dicChannel.put(channel, chann);//做缓存
			}
		}
		return chann;
	}
}
