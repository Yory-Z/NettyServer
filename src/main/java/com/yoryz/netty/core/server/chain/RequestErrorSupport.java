package com.yoryz.netty.core.server.chain;

import com.yoryz.netty.util.MyResponse;
import io.netty.handler.codec.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 * @date 2019/10/26 14:08
 */
public class RequestErrorSupport extends AbstractChainSupport {

    private static Logger logger = LogManager.getLogger(RequestErrorSupport.class);

    public RequestErrorSupport(String supportName) {
        super(supportName);
    }

    @Override
    protected boolean resolve(MyResponse myResponse) {
        return MyResponse.isRequestError(myResponse);
    }

    @Override
    protected FullHttpResponse solve(MyResponse myResponse) {
        logger.info(getSupportName() + " resolve the MyResponse");

        // return 400
        // parameter name not match
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.BAD_REQUEST, constructResource(myResponse));
    }
}
