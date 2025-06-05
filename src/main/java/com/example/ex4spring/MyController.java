package com.example.ex4spring;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MyController {
    /**
     * This method handles GET requests to the root URL ("/") and returns the name of the view to be rendered.
     * The view resolver will look for a template named "index.html" in the templates directory.
     *
     * @return the name of the view to be rendered
     *
     * Using generated security password: 28d6fb73-cffb-41a1-8078-a2c7a3a65c0e
     */

    @GetMapping("/")
    public String index() {
        return "index"; // This will resolve to src/main/resources/templates/index.html
    }
}
