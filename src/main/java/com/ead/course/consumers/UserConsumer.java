package com.ead.course.consumers;

import com.ead.course.dtos.UserEventDto;
import com.ead.course.enums.ActionType;
import com.ead.course.models.UserModel;
import com.ead.course.services.UserService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Componente responsável por consumir eventos de usuário enviados pelo MS AuthUser.
 * <p>
 * Essa classe escuta eventos da fila configurada via RabbitMQ e realiza ações de acordo com o tipo
 * de evento recebido (como criação de usuários).
 */
@Component
public class UserConsumer {

    @Autowired
    private UserService userService;

    /**
     * Consome eventos da fila de usuários e executa operações específicas conforme o tipo de ação recebido.
     * <p>
     * Escuta eventos publicados em um exchange do tipo {@code FANOUT},
     * configurado via propriedades externas, e trata os seguintes tipos de ação:
     * <ul>
     *     <li>{@code CREATE} - Converte o {@link UserEventDto} recebido em um {@link UserModel}
     *     e persiste o novo usuário no banco de dados utilizando o {@link UserService}.</li>
     *     <li>{@code UPDATE} - Converte o {@link UserEventDto} em um {@link UserModel}
     *     e atualiza os dados do usuário existente no banco de dados via {@link UserService}.</li>
     *     <li>{@code DELETE} - Remove o usuário identificado pelo ID fornecido no {@link UserEventDto}
     *     utilizando o {@link UserService}.</li>
     * </ul>
     *
     * A comunicação com a fila e o exchange é realizada através de configuração externa
     * (valores definidos nas propriedades da aplicação).
     *
     * @param userEventDto o DTO contendo as informações do usuário e o tipo de ação a ser processada
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${ead.broker.queue.userEventQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${ead.broker.exchange.userEventExchange}", type = ExchangeTypes.FANOUT,
            ignoreDeclarationExceptions = "true")
    ))
    public void listenUserEvent(@Payload UserEventDto userEventDto) {
        var userModel = userEventDto.convertToUserModel();
        switch (ActionType.valueOf(userEventDto.getActionType())) {
            case CREATE:
            case UPDATE:
                userService.save(userModel);
                break;
            case DELETE:
                userService.delete(userEventDto.getUserId());
                break;
        }
    }

}