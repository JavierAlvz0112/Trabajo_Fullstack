package com.foodtruck.pagos_service.controller;

import com.foodtruck.pagos_service.dto.PagoDto;
import com.foodtruck.pagos_service.service.PagoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PagoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PagoService pagoService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PagoDto pagoDto;

    @BeforeEach
    void setUp() {
        PagoController controller = new PagoController(pagoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        pagoDto = PagoDto.builder()
                .idPago(1L)
                .idPedido(10L)
                .monto(5000.0)
                .metodoPago("Tarjeta")
                .estado("completado")
                .fechaPago(new Date())
                .build();
    }
    // POST /api/pagos

    @Test
    void testRegistrar_retorna201() throws Exception {
        when(pagoService.registrarPago(any(PagoDto.class))).thenReturn(pagoDto);

        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagoDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPago").value(1L))
                .andExpect(jsonPath("$.idPedido").value(10L))
                .andExpect(jsonPath("$.monto").value(5000.0))
                .andExpect(jsonPath("$.metodoPago").value("Tarjeta"))
                .andExpect(jsonPath("$.estado").value("completado"));
    }

    //
    // GET /api/pagos
    //

    @Test
    void testListar_retornaListaConUnPago() throws Exception {
        when(pagoService.listarTodosLosPagos()).thenReturn(List.of(pagoDto));

        mockMvc.perform(get("/api/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPago").value(1L))
                .andExpect(jsonPath("$[0].metodoPago").value("Tarjeta"))
                .andExpect(jsonPath("$[0].monto").value(5000.0));
    }

    @Test
    void testListar_listaVacia() throws Exception {
        when(pagoService.listarTodosLosPagos()).thenReturn(List.of());

        mockMvc.perform(get("/api/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // GET /api/pagos/{id}

    @Test
    void testBuscarPorId_encontrado() throws Exception {
        when(pagoService.obtenerPagoPorId(1L)).thenReturn(pagoDto);

        mockMvc.perform(get("/api/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPago").value(1L))
                .andExpect(jsonPath("$.estado").value("completado"));
    }

    // PUT /api/pagos/{id}

    @Test
    void testActualizar_exitoso() throws Exception {
        PagoDto dtoActualizado = PagoDto.builder()
                .idPago(1L)
                .idPedido(10L)
                .monto(9000.0)
                .metodoPago("Efectivo")
                .estado("pendiente")
                .fechaPago(new Date())
                .build();

        when(pagoService.actualizarPago(eq(1L), any(PagoDto.class))).thenReturn(dtoActualizado);

        mockMvc.perform(put("/api/pagos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monto").value(9000.0))
                .andExpect(jsonPath("$.metodoPago").value("Efectivo"))
                .andExpect(jsonPath("$.estado").value("pendiente"));
    }

    // DELETE /api/pagos/{id}

    @Test
    void testEliminar_retornaMensaje() throws Exception {
        doNothing().when(pagoService).eliminarPago(1L);

        mockMvc.perform(delete("/api/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Pago eliminado correctamente"));

        verify(pagoService, times(1)).eliminarPago(1L);
    }

    // GET /api/pagos/total-por-pedido

    @Test
    void testTotalPorPedido() throws Exception {
        when(pagoService.totalPorPedido(10L)).thenReturn(15000.0);

        mockMvc.perform(get("/api/pagos/total-por-pedido").param("pedidoId", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string("15000.0"));
    }

    // GET /api/pagos/buscar-por-estado

    @Test
    void testBuscarPorEstado() throws Exception {
        when(pagoService.buscarPorEstado("completado")).thenReturn(List.of(pagoDto));

        mockMvc.perform(get("/api/pagos/buscar-por-estado").param("estado", "completado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("completado"));
    }
}
