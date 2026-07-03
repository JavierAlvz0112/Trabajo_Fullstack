package com.foodtruck.usuarios_service.controller;

import com.foodtruck.usuarios_service.dto.UsuarioDto;
import com.foodtruck.usuarios_service.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con los usuarios del sistema FoodTruck")
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Listar todos los usuarios",
               description = "Retorna la lista completa de usuarios registrados en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    @GetMapping
    public List<UsuarioDto> getAllUsuarios() {
        log.info("GET /api/usuarios - Listando todos los usuarios");
        List<UsuarioDto> usuarios = usuarioService.listarUsuarios();
        log.info("GET /api/usuarios - {} usuarios encontrados", usuarios.size());
        return usuarios;
    }

    @Operation(summary = "Obtener usuario por ID",
               description = "Retorna un usuario específico según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(schema = @Schema(implementation = UsuarioDto.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public UsuarioDto getUsuarioById(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/usuarios/{} - Buscando usuario", id);
        return usuarioService.findById(id);
    }

    @Operation(summary = "Crear un nuevo usuario",
               description = "Registra un nuevo usuario en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente",
            content = @Content(schema = @Schema(implementation = UsuarioDto.class),
                examples = @ExampleObject(value = """
                    {
                      "idUsuario": 1,
                      "nombre": "Juan Pérez",
                      "correo": "juan@mail.com",
                      "rol": "Cliente",
                      "activo": true
                    }"""))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public UsuarioDto createUsuario(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos del usuario a crear",
                content = @Content(examples = @ExampleObject(value = """
                    {
                      "nombre": "Juan Pérez",
                      "correo": "juan@mail.com",
                      "contraseña": "pass123",
                      "rol": "Cliente",
                      "activo": true
                    }""")))
            @RequestBody UsuarioDto usuarioDto) {
        log.info("POST /api/usuarios - Creando usuario con correo: {}", usuarioDto.getCorreo());
        UsuarioDto creado = usuarioService.crearUsuario(usuarioDto);
        log.info("POST /api/usuarios - Usuario creado con ID: {}", creado.getIdUsuario());
        return creado;
    }

    @Operation(summary = "Actualizar un usuario",
               description = "Actualiza los datos de un usuario existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public UsuarioDto updateUsuario(
            @Parameter(description = "ID del usuario a actualizar", example = "1")
            @PathVariable Long id,
            @RequestBody UsuarioDto usuarioDto) {
        log.info("PUT /api/usuarios/{} - Actualizando usuario", id);
        UsuarioDto actualizado = usuarioService.actualizarUsuario(id, usuarioDto);
        log.info("PUT /api/usuarios/{} - Usuario actualizado exitosamente", id);
        return actualizado;
    }

    @Operation(summary = "Eliminar un usuario",
               description = "Elimina un usuario del sistema según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(
            @Parameter(description = "ID del usuario a eliminar", example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/usuarios/{} - Eliminando usuario", id);
        usuarioService.eliminarUsuario(id);
        log.info("DELETE /api/usuarios/{} - Usuario eliminado exitosamente", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener pedidos de un usuario",
               description = "Retorna todos los pedidos realizados por un usuario específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedidos obtenidos exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}/pedidos")
    public List<Map> getPedidosByUsuario(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/usuarios/{}/pedidos - Obteniendo pedidos del usuario", id);
        return usuarioService.obtenerPedidosDeUsuario(id);
    }

    @Operation(summary = "Total de pagos de un usuario",
               description = "Calcula el monto total pagado por un usuario en todos sus pedidos")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Total calculado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}/pagos-total")
    public Double getTotalPagosByUsuario(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/usuarios/{}/pagos-total - Calculando total de pagos", id);
        Double total = usuarioService.totalPagosPorUsuario(id);
        log.info("GET /api/usuarios/{}/pagos-total - Total: {}", id, total);
        return total;
    }
}
