package net.dflmngr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SpaController {

    private static final String INDEX = "forward:/index.html";

    @GetMapping("/")
    public String index() {
        return INDEX;
    }

    @GetMapping("/fixtures")
    public String fixtures() {
        return INDEX;
    }

    @GetMapping("/results")
    public String results() {
        return INDEX;
    }

    @GetMapping("/results/{round}/{game}")
    public String results(@PathVariable int round, @PathVariable int game) {
        return INDEX;
    }
}
