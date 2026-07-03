package com.foodtruck.usuarios_service.controller;

import com.foodtruck.usuarios_service.dto.UsuarioDto;
import com.foodtruck.usuarios_service.service.UsuarioService;
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
class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private UsuarioDto usuarioDto;

    @BeforeEach
    void setUp() {
        UsuarioController controller = new UsuarioController(usuarioService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        usuarioDto = UsuarioDto.builder()
                .idUsuario(1L)
                .nombre("Juan Pérez")
                .correo("juan@mail.com")
                .contraseña("pass123")
                .rol("Cliente")
                .activo(true)
                .build();
    }

    // GET /api/usuarios
    @Test
    void testGetAll_retornaLista() throws Exception {
        when(usuarioService.listarUsuarios()).thenReturn(List.of(usuarioDto));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$[0].correo").value("juan@mail.com"))
                .andExpect(jsonPath("$[0].rol").value("Cliente"));
    }

    @Test
    void testGetAll_listaVacia() throws Exception {
        when(usuarioService.listarUsuarios()).thenReturn(List.of());

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // GET /api/usuarios/{id}
    @Test
    void testGetById_encontrado() throws Exception {
        when(usuarioService.findById(1L)).thenReturn(usuarioDto);

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.correo").value("juan@mail.com"));
    }

    // POST /api/usuarios
    @Test
    void testCreate_exitoso() throws Exception {
        when(usuarioService.crearUsuario(any(UsuarioDto.class))).thenReturn(usuarioDto);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$.rol").value("Cliente"));
    }

    // PUT /api/usuarios/{id}
    @Test
    void testUpdate_exitoso() throws Exception {
        UsuarioDto actualizado = UsuarioDto.builder()
                .idUsuario(1L)
                .nombre("Juan Actualizado")
                .correo("nuevo@mail.com")
                .contraseña("newpass")
                .rol("Administrador")
                .activo(false)
                .build();

        when(usuarioService.actualizarUsuario(eq(1L), any(UsuarioDto.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Actualizado"))
                .andExpect(jsonPath("$.rol").value("Administrador"))
                .andExpect(jsonPath("$.activo").value(false));
    }

    // DELETE /api/usuarios/{id}
    @Test
    void testDelete_retorna204() throws Exception {
        doNothing().when(usuarioService).eliminarUsuario(1L);

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).eliminarUsuario(1L);
    }
}
