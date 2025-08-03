package com.peter.tanxuannewapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .name("Auth type")
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    private Server createServer(String url, String description) {
        Server server = new Server();
        server.setUrl(url);
        server.setDescription(description);
        return server;
    }

    private Contact createContact(String name, String email, String url) {
        Contact contact = new Contact();
        contact.setName(name);
        contact.setEmail(email);
        contact.setUrl(url);
        return contact;
    }

    private Info createInfo() {
        return new Info()
                .title("Tan Xuan App API")
                .description("This API exposes all endpoints (Tan Xuan App)")
                .version("1.0")
                .contact(createContact("Quốc Bùi", "quocbui2110@gmail.com",
                        "https://www.linkedin.com/in/bui-anh-quoc-23b071309/"))
                .license(new License()
                        .name("MIT")
                        .url("https://opensource.org/licenses/MIT"));
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(createInfo())
                .servers(List.of(createServer("http://localhost:8083", "Server URL in Dev environment")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }
}
