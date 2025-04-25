package com.ead.course.dtos;

import com.ead.course.models.UserModel;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

/**
 * DTO utilizado para representar os dados de um usuário em eventos publicados no sistema.
 * <p>
 * Esta classe é usada principalmente para a comunicação entre serviços via mensageria (ex: RabbitMQ),
 * transportando os dados relevantes de um usuário e o tipo de ação executada ({@code CREATE}, {@code UPDATE}, {@code DELETE}).
 * Todos os atributos são representados como {@code String} ou {@code UUID} para facilitar a serialização.
 *
 * @see UserModel
 */
@Data
public class UserEventDto {

    private UUID userId;
    private String username;
    private String email;
    private String fullName;
    private String userStatus;
    private String userType;
    private String phoneNumber;
    private String cpf;
    private String imageUrl;
    private String actionType;

    /**
     * Converte esta instância de {@link UserEventDto} em uma nova instância de {@link UserModel}.
     * <p>
     * Utiliza {@link BeanUtils#copyProperties(Object, Object)} para realizar a cópia automática dos dados
     * entre os objetos. Não realiza conversões de tipos específicos (por exemplo, {@code String} para {@code Enum}),
     * sendo necessário tratamento adicional caso esses campos precisem ser utilizados como enumerações no {@link UserModel}.
     *
     * @return uma nova instância de {@link UserModel} com os dados copiados deste DTO
     */
    public UserModel convertToUserModel() {
        var userModel = new UserModel();
        BeanUtils.copyProperties(this, userModel);
        return userModel;
    }

}