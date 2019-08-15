# NettyServer
Netty 服务器，能处理 Http 请求，还有通过 WebSocket 实现聊天功能，低仿的 Spring 框架

功能：实现简单的Http请求解析，通过路由转发至指定的接口，Http响应。使用自定义注解标记每个模块类，例如：Controller, Service. 使用反射实现类属性注入、请求转发。实现简单的一对一聊天。
技术：Netty, WebSocket, Java反射, Mybatis, MySql
