package hac.repository;

import hac.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public interface StudentRepository extends JpaRepository<Student, Long> {

	    Student findStudentsByStudentId(String studentId); 
	    boolean existsByStudentId(String studentId);  // ✅ Add this line
	    
	    Student findByStudentEmail(String studentEmail);
	}
