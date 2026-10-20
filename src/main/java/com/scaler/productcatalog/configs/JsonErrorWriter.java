package com.scaler.productcatalog.configs;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

final class JsonErrorWriter {
    private JsonErrorWriter() {
    }

    // Security errors happen before the DispatcherServlet, so they bypass GlobalExceptionHandler
    static void write(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String safe = message == null ? "" : message.replace("\\", "\\\\").replace("\"", "\\\"");
        response.getWriter().write("{\"message\":\"" + safe + "\",\"status\":" + status.value() + "}");
    }
}
