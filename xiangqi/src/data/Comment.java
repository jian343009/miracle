package data;

public class Comment {//评论记录
	private int id = 0;
	private int device = 0;
	private String userAge = "";
	private int praise = 0; //赞同数
	private boolean display = false; //是否公开
	private boolean checked = false;	
	private String timeStr = "";
	private String channel = "";
	private String userMail = "";
	private String userName = "";
	private String content = "";
	
	public int getId() {
		return id;
	}

	public int getDevice() {
		return device;
	}

	public String getUserAge() {
		return userAge;
	}

	public int getPraise() {
		return praise;
	}

	public boolean isDisplay() {
		return display;
	}

	public boolean isChecked() {
		return checked;
	}

	public String getTimeStr() {
		return timeStr;
	}

	public String getChannel() {
		return channel;
	}

	public String getUserMail() {
		return userMail;
	}

	public String getUserName() {
		return userName;
	}

	public String getContent() {
		return content;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDevice(int device) {
		this.device = device;
	}

	public void setUserAge(String userAge) {
		this.userAge = userAge;
	}

	public void setPraise(int praise) {
		this.praise = praise;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String toString(){
		return "ID:"+id+",userName:"+userName+",timeStr:"+timeStr;
	}
}
