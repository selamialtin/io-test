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
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Serial implements Runnable {

    private final Map<String, String> params;

    public Serial(Map<String, String> params) {

        this.params = params;
    }

    @Override
    public void run() {

        EventLoopGroup group = new OioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();

            b.group(group)
                    .channel(RxtxChannel.class)
                    .option(RxtxChannelOption.BAUD_RATE, Integer.parseInt(params.get(Main.PARAM_BAUD)))
                    .option(RxtxChannelOption.DATA_BITS, RxtxChannelConfig.Databits.valueOf(Integer.parseInt(params.get(Main.PARAM_DATABIT))))
                    .option(RxtxChannelOption.STOP_BITS, RxtxChannelConfig.Stopbits.valueOf(Integer.parseInt(params.get(Main.PARAM_STOPBIT))))
                    .option(RxtxChannelOption.PARITY_BIT, RxtxChannelConfig.Paritybit.valueOf(Integer.parseInt(params.get(Main.PARAM_PARITY))))
                    .handler(new ChannelInitializer<RxtxChannel>() {
                        @Override
                        public void initChannel(RxtxChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new LoggingHandler(LogLevel.INFO),
                                    new LineBasedFrameDecoder(32768),
                                    new StringEncoder(),
                                    new StringDecoder(),
                                    new RxClientHandler(false)
                            );
                        }
                    });

            ChannelFuture f = b.connect(new RxtxDeviceAddress(params.get(Main.PARAM_SERIAL))).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            Logger.getLogger(Serial.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            group.shutdownGracefully();
        }

    }
}
