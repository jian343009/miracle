package data;

public class AppleProduct {
	
	private int id;
	private String name = "";
	private String productIdentifier = "";
	private int uniqueUsed = 0;
	private int used = 0;
	private int uniqueBuy = 0;
	private int buy = 0;
	private int testUniBuy = 0;
	private int testBuy = 0;
	private int score = 0;
	private int lesson = 1;
	private int price = 1;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProductIdentifier() {
		return productIdentifier;
	}
	public void setProductIdentifier(String productIdentifier) {
		this.productIdentifier = productIdentifier;
	}
	public int getUniqueUsed() {
		return uniqueUsed;
	}
	public void setUniqueUsed(int uniqueUsed) {
		this.uniqueUsed = uniqueUsed;
	}
	public int getUsed() {
		return used;
	}
	public void setUsed(int used) {
		this.used = used;
	}
	public int getUniqueBuy() {
		return uniqueBuy;
	}
	public void setUniqueBuy(int uniqueBuy) {
		this.uniqueBuy = uniqueBuy;
	}
	public int getBuy() {
		return buy;
	}
	public void setBuy(int buy) {
		this.buy = buy;
	}
	public int getTestUniBuy() {
		return testUniBuy;
	}
	public void setTestUniBuy(int testUniBuy) {
		this.testUniBuy = testUniBuy;
	}
	public int getTestBuy() {
		return testBuy;
	}
	public void setTestBuy(int testBuy) {
		this.testBuy = testBuy;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getLesson() {
		return lesson;
	}
	public void setLesson(int lesson) {
		this.lesson = lesson;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
}
