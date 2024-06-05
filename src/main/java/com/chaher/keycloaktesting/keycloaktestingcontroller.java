package com.chaher.keycloaktesting;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/keycloak")
public class keycloaktestingcontroller {
    @GetMapping
    @PreAuthorize("hasRole('client_user')")
    public String HelloWorld() {
        return "Hello from Keycloak -User";
    }
    @GetMapping("/admin")
    @PreAuthorize("hasRole('client_admin')")
    public String HelloAdmin() {
        return "Hello from Keycloak -Admin";
    }
}
