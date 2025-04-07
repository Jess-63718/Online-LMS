package hac.repository;

import hac.entity.Course;
import hac.entity.Student;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

@Repository
@Transactional
public interface CourseRepository extends JpaRepository<Course, Integer> {

    @Modifying
    @Query("DELETE FROM Course c WHERE c.id = 1")
    void deleteCourseWithIdOne();
    
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Course c WHERE c.name = :name")
    boolean existsByName(@Param("name") String name);
    
    // Corrected method to check if a course exists by code
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Course c WHERE c.code = :code")
    boolean existsByCode(@Param("code") String code);
    
    @Query("SELECT c.name FROM Course c GROUP BY c.name HAVING COUNT(c) > 1")
    List<String> findDuplicateCourseNames();

    List<Course> findByNameOrderByIdAsc(String name);
    
    @Modifying
    @Transactional
    @Query(
        value = "UPDATE course_student SET grade = :grade WHERE course_id = :courseId AND student_id = :studentId", 
        nativeQuery = true
    )
    void updateGrade(
        @Param("courseId") int courseId,
        @Param("studentId") long studentId,
        @Param("grade") String grade
    );
    @Query("SELECT c FROM Course c JOIN FETCH c.studentGrades WHERE KEY(c.studentGrades) = :student")
    List<Course> findCoursesWithGradesForStudent(@Param("student") Student student);
}