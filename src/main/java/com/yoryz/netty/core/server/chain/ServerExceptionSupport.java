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
 * @date 2019/10/26 14:09
 */
public class ServerExceptionSupport extends AbstractChainSupport {

    private static Logger logger = LogManager.getLogger(ServerExceptionSupport.class);

    public ServerExceptionSupport(String supportName) {
        super(supportName);
    }

    @Override
    protected boolean resolve(MyResponse myResponse) {
        return MyResponse.isServerException(myResponse);
    }

    @Override
    protected FullHttpResponse solve(MyResponse myResponse) {
        logger.info(getSupportName() + " resolve the MyResponse");

        // return 500
        // may be the executing process encountered exception
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR, constructResource(myResponse));
    }
}
