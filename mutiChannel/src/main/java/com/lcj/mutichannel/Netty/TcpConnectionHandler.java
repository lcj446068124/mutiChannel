package com.lcj.mutichannel.Netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class TcpConnectionHandler {
    @Value("${tcpConnection.port}")
    private int port;

    public static final int DATA_LEN = 500;
    public static Deque<List<Double>> data = new LinkedList<>(new LinkedList<>());

    /**
     * 创建两个EventLoopGroup，即两个线程池，boss线程池用于接收客户端的连接，
     * 一个线程监听一个端口，一般只会监听一个端口所以只需一个线程
     * work池用于处理网络连接数据读写或者后续的业务处理（可指定另外的线程处理业务，
     * work完成数据读写）
     */
    private final EventLoopGroup boss = new NioEventLoopGroup(1);
    private final EventLoopGroup work = new NioEventLoopGroup();

    public void start(){
        try{
            ServerBootstrap server = new ServerBootstrap()
                    .group(boss,work).channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new FixedLengthFrameDecoder(16))
                                    .addLast(new ConnectionHandler());
                        }
                    });
            ChannelFuture future = server.bind().sync();
            log.info("Tcp DataSource Listen {}",port);
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            stop();
        }
    }

    public void stop(){
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }

    public static class ConnectionHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            log.info("start listening");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            LinkedList<Double> doubles = new LinkedList<>();
            for (int i = 0; i < buf.readableBytes(); i+=2) {
                int unsignedShort = buf.getUnsignedShort(i);
                Double dt = (unsignedShort - 32768) * 10.24 / 32768.0;
                dt = dt + (dt - 2.227) * 0.0002;
                doubles.add(dt);
            }
            log.info(doubles.toString());
            TcpConnectionHandler.data.addLast(doubles);
            if (TcpConnectionHandler.data.size() > DATA_LEN) {
                TcpConnectionHandler.data.removeFirst();
            }
        }
    }
}
