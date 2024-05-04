package com.github.helendigger.jwtsecurity.controller;

import com.github.helendigger.jwtsecurity.model.dto.PasswordTokenRequest;
import com.github.helendigger.jwtsecurity.model.dto.RefreshTokenRequest;
import com.github.helendigger.jwtsecurity.model.dto.TokenData;
import com.github.helendigger.jwtsecurity.model.dto.TokenResponse;
import com.github.helendigger.jwtsecurity.model.exception.AuthException;
import com.github.helendigger.jwtsecurity.model.exception.UserCreateException;
import com.github.helendigger.jwtsecurity.model.exception.UserNotFoundException;
import com.github.helendigger.jwtsecurity.service.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Public token controller", description = "Operations to authenticate by JWT")
@RequestMapping("/api/v1/public/token")
@RequiredArgsConstructor
public class TokenController {
    private final SecurityService securityService;

    @Operation(summary = "Authenticate by username and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token and refresh token", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TokenResponse.class))
            }),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/password")
    public ResponseEntity<TokenResponse> password(@Parameter(description = "User authentication data")
                                                      @RequestBody @Validated PasswordTokenRequest request) {
        TokenData tokenData = securityService.processPasswordToken(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new TokenResponse(tokenData.getToken(), tokenData.getRefreshToken()));
    }

    @Operation(summary = "Refresh current JWT token by refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New token and refresh token", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TokenResponse.class))
            }),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Parameter(description = "Refresh token")
                                                     @RequestBody @Validated RefreshTokenRequest request) {
        return securityService.processRefreshToken(request)
                .map(tokenData -> new TokenResponse(tokenData.getToken(), tokenData.getRefreshToken()))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Handle validation errors, return bad request and a map with invalid values
     * @param exception exception to handle
     * @return response entity with bad request status and a map of invalid values and reason why they are invalid
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRequestArguments(MethodArgumentNotValidException exception) {
        var validationErrors = exception.getBindingResult().getAllErrors().stream()
                .filter(FieldError.class::isInstance)
                .map(FieldError.class::cast)
                .collect(Collectors.toMap(FieldError::getField, e -> Optional.ofNullable(e.getDefaultMessage())
                        .orElseGet(() -> "Invalid")));
        return ResponseEntity.badRequest().body(validationErrors);
    }

    /**
     * Handle argument error, return bad request and an object {"error" : "description"} back to user
     * @param exception exception to handle
     * @return response entity with bad request status and object with error
     */
    @ExceptionHandler(value = {MethodArgumentConversionNotSupportedException.class,
            MethodArgumentTypeMismatchException.class, UserCreateException.class})
    public ResponseEntity<Map<String, String>> handleInvalidConversion(Exception exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(UserNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(value = AuthException.class)
    public ResponseEntity<Map<String, String>> handleAuthException(AuthException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
    }

    /**
     * Every other error that is not validation or parsing request should be treated as Internal
     * @param throwable error that occurred inside the service
     * @return response entity with internal server error and object with error
     */
    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleGenericException(Throwable throwable) {
        return ResponseEntity.internalServerError().body(Map.of("error", throwable.getMessage()));
    }
}
