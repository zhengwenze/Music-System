package org.zwz.music_gateway.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 信息过滤器 - 已禁用
 */
// @Component 已移除，禁用此过滤器
public class InfoFilter implements GlobalFilter, Ordered {

    private static final Log logger = LogFactory.getLog(InfoFilter.class);
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        
        // 记录请求信息
        final String requestId;
        String tempRequestId = request.getHeaders().getFirst("X-Request-ID");
        if (tempRequestId == null) {
            tempRequestId = generateRequestId();
        }
        requestId = tempRequestId;
        
        // 记录请求开始时间
        final long startTime = System.currentTimeMillis();
        
        logger.info("Request received: ID=[" + requestId + "] Method=[" + request.getMethod() + "] Path=[" + request.getURI().getPath() + "] From=[" + request.getRemoteAddress() + "]");
        
        // 添加请求ID到响应头
        response.getHeaders().add("X-Request-ID", requestId);
        
        // 处理请求并记录响应信息
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long endTime = System.currentTimeMillis();
            long latency = endTime - startTime;
            
            logger.info("Response sent: ID=[" + requestId + "] Status=[" + response.getStatusCode() + "] Latency=[" + latency + "ms]");
        }));
    }

    /**
     * 生成请求ID
     */
    private String generateRequestId() {
        return "REQ-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 1000000);
    }
    
    @Override
    public int getOrder() {
        // 设置过滤器执行顺序，比权限过滤器低优先级
        return 0;
    }
}