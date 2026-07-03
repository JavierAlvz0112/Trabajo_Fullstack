package com.foodtruck.almacen_service.controller;

import com.foodtruck.almacen_service.dto.AlmacenDto;
import com.foodtruck.almacen_service.service.AlmacenService;

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
@RequestMapping("/api/almacen")
@Tag(name = "Almacén", description = "Gestión de stock e inventario del FoodTruck")
public class AlmacenController {

    private static final Logger log = LoggerFactory.getLogger(AlmacenController.class);
    private final AlmacenService almacenService;

    public AlmacenController(AlmacenService almacenService) {
        this.almacenService = almacenService;
    }

    @Operation(summary = "Registrar stock", description = "Agrega un registro de stock para un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Stock registrado exitosamente",
            content = @Content(examples = @ExampleObject(value = """
                {
                  "idAlmacen": 1,
                  "idProducto": 3,
                  "stock": 50,
                  "descripcion": "Completos en bodega principal"
                }"""))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<AlmacenDto> registrar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos del stock a registrar",
                content = @Content(examples = @ExampleObject(value = """
                    {
                      "idProducto": 3,
                      "stock": 50,
                      "descripcion": "Completos en bodega principal"
                    }""")))
            @RequestBody AlmacenDto dto) {
        log.info("POST /api/almacen - Registrando stock para producto {}", dto.getIdProducto());
        AlmacenDto resultado = almacenService.registrarStock(dto);
        log.info("POST /api/almacen - Stock registrado con ID {}", resultado.getIdAlmacen());
        return ResponseEntity.status(201).body(resultado);
    }

    @Operation(summary = "Listar todo el stock")
    @ApiResponse(responseCode = "200", description = "Stock obtenido exitosamente")
    @GetMapping
    public ResponseEntity<List<AlmacenDto>> listar() {
        log.info("GET /api/almacen - Listando todo el stock");
        return ResponseEntity.ok(almacenService.listarStock());
    }

    @Operation(summary = "Obtener stock por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Registro encontrado"),
        @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlmacenDto> buscarPorId(
            @Parameter(description = "ID del registro de almacén", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/almacen/{} - Buscando registro", id);
        return ResponseEntity.ok(almacenService.obtenerPorId(id));
    }

    @Operation(summary = "Stock por producto", description = "Retorna todos los registros de stock de un producto")
    @ApiResponse(responseCode = "200", description = "Stock del producto obtenido")
    @GetMapping(params = "productoId")
    public ResponseEntity<List<AlmacenDto>> buscarPorProducto(
            @Parameter(description = "ID del producto", example = "3")
            @RequestParam Long productoId) {
        log.info("GET /api/almacen?productoId={}", productoId);
        return ResponseEntity.ok(almacenService.obtenerPorProducto(productoId));
    }

    @Operation(summary = "Actualizar stock")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock actualizado"),
        @ApiResponse(responseCode = "404", description = "Registro no encontrado"),
        @ApiResponse(responseCode = "400", description = "Stock negativo")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AlmacenDto> actualizar(
            @Parameter(description = "ID del registro", example = "1")
            @PathVariable Long id,
            @RequestBody AlmacenDto dto) {
        log.info("PUT /api/almacen/{} - Actualizando stock", id);
        AlmacenDto resultado = almacenService.actualizarStock(id, dto);
        log.info("PUT /api/almacen/{} - Stock actualizado exitosamente", id);
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Eliminar registro de stock")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Registro eliminado"),
        @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID del registro", example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/almacen/{} - Eliminando registro", id);
        almacenService.eliminarStock(id);
        log.info("DELETE /api/almacen/{} - Eliminado exitosamente", id);
        return ResponseEntity.ok("Registro de stock eliminado correctamente");
    }

    @Operation(summary = "Stock enriquecido", description = "Retorna el stock con datos del producto asociado")
    @ApiResponse(responseCode = "200", description = "Stock enriquecido obtenido")
    @GetMapping("/enriquecer/{id}")
    public ResponseEntity<Map> enriquecer(
            @Parameter(description = "ID del registro", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/almacen/enriquecer/{}", id);
        return ResponseEntity.ok(almacenService.enriquecerStock(id));
    }
}