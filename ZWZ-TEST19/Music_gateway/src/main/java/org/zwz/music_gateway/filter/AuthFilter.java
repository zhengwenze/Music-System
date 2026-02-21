package org.zwz.music_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 权限过滤器 - 已禁用
 */
// @Component 已移除，禁用此过滤器
public class AuthFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 权限验证逻辑
        String path = request.getURI().getPath();
        
        // 排除不需要验证的路径
        if (path.startsWith("/api/login") || path.startsWith("/api/register") || path.startsWith("/api/public")) {
            return chain.filter(exchange);
        }

        // 获取Authorization头
        String token = request.getHeaders().getFirst("Authorization");
        
        // 简单的token验证示例
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // 这里可以添加更复杂的token验证逻辑
        // 例如：解析JWT，验证签名，检查过期时间等

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 设置过滤器执行顺序，数字越小优先级越高
        return -100;
    }
}