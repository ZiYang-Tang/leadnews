package com.heima.wemedia.gateway.filter;

import com.heima.wemedia.gateway.utils.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局网关过滤器
 *
 * @author houhai
 */
@Component
@Log4j2
public class AuthorizeFilter implements GlobalFilter, Ordered {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //1.获取请求对象和响应对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //2.判断当前的请求是否为登录，如果是，直接放行
        final String login = "/login/in";
        if (request.getURI().getPath().contains(login)) {
            //判断url包含登录请求就放行
            return chain.filter(exchange);
        }

        //3.获取当前用户的请求头jwt信息
        HttpHeaders headers = request.getHeaders();
        //String token = headers.getFirst("token");

        //因为之前登陆设置的带的是短表示，现在获取JTI短标识
        String jti = headers.getFirst("token");

        //4.判断当前令牌是否存在
        if (StringUtils.isEmpty(jti)) {
            //不存在直接向客户端返回错误提示
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //从redis中获取真正的token
        String token = (String) redisTemplate.boundValueOps(jti).get();

        try {
            //5.如果令牌存在，解析jwt令牌，判断该令牌是否合法，如果不合法，则向客户端返回错误信息
            //获得token的载荷
            Claims claims = AppJwtUtil.getClaimsBody(token);
            //判断token是否过期
            int result = AppJwtUtil.verifyToken(claims);
            if (result == 0 || result == -1) {
                //5.1 合法，则向header中重新设置userId
                Integer id = (Integer) claims.get("id");
                //日志记录
                log.info("find userid:{} from uri:{}", id, request.getURI());
                //重新把token设置到header中
                ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> {
                    httpHeaders.add("userId", id + "");
                }).build();

                exchange.mutate().request(serverHttpRequest).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            //向客户端返回错误提示信息
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //6.放行
        return chain.filter(exchange);

    }

    /**
     * 优先级设置
     * 值越小，优先级越高
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
