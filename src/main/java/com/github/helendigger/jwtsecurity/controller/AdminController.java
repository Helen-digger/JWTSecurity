package com.github.helendigger.jwtsecurity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Admin controller", description = "Administration only operations, allowed only to ADMIN role")
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@EnableMethodSecurity(proxyTargetClass = true)
@Slf4j
public class AdminController {

    /**
     * An operation that accessible only to authenticated users with role ADMIN
     * @return entity with string
     */
    @Operation(summary = "Get admin page", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin page", content = {
                    @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)}),
            @ApiResponse(responseCode = "401", description = "Unauthenticated access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content),
    })
    @PreAuthorize("isAuthenticated() && hasAuthority('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<String> admin() {
        return ResponseEntity.ok("admin");
    }

}
