package net.dflmngr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping(value = { "/", "/fixtures", "/results", "/results/**" })
    public String spa() {
        return "forward:/index.html";
    }
}
