package com.lxs.protobuf.server;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class NettyServerChannelPool {
    
    public static ChannelGroup channels=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
}
