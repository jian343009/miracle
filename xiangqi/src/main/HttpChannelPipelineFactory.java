package main;

import http.Manager;

import java.util.logging.Level;
import java.util.logging.Logger;



import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

public class HttpChannelPipelineFactory implements ChannelPipelineFactory {
	
	private static final Logger logger = Logger.getLogger("HttpChannelPipelineFactory");

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		logger.log(Level.INFO,"new pipeline!");
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("decoder", new HttpRequestDecoder());
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("aggregator", new HttpChunkAggregator(255360));
		pipeline.addLast("handler", new Manager());
		return pipeline;
	}
	
}
