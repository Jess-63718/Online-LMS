package hac.controllers;

import hac.entity.Course;
import hac.repository.CourseRepository;
import hac.repository.StudentRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private StudentRepository studentRepository;

    // Initialize repositories
    @Autowired
    public void setCourseRepository(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Autowired
    public void setStudentRepository(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Initializes data on application startup:
     * 1. Cleans duplicate courses
     * 2. Deletes course with ID 1 (if exists)
     * 3. Adds default courses
     */
    @PostConstruct
    @Transactional
    public void initData() {
        cleanDuplicateCourses();  // Step 1: Clean duplicates
        courseRepository.deleteCourseWithIdOne();  // Step 2: Delete specific course
        
        // Step 3: Initialize default courses
        Course[] defaultCourses = {
            new Course("Introduction to Computer Science", "Dr. Yoram Biberman", "10204011", 
                      "In this course, we will get to know the basics of programming..."),
            new Course("Digital Systems", "Dr. Simcha Rozen", "10203012", 
                      "How is data stored on a computer?..."),
            new Course("Discrete Mathematics", "Dr. Eran London", "10202011", 
                      "The course begins with the fundamentals of the language of mathematics...")
        };

        for (Course course : defaultCourses) {
            saveCourseIfNotExists(course);
        }
    }

    /**
     * Cleans duplicate courses (keeps the earliest created course by ID)
     */
    @Transactional
    public void cleanDuplicateCourses() {
        List<String> duplicateNames = courseRepository.findDuplicateCourseNames();
        
        for (String name : duplicateNames) {
            List<Course> duplicates = courseRepository.findByNameOrderByIdAsc(name);
            
            if (duplicates.size() > 1) {
                // Keep first course (lowest ID) and delete others
                for (int i = 1; i < duplicates.size(); i++) {
                    courseRepository.delete(duplicates.get(i));
                    System.out.println("Deleted duplicate course: " + name + 
                                     " (ID: " + duplicates.get(i).getId() + ")");
                }
            }
        }
    }

    /**
     * Saves a course only if it doesn't already exist (by code or name)
     */
    @Transactional
    public void saveCourseIfNotExists(Course course) {
        if (!courseRepository.existsByCode(course.getCode())) {
            if (!courseRepository.existsByName(course.getName())) {
                courseRepository.save(course);
                System.out.println("Saved new course: " + course.getName());
            } else {
                System.out.println("Course with name '" + course.getName() + "' already exists");
            }
        } else {
            System.out.println("Course with code '" + course.getCode() + "' already exists");
        }
    }



    //--------------------------------------------------------------admin----------------------------------------------

    /**
     * Retrieves the course information for editing.
     *
     * @param model The model object to be populated with data.
     * @param id    The ID of the course to be edited.
     * @return The view for editing the course.
     */
    @GetMapping(path = "/admin/course-edit/{id}")
    public String editCourse(Model model, @PathVariable(value = "id") int id) {
        model.addAttribute("course", courseRepository.findById(id));
        return "course-adding";
    }

    /**
     * Saves the course information after adding or editing a course.
     *
     * @param course         The course object to be saved.
     * @param bindingResult  The binding result for validation.
     * @param redirectAttrs  The redirect attributes for flash messages.
     * @return The redirection URL after saving the course.
     */
    @PostMapping(path = "/admin/add-course")
    public String saveCourse(@Validated Course course, BindingResult bindingResult, RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            return "course-adding";
        } else {
            for (int i = 0; i < courseRepository.findAll().size(); i++) {
                if (courseRepository.findAll().get(i).getCode().equals(course.getCode())) {
                    course.setStudents(courseRepository.findAll().get(i).getStudents());
                }
            }

            courseRepository.save(course);
            redirectAttrs.addFlashAttribute("message", "A new course has been added successfully.");
        }
        return "redirect:/all-courses";
    }

    /**
     * Deletes a course with the specified ID.
     *
     * @param id     The ID of the course to be deleted.
     * @param model  The model object to be populated with data.
     * @return The redirection URL after deleting the course.
     */
    @GetMapping(path = "/admin/course-delete/{id}")
    public String deleteCourse(@PathVariable(value = "id") int id, RedirectAttributes model) {
        try {
            courseRepository.deleteById(id);

            // Get the remaining courses from the repository
            List<Course> remainingCourses = courseRepository.findAll();

            // Update the new IDs for the remaining courses
            for (int i = 0; i < remainingCourses.size(); i++) {
                Course course = remainingCourses.get(i);
                course.setId(i + 1);
                courseRepository.save(course);
            }

            model.addFlashAttribute("message", "The course has been deleted successfully.");
        } catch (Exception e) {
            e.getMessage();
        }
        return "redirect:/all-courses";
    }

    /**
     * Deletes a student from a specific course.
     *
     * @param id          The ID of the course.
     * @param studentId   The ID of the student to be deleted.
     * @param model       The model object to be populated with data.
     * @return The redirection URL after deleting the student from the course.
     */
    @PostMapping("/admin/course-delete/{id}/{studentId}")
    public String deleteStudentFromCourse(
            @PathVariable("id") int id,
            @PathVariable("studentId") int studentId,
            RedirectAttributes redirectAttributes) {
        
        try {
            Course course = courseRepository.findById(id).orElse(null);
            if (course != null) {
                course.deleteStudent(studentId);
                courseRepository.save(course);  // Don't forget to save changes
                redirectAttributes.addFlashAttribute("success", "Student removed successfully");
            } else {
                redirectAttributes.addFlashAttribute("error", "Course not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error removing student: " + e.getMessage());
        }
        
        return "redirect:/admin/course/" + id + "/students";
    }
    /**
     * Displays the form for adding a new course.
     *
     * @param model The model object to be populated with data.
     * @return The view for adding a new course.
     */
    @GetMapping(path = "admin/addNewcourses")
    public String addCourse(Model model) {
        model.addAttribute("course", new Course());
        return "course-adding";
    }

    /**
     * Retrieves all students and displays them in the admin dashboard.
     *
     * @param model The model object to be populated with data.
     * @return The view for the admin dashboard.
     */
    @GetMapping(path = "admin/all-students")
    public String getDashboard(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("course", null);
        return "students-page";
    }
    
    

    /**
     * Retrieves the number of courses and students and displays them in the admin page.
     *
     * @param model The model object to be populated with data.
     * @return The view for the admin page.
     */
    @GetMapping(path = "/admin")
    public String getAdminPage(Model model) {
        model.addAttribute("courses", courseRepository.findAll().size());
        model.addAttribute("students", studentRepository.findAll().size());
        return "admin-page";
    }
}
