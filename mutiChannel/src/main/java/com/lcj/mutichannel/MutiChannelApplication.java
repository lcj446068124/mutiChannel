package com.lcj.mutichannel;

import com.lcj.mutichannel.Netty.TcpConnectionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MutiChannelApplication implements CommandLineRunner {

    @Autowired
    TcpConnectionHandler tcpConnectionHandler;

    public static void main(String[] args) {
        SpringApplication.run(MutiChannelApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        tcpConnectionHandler.start();
    }
}
