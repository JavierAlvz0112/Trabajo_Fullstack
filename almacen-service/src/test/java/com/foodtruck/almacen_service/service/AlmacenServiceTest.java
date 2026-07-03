package com.foodtruck.almacen_service.service;

import com.foodtruck.almacen_service.dto.AlmacenDto;
import com.foodtruck.almacen_service.exception.BadRequestException;
import com.foodtruck.almacen_service.exception.ResourceNotFoundException;
import com.foodtruck.almacen_service.model.Almacen;
import com.foodtruck.almacen_service.repository.AlmacenRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlmacenServiceTest {

    private AlmacenService almacenService;

    @Mock
    private AlmacenRepository almacenRepository;

    @Mock
    private RestTemplate restTemplate;

    private Almacen almacenEjemplo;
    private AlmacenDto dtoEjemplo;

    @BeforeEach
    void setUp() {
        almacenService = new AlmacenService(almacenRepository, restTemplate);

        almacenEjemplo = new Almacen(1L, 3L, 50, "Bodega principal");

        dtoEjemplo = AlmacenDto.builder()
                .idAlmacen(1L)
                .idProducto(3L)
                .stock(50)
                .descripcion("Bodega principal")
                .build();
    }


    @Test
    void testRegistrarStock_exitoso() {
       
        when(almacenRepository.save(any(Almacen.class))).thenReturn(almacenEjemplo);

  
        AlmacenDto resultado = almacenService.registrarStock(dtoEjemplo);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdAlmacen());
        assertEquals(3L, resultado.getIdProducto());
        assertEquals(50, resultado.getStock());
        assertEquals("Bodega principal", resultado.getDescripcion());
        verify(almacenRepository).save(any(Almacen.class));
    }

    @Test
    void testRegistrarStock_sinIdProducto_lanzaExcepcion() {
      
        AlmacenDto dtoSinProducto = AlmacenDto.builder()
                .stock(50).descripcion("test").build();

    
        assertThrows(BadRequestException.class,
                () -> almacenService.registrarStock(dtoSinProducto));
    }

    @Test
    void testRegistrarStock_stockNegativo_lanzaExcepcion() {
  
        AlmacenDto dtoStockNegativo = AlmacenDto.builder()
                .idProducto(3L).stock(-5).build();

        assertThrows(BadRequestException.class,
                () -> almacenService.registrarStock(dtoStockNegativo));
    }

    @Test
    void testListarStock_retornaLista() {
      
        when(almacenRepository.findAll()).thenReturn(List.of(almacenEjemplo));

        List<AlmacenDto> resultado = almacenService.listarStock();

   
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(50, resultado.get(0).getStock());
        verify(almacenRepository).findAll();
    }

    @Test
    void testListarStock_listaVacia() {

        when(almacenRepository.findAll()).thenReturn(List.of());


        List<AlmacenDto> resultado = almacenService.listarStock();

        assertEquals(0, resultado.size());
    }

    @Test
    void testObtenerPorId_encontrado() {
  
        when(almacenRepository.findById(1L)).thenReturn(Optional.of(almacenEjemplo));

        AlmacenDto resultado = almacenService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdAlmacen());
        assertEquals(3L, resultado.getIdProducto());
        verify(almacenRepository).findById(1L);
    }

    @Test
    void testObtenerPorId_noEncontrado_lanzaExcepcion() {

        when(almacenRepository.findById(99L)).thenReturn(Optional.empty());

 
        assertThrows(ResourceNotFoundException.class,
                () -> almacenService.obtenerPorId(99L));
    }


    @Test
    void testObtenerPorProducto_retornaLista() {

        when(almacenRepository.findByIdProducto(3L)).thenReturn(List.of(almacenEjemplo));

        List<AlmacenDto> resultado = almacenService.obtenerPorProducto(3L);

        assertEquals(1, resultado.size());
        assertEquals(3L, resultado.get(0).getIdProducto());
    }


    @Test
    void testActualizarStock_exitoso() {
        
        AlmacenDto dtoActualizado = AlmacenDto.builder()
                .stock(100).descripcion("Bodega secundaria").build();
        Almacen actualizado = new Almacen(1L, 3L, 100, "Bodega secundaria");
        when(almacenRepository.findById(1L)).thenReturn(Optional.of(almacenEjemplo));
        when(almacenRepository.save(any(Almacen.class))).thenReturn(actualizado);

        AlmacenDto resultado = almacenService.actualizarStock(1L, dtoActualizado);

        assertEquals(100, resultado.getStock());
        assertEquals("Bodega secundaria", resultado.getDescripcion());
        verify(almacenRepository).save(any(Almacen.class));
    }

    @Test
    void testActualizarStock_noExiste_lanzaExcepcion() {
        when(almacenRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> almacenService.actualizarStock(99L, dtoEjemplo));
    }

    @Test
    void testActualizarStock_negativo_lanzaExcepcion() {
        AlmacenDto dtoNegativo = AlmacenDto.builder().stock(-10).build();
        when(almacenRepository.findById(1L)).thenReturn(Optional.of(almacenEjemplo));

        assertThrows(BadRequestException.class,
                () -> almacenService.actualizarStock(1L, dtoNegativo));
    }

    @Test
    void testEliminarStock_exitoso() {
        when(almacenRepository.existsById(1L)).thenReturn(true);
        doNothing().when(almacenRepository).deleteById(1L);

        almacenService.eliminarStock(1L);

        verify(almacenRepository).deleteById(1L);
    }

    @Test
    void testEliminarStock_noExiste_lanzaExcepcion() {
        when(almacenRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> almacenService.eliminarStock(99L));
    }
}
