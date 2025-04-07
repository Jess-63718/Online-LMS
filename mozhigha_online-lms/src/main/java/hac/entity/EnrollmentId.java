package hac.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EnrollmentId implements Serializable {
    private Integer courseId;
    private Long studentId;
    
    // Default constructor
    public EnrollmentId() {}
    
    // Parameterized constructor
    public EnrollmentId(Integer courseId, Long studentId) {
        this.courseId = courseId;
        this.studentId = studentId;
    }
    
    // Getters and setters
    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    // equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EnrollmentId)) return false;
        EnrollmentId that = (EnrollmentId) o;
        return Objects.equals(courseId, that.courseId) && 
               Objects.equals(studentId, that.studentId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(courseId, studentId);
    }
}