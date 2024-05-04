package com.github.helendigger.jwtsecurity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Tag(name = "User controller", description = "User operations, allowed only for USER role")
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@EnableMethodSecurity(proxyTargetClass = true)
public class UserController {

    @Operation(summary = "Get user page", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User page", content = {
                    @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)}),
            @ApiResponse(responseCode = "401", description = "Unauthenticated access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PreAuthorize("isAuthenticated() && hasAuthority('USER')")
    @GetMapping
    public ResponseEntity<String> user() {
        return ResponseEntity.ok("user");
    }
}
