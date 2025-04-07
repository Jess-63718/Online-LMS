package hac.controllers;

import hac.entity.Course;
import hac.entity.Student;
import hac.repository.CourseRepository;
import hac.repository.StudentRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Controller
public class StudentController {
    private Student student;

    @Autowired
    private StudentRepository studentRepository;
    private CourseRepository courseRepository;

    @Autowired
    public void setStudentRepository(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Autowired
    public void setCourseRepository(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    //-----------------------------------------admin-----------------------------------------------

    /**
     * Get all students enrolled in a specific course (admin view).
     *
     * @param model    The model object to be populated with data.
     * @param courseId The ID of the course.
     * @return The view for displaying all students in the course.
     */
    @GetMapping(path = "/admin/course/{courseId}/students")
    public String getAllStudents(Model model, @PathVariable(value = "courseId") int courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            model.addAttribute("students", course.getStudents());
            model.addAttribute("course", course);
        }
        return "students-page";
    }
    
    @PostMapping("/admin/course/{courseId}/grade/{studentId}")
    public ResponseEntity<String> assignGrade(
            @PathVariable int courseId,
            @PathVariable Long studentId,
            @RequestParam String grade) {
        
        // Verify course exists (don't need full entity)
        if (!courseRepository.existsById(courseId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }
        
        // Verify student exists
        if (!studentRepository.existsById(studentId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        }
        
        // Update grade directly
        courseRepository.updateGrade(courseId, studentId, grade);
        
        return ResponseEntity.ok("Grade assigned successfully");
    }
    //-----------------------------------admin finish-------------------------------------------------------------

    /**
     * Get the student page.
     *
     * @param session   The HttpSession object.
     * @param model     The model object to be populated with data.
     * @param principal The Principal object representing the currently authenticated user.
     * @return The view for the student page.
     */
    @GetMapping("/student")
    public String getStudentPage(HttpSession session, Model model, Principal principal) {
        if (principal.getName() == null) {
            return "redirect:/login";
        } else {
        	if (this.student == null) {
        	    this.student = studentRepository.findStudentsByStudentId(principal.getName());
        	}

        }

        model.addAttribute("email", principal.getName());
        return "student-page";
    }

    /**
     * Get the list of courses for a specific student.
     *
     * @param model     The model object to be populated with data.
     * @param principal The Principal object representing the currently authenticated user.
     * @return The view for the student courses page.
     */
    @GetMapping(path = "/student/courses")
    public String coursesListOfSpecificStudent(Model model, Principal principal, HttpServletRequest request) {
        // Debugging: Print request info
        System.out.println("\n=== DEBUG: Handling request to /student/courses ===");
        System.out.println("Request URL: " + request.getRequestURL());
        System.out.println("Principal: " + (principal != null ? principal.getName() : "null"));

        if (principal == null || principal.getName() == null) {
            System.out.println("DEBUG: No principal found - redirecting to login");
            return "redirect:/login";
        } else {
            try {
                // Debug: Before student lookup
                System.out.println("\nDEBUG: Looking up student with ID: " + principal.getName());
                
                Student student = studentRepository.findStudentsByStudentId(principal.getName());
                
                // Debug: After student lookup
                if (student == null) {
                    System.out.println("DEBUG ERROR: No student found with the ID: " + principal.getName());
                } else {
                    System.out.println("DEBUG: Found student - ID: " + student.getId() + 
                                      ", Email: " + student.getStudentEmail());
                }

                // Debug: Before course lookup
                System.out.println("\nDEBUG: Fetching courses for student ID: " + (student != null ? student.getId() : "null"));
                
                List<Course> courses = courseRepository.findCoursesWithGradesForStudent(student);
                
                // Debug: After course lookup
                System.out.println("DEBUG: Found " + courses.size() + " courses");
                for (Course course : courses) {
                    System.out.println("  Course: " + course.getId() + " - " + course.getName() + 
                                      " | Grades Map Size: " + course.getStudentGrades().size());
                }

                Map<Integer, String> courseGrades = new HashMap<>();
                System.out.println("\nDEBUG: Processing grades:");
                
                for (Course course : courses) {
                    String grade = course.getStudentGrades().get(student);
      
                    
                    if (grade == null || grade.trim().isEmpty()) {
                        grade = "Nil"; // or you could directly use "Yet to be announced" here
                    }
                    courseGrades.put(course.getId(), grade);
                    
                    // Debug: Print grade info for each course
                    System.out.println("  Course ID: " + course.getId() + 
                                     " | Grade: " + grade + 
                                     " | Grades Map Contains Student: " + 
                                     course.getStudentGrades().containsKey(student));
                }

                // Debug: Before adding to model
                System.out.println("\nDEBUG: Model attributes being added:");
                System.out.println("  courses: " + courses.size() + " items");
                System.out.println("  courseGrades: " + courseGrades);
                System.out.println("  email: " + principal.getName());

                model.addAttribute("courses", courses);
                model.addAttribute("courseGrades", courseGrades);
                model.addAttribute("email", principal.getName());

                // Debug: Final confirmation
                System.out.println("\nDEBUG: Rendering student-courses template");
                return "student-courses";
                
            } catch (Exception e) {
                // Debug: Error handling
                System.out.println("\nDEBUG ERROR: Exception occurred: " + e.getMessage());
                e.printStackTrace();
                throw e; // Re-throw or handle as appropriate for your application
            }
        }
    }
    /**
     * Get the list of courses for a specific student based on their email.
     *
     * @param email The email of the student.
     * @return The list of courses for the student.
     */
    public List<Course> getCoursesByStudentId(String email) {
        List<Course> courses = new ArrayList<>();

        for (Course course : courseRepository.findAll()) {
        	Set<Student> studentSet = new HashSet<>(course.getStudents());
            for (Student student : studentSet) {
                if (student.getStudentId().equals(email)) {
                    courses.add(course);
                    break;
                }
            }
        }
        return courses;
    }

    /**
     * Get the list of all courses (admin view).
     *
     * @param model          The model object to be populated with data.
     * @param authentication The Authentication object representing the current user's authentication.
     * @param principal      The Principal object representing the currently authenticated user.
     * @return The view for displaying all courses.
     */
    @GetMapping(path = "/all-courses")
    public String getAllCourses(Model model, Authentication authentication, Principal principal) {
        if (authentication != null && authentication.isAuthenticated()) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

            model.addAttribute("email", principal.getName());
            if (isAdmin) {
                model.addAttribute("courses", courseRepository.findAll());
            } else {
                model.addAttribute("courses", getUnCoursesByStudentId(principal.getName()));
            }
            return "courses-page";
        }

        return "redirect:/login";
    }

    /**
     * Get information about a specific course.
     *
     * @param model     The model object to be populated with data.
     * @param id        The ID of the course.
     * @param principal The Principal object representing the currently authenticated user.
     * @return The view for displaying the course information.
     */
    @GetMapping(path = "/courses/{id}")
    public String getSpecificCourseInfo(Model model, @PathVariable(value = "id") int id, Principal principal) {
        Optional<Course> c = courseRepository.findById(id);
        if (c.isPresent()) {
            Course course = c.get();
            model.addAttribute("course", course);
            if (principal.getName() != null) {
                model.addAttribute("email", principal.getName());
            }
            return "course-page";
        }
        return "error";
    }

    /**
     * Get the list of uncourses for a specific student based on their email.
     *
     * @param email The email of the student.
     * @return The list of uncourses for the student.
     */
    public List<Course> getUnCoursesByStudentId(String email) {
        List<Course> courses = new ArrayList<>();

        for (Course course : courseRepository.findAll()) {
        	Set<Student> studentSet = new HashSet<>(course.getStudents());
            boolean found = false;
            for (Student student : studentSet) {
                if (student.getStudentEmail().equals(email)) {
                    found = true;
                    break;
                }
            }
            if (!found)
                courses.add(course);
        }
        return courses;
    }

    /**
     * Add a student to a specific course.
     *
     * @param model     The model object to be populated with data.
     * @param id        The ID of the course.
     * @param principal The Principal object representing the currently authenticated user.
     * @return The view for adding the student to the course.
     */
    @GetMapping(path = "/student/courses/{id}/add")
    public String getAddStudentPage(Model model, @PathVariable(value = "id") int id, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            Course course = courseRepository.findById(id).orElse(null);
            Student student = studentRepository.findStudentsByStudentId(principal.getName());


            if (course == null) {
                System.out.println("ERROR: Course not found for ID: " + id);
                return "redirect:/error";
            }

            if (student == null) {
                System.out.println("ERROR: Student not found for email: " + principal.getName());
                return "redirect:/error";
            }

            System.out.println("INFO: Adding student " + student.getId() + " to course " + course.getId());
            
            // Adding student to course
            course.addStudent(student);
            
            // Explicitly setting the relationship if needed
            student.getCourses().add(course);

            courseRepository.save(course);
            System.out.println("INFO: Student successfully added to course!");

            return "redirect:/student/courses";

        } catch (Exception e) {
            System.out.println("ERROR: Exception occurred while adding student to course");
            e.printStackTrace(); // This will print the full stack trace in the logs
            return "redirect:/error";
        }
    }
}
