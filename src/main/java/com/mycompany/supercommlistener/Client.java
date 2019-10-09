/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.supercommlistener;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements Runnable {

    private final int port;
    private final String ip;

    public Client(String ip, int port) {
        
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {
        EventLoopGroup masterGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.group(workerGroup);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap.remoteAddress(new InetSocketAddress(ip, port));
            clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                    socketChannel.pipeline().addLast(new ServerHandler(true));
                }
            });
            ChannelFuture channelFuture = clientBootstrap.connect().sync();
            channelFuture.channel().closeFuture().sync();

//            bootstrap.group(masterGroup, workerGroup)
//                    .channel(NioServerSocketChannel.class)
//                    .handler(new LoggingHandler(LogLevel.ERROR))
//                    .childHandler(new ChannelInitializer() {
//                        @Override
//                        protected void initChannel(Channel c) throws Exception {
//                            c.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
//                            c.pipeline().addLast(new ServerHandler());
//                        }
//                    })
//                    .option(ChannelOption.SO_BACKLOG, 128)
//                    .childOption(ChannelOption.SO_KEEPALIVE, true);
//
//            ChannelFuture future = bootstrap.bind(this.port).sync();
//            future.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            workerGroup.shutdownGracefully();
            masterGroup.shutdownGracefully();
        }
    }
}
