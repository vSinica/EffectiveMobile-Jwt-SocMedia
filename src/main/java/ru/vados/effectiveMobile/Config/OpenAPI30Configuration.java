package ru.vados.effectiveMobile.Config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenAPI30Configuration {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(apiInfo());
    }


    private Info apiInfo() {
        return new Info()
                .title("Human cloning API")
                .description("API for creating clone who will fight in the clones wars")
                .version("2.0")
                .contact(apiContact())
                .license(apiLicence());
    }

    private License apiLicence() {
        return new License()
                .name("MIT Licence")
                .url("https://opensource.org/licenses/mit-license.php");
    }

    private Contact apiContact() {
        return new Contact()
                .name("Erwan LE TUTOUR")
                .email("erwanletutour.elt@gmail.com")
                .url("https://github.com/ErwanLT");
    }


}
