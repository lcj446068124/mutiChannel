package com.lcj.mutichannel.Netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhanghaoyan.zhy
 * @date 2021/6/23
 */

@Slf4j
public class TcpDataClient {
    private static final String HOST = "127.0.0.1";
    private static final int PORT= 23333;

    public static void main(String[] args){
        new TcpDataClient().start(HOST, PORT);
    }

    public void start(String host, int port) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap client = new Bootstrap().group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new HelloWorldClientHandler());
                        }
                    });
            ChannelFuture future = client.connect(host, port).sync();
            log.debug("client start");
            future.channel().closeFuture().sync();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();

        }

    }

    public static class HelloWorldClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            int cnt = 0;
//            byte[] bytes = {1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8};
            byte[] bytes = {1, 2};
            while (true) {
                bytes[0]++;
                bytes[0] %= 127;
                ctx.writeAndFlush(Unpooled.wrappedBuffer(bytes));
                Thread.sleep(1);
                cnt++;
                if(cnt > 0)
                    break;
            }

        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("HelloWorldClientHandler read Message:ff"+ msg.toString());
        }
    }
}
