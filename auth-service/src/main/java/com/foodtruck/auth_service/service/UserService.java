package com.foodtruck.auth_service.service;

import com.foodtruck.auth_service.model.User;
import com.foodtruck.auth_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final HashService hashService;

    public UserService(UserRepository userRepository,
                       JwtService jwtService,
                       HashService hashService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.hashService = hashService;
    }

    // Retorna el token JWT si las credenciales son correctas, null si no
    public String login(String email, String password) {
        log.info("Intento de login para: {}", email);
        User user = userRepository.findByEmail(email);

        if (user == null) {
            log.warn("Login fallido - usuario no encontrado: {}", email);
            return null;
        }

        String hashedInput = hashService.sha1(password);
        if (!hashedInput.equals(user.getPassword())) {
            log.warn("Login fallido - password incorrecto para: {}", email);
            return null;
        }

        log.info("Login exitoso para: {}", email);
        return jwtService.generateToken(email);
    }

    // Registra un nuevo usuario con password hasheado
    public String register(String email, String password) {
        log.info("Intento de registro para: {}", email);
        User existing = userRepository.findByEmail(email);

        if (existing != null) {
            log.warn("Registro fallido - email ya existe: {}", email);
            return "Usuario ya existe!";
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(hashService.sha1(password));
        user.setRole("USER");
        userRepository.save(user);

        log.info("Usuario registrado exitosamente: {}", email);
        return "Usuario creado exitosamente!";
    }

    public String getRole(String email) {
        User user = userRepository.findByEmail(email);
        return user != null ? user.getRole() : null;
    }
}
