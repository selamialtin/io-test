/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.supercommlistener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.Scanner;

/**
 *
 * @author selami
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private final boolean waitAfterConnect;

    public ServerHandler(boolean waitAfterConnect) {
        this.waitAfterConnect = waitAfterConnect;
    }

    public void waitCommand(final ChannelHandlerContext ctx) {
        new Thread(() -> {
            Scanner s = new Scanner(System.in);
            String command = s.next();
            System.out.println("Send command (press Q for exit)");
            if (!command.toUpperCase().equals("Q")) {
                ByteBuf b = ctx.alloc().buffer();
                b.writeBytes(hexStringToByteArray(command));
                ctx.write(b);
            }
        }).start();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx); //To change body of generated methods, choose Tools | Templates.
        ByteBuf b = ctx.alloc().buffer();
        ctx.writeAndFlush(b);
        System.out.println("Device connected");
        if (waitAfterConnect) {
            waitCommand(ctx);
        }
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
//        System.out.println("Server received: " + in.readableBytes());
//        System.out.println(ByteBufUtil.hexDump(in));
        in.release();
        waitCommand(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx); //To change body of generated methods, choose Tools | Templates.
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
