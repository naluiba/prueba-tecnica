package com.example.demo.config;


import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import javax.crypto.spec.SecretKeySpec;
import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;

@Configuration
@EnableWebSecurity
public class OpenApiConfig extends WebSecurityConfigurerAdapter {

    @Value("${springdoc.info.title}")
    private String title;

    @Value("${springdoc.info.version}")
    private String version;

    @Value("${springdoc.info.description}")
    private String description;

    @Value("${jwt.service.secretKey}")
    String SECRET_KEY;

    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable().cors().and()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**", "/api/users/sign-up").permitAll()
                .antMatchers("/api/users/login").authenticated()
                .and()
                .oauth2ResourceServer().jwt();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .version(version)
                        .description(description))
                /*.addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .type(HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))*/;
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder
                .withSecretKey(new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256"))
                .build();
    }

}

