package com.ead.course.services.impl;

import com.ead.course.models.CourseModel;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ModuleRepository moduleRepository;

    @Autowired
    LessonRepository lessonRepository;

    /**
     * Exclui um curso específico juntamente com todos os seus módulos, aulas associadas
     * e relacionamentos entre curso e usuários.
     * <p>
     * O processo de exclusão é feito de forma encadeada e segura, removendo:
     * <ul>
     *     <li>Todas as lições de cada módulo pertencente ao curso</li>
     *     <li>Todos os módulos do curso</li>
     *     <li>Relacionamentos entre o curso e os usuários</li>
     *     <li>O próprio curso</li>
     * </ul>
     * É executado dentro de uma transação, garantindo que todas as operações
     * sejam atômicas — ou todas são realizadas com sucesso, ou nenhuma alteração é persistida
     * em caso de falha.
     *
     * @param courseModel o curso a ser excluído, incluindo seus módulos e lições relacionadas
     */
    @Transactional
    @Override
    public void delete(CourseModel courseModel) {
        List<ModuleModel> moduleModelList = moduleRepository.findAllModulesIntoCourse(courseModel.getCourseId());
        if (!moduleModelList.isEmpty()) {
            for (ModuleModel moduleModel : moduleModelList) {
                List<LessonModel> lessonModelList = lessonRepository.findAllLessonsIntoModule(moduleModel.getModuleId());
                if (!lessonModelList.isEmpty()) {
                    lessonRepository.deleteAll(lessonModelList);
                }
            }
            moduleRepository.deleteAll(moduleModelList);
        }
        courseRepository.deleteCourseUserByCourse(courseModel.getCourseId());
        courseRepository.delete(courseModel);
    }

    @Override
    public CourseModel save(CourseModel courseModel) {
        return courseRepository.save(courseModel);
    }

    @Override
    public Optional<CourseModel> findById(UUID courseId) {
        return courseRepository.findById(courseId);
    }

    @Override
    public Page<CourseModel> findAll(Specification<CourseModel> courseSpec, Pageable pageable) {
        return courseRepository.findAll(courseSpec, pageable);
    }

    @Override
    public boolean existsByCourseAndUser(UUID courseId, UUID userId) {
        return courseRepository.existsByCourseIdAndUserId(courseId, userId);
    }

    /**
     * Insere uma nova associação entre um curso e um usuário na tabela {@code tb_courses_users}.
     * <p>
     * Executa uma consulta SQL nativa para adicionar um registro na tabela de associação entre cursos
     * e usuários. É utilizado para registrar a matrícula ou vinculação de um usuário a um curso específico.
     * </p>
     *
     * @param courseId o identificador do curso
     * @param userId   o identificador do usuário
     */
    @Transactional
    @Override
    public void saveSubscriptionUserInCourse(UUID courseId, UUID userId) {
        courseRepository.saveCourseUser(courseId, userId);
    }

}