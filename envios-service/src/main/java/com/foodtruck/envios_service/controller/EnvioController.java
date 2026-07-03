package com.foodtruck.envios_service.controller;

import com.foodtruck.envios_service.dto.EnvioDto;
import com.foodtruck.envios_service.service.EnvioService;
 
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
import java.util.Map;
 
@RestController
@RequestMapping("/api/envios")
@Tag(name = "Envios", description = "Gestión de envíos del sistema FoodTruck")
public class EnvioController {
 
    private static final Logger log = LoggerFactory.getLogger(EnvioController.class);
    private final EnvioService envioService;
 
    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }
 
    @Operation(summary = "Crear envío", description = "Registra un nuevo envío asociado a un pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Envío creado exitosamente",
            content = @Content(examples = @ExampleObject(value = """
                {
                  "idEnvio": 1,
                  "idPedido": 5,
                  "direccion": "Av. Siempreviva 742",
                  "estado": "pendiente",
                  "fecha": "2026-07-01T10:00:00"
                }"""))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<EnvioDto> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos del envío",
                content = @Content(examples = @ExampleObject(value = """
                    {
                      "idPedido": 5,
                      "direccion": "Av. Siempreviva 742",
                      "estado": "pendiente"
                    }""")))
            @RequestBody EnvioDto dto) {
        log.info("POST /api/envios - Creando envio para pedido {}", dto.getIdPedido());
        EnvioDto resultado = envioService.crearEnvio(dto);
        log.info("POST /api/envios - Envio creado con ID {}", resultado.getIdEnvio());
        return ResponseEntity.status(201).body(resultado);
    }
 
    @Operation(summary = "Listar todos los envíos")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<EnvioDto>> listar() {
        log.info("GET /api/envios - Listando todos los envios");
        return ResponseEntity.ok(envioService.listarEnvios());
    }
 
    @Operation(summary = "Obtener envío por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Envío encontrado"),
        @ApiResponse(responseCode = "404", description = "Envío no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EnvioDto> buscarPorId(
            @Parameter(description = "ID del envío", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/envios/{} - Buscando envio", id);
        return ResponseEntity.ok(envioService.obtenerEnvioPorId(id));
    }
 
    @Operation(summary = "Buscar envíos por pedido")
    @ApiResponse(responseCode = "200", description = "Envíos del pedido obtenidos")
    @GetMapping(params = "pedidoId")
    public ResponseEntity<List<EnvioDto>> buscarPorPedido(
            @Parameter(description = "ID del pedido", example = "5")
            @RequestParam Long pedidoId) {
        log.info("GET /api/envios?pedidoId={}", pedidoId);
        return ResponseEntity.ok(envioService.buscarPorPedido(pedidoId));
    }
 
    @Operation(summary = "Buscar envíos por estado",
               description = "Estados posibles: pendiente, en_camino, entregado, cancelado")
    @ApiResponse(responseCode = "200", description = "Envíos filtrados por estado")
    @GetMapping("/buscar-por-estado")
    public ResponseEntity<List<EnvioDto>> buscarPorEstado(
            @Parameter(description = "Estado del envío", example = "pendiente")
            @RequestParam String estado) {
        log.info("GET /api/envios/buscar-por-estado?estado={}", estado);
        return ResponseEntity.ok(envioService.buscarPorEstado(estado));
    }
 
    @Operation(summary = "Actualizar envío completo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Envío actualizado"),
        @ApiResponse(responseCode = "404", description = "Envío no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EnvioDto> actualizar(
            @Parameter(description = "ID del envío", example = "1")
            @PathVariable Long id,
            @RequestBody EnvioDto dto) {
        log.info("PUT /api/envios/{} - Actualizando envio", id);
        EnvioDto resultado = envioService.actualizarEnvio(id, dto);
        log.info("PUT /api/envios/{} - Actualizado exitosamente", id);
        return ResponseEntity.ok(resultado);
    }
 
    @Operation(summary = "Actualizar solo el estado del envío")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado"),
        @ApiResponse(responseCode = "404", description = "Envío no encontrado")
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<EnvioDto> actualizarEstado(
            @Parameter(description = "ID del envío", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado", example = "en_camino")
            @RequestParam String estado) {
        log.info("PATCH /api/envios/{}/estado - Nuevo estado: {}", id, estado);
        return ResponseEntity.ok(envioService.actualizarEstado(id, estado));
    }
 
    @Operation(summary = "Eliminar envío")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Envío eliminado"),
        @ApiResponse(responseCode = "404", description = "Envío no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID del envío", example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/envios/{} - Eliminando envio", id);
        envioService.eliminarEnvio(id);
        log.info("DELETE /api/envios/{} - Eliminado exitosamente", id);
        return ResponseEntity.ok("Envío eliminado correctamente");
    }
 
    @Operation(summary = "Envío enriquecido", description = "Retorna el envío con datos del pedido asociado")
    @ApiResponse(responseCode = "200", description = "Envío enriquecido obtenido")
    @GetMapping("/enriquecer/{id}")
    public ResponseEntity<Map> enriquecer(
            @Parameter(description = "ID del envío", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/envios/enriquecer/{}", id);
        return ResponseEntity.ok(envioService.enriquecerEnvio(id));
    }
}
 