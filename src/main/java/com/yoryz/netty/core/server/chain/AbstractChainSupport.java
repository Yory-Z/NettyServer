package com.yoryz.netty.core.server.chain;

import com.alibaba.fastjson.JSON;
import com.yoryz.netty.util.MyResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 * @date 2019/10/26 13:51
 */
public abstract class AbstractChainSupport {

    private String supportName;

    private AbstractChainSupport next;

    AbstractChainSupport(String supportName) {
        this.supportName = supportName;
    }

    public AbstractChainSupport setNext(AbstractChainSupport next) {
        this.next = next;
        return next;
    }

    public final FullHttpResponse support(MyResponse myResponse) {
        for (AbstractChainSupport now = this; ; now = now.next) {
            if (now.resolve(myResponse)) {
                return now.solve(myResponse);
            } else if (now.next == null) {
                break;
            }
        }
        return null;
    }

    /**
     * the sub classes need to implement this abstract method,
     * if the concrete support class can resolve the MyResponse, return true
     * or return false
     *
     * @param myResponse the object need to check
     * @return true means the concrete support class can resolve this MyResponse
     * or return false
     */
    protected abstract boolean resolve(MyResponse myResponse);

    /**
     * the method the concrete support resolve the MyResponse
     *
     * @param myResponse the object need to resolve
     * @return the HttpResponse return to the client
     */
    protected abstract FullHttpResponse solve(MyResponse myResponse);

    protected String getSupportName() {
        return supportName;
    }

    ByteBuf constructResource(MyResponse myResponse) {
        return Unpooled.wrappedBuffer(JSON.toJSON(myResponse).toString().getBytes());
    }

}
