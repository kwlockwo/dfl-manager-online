package net.dflmngr.servlet.filters;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import net.dflmngr.ProductionConditional;

@Component
@Conditional(ProductionConditional.class)
public class HttpsEnforcer implements Filter {

    public static final String X_FORWARDED_PROTO = "x-forwarded-proto";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String proto = request.getHeader(X_FORWARDED_PROTO);
        if (proto != null && !proto.startsWith("https")) {
            String path = request.getRequestURI();
            String query = request.getQueryString();
            String redirectUrl = "https://" + request.getServerName() + path + (query != null ? "?" + query : "");
            response.sendRedirect(redirectUrl);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
