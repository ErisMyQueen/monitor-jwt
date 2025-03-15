package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Client;
import com.example.entity.vo.request.ClientDetailVO;

public interface ClientService extends IService<Client> {
    String registerToken();
    Client findClientByToken(String token);
    Client findClientById(int id);
    boolean verifyAndRegister(String token);
    void updateClientDetail(ClientDetailVO vo,Client client);
}
