package org.wgtech.wgmall_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("WG Mall API").version("1.0"))
                .servers(List.of(
                        new Server().url("https://wgmall-production.up.railway.app") // ✅ 强制使用 https
                ));
    }
}
