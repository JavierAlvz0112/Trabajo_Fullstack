package com.foodtruck.pagos_service.service;

import com.foodtruck.pagos_service.dto.PagoDto;
import com.foodtruck.pagos_service.model.Pago;
import com.foodtruck.pagos_service.repository.PagoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    private PagoService pagoService;

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private RestTemplate restTemplate;

    // Datos de prueba reutilizables
    private Pago pagoEjemplo;
    private PagoDto dtoPagoEjemplo;

    @BeforeEach
    void setUp() {
        pagoService = new PagoService(pagoRepository, restTemplate);

        pagoEjemplo = new Pago(1L, 10L, 5000.0, "Tarjeta", "completado", new Date());

        dtoPagoEjemplo = PagoDto.builder()
                .idPago(1L)
                .idPedido(10L)
                .monto(5000.0)
                .metodoPago("Tarjeta")
                .estado("completado")
                .fechaPago(new Date())
                .build();
    }

    // -------------------------------------------------------
    // registrarPago
    // -------------------------------------------------------

    @Test
    void testRegistrarPago_exitoso() {
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoEjemplo);

        PagoDto resultado = pagoService.registrarPago(dtoPagoEjemplo);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPago());
        assertEquals(10L, resultado.getIdPedido());
        assertEquals(5000.0, resultado.getMonto());
        assertEquals("Tarjeta", resultado.getMetodoPago());
        assertEquals("completado", resultado.getEstado());
        verify(pagoRepository).save(any(Pago.class));
    }

    // -------------------------------------------------------
    // listarTodosLosPagos
    // -------------------------------------------------------

    @Test
    void testListarTodosLosPagos_devuelveListaCorrectamente() {
        when(pagoRepository.findAll()).thenReturn(List.of(pagoEjemplo));

        List<PagoDto> resultado = pagoService.listarTodosLosPagos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Tarjeta", resultado.get(0).getMetodoPago());
        verify(pagoRepository).findAll();
    }

    @Test
    void testListarTodosLosPagos_listaVacia() {
        when(pagoRepository.findAll()).thenReturn(List.of());

        List<PagoDto> resultado = pagoService.listarTodosLosPagos();

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

    // -------------------------------------------------------
    // obtenerPagoPorId
    // -------------------------------------------------------

    @Test
    void testObtenerPagoPorId_encontrado() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoEjemplo));

        PagoDto resultado = pagoService.obtenerPagoPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPago());
        verify(pagoRepository).findById(1L);
    }

    @Test
    void testObtenerPagoPorId_noEncontrado_lanzaExcepcion() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pagoService.obtenerPagoPorId(99L));
        verify(pagoRepository).findById(99L);
    }

    // -------------------------------------------------------
    // actualizarPago
    // -------------------------------------------------------

    @Test
    void testActualizarPago_exitoso() {
        PagoDto dtoActualizado = PagoDto.builder()
                .idPedido(10L)
                .monto(9000.0)
                .metodoPago("Efectivo")
                .estado("pendiente")
                .fechaPago(new Date())
                .build();

        Pago pagoActualizado = new Pago(1L, 10L, 9000.0, "Efectivo", "pendiente", new Date());

        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoEjemplo));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoActualizado);

        PagoDto resultado = pagoService.actualizarPago(1L, dtoActualizado);

        assertNotNull(resultado);
        assertEquals(9000.0, resultado.getMonto());
        assertEquals("Efectivo", resultado.getMetodoPago());
        assertEquals("pendiente", resultado.getEstado());
        verify(pagoRepository).findById(1L);
        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    void testActualizarPago_noExiste_lanzaExcepcion() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> pagoService.actualizarPago(99L, dtoPagoEjemplo));
    }

    // -------------------------------------------------------
    // eliminarPago
    // -------------------------------------------------------

    @Test
    void testEliminarPago() {
        doNothing().when(pagoRepository).deleteById(1L);

        pagoService.eliminarPago(1L);

        verify(pagoRepository).deleteById(1L);
    }

    // -------------------------------------------------------
    // buscarPorPedido
    // -------------------------------------------------------

    @Test
    void testBuscarPorPedido_devuelvePagos() {
        when(pagoRepository.findByIdPedido(10L)).thenReturn(List.of(pagoEjemplo));

        List<PagoDto> resultado = pagoService.buscarPorPedido(10L);

        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getIdPedido());
    }

    // -------------------------------------------------------
    // buscarPorEstado
    // -------------------------------------------------------

    @Test
    void testBuscarPorEstado_completado() {
        when(pagoRepository.findByEstado("completado")).thenReturn(List.of(pagoEjemplo));

        List<PagoDto> resultado = pagoService.buscarPorEstado("completado");

        assertEquals(1, resultado.size());
        assertEquals("completado", resultado.get(0).getEstado());
    }

    // -------------------------------------------------------
    // totalPorPedido
    // -------------------------------------------------------

    @Test
    void testTotalPorPedido_conPagos() {
        when(pagoRepository.sumMontoByPedido(10L)).thenReturn(15000.0);

        Double total = pagoService.totalPorPedido(10L);

        assertEquals(15000.0, total);
    }

    @Test
    void testTotalPorPedido_sinPagos_retornaCero() {
        when(pagoRepository.sumMontoByPedido(99L)).thenReturn(null);

        Double total = pagoService.totalPorPedido(99L);

        assertEquals(0.0, total);
    }
}
