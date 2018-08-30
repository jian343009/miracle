package cmd;

import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.dom4j.Node;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import main.*;
import dao.Dao;
import data.*;

public class CMD10 implements ICMD {
	private static final Logger log = Logger.getLogger(CMD10.class.getName());

	@Override
	public ChannelBuffer getBytes(int cmd, ChannelBuffer data) {
		String name = Global.readUTF(data);
		String content = BaseData.getContent(name);
		if ("配置信息".equals(name)) {
			content = this.getXMLcontent(content, data);
		}
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(cmd);
		buf.writeBytes(Global.getUTF(content));
		return buf;
	}

	/**
	 * 以下方法都是在改配置文件，加密用。
	 */
	private String getXMLcontent(String content, ChannelBuffer data) {
		Document doc = Global.xmlParser(content);// 解析content为可读文档
		if (doc.getElementsByTagName("lesson").getLength() == 0) {
			log.warning("");
			return "";
		}
		String token = "";
		int unlocky = 0;
		Device device = null;
		if (data.readableBytes() > 2) {
			String imei = Global.readUTF(data);
			log.info("imei = " + imei);
			if (imei.length() > 0) {
				device = Dao.getDevice(0, imei, "CMD10");
			}
		}
		if (device == null) {	/* 没有imi就生成一个token，通过channel信息带到客户端。 */
			token = Global.md5(ServerTimer.getFullWithS() + Math.random());
			unlocky = getUnlockyByToken(token);
			log.info("这是无imei的情况----token = " + token + "----unlocky = " + unlocky);
			NodeList channels = doc.getElementsByTagName("channel").item(0).getChildNodes();
			for (int i = 0; i < channels.getLength(); i++) {				
				Element e = (Element) channels.item(i);
				String Channelname = e.getAttribute("info") + "#" + token;
				e.setAttribute("info", Channelname);		
			}
		} else {
			if (device.getToken() == null || device.getToken().isEmpty()) {
				token = Global.md5(device.getId() + device.getImei() + device.getFirstTime() + Math.random());
				device.setToken(token);
			} else {// 这是有token的情况
				token = device.getToken();
			}
			unlocky = Global.getRandom(99999);
			device.setUnlockKey(unlocky);
			Dao.save(device);// 保存
			log.info("这是有imei的情况----token = " + token + "----unlocky = " + unlocky);
		}// Dao.getDevice必然会得到一个device
		/* 改url*/
		NodeList lessons = doc.getElementsByTagName("lesson");
		for (int i = 0; i < lessons.getLength(); i++) {
			Element e = (Element) lessons.item(i);
			int lesson = Global.getInt(e.getAttribute("id"));
			e.setAttribute("code", "" + next(unlocky, lesson));
			this.changeUrl("url", token, lesson, e);
			this.changeUrl("urlHW", token, lesson, e);
			this.changeUrl("urlTV", token, lesson, e);
		}
		String outxml = null;
		try {// Document 转回 String
			outxml = this.docmentToString(doc);
		} catch (TransformerException e) {
			log.warning("xml回写出错：" + e.getMessage());
		} // log.info("outxml = " + outxml);
		return outxml;
	}

	private void changeUrl(String url, String token, int lesson, Element e) {
		if (url.isEmpty() || !e.hasAttribute(url)) {// 防止不同配置文件url不同而报错
			return;
		}
		String urlValue = e.getAttribute(url);
		int cut = urlValue.lastIndexOf('/');
		urlValue = urlValue.substring(0, cut) + "/down2.php?token=" + token + "&lesson=" + lesson;
		e.setAttribute(url, urlValue);// 关键执行步骤
	}

	public String docmentToString(Document doc) throws TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer tf = factory.newTransformer();
		tf.setOutputProperty("encoding", "utf8");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		tf.transform(new DOMSource(doc), new StreamResult(bos));
		return bos.toString();
	}

	/**
	 * 下面都是公开方法，unlocky的统一算法
	 */
	public static int next(int code, int count) {
		for (int i = 0; i < count; i++) {
			code ^= (code << 20);
			code ^= (code >> 21);
			code ^= (code << 5);
		}
		code = 0xFFFF & code + code >> 16;
		return code;
	}

	public static int getUnlockyByToken(String token) {
		int code = 0;
		if (token == null) {
			return code;
		}
		for (int i = 0; i < token.length(); i++) {
			code += token.charAt(i) << (i % 5);
		}
		return code;
	}
}
