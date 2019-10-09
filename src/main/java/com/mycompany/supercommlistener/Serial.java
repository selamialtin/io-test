/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.supercommlistener;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.rxtx.RxtxChannel;
import io.netty.channel.rxtx.RxtxChannelConfig;
import io.netty.channel.rxtx.RxtxChannelOption;
import io.netty.channel.rxtx.RxtxDeviceAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Serial implements Runnable {

    private final String port;

    public Serial(String port) {

        this.port = port;
    }

    @Override
    public void run() {

        EventLoopGroup group = new OioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();

            b.group(group)
                    .channel(RxtxChannel.class)
                    .option(RxtxChannelOption.BAUD_RATE, 115200)
                    .option(RxtxChannelOption.DATA_BITS, RxtxChannelConfig.Databits.DATABITS_8)
                    .option(RxtxChannelOption.PARITY_BIT, RxtxChannelConfig.Paritybit.NONE)
                    .handler(new ChannelInitializer<RxtxChannel>() {
                        @Override
                        public void initChannel(RxtxChannel ch) throws Exception {
                            ch.pipeline().addLast(
//                                    new LineBasedFrameDecoder(32768),
//                                    new StringEncoder(),
//                                    new StringDecoder(),
                                    new ServerHandler()
                            );
                        }
                    });

            ChannelFuture f = b.connect(new RxtxDeviceAddress(port)).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            Logger.getLogger(Serial.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            group.shutdownGracefully();
        }

    }
}
