package hac.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(min = 9, max = 9, message = "ID must be exactly 9 digits")
    @Pattern(regexp = "\\d{9}", message = "ID must contain only digits")
    private String studentId;

    @NotBlank
    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String studentEmail;

    @ManyToMany(mappedBy = "students", fetch = FetchType.LAZY)
    private Set<Course> courses = new HashSet<>();
    
    
    

    public Student() {}

    public Student(String studentId, String studentEmail) {
        this.studentId = studentId;
        this.studentEmail = studentEmail;
    }
    
    

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }
    public void setId(Integer id) {
        this.id = id;
    }


    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }
}