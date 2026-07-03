package com.foodtruck.pedidos_service.controller;

import com.foodtruck.pedidos_service.dto.PedidoDto;
import com.foodtruck.pedidos_service.service.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Operaciones de gestión de pedidos del sistema FoodTruck")
public class PedidoController {

    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Operation(summary = "Crear pedido", description = "Registra un nuevo pedido con sus detalles")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<PedidoDto> crear(@RequestBody PedidoDto dto) {
        log.info("POST /api/pedidos - Creando pedido para usuario {}", dto.getIdUsuario());
        PedidoDto resultado = pedidoService.crearPedido(dto);
        log.info("POST /api/pedidos - Pedido creado con ID {}", resultado.getIdPedido());
        return ResponseEntity.status(201).body(resultado);
    }

    @Operation(summary = "Listar todos los pedidos")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida")
    @GetMapping
    public ResponseEntity<List<PedidoDto>> listar() {
        log.info("GET /api/pedidos - Listando todos los pedidos");
        return ResponseEntity.ok(pedidoService.listarPedidos(null));
    }

    @Operation(summary = "Listar pedidos por usuario")
    @ApiResponse(responseCode = "200", description = "Pedidos del usuario obtenidos")
    @GetMapping(params = "usuarioId")
    public ResponseEntity<List<PedidoDto>> listarPorUsuario(
            @Parameter(description = "ID del usuario", example = "1")
            @RequestParam Long usuarioId) {
        log.info("GET /api/pedidos?usuarioId={}", usuarioId);
        return ResponseEntity.ok(pedidoService.listarPorUsuario(usuarioId));
    }

    @Operation(summary = "Listar pedidos por producto")
    @ApiResponse(responseCode = "200", description = "Pedidos con el producto obtenidos")
    @GetMapping(params = "productoId")
    public ResponseEntity<List<PedidoDto>> listarPorProducto(
            @Parameter(description = "ID del producto", example = "1")
            @RequestParam Long productoId) {
        log.info("GET /api/pedidos?productoId={}", productoId);
        return ResponseEntity.ok(pedidoService.listarPorProducto(productoId));
    }

    @Operation(summary = "Obtener pedido por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDto> buscarPorId(
            @Parameter(description = "ID del pedido", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/pedidos/{} - Buscando pedido", id);
        PedidoDto resultado = pedidoService.buscarPorId(id);
        log.info("GET /api/pedidos/{} - Pedido encontrado, usuario={}", id, resultado.getIdUsuario());
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Actualizar estado del pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PedidoDto> actualizar(
            @Parameter(description = "ID del pedido", example = "1")
            @PathVariable Long id,
            @RequestBody String estado) {
        log.info("PUT /api/pedidos/{} - Actualizando estado a: {}", id, estado);
        PedidoDto resultado = pedidoService.actualizarPedido(id, estado);
        log.info("PUT /api/pedidos/{} - Estado actualizado exitosamente", id);
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Eliminar pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido eliminado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID del pedido", example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/pedidos/{} - Eliminando pedido", id);
        pedidoService.eliminarPedido(id);
        log.info("DELETE /api/pedidos/{} - Pedido eliminado exitosamente", id);
        return ResponseEntity.ok("Pedido eliminado correctamente");
    }

    @Operation(summary = "Total de pedidos por usuario", description = "Suma el total de todos los pedidos de un usuario")
    @ApiResponse(responseCode = "200", description = "Total calculado")
    @GetMapping("/total-por-usuario")
    public ResponseEntity<Double> totalPorUsuario(
            @Parameter(description = "ID del usuario", example = "1")
            @RequestParam Long usuarioId) {
        log.info("GET /api/pedidos/total-por-usuario?usuarioId={}", usuarioId);
        return ResponseEntity.ok(pedidoService.totalPorUsuario(usuarioId));
    }

    @Operation(summary = "Buscar pedidos por rango de fechas")
    @ApiResponse(responseCode = "200", description = "Pedidos en el rango obtenidos")
    @GetMapping("/buscar-por-fecha")
    public ResponseEntity<List<PedidoDto>> buscarPorFecha(
            @Parameter(description = "Fecha inicio (ISO)", example = "2026-01-01T00:00:00")
            @RequestParam String from,
            @Parameter(description = "Fecha fin (ISO)", example = "2026-12-31T23:59:59")
            @RequestParam String to) {
        log.info("GET /api/pedidos/buscar-por-fecha from={} to={}", from, to);
        return ResponseEntity.ok(pedidoService.buscarPorRangoFechas(
                LocalDateTime.parse(from), LocalDateTime.parse(to)));
    }

    @Operation(summary = "Pedido enriquecido", description = "Retorna el pedido con datos del usuario y sus pagos")
    @ApiResponse(responseCode = "200", description = "Pedido enriquecido obtenido")
    @GetMapping("/enriquecer/{id}")
    public ResponseEntity<Map<String, Object>> enriquecer(
            @Parameter(description = "ID del pedido", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/pedidos/enriquecer/{} - Enriqueciendo pedido", id);
        return ResponseEntity.ok(pedidoService.enriquecerPedido(id));
    }
}
