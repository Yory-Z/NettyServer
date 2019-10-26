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
 * @date 2019/10/26 14:07
 */
public class NotFoundSupport extends AbstractChainSupport {

    private static Logger logger = LogManager.getLogger(NotFoundSupport.class);

    public NotFoundSupport(String supportName) {
        super(supportName);
    }

    @Override
    protected boolean resolve(MyResponse myResponse) {
        return MyResponse.isNotFound(myResponse);
    }

    @Override
    protected FullHttpResponse solve(MyResponse myResponse) {
        logger.info(getSupportName() + " resolve the MyResponse");

        // return 404
        // the uri doesn't exist
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.NOT_FOUND, constructResource(myResponse));
    }
}
