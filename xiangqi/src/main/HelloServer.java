package main;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.netty.bootstrap.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import dao.Dao;


public class HelloServer {

	private static final Logger logger = Logger.getLogger(HelloServer.class.getName());
	public static String sip = "120.55.126.21";
	public static String cbip = "xiangqipay.miracle-cn.com";
	public static int httpPort = 20000;
	
	public static void main(String[] args){
		if (args.length >0) {
			httpPort = Global.getInt(args[0]);
			for(String str : args){
				if(str.startsWith("sip=")){
					sip = str.replace("sip=", "");
				}else if(str.startsWith("cbip=")){
					cbip = str.replace("cbip=", "");
				}
			}
		}
		logger.log(Level.INFO, "Http Server init!");
        ServerBootstrap httpbootstrap = new ServerBootstrap(new NioServerSocketChannelFactory());
		httpbootstrap.setPipelineFactory(new HttpChannelPipelineFactory());
		httpbootstrap.bind(new InetSocketAddress(HelloServer.httpPort));
        logger.log(Level.INFO, "Http Server started!");
	}

}
