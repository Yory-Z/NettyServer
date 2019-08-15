package com.yoryz.netty.core.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 * @date 2019/5/5 17:04
 */
public class ChatServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new ChunkedWriteHandler());
        // ===================== the above is the supports for http ========================

        /*
          the routing for the client to request: /ws
          This handler does all the heavy lifting to run a websocket server.
          this will handle handshaking (close, ping, pong) ping + pong = heartbeat
          for websocket, it transfer data by frames, different frames relevant to the different data type
        */
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

        // my http handler
        pipeline.addLast(new HttpRequestHandler());

        // self WebSocket defined handler
        pipeline.addLast(new TextWebSocketHandler());
    }
}
