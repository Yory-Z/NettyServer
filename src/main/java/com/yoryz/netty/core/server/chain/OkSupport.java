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
 * @date 2019/10/26 14:03
 */
public class OkSupport extends AbstractChainSupport {

    private static Logger logger = LogManager.getLogger(OkSupport.class);

    public OkSupport(String supportName) {
        super(supportName);
    }

    @Override
    protected boolean resolve(MyResponse myResponse) {
        return MyResponse.isOk(myResponse);
    }

    @Override
    protected FullHttpResponse solve(MyResponse myResponse) {
        logger.info(getSupportName() + " resolve the MyResponse");

        // return 200
        // executed successfully
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK, constructResource(myResponse));
    }
}
