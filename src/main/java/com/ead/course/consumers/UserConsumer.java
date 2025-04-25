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
     * Escuta eventos da fila de usuários e executa ações com base no tipo de evento recebido.
     * <p>
     * Atualmente, trata apenas eventos do tipo {@code CREATE}, convertendo o {@link UserEventDto}
     * recebido em um {@link UserModel} e salvando-o no banco de dados via {@link UserService}.
     * <p>
     * A fila e o exchange são configurados via propriedades externas.
     *
     * @param userEventDto o objeto contendo os dados do usuário e o tipo de ação a ser processada
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${ead.broker.queue.userEventQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${ead.broker.exchange.userEventExchange}", type = ExchangeTypes.FANOUT,
            ignoreDeclarationExceptions = "true")
    ))
    public void listenUserEvent(@Payload UserEventDto userEventDto) {
        var userMode = userEventDto.convertToUserModel();
        switch (ActionType.valueOf(userEventDto.getActionType())) {
            case CREATE:
                userService.save(userMode);
                break;
        }
    }

}