package org.abby.springsecurity.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * SpringDoc API Setting
 */
@Slf4j
@Configuration
public class SpringDocConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI springOpenApi() {
        return new OpenAPI().info(new Info()
                        .title("Spring Security Doc API")
                        .description("Spring Security")
                        .version("1.0"))
                .addServersItem(getServersItem())
                //啟用JWT
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Value("${spring.profiles.active}")
    String springProfilesActive;

    /**
     * 依照環境區分前綴字
     */
    private Server getServersItem() {
        log.info("springProfilesActive:{}", springProfilesActive);
        if ("dev".equals(springProfilesActive)) {
            log.info(" Springdoc.Server().url: {} ,description: {} {} ", "", springProfilesActive, "Server");
            return new Server().url("").description(springProfilesActive + " Server");
        } else if ("test".equals(springProfilesActive)) {
            log.info(" Springdoc.Server().url: {} ,description: {} {} ", "/test", springProfilesActive, "Server");
            return new Server().url("/test").description(springProfilesActive + " Server");
        } else if ("prop".equals(springProfilesActive)) {
            log.info("no SpringDoc");
            return new Server();
        } else {
            throw new IllegalArgumentException("yml fail : spring.profiles.active: " + springProfilesActive + " not set Springdoc Server");
        }
    }
}
