package com.yoryz.netty.core.server;

import com.alibaba.fastjson.JSON;
import com.yoryz.netty.util.MyResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static Logger logger = LogManager.getLogger(HttpRequestHandler.class);

    private static Router router = Router.getInstance();

    /**
     * the root file path this project
     */
    private final static String ROOT_URI;

    /**
     * http header value, text/html
     */
    private final static String TEXT_HTML = "text/html";

    /**
     * http header value, image/jep
     */
    private final static String IMAGE = "image/jpeg";

    private final static String APPLICATION_JSON = "application/json";

    /**
     * for browser request
     */
    private final static String FAVICON = "/favicon.ico";

    /**
     * favicon.ico path
     */
    private final static File ICON;

    /**
     * the index file of this project
     */
    private final static File INDEX_FILE;

    static {
        try {
            String path = HttpRequestHandler.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI().toString();
            ROOT_URI = !path.contains("file:") ? path : path.substring(5);

            ICON = new File(ROOT_URI + "coffee.png");
            INDEX_FILE = new File("E:\\FrontProject\\IMessage\\index.html");

        } catch (URISyntaxException e) {
            throw new RuntimeException("Welcome file not found.");
        }
    }

    /**
     * handle the http request, the request data is json
     *
     * @param ctx ChannelHandlerContext
     * @param msg FullHttpRequest
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
/*        if (HttpUtil.is100ContinueExpected(msg)) {
            send100Continue(ctx);
        }*/

        String uri = msg.uri();
        logger.info("uri: {}", uri);
        logger.info("msg content: {}", msg.content().toString(CharsetUtil.UTF_8));

        FullHttpResponse response;
        String contentType;

        if (FAVICON.equals(uri)) {
            // the browser will request this icon, favicon.ico
            response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.OK, readFile(ICON));
            contentType = IMAGE;

        } else if ("/".equals(uri)){
            // when request the domain or ip, not path
            response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.OK, readFile(INDEX_FILE));
            contentType = TEXT_HTML;

        } else {
            // deal with this project business, request uri has specified path
            MyResponse myResponse = router.analyse(msg);
            ByteBuf res = Unpooled.wrappedBuffer(JSON.toJSON(myResponse).toString().getBytes());
            if (MyResponse.isOk(myResponse)) {
                // return 200
                // executed successfully
                response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, HttpResponseStatus.OK, res);

            } else if (MyResponse.isNotFound(myResponse)){
                // return 404
                // the uri doesn't exist
                response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, res);

            } else if (MyResponse.isRequestError(myResponse)) {
                // return 400
                // parameter name not match
                response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, res);

            } else {
                // return 500
                // may be the executing process encountered exception
                response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, res);

            }
            contentType = APPLICATION_JSON;

        }
        setHeaders(msg, response, contentType);
        ctx.write(response);
    }

    /**
     * read the specified file, return ByteBuf
     *
     * @param file the file need to read
     * @return file's ByteBuf
     * @throws IOException this file is not correct
     */
    private ByteBuf readFile(File file) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        ByteBuf buf = Unpooled.buffer();
        byte[] bytes = new byte[1024];
        int len;
        while ((len = bis.read(bytes)) != -1) {
            buf.writeBytes(bytes, 0, len);
        }
        return buf;
    }

    /**
     * fill the headers of HttpResponse
     *
     * @param request httpRequest, used to get the keep-alive
     * @param response fill it's headers, contentType, contentLength and Connection
     * @param contentType the response data's contentType
     */
    private void setHeaders(HttpRequest request, FullHttpResponse response, String contentType) {
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        if (HttpUtil.isKeepAlive(request)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        logger.info("Http request channel read complete...");
        ctx.flush();
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Encounter exception: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
