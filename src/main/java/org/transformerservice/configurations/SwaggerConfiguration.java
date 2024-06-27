package org.transformerservice.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("Transformer API")
                .pathsToMatch("/api/v1/transform")
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        var localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("local");
        return new OpenAPI()
                .info(new Info().title("Transformer Service API").version("V1"))
                .servers(List.of(localServer));
    }
}
