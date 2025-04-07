package hac.controllers;

import hac.entity.Course;
import hac.repository.CourseRepository;
import hac.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false)  // ✅ Disable security filters for test execution
class CourseControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(CourseControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private StudentRepository studentRepository;

    private Course testCourse;

    @BeforeEach
    void setUp() {
        logger.info("Setting up test data...");
        testCourse = new Course("Test Course", "Test Instructor", "1111", "Test Description");
        testCourse.setId(1);
    }

    @Test
    void testGetAdminPage() throws Exception {
        logger.info("Running test: testGetAdminPage");
        when(courseRepository.findAll()).thenReturn(List.of(testCourse));
        when(studentRepository.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("courses"))
                .andExpect(model().attributeExists("students"))
                .andExpect(view().name("admin-page"));

        logger.info("testGetAdminPage passed.");
    }

    @Test
    void testAddCoursePage() throws Exception {
        logger.info("Running test: testAddCoursePage");

        mockMvc.perform(get("/admin/addNewcourses"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("course"))
                .andExpect(view().name("course-adding"));

        logger.info("testAddCoursePage passed.");
    }

    @Test
    void testDeleteCourse() throws Exception {
        logger.info("Running test: testDeleteCourse");

        when(courseRepository.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/admin/course-delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/all-courses"));

        verify(courseRepository).deleteById(1);
        logger.info("testDeleteCourse passed.");
    }

    @Test
    void testEditCoursePage() throws Exception {
        logger.info("Running test: testEditCoursePage");

        when(courseRepository.findById(1)).thenReturn(Optional.of(testCourse));

        mockMvc.perform(get("/admin/course-edit/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("course"))
                .andExpect(view().name("course-adding"));

        logger.info("testEditCoursePage passed.");
    }

    @Test
    void testSaveCourse_Valid() throws Exception {
        logger.info("Running test: testSaveCourse_Valid");

        when(courseRepository.findAll()).thenReturn(List.of(testCourse));

        mockMvc.perform(post("/admin/add-course")
                        .param("name", "New Course")
                        .param("code", "ABC12345")
                        .param("professor", "Dr. XYZ")
                        .param("description", "Some description"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/all-courses"));

        verify(courseRepository).save(any(Course.class));
        logger.info("testSaveCourse_Valid passed.");
    }
}
