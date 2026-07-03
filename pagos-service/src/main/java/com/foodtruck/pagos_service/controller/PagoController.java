package com.foodtruck.pagos_service.controller;
import com.foodtruck.pagos_service.dto.PagoDto;
import com.foodtruck.pagos_service.service.PagoService;

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

@RestController
@RequestMapping("/api/pagos")
@Tag(name = "Pagos", description = "Operaciones relacionadas con los pagos de pedidos")
public class PagoController {

    private static final Logger log = LoggerFactory.getLogger(PagoController.class);

    @Autowired
    private PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @Operation(summary = "Registrar un nuevo pago",
               description = "Crea un pago asociado a un pedido existente")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pago creado exitosamente",
            content = @Content(schema = @Schema(implementation = PagoDto.class),
                examples = @ExampleObject(value = """
                    {
                      "idPago": 1,
                      "idPedido": 10,
                      "monto": 15000.0,
                      "metodoPago": "Tarjeta",
                      "estado": "completado",
                      "fechaPago": "2026-06-24T10:00:00"
                    }"""))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<PagoDto> registrar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos del pago a registrar",
                content = @Content(examples = @ExampleObject(value = """
                    {
                      "idPedido": 10,
                      "monto": 15000.0,
                      "metodoPago": "Tarjeta",
                      "estado": "completado"
                    }""")))
            @RequestBody PagoDto dto) {
        log.info("POST /api/pagos - Registrando pago para pedido {}", dto.getIdPedido());
        PagoDto resultado = pagoService.registrarPago(dto);
        log.info("POST /api/pagos - Pago creado con ID {}", resultado.getIdPago());
        return ResponseEntity.status(201).body(resultado);
    }

    @Operation(summary = "Listar todos los pagos",
               description = "Retorna la lista completa de pagos registrados")
    @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<PagoDto>> listar() {
        log.info("GET /api/pagos - Listando todos los pagos");
        return ResponseEntity.ok(pagoService.listarTodosLosPagos());
    }

    @Operation(summary = "Obtener pago por ID",
               description = "Retorna un pago específico según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago encontrado"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PagoDto> buscarPorId(
            @Parameter(description = "ID del pago", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/pagos/{} - Buscando pago por ID", id);
        return ResponseEntity.ok(pagoService.obtenerPagoPorId(id));
    }

    @Operation(summary = "Listar pagos por pedido",
               description = "Retorna todos los pagos asociados a un pedido específico")
    @ApiResponse(responseCode = "200", description = "Pagos del pedido obtenidos")
    @GetMapping(params = "pedidoId")
    public ResponseEntity<List<PagoDto>> listarPorPedido(
            @Parameter(description = "ID del pedido", example = "10")
            @RequestParam Long pedidoId) {
        log.info("GET /api/pagos?pedidoId={} - Buscando pagos por pedido", pedidoId);
        return ResponseEntity.ok(pagoService.buscarPorPedido(pedidoId));
    }

    @Operation(summary = "Total pagado por pedido",
               description = "Calcula el monto total de todos los pagos de un pedido")
    @ApiResponse(responseCode = "200", description = "Total calculado exitosamente")
    @GetMapping("/total-por-pedido")
    public ResponseEntity<Double> totalPorPedido(
            @Parameter(description = "ID del pedido", example = "10")
            @RequestParam Long pedidoId) {
        log.info("GET /api/pagos/total-por-pedido?pedidoId={}", pedidoId);
        return ResponseEntity.ok(pagoService.totalPorPedido(pedidoId));
    }

    @Operation(summary = "Buscar pagos por estado",
               description = "Filtra pagos según su estado: completado, pendiente, rechazado")
    @ApiResponse(responseCode = "200", description = "Pagos filtrados por estado")
    @GetMapping("/buscar-por-estado")
    public ResponseEntity<List<PagoDto>> buscarPorEstado(
            @Parameter(description = "Estado del pago", example = "completado")
            @RequestParam String estado) {
        log.info("GET /api/pagos/buscar-por-estado?estado={}", estado);
        return ResponseEntity.ok(pagoService.buscarPorEstado(estado));
    }

    @Operation(summary = "Actualizar un pago",
               description = "Actualiza los datos de un pago existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PagoDto> actualizar(
            @Parameter(description = "ID del pago a actualizar", example = "1")
            @PathVariable Long id,
            @RequestBody PagoDto dto) {
        log.info("PUT /api/pagos/{} - Actualizando pago", id);
        PagoDto resultado = pagoService.actualizarPago(id, dto);
        log.info("PUT /api/pagos/{} - Pago actualizado exitosamente", id);
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Eliminar un pago",
               description = "Elimina un pago del sistema según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID del pago a eliminar", example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/pagos/{} - Eliminando pago", id);
        pagoService.eliminarPago(id);
        log.info("DELETE /api/pagos/{} - Pago eliminado exitosamente", id);
        return ResponseEntity.ok("Pago eliminado correctamente");
    }
}

