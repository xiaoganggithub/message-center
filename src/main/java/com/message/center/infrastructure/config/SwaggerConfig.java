package com.message.center.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置类
 * 用于生成API文档
 */
@Configuration
public class SwaggerConfig {

    /**
     * 创建OpenAPI对象
     * @return OpenAPI对象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("消息中心API文档")
                        .version("1.0.0")
                        .description("消息中心系统提供统一的消息发送能力，支持多种渠道发送消息")
                        .contact(new Contact()
                                .name("消息中心开发团队")
                                .email("message-center@example.com")
                                .url("http://example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
