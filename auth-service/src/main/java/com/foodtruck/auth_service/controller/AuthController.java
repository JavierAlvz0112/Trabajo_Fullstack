package com.foodtruck.auth_service.controller;
import com.foodtruck.auth_service.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Registro e inicio de sesión FoodTruck")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Iniciar sesión",
               description = "Valida credenciales y retorna un token JWT válido por 1 hora")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso",
            content = @Content(examples = @ExampleObject(value = """
                { "status": "ok", "token": "eyJhbGciOiJIUzI1NiJ9..." }
            """))),
        @ApiResponse(responseCode = "200", description = "Credenciales incorrectas",
            content = @Content(examples = @ExampleObject(value = """
                { "status": "error", "token": "" }
            """)))
    })
    @PostMapping("/login")
    public Map<String, String> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Email y password del usuario",
                content = @Content(examples = @ExampleObject(value = """
                    { "email": "juan@mail.com", "password": "pass123" }
                """)))
            @RequestBody Map<String, String> request) {

        String email = request.get("email");
        String password = request.get("password");
        log.info("POST /auth/login - email: {}", email);

        String token = userService.login(email, password);

        Map<String, String> resp = new HashMap<>();
        if (token == null) {
            log.warn("POST /auth/login - credenciales incorrectas para: {}", email);
            resp.put("status", "error");
            resp.put("token", "");
        } else {
            log.info("POST /auth/login - login exitoso para: {}", email);
            resp.put("status", "ok");
            resp.put("token", token);
        }
        return resp;
    }

    @Operation(summary = "Registrar usuario",
               description = "Crea un nuevo usuario. El password se guarda hasheado con SHA-1.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario creado o ya existe",
            content = @Content(examples = @ExampleObject(value = """
                { "message": "Usuario creado exitosamente!" }
            """)))
    })
    @PostMapping("/register")
    public Map<String, String> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Email y password del nuevo usuario",
                content = @Content(examples = @ExampleObject(value = """
                    { "email": "juan@mail.com", "password": "pass123" }
                """)))
            @RequestBody Map<String, String> request) {

        String email = request.get("email");
        String password = request.get("password");
        log.info("POST /auth/register - email: {}", email);

        String resultado = userService.register(email, password);
        log.info("POST /auth/register - resultado: {}", resultado);

        Map<String, String> resp = new HashMap<>();
        resp.put("message", resultado);
        return resp;
    }
}
