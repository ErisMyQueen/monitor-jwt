package com.example.filter;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.entity.RestBean;
import com.example.entity.dto.Client;
import com.example.service.ClientService;
import com.example.utils.Const;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
/**
 * 用于对请求头中Jwt令牌进行校验的工具，为当前请求添加用户验证信息
 * 并将用户的ID存放在请求对象属性中，方便后续使用
 */
@Component
public class JwtAuthorizeFilter extends OncePerRequestFilter {

    @Resource
    JwtUtils utils;

    @Resource
    ClientService service;

    /*
    验证了请求头中Authorization的Token并将其包装为认证信息,UsernamePasswordAuthenticationToken
    将认证信息authentication 存储到全局的SecurityContextHolder, 使请求的认证信息（身份、权限）可以被后续复用
    将用户的ID存储到请求属性中 jwtAuthorizeFliter
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");// 从请求头中读取token
        String uri=request.getRequestURI();
        if(uri.startsWith("/monitor"))
        {
            if(!uri.endsWith("/register"))
            {
                Client client=service.findClientByToken(authorization);
                if(client==null)
                {
                    response.setStatus(401);
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().write(RestBean.failure(401,"未注册").asJsonString());
                    return;
                }
                else{
                    request.setAttribute(Const.ATTR_CLIENT,client);
                }
            }
        }
        else {
            DecodedJWT jwt=utils.resolveJwt(authorization); //解析token
            // jwt不等于null才进行授权， 等于空直接下一步
            if(jwt != null) {
                UserDetails user = utils.toUser(jwt); //将jwt转换为userdetails
                UsernamePasswordAuthenticationToken authentication = //给用户授权
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));//给用户授权
                SecurityContextHolder.getContext().setAuthentication(authentication);// 将认证信息丢入里面，就表示认证已经通过了
                request.setAttribute(Const.ATTR_USER_ID, utils.toId(jwt));
            }
        }
        filterChain.doFilter(request, response);
    }
}
