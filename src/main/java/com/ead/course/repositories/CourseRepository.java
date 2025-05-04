package com.ead.course.repositories;

import com.ead.course.models.CourseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CourseRepository extends JpaRepository<CourseModel, UUID>, JpaSpecificationExecutor<CourseModel> {

    /**
     * Verifica se existe uma associação entre o curso e o usuário na tabela {@code tb_courses_users}.
     * <p>
     * Executa uma consulta SQL nativa para verificar se já existe uma entrada na tabela {@code tb_courses_users}
     * que associe o usuário especificado ao curso dado. Retorna {@code true} se a associação existir (count(tcu) > 0)
     * e {@code false} caso contrário.
     * </p>
     *
     * @param courseId o ID do curso para verificar a associação
     * @param userId   o ID do usuário para verificar a associação
     *
     * @return {@code true} se a associação entre o curso e o usuário existir, {@code false} caso contrário
     */
    @Query(value =
            "SELECT CASE " +
                    "WHEN COUNT(tcu) > 0 THEN true " +
                    "ELSE false " +
                "END " +
            "FROM tb_courses_users tcu " +
            "WHERE tcu.course_id = :courseId " +
            "AND tcu.user_id = :userId", nativeQuery = true)
    boolean existsByCourseIdAndUserId(@Param("courseId") UUID courseId, @Param("userId") UUID userId);

    @Modifying
    @Query(value =
            "INSERT INTO tb_courses_users " +
            "VALUES (:courseId, :userId)", nativeQuery = true)
    void saveCourseUser(@Param("courseId") UUID courseId, @Param("userId") UUID userId);
}