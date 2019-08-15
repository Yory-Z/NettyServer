package com.yoryz.netty;

import com.yoryz.netty.core.parse.Populate;
import com.yoryz.netty.core.scan.AbstractPackageScanner;
import com.yoryz.netty.core.scan.ComponentFactory;
import com.yoryz.netty.core.server.ChatServer;
import com.yoryz.netty.core.parse.InterfaceParser;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 * @date 2019/5/16 20:15
 */
public class AppBooster {

    public static void main(String[] args) throws Exception {

        final String basePackage = "com.yoryz.netty";

        AbstractPackageScanner scanner =  ComponentFactory.getInstance();
        scanner.scan(basePackage);

        Populate.getInstance().populateBean();

        // invoke the getInstance method, initializing the InterfaceParser process
        InterfaceParser.getInstance();

        new ChatServer().start(8080);

    }

}
