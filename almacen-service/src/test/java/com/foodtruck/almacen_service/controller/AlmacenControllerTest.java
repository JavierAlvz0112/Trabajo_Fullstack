package com.foodtruck.almacen_service.controller;

import com.foodtruck.almacen_service.dto.AlmacenDto;
import com.foodtruck.almacen_service.service.AlmacenService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AlmacenControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AlmacenService almacenService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private AlmacenDto almacenDto;

    @BeforeEach
    void setUp() {
        AlmacenController controller = new AlmacenController(almacenService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        almacenDto = AlmacenDto.builder()
                .idAlmacen(1L)
                .idProducto(3L)
                .stock(50)
                .descripcion("Bodega principal")
                .build();
    }

    @Test
    void testRegistrar_retorna201() throws Exception {
        // GIVEN
        when(almacenService.registrarStock(any(AlmacenDto.class))).thenReturn(almacenDto);

        // WHEN - THEN
        mockMvc.perform(post("/api/almacen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(almacenDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idAlmacen").value(1L))
                .andExpect(jsonPath("$.idProducto").value(3L))
                .andExpect(jsonPath("$.stock").value(50))
                .andExpect(jsonPath("$.descripcion").value("Bodega principal"));
    }

    @Test
    void testListar_retornaLista() throws Exception {
        // GIVEN
        when(almacenService.listarStock()).thenReturn(List.of(almacenDto));


        mockMvc.perform(get("/api/almacen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idAlmacen").value(1L))
                .andExpect(jsonPath("$[0].stock").value(50));
    }

    @Test
    void testBuscarPorId_encontrado() throws Exception {
        // GIVEN
        when(almacenService.obtenerPorId(1L)).thenReturn(almacenDto);

        // WHEN - THEN
        mockMvc.perform(get("/api/almacen/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAlmacen").value(1L))
                .andExpect(jsonPath("$.descripcion").value("Bodega principal"));
    }

    @Test
    void testActualizar_exitoso() throws Exception {
        // GIVEN
        AlmacenDto actualizado = AlmacenDto.builder()
                .idAlmacen(1L).idProducto(3L)
                .stock(100).descripcion("Bodega secundaria")
                .build();
        when(almacenService.actualizarStock(eq(1L), any(AlmacenDto.class))).thenReturn(actualizado);

        // WHEN - THEN
        mockMvc.perform(put("/api/almacen/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(100))
                .andExpect(jsonPath("$.descripcion").value("Bodega secundaria"));
    }

    @Test
    void testEliminar_retornaMensaje() throws Exception {
        // GIVEN
        doNothing().when(almacenService).eliminarStock(1L);

        // WHEN - THEN
        mockMvc.perform(delete("/api/almacen/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Registro de stock eliminado correctamente"));

        verify(almacenService, times(1)).eliminarStock(1L);
    }
}
