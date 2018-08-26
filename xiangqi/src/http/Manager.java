package http;

import http.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import main.Global;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.CharsetUtil;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

//import sun.tools.tree.ThisExpression;

import cmd.*;

public class Manager extends SimpleChannelUpstreamHandler implements TimerTask {
	private static final Logger logger = Logger.getLogger(Manager.class.getName());
	private static Timer timer = new HashedWheelTimer(500,TimeUnit.MILLISECONDS);
	private int count = 0;
	private Timeout timeout = null;
	private Channel channel = null;
	

	private void start(){
		if(this.timeout == null){
			this.timeout = timer.newTimeout(this, 1, TimeUnit.SECONDS);
		}
	}
	private void stop(){
		if(this.channel != null){
			this.channel.close();
			this.channel = null;
		}
		if(this.timeout != null){
			this.timeout.cancel();
			this.timeout = null;
		}
	}
	@Override
	public void run(Timeout timeout) throws Exception {
		if(this.channel == null || this.count >60){
			this.stop();
		}else{
			this.count++;
			this.timeout = timer.newTimeout(this, 1, TimeUnit.SECONDS);
		}
		
	}
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		super.channelOpen(ctx, e);
		this.channel = ctx.getChannel();
		start();
	}
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		super.channelClosed(ctx, e);
		this.channel = null;
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		super.exceptionCaught(ctx, e);
		logger.warn(this+" exceptionCaught() channel:"+this.channel);
		e.getCause().printStackTrace();
		this.stop();
	}
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e){
		HttpRequest request = (HttpRequest) e.getMessage();  
		String url = request.getUri();
		ChannelBuffer buf = request.getContent();
		
		ChannelBuffer buffer=ChannelBuffers.dynamicBuffer();
		String type = "text/html; charset=UTF-8";
		
		if(url.equals("/weiqicmd"))
		{
			if(buf.readableBytes() >= 2){
				int code = buf.readUnsignedShort();
				logger.info("HttpCMD cmdID:"+code);
				ICMD cmd = null;
				try {
					cmd = (ICMD) Class.forName("cmd.CMD"+code).newInstance();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				if(cmd != null){
					buffer.writeBytes(cmd.getBytes(code, buf));
				}
			}
		}else{
			String content = buf.toString(Charset.forName("UTF-8"));
			if( url.startsWith("/heepay") ||
				url.startsWith("/letvpay") ||
				url.startsWith("/basedata")){
				
			}else{
				try {
					url = URLDecoder.decode(url, "utf-8");
					content = URLDecoder.decode(content, "utf-8");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			logger.warn("url:"+url+",content:"+content+","+url.startsWith("/getunlockkey?"));
			
			IHtml h = new Html();
			String hl = h.getHtml(content);
			if(url.equals("/record")){
				hl = new Html_record().getHtml(content);
			}else if(url.equals("/alipay")){
				hl = new Html_alipay().getHtml(content);
			}else if(url.equals("/wpay")){
				hl = new Html_wiipay().getHtml(content);
			}else if(url.equals("/rate")){
				hl = new Html_rate().getHtml(content);
			}else if(url.startsWith("/heepay")){
				hl = new Html_heepay().getHtml(url.replace("/heepay?", ""));
				Global.addRecord(0, "", url, content+" #return:"+hl);
			}else if(url.startsWith("/letvpay?")){
				hl = new Html_letvpay().getHtml(url.replace("/letvpay?", ""));
				Global.addRecord(0, "", url, content+" #return:"+hl);
			}else if(url.equals("/newletvpay")){
				hl = new Html_newletvpay().getHtml(content);
				Global.addRecord(0, "", url, content+" #return:"+hl);
			}else if(url.startsWith("/newletvpay?")){
				hl = new Html_newletvpay().getHtml(url.replace("/newletvpay?", ""));
				Global.addRecord(0, "", url, content+" #return:"+hl);
			}else if(url.equals("/newletvrefund") || url.startsWith("/newletvrefund?")){
				hl = "{\"code\":0,\"msg\":\"\"}";
			}else if(url.equals("/manage_device")){
				Global.addRecord(0, "", url, content);
				hl = new Html_editdevice().getHtml(content);
			}else if(url.equals("/updatedevice")){
				Global.addRecord(0, "", url, content);
				hl = new Html_updatedevice().getHtml(content);
			}else if(url.equals("/basedata")){
				hl = new Html_basedata().getHtml(content);
			}else if(url.equals("/hwsign")){
				hl = new Html_hwsign().getHtml(content);
			}else if(url.equals("/hwpay")){
				hl = new Html_hwpay().getHtml(content);
				type = "application/json; charset=UTF-8";
				Global.addRecord(0, "", url, content+" #return:"+hl);
			}else if(url.startsWith("/tuan?")){
				hl = new Html_tuan().getHtml(url.replace("/tuan?", ""));
			}else if(url.equals("/wxpay")){
				hl = new Html_wxpay().getHtml(content);
				Global.addRecord(0, url, content, ""+hl);
			}else if(url.startsWith("/getunlockkey?")){
				logger.info("unlock:"+url.replace("/getunlockkey?", ""));
				hl = new Html_getUnlockKey().getHtml(url.replace("/getunlockkey?", ""));
			}else if(url.startsWith("/comment")){
				hl = new Html_comment().getHtml(content);
			}
			
			logger.warn("url:"+url+",length:"+hl.length());
			buffer.writeBytes(hl.getBytes(Charset.forName("utf-8")));
		}
		
		HttpMessage response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);  
		response.setContent(buffer);    
		response.headers().add("Access-Control-Allow-Origin", "*");
		response.headers().add("Content-Type", type);
		response.headers().add("Content-Length", response.getContent().writerIndex()); 
		Channel ch = e.getChannel(); 
		ch.write(response).addListener(ChannelFutureListener.CLOSE);
//		ch.disconnect();
//		ch.close(); 
	}

}
