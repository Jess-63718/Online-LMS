package hac.config;

import hac.entity.Student;
import hac.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class ApplicationConfig {

    private final StudentRepository studentRepository;

    @Autowired
    public ApplicationConfig(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        // Admin user
        manager.createUser(User.withUsername("123456788")
                .password(passwordEncoder.encode("Jessica123"))
                .roles("ADMIN")
                .build());

        // Student users - ID is username, password is encoded
        manager.createUser(User.withUsername("123456789")
                .password(passwordEncoder.encode("password123"))
                .roles("STUDENT")
                .build());

        // Save student record if not exists
        if (!studentRepository.existsByStudentId("123456789")) {
            studentRepository.save(new Student("123456789", "student1@university.edu"));
        }
        
        

        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(withDefaults())
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**") // ✅ Disable CSRF for API
            )
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/img/**", "/", "/login", "/logout").permitAll()
                .requestMatchers("/api/**").authenticated()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/student/**").hasRole("STUDENT")
                .anyRequest().authenticated()
            )
            .httpBasic(withDefaults()) // ✅ Enable HTTP Basic for API calls
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {
                    for (GrantedAuthority auth : authentication.getAuthorities()) {
                        if (auth.getAuthority().equals("ROLE_ADMIN")) {
                            response.sendRedirect("/admin");
                            return;
                        }
                        if (auth.getAuthority().equals("ROLE_STUDENT")) {
                            response.sendRedirect("/student");
                            return;
                        }
                    }
                    response.sendRedirect("/");
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedPage("/403")
            );

        return http.build();
    }
}