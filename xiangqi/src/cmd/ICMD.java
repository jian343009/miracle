package cmd;

import org.jboss.netty.buffer.ChannelBuffer;

public interface ICMD {
	public ChannelBuffer getBytes(int code, ChannelBuffer data);
}
