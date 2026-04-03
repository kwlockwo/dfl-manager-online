package net.dflmngr.controllers.rest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice(basePackages = "net.dflmngr.controllers.rest")
public class NoCacheAdvice {

    @ModelAttribute
    public void setNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
    }
}
