package com.lcj.mutichannel.Controller;

import com.lcj.mutichannel.Netty.TcpConnectionHandler;
import com.lcj.mutichannel.model.RespBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class AdcController {

    @GetMapping("/getAdcData")
    public RespBean adcData(@RequestParam Integer pageSize) {
        int size = TcpConnectionHandler.data.size();
        if (size == 0) {
            return RespBean.serverError("数据还没准备好");
        } else if (pageSize >= TcpConnectionHandler.DATA_LEN || pageSize >= size) {
            return RespBean.ok("获取成功1", TcpConnectionHandler.data);
        } else {
            return RespBean.ok("获取成功2", new ArrayList<>(TcpConnectionHandler.data).subList(size - pageSize, size));
        }
    }
}
