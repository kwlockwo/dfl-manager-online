package net.dflmngr.servlet.filters;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        long start = System.currentTimeMillis();
        CountingResponseWrapper wrapper = new CountingResponseWrapper(response);
        try {
            chain.doFilter(request, wrapper);
        } finally {
            long duration = System.currentTimeMillis() - start;
            logger.info("request_id={} method={} path={} host={} status={} response_size={} duration={}ms user_agent={}",
                    requestId,
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getServerName(),
                    wrapper.getStatus(),
                    wrapper.getByteCount(),
                    duration,
                    request.getHeader("User-Agent"));
        }
    }

    private static class CountingResponseWrapper extends HttpServletResponseWrapper {

        private final CountingOutputStream countingStream;
        private int status = SC_OK;

        CountingResponseWrapper(HttpServletResponse response) throws IOException {
            super(response);
            this.countingStream = new CountingOutputStream(response.getOutputStream());
        }

        @Override
        public void setStatus(int sc) {
            this.status = sc;
            super.setStatus(sc);
        }

        @Override
        public void sendError(int sc) throws IOException {
            this.status = sc;
            super.sendError(sc);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            this.status = sc;
            super.sendError(sc, msg);
        }

        @Override
        public ServletOutputStream getOutputStream() {
            return countingStream;
        }

        @Override
        public int getStatus() {
            return status;
        }

        long getByteCount() {
            return countingStream.count;
        }
    }

    private static class CountingOutputStream extends ServletOutputStream {

        private final ServletOutputStream delegate;
        private long count = 0;

        CountingOutputStream(ServletOutputStream delegate) {
            this.delegate = delegate;
        }

        @Override
        public void write(int b) throws IOException {
            delegate.write(b);
            count++;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            delegate.write(b, off, len);
            count += len;
        }

        @Override
        public boolean isReady() {
            return delegate.isReady();
        }

        @Override
        public void setWriteListener(WriteListener listener) {
            delegate.setWriteListener(listener);
        }
    }
}
