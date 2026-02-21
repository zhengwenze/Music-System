package org.zwz.music_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * 获取用户ID过滤器 - 已禁用
 */
// @Component 已移除，禁用此过滤器
public class GetUserIdFilterFactory extends AbstractGatewayFilterFactory<GetUserIdFilterFactory.Config> {

    public GetUserIdFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 获取请求
            ServerHttpRequest request = exchange.getRequest();
            
            // 这里可以根据实际情况从请求中获取用户ID
            // 例如：从请求头、Cookie、JWT等中获取
            String userId = request.getHeaders().getFirst("id");
            
            // 如果没有获取到用户ID，可以使用默认值
            if (userId == null) {
                userId = String.valueOf(config.getDefaultUserId());
            }
            
            // 将用户ID添加到请求头中，供后续服务使用
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("userId", userId)
                    .build();
            
            // 继续处理请求
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }

    /**
     * 过滤器配置类
     */
    public static class Config {
        private int defaultUserId;

        public int getDefaultUserId() {
            return defaultUserId;
        }

        public void setDefaultUserId(int defaultUserId) {
            this.defaultUserId = defaultUserId;
        }
    }
}