package hac.controllers;

import hac.entity.Course;
import hac.entity.Student;
import hac.repository.CourseRepository;
import hac.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(StudentControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private CourseRepository courseRepository;

    private Course course;
    private Student student;

    @BeforeEach
    public void setup() {
        logger.info("Setting up test data...");
        student = new Student();
        student.setId(1);
        student.setStudentId("124568907");
        student.setStudentEmail("student1@example.com");

        course = new Course();
        course.setId(1);
        course.setName("Mathematics");

        Set<Student> students = new HashSet<>();
        students.add(student);
        course.setStudents(students);

        logger.info("Setup complete.");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetAllStudentsInCourse() throws Exception {
        logger.info("Running testGetAllStudentsInCourse...");
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        mockMvc.perform(get("/admin/course/1/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("students-page"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("course"));

        logger.info("testGetAllStudentsInCourse passed.");
    }

    @Test
    @WithMockUser(username = "student1", roles = {"STUDENT"})
    public void testGetStudentCoursesRedirectsIfNoPrincipal() throws Exception {
        logger.info("Running testGetStudentCoursesRedirectsIfNoPrincipal...");
        when(studentRepository.findStudentsByStudentId("student1")).thenReturn(student);
        when(courseRepository.findCoursesWithGradesForStudent(student)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/student/courses"))
                .andExpect(status().isOk())
                .andExpect(view().name("student-courses"));

        logger.info("testGetStudentCoursesRedirectsIfNoPrincipal passed.");
    }

    @Test
    @WithMockUser(username = "student1", roles = {"STUDENT"})
    public void testAddStudentToCourse() throws Exception {
        logger.info("Running testAddStudentToCourse...");
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(studentRepository.findStudentsByStudentId("student1")).thenReturn(student);

        mockMvc.perform(get("/student/courses/1/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/courses"));

        logger.info("testAddStudentToCourse passed.");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetAllCoursesAsAdmin() throws Exception {
        logger.info("Running testGetAllCoursesAsAdmin...");
        List<Course> courses = Collections.singletonList(course);
        when(courseRepository.findAll()).thenReturn(courses);

        mockMvc.perform(get("/all-courses"))
                .andExpect(status().isOk())
                .andExpect(view().name("courses-page"))
                .andExpect(model().attributeExists("courses"));

        logger.info("testGetAllCoursesAsAdmin passed.");
    }
}
