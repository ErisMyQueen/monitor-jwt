package com.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {

    @Value("${spring.security.jwt.key}")
    String key;   //密钥

    @Value("${spring.security.jwt.expire}")
    int expire;   //Jwt过期时间

    @Resource
    StringRedisTemplate template;


    public boolean invalidateJwt(String headerToken) {
        String token = this.convertToken(headerToken);
        if(token == null) return false;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try{
            DecodedJWT jwt = jwtVerifier.verify(token); //验证是否异常
            String id = jwt.getId();
            return deleteToken(id,jwt.getExpiresAt());
        }
        catch(JWTVerificationException e){
            return false;
        }
    }


    private boolean deleteToken(String uuid,Date time) {
        if(this.isInvalidToken(uuid))
            return false;
        Date now = new Date();
        long expire=Math.max(time.getTime()-now.getTime(),0);
        template.opsForValue().set(Const.JWT_BLACK_LIST+uuid,"",expire, TimeUnit.MILLISECONDS);
        return true;
    }

    private boolean isInvalidToken(String uuid) {
        return Boolean.TRUE.equals(template.hasKey(Const.JWT_BLACK_LIST + uuid));
    }

    /**
     * 解析Jwt令牌
     * @param headerToken 请求头中携带的令牌
     * @return DecodedJWT
     */
    public DecodedJWT resolveJwt(String headerToken){
        String token = this.convertToken(headerToken);
        if(token == null) return null;
        Algorithm algorithm = Algorithm.HMAC256(key);   //使用原算法的key进行解析
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();   //使用原算法的key进行解析
        try {
            DecodedJWT verify = jwtVerifier.verify(token);// 验证Jwt是否合法，是否篡改过
            if(this.isInvalidToken(verify.getId()))
                return  null;
            Date expiresAt = verify.getExpiresAt();//获取过期的日期

            return new Date().after(expiresAt)?null:verify;//判断是否过期
        }
        // jwt验证失败
        catch (JWTVerificationException e) {
            return null;
        }
    }

    /**
     * 根据配置快速计算过期时间
     * @return 过期时间
     */
    public Date expireTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, expire*24);
        return calendar.getTime();
    }

    // 创建Jwt令牌
    public String createJwt(UserDetails details,int id ,String username)
    {
        Algorithm algorithm = Algorithm.HMAC256(key); //加密算法
        Date expire=this.expireTime();
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("id",id)
                .withClaim("name",username)
//                .withClaim("authorities", String.valueOf(details.getAuthorities()))
                .withClaim("authorities", details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()) //设置权限
                .withExpiresAt(expire) //设置过期时间
                .withIssuedAt(new Date())//Jwt颁发时间
                .sign(algorithm); //算法签名


    }


    /**
     * 将jwt对象中的内容封装为UserDetails
     * @param jwt 已解析的Jwt对象
     * @return UserDetails
     */
    public UserDetails toUser(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return User
                .withUsername(claims.get("name").asString())
                .password("******")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }

    /**
     * 将jwt对象中的用户ID提取出来
     * @param jwt 已解析的Jwt对象
     * @return 用户ID
     */
    public Integer toId(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("id").asInt();
    }

    /**
     * 校验并转换请求头中的Token令牌
     * @param headerToken 请求头中的Token
     * @return 转换后的令牌
     */
    private String convertToken(String headerToken){
        if(headerToken == null || !headerToken.startsWith("Bearer "))
            return null;
        return headerToken.substring(7);
    }

}
