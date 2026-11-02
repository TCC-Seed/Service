package com.tccseed.tcc_seed.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info().title("TCC Seed API").description("API da aplicação Seed").version("1.0"));
  }

  @Bean
  public OpenApiCustomizer swaggerTagOrder() {
    List<String> order =
        List.of(
            "auth-controller",
            "funcionario-controller",
            "estudante-controller",
            "ies-controller",
            "evento-controller",
            "publicacao-controller",
            "formulario-base-funcionario",
            "formulario-base-estudante",
            "formulario-personalizado-funcionario",
            "formulario-personalizado-estudante");

    return openApi -> {
      List<Tag> tags = order.stream().map(name -> new Tag().name(name)).toList();
      openApi.setTags(tags);
    };
  }
}
