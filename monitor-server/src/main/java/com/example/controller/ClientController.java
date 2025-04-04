package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.Client;
import com.example.entity.vo.request.ClientDetailVO;
import com.example.service.ClientService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RequestMapping
@RestController("/monitor")
public class ClientController {

    @Resource
    ClientService service;

    @GetMapping("/register")
    public RestBean<Void> registerClient(@RequestHeader("Authorization")String token) {
        return service.verifyAndRegister(token)?
                RestBean.success():RestBean.failure(401,"注册失败，前检查Token是否正确");
    }

    @PostMapping("/detail")
    public RestBean<Void> updateClientDetails(@RequestAttribute(Const.ATTR_CLIENT) Client client,
                                              @RequestBody @Valid ClientDetailVO vo) {
        service.updateClientDetail(vo, client);
        return RestBean.success();
    }
}
