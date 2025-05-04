package com.ead.course.validation;

import com.ead.course.dtos.CourseDto;
import com.ead.course.enums.UserType;
import com.ead.course.models.UserModel;
import com.ead.course.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;
import java.util.UUID;

/**
 * Validador personalizado para objetos do tipo {@link CourseDto}.
 * <p>
 * Esta classe implementa a interface {@link Validator} do Spring para realizar validações adicionais
 * em cursos.
 * </p>
 * <p>
 * Ela utiliza outro validador injetado via {@code @Qualifier("defaultValidator")} para executar
 * validações genéricas antes de aplicar a lógica personalizada.
 */
@Component
public class CourseValidator implements Validator {

    /**
     * Validador padrão que será usado como etapa inicial da validação.
     * Esse validador pode validar os campos genéricos como not null, formatos, etc.
     */
    @Autowired
    @Qualifier("defaultValidator")
    private Validator validator;

    @Autowired
    UserService userService;

    /**
     * Indica se o validador suporta a validação de instâncias da classe informada.
     * <p>Sempre retorna {@code false}, e deve ser ajustado para verificar
     * se {@code aClass} é atribuível a {@code CourseDto}.</p>
     *
     * @param aClass a classe do objeto a ser validado
     * @return {@code false}, indicando que a classe não está atualmente suportada
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    /**
     * Executa a validação no objeto fornecido.
     * <p>Primeiro delega a validação ao {@link #validator} injetado. Caso não haja erros,
     * realiza a verificação do instrutor associado ao curso,
     * que é feita pelo {@link #validateUserInstructor(UUID, Errors)}.</p>
     *
     * @param o      o objeto a ser validado (deve ser uma instância de {@link CourseDto})
     * @param errors objeto que armazena os erros de validação
     */
    @Override
    public void validate(Object o, Errors errors) {
        CourseDto courseDto = (CourseDto) o;
        validator.validate(courseDto, errors);
        if (!errors.hasErrors()) {
            validateUserInstructor(courseDto.getUserInstructor(), errors);
        }
    }

    /**
     * Verifica se o instrutor fornecido é válido.
     * <p>Gera erros caso o usuário não exista ou não seja do tipo INSTRUCTOR ou ADMIN.</p>
     *
     * @param instructorId UUID do instrutor a ser validado
     * @param errors       objeto para registrar erros de validação
     */
    private void validateUserInstructor(UUID instructorId, Errors errors) {
        Optional<UserModel> userModelOptional = userService.findById(instructorId);
        if (!userModelOptional.isPresent()) {
            errors.rejectValue("instructorId", "UserInstructorError", "Instructor not found.");
        }
        if (userModelOptional.get().getUserType().equals(UserType.STUDENT.toString())) {
            errors.rejectValue("instructorId", "UserInstructorError", "User must be INSTRUCTOR or ADMIN.");
        }
    }

}