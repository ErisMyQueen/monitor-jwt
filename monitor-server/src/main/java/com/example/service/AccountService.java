package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Account;

import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailResetVO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account> , UserDetailsService {
    Account findAccountByNameOrEmail(String text);

    Account findAccountById(int id);

    String registerEmailVerifyCode(String type,String email,String ip);

    String resetConfirm(ConfirmResetVO vo); // 重置密码的第一步

    String resetEmailAccountPassword(EmailResetVO vo);//重置密码的第二步



}
