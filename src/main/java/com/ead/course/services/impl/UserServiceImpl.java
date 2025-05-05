package com.ead.course.services.impl;

import com.ead.course.models.UserModel;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.UserRepository;
import com.ead.course.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CourseRepository courseRepository;

    @Override
    public Page<UserModel> findAll(Specification<UserModel> spec, Pageable pageable) {
        return userRepository.findAll(spec, pageable);
    }

    @Override
    public UserModel save(UserModel userMode) {
        return userRepository.save(userMode);
    }

    /**
     * Exclui um usuário do sistema com base no seu identificador único (UUID).
     * <p>
     * Antes de remover o usuário, elimina todos os relacionamentos existentes
     * entre o usuário e os cursos, garantindo a integridade referencial.
     * <p>
     * É executado dentro de uma transação, garantindo que a exclusão
     * dos relacionamentos e do próprio usuário ocorra de forma atômica. Caso
     * alguma falha aconteça durante o processo, nenhuma alteração será persistida.
     *
     * @param userId o identificador único do usuário a ser removido
     */
    @Transactional
    @Override
    public void delete(UUID userId) {
        courseRepository.deleteCourseUserByCourse(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public Optional<UserModel> findById(UUID instructorId) {
        return userRepository.findById(instructorId);
    }

}