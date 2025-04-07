package hac.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private int id;

    @NotBlank(message = "Please enter course name")
    @Column(name = "name", length = 100, unique = true) 
    private String name;

    @NotBlank(message = "Please enter professor name")
    @Column(name = "professor", length = 100)
    private String professor;

    @NotBlank(message = "Please enter course code")
    @Size(min = 8, max = 8, message = "Code name should be 8 characters")
    @Column(length = 8, unique = true)
    private String code;

    @NotBlank(message = "Please enter description")
    @Column(length = 100000)
    private String description;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(
        name = "course_student",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> students = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "course_student", joinColumns = @JoinColumn(name = "course_id"))
    @MapKeyJoinColumn(name = "student_id")
    @Column(name = "grade")
    private Map<Student, String> studentGrades = new HashMap<>();

    public Course() {}

    public Course(String name, String professor, String code, String description) {
        this.name = name;
        this.professor = professor;
        this.code = code;
        this.description = description;
    }

    public void addStudent(Student student) {
        students.add(student);
        student.getCourses().add(this);
    }
    
    public void deleteStudent(int studentId) {
        students.removeIf(student -> student.getId() == studentId);
        studentGrades.entrySet().removeIf(entry -> entry.getKey().getId() == studentId);
    }


    public void removeStudent(Student student) {
        students.remove(student);
        student.getCourses().remove(this);
        studentGrades.remove(student);
    }

    public void assignGrade(Student student, String grade) {
        if (students.contains(student)) {
            studentGrades.put(student, grade);
        }
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }

    public Map<Student, String> getStudentGrades() {
        return studentGrades;
    }

    public void setStudentGrades(Map<Student, String> studentGrades) {
        this.studentGrades = studentGrades;
    }
}
