package com.example.filter;

import com.example.entity.RestBean;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


//Redis实现简单的限流
@Component
@Order(Const.ORDER_LIMIT)
public class FlowLimitFilter extends HttpFilter {

    @Resource
    StringRedisTemplate template;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String address = request.getRemoteAddr();
        if(this.tryCount(address))
        {
            chain.doFilter(request, response);//下一步
        }
        else {
            this.writeBlockMessage(response);
        }
    }

    // 对请求次数过多的用户进行堵塞
    private void writeBlockMessage(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(RestBean.forbidden("操作频繁，请稍后再试").asJsonString());
    }

    // 使用redis对同一ip地址的用户请求次数进行计数
    private boolean tryCount(String ip)
    {  //对ip地址加锁 防止同时同一访问
        synchronized (ip.intern()){
            if (Boolean.TRUE.equals(template.hasKey(Const.FLOW_LIMIT_BLOCK + ip))) {
                return false;
            }
            return this.limitPeriodCheck(ip);
        }
    }

    private boolean limitPeriodCheck(String ip)
    {
        if(Boolean.TRUE.equals(template.hasKey(Const.FLOW_LIMIT_COUINTER + ip)))
        {
            Long increment = Optional.ofNullable(template.opsForValue().increment(Const.FLOW_LIMIT_COUINTER)).orElse(0L);
            if (increment>10) {
                template.opsForValue().set(Const.FLOW_LIMIT_BLOCK+ip,"",30,TimeUnit.SECONDS);
                return false;
            }
        }
        else{
            template.opsForValue().set(Const.FLOW_LIMIT_COUINTER+ip,"1",3, TimeUnit.SECONDS);
        }
        return true;
    }

}
