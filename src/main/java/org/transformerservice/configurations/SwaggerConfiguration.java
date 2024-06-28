package org.transformerservice.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI openAPI(@Value("${server.port}") int serverPort) {
        var localServer = new Server();
        localServer.setUrl("http://localhost:" + serverPort);
        localServer.setDescription("local");
        return new OpenAPI()
                .info(new Info().title("Transformer Service API").version("V1"))
                .servers(List.of(localServer));
    }
}
