package com.ead.course.configs;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Classe de configuração responsável por fornecer uma instância de {@link RestTemplate}
 * com balanceamento de carga habilitado.
 *
 * <p>
 * A anotação {@link LoadBalanced} permite que o {@link RestTemplate} resolva nomes de serviço
 * registrados no serviço de descoberta (como Eureka), permitindo chamadas HTTP usando o nome lógico
 * dos serviços.
 * </p>
 *
 * <p>
 * O bean definido aqui pode ser injetado em outras partes da aplicação para realizar chamadas
 * HTTP para serviços externos com suporte a balanceamento de carga.
 * </p>
 */
@Configuration
public class RestTemplateConfig {


    /**
     * Cria e expõe uma instância de {@link RestTemplate} com suporte a balanceamento de carga.
     *
     * @param builder construtor de {@link RestTemplate} fornecido pelo Spring Boot
     * @return instância configurada de {@link RestTemplate}
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}