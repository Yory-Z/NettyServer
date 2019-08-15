package com.yoryz.netty.core.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 * @date 2019/5/5 16:56
 */
public class TextWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Logger logger = LogManager.getLogger(TextWebSocketHandler.class);

    /**
     * used to keep and manage the client's channel
     */
    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // receive data from the client
        String content = msg.text();
        logger.info("receive data: {}", content);

        for (Channel channel : clients) {
            if (!channel.equals(ctx.channel())) {
                channel.writeAndFlush(new TextWebSocketFrame(content));
            }
        }
    }

    /**
     * after the client link with the server
     * get the client's channel, and added into ChannelGroup
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        clients.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // when trigger the function, the ChannelGroup will remove the client's channel automatically
//        clients.remove(ctx.channel());

        logger.info("client close, the channel's long id is: {}", ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Encounter error");
        cause.printStackTrace();
    }

}
