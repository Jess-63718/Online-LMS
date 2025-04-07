package hac.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * This is a test controller. Delete or replace it when you start working on your project.
 */
@Controller
public class Default {

    /**
     * Handles the request for the index page.
     *
     * @param model The model object to be populated with data.
     * @return The view for the index page.
     */
    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    /**
     * Handles the request for the login page.
     *
     * @return The view for the login page.
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
