package com.marketplace.auth_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.marketplace.auth_service.model.Role;
import com.marketplace.auth_service.service.AuthService;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    private final AuthService authService;

    @Value("${internal.api-key:internal-dev-key}")
    private String internalApiKey;

    public InternalUserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public List<Map<String, String>> getUsersByRole(
        @RequestParam String role,
        @RequestHeader(value = "X-Internal-Api-Key", required = false) String providedApiKey
    ) {
        validateInternalApiKey(providedApiKey);
        Role parsedRole = Role.valueOf(role.trim().toUpperCase());
        return authService.getUsersByRole(parsedRole);
    }

    @PutMapping("/{email}/role")
    public Map<String, String> updateRole(
        @PathVariable String email,
        @RequestBody Map<String, String> body,
        @RequestHeader(value = "X-Internal-Api-Key", required = false) String providedApiKey
    ) {
        validateInternalApiKey(providedApiKey);

        String roleValue = body.get("role");
        if (roleValue == null || roleValue.trim().isEmpty()) {
            throw new RuntimeException("Role is required");
        }

        Role role = Role.valueOf(roleValue.trim().toUpperCase());
        return authService.updateUserRole(email, role);
    }

    private void validateInternalApiKey(String providedApiKey) {
        if (providedApiKey == null || !providedApiKey.equals(internalApiKey)) {
            throw new UnauthorizedInternalCallException("Unauthorized internal request");
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    static class UnauthorizedInternalCallException extends RuntimeException {
        UnauthorizedInternalCallException(String message) {
            super(message);
        }
    }
}
