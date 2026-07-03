package com.foodtruck.productos_service.controller;
import com.foodtruck.productos_service.dto.BebidaDto;
import com.foodtruck.productos_service.service.BebidaService;
 
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
@RequestMapping("/api/bebidas")
@Tag(name = "Bebidas", description = "Gestión de bebidas del FoodTruck")
public class BebidaController {
 
    private static final Logger log = LoggerFactory.getLogger(BebidaController.class);
    private final BebidaService bebidaService;
 
    public BebidaController(BebidaService bebidaService) {
        this.bebidaService = bebidaService;
    }
 
    @Operation(summary = "Crear bebida", description = "Registra una nueva bebida en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Bebida creada exitosamente",
            content = @Content(examples = @ExampleObject(value = """
                {
                  "idBebida": 1,
                  "nombre": "Bebida Energética",
                  "descripcion": "Bebida con cafeína y taurina",
                  "ingredientes": "agua, cafeína, taurina, azúcar",
                  "infoNutricional": "110 kcal",
                  "precio": 1500.0,
                  "sellos": "alto en azúcar",
                  "sabor": "manzana",
                  "tamano": "500ml"
                }"""))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<BebidaDto> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos de la bebida a crear",
                content = @Content(examples = @ExampleObject(value = """
                    {
                      "nombre": "Bebida Energética",
                      "descripcion": "Bebida con cafeína y taurina",
                      "ingredientes": "agua, cafeína, taurina, azúcar",
                      "infoNutricional": "110 kcal",
                      "precio": 1500.0,
                      "sellos": "alto en azúcar",
                      "sabor": "manzana",
                      "tamano": "500ml"
                    }""")))
            @RequestBody BebidaDto dto) {
        log.info("POST /api/bebidas - Creando bebida: {}", dto.getNombre());
        BebidaDto resultado = bebidaService.crearBebida(dto);
        log.info("POST /api/bebidas - Bebida creada con ID: {}", resultado.getIdBebida());
        return ResponseEntity.status(201).body(resultado);
    }
 
    @Operation(summary = "Listar bebidas", description = "Retorna todas las bebidas disponibles")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<BebidaDto>> listar() {
        log.info("GET /api/bebidas - Listando todas las bebidas");
        List<BebidaDto> lista = bebidaService.listarBebidas();
        log.info("GET /api/bebidas - {} bebidas encontradas", lista.size());
        return ResponseEntity.ok(lista);
    }
 
    @Operation(summary = "Obtener bebida por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bebida encontrada"),
        @ApiResponse(responseCode = "404", description = "Bebida no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BebidaDto> buscarPorId(
            @Parameter(description = "ID de la bebida", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/bebidas/{} - Buscando bebida", id);
        return ResponseEntity.ok(bebidaService.obtenerBebidaPorId(id));
    }
 
    @Operation(summary = "Actualizar bebida")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bebida actualizada"),
        @ApiResponse(responseCode = "404", description = "Bebida no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BebidaDto> actualizar(
            @Parameter(description = "ID de la bebida", example = "1")
            @PathVariable Long id,
            @RequestBody BebidaDto dto) {
        log.info("PUT /api/bebidas/{} - Actualizando bebida", id);
        BebidaDto resultado = bebidaService.actualizarBebida(id, dto);
        log.info("PUT /api/bebidas/{} - Actualizada exitosamente", id);
        return ResponseEntity.ok(resultado);
    }
 
    @Operation(summary = "Eliminar bebida")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bebida eliminada"),
        @ApiResponse(responseCode = "404", description = "Bebida no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID de la bebida", example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/bebidas/{} - Eliminando bebida", id);
        bebidaService.eliminarBebida(id);
        log.info("DELETE /api/bebidas/{} - Eliminada exitosamente", id);
        return ResponseEntity.ok("Bebida eliminada correctamente");
    }
 
    @Operation(summary = "Inventario de bebida", description = "Consulta el stock disponible de esta bebida")
    @ApiResponse(responseCode = "200", description = "Inventario obtenido")
    @GetMapping("/{id}/inventario")
    public ResponseEntity<?> inventario(
            @Parameter(description = "ID de la bebida", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/bebidas/{}/inventario", id);
        return ResponseEntity.ok(bebidaService.obtenerInventario(id));
    }
 
    @Operation(summary = "Pedidos de bebida", description = "Consulta pedidos que incluyen esta bebida")
    @ApiResponse(responseCode = "200", description = "Pedidos obtenidos")
    @GetMapping("/{id}/pedidos")
    public ResponseEntity<List<Map>> pedidos(
            @Parameter(description = "ID de la bebida", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/bebidas/{}/pedidos", id);
        return ResponseEntity.ok(bebidaService.obtenerPedidosPorProducto(id));
    }
}
 