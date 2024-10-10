package com.epam.learn.springcore.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Sergii Vakaliuk",
                        email = "vakals@gmail.com"
                ),
                description = "OpenApi documentation for Gym application",
                title = "OpenApi specifications"
        ),
        servers = @Server(
                description = "Local environment",
                url = "http://localhost:8080"
        )
)
public class OpenApiConfig {
}
