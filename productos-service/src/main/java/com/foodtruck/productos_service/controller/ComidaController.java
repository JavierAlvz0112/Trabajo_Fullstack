package com.foodtruck.productos_service.controller;

import com.foodtruck.productos_service.dto.ComidaDto;
import com.foodtruck.productos_service.service.ComidaService;

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
@RequestMapping("/api/comidas")
@Tag(name = "Comidas", description = "Gestión de comidas del FoodTruck")
public class ComidaController {

    private static final Logger log = LoggerFactory.getLogger(ComidaController.class);
    private final ComidaService comidaService;

    public ComidaController(ComidaService comidaService) {
        this.comidaService = comidaService;
    }

    @Operation(summary = "Crear comida", description = "Registra una nueva comida en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Comida creada exitosamente",
            content = @Content(examples = @ExampleObject(value = """
                {
                  "idComida": 1,
                  "nombre": "Completo Italiano",
                  "descripcion": "Hot dog con tomate, palta y mayonesa",
                  "ingredientes": "pan, vienesa, tomate, palta, mayonesa",
                  "infoNutricional": "450 kcal",
                  "precio": 2500.0,
                  "tipo": "plato_principal"
                }"""))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<ComidaDto> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos de la comida a crear",
                content = @Content(examples = @ExampleObject(value = """
                    {
                      "nombre": "Completo Italiano",
                      "descripcion": "Hot dog con tomate, palta y mayonesa",
                      "ingredientes": "pan, vienesa, tomate, palta, mayonesa",
                      "infoNutricional": "450 kcal",
                      "precio": 2500.0,
                      "tipo": "plato_principal"
                    }""")))
            @RequestBody ComidaDto dto) {
        log.info("POST /api/comidas - Creando comida: {}", dto.getNombre());
        ComidaDto resultado = comidaService.crearComida(dto);
        log.info("POST /api/comidas - Comida creada con ID: {}", resultado.getIdComida());
        return ResponseEntity.status(201).body(resultado);
    }

    @Operation(summary = "Listar comidas", description = "Retorna todas las comidas disponibles")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<ComidaDto>> listar() {
        log.info("GET /api/comidas - Listando todas las comidas");
        List<ComidaDto> lista = comidaService.listarComidas();
        log.info("GET /api/comidas - {} comidas encontradas", lista.size());
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener comida por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comida encontrada"),
        @ApiResponse(responseCode = "404", description = "Comida no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ComidaDto> buscarPorId(
            @Parameter(description = "ID de la comida", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/comidas/{} - Buscando comida", id);
        return ResponseEntity.ok(comidaService.obtenerComidaPorId(id));
    }

    @Operation(summary = "Actualizar comida")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comida actualizada"),
        @ApiResponse(responseCode = "404", description = "Comida no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ComidaDto> actualizar(
            @Parameter(description = "ID de la comida", example = "1")
            @PathVariable Long id,
            @RequestBody ComidaDto dto) {
        log.info("PUT /api/comidas/{} - Actualizando comida", id);
        ComidaDto resultado = comidaService.actualizarComida(id, dto);
        log.info("PUT /api/comidas/{} - Actualizada exitosamente", id);
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Eliminar comida")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comida eliminada"),
        @ApiResponse(responseCode = "404", description = "Comida no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID de la comida", example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/comidas/{} - Eliminando comida", id);
        comidaService.eliminarComida(id);
        log.info("DELETE /api/comidas/{} - Eliminada exitosamente", id);
        return ResponseEntity.ok("Comida eliminada correctamente");
    }

    @Operation(summary = "Inventario de comida", description = "Consulta el stock disponible de esta comida")
    @ApiResponse(responseCode = "200", description = "Inventario obtenido")
    @GetMapping("/{id}/inventario")
    public ResponseEntity<?> inventario(
            @Parameter(description = "ID de la comida", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/comidas/{}/inventario", id);
        return ResponseEntity.ok(comidaService.obtenerInventario(id));
    }

    @Operation(summary = "Pedidos de comida", description = "Consulta pedidos que incluyen esta comida")
    @ApiResponse(responseCode = "200", description = "Pedidos obtenidos")
    @GetMapping("/{id}/pedidos")
    public ResponseEntity<List<Map>> pedidos(
            @Parameter(description = "ID de la comida", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/comidas/{}/pedidos", id);
        return ResponseEntity.ok(comidaService.obtenerPedidosPorProducto(id));
    }
}
