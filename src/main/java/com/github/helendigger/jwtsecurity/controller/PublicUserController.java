package com.github.helendigger.jwtsecurity.controller;

import com.github.helendigger.jwtsecurity.model.UserId;
import com.github.helendigger.jwtsecurity.model.dto.CreateUserRequest;
import com.github.helendigger.jwtsecurity.model.exception.UserCreateException;
import com.github.helendigger.jwtsecurity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
@Tag(name = "Public user controller", description = "Operations to manipulate users")
@RequestMapping("/api/v1/public/user")
@RequiredArgsConstructor
public class PublicUserController {
    private final UserService userService;

    @Operation(summary = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New user id", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserId.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UserId> createUser(@Parameter(description = "User creating parameters",
            in = ParameterIn.DEFAULT, required = true)
                                                 @RequestBody @Validated CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
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
