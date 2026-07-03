package com.foodtruck.usuarios_service.service;

import com.foodtruck.usuarios_service.dto.UsuarioDto;
import com.foodtruck.usuarios_service.model.Usuario;
import com.foodtruck.usuarios_service.repository.UsuarioRepository;

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
class UsuarioServiceTest {

    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RestTemplate restTemplate;

    // Datos de prueba reutilizables
    private Usuario usuarioEjemplo;
    private UsuarioDto dtoEjemplo;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService();
        // inyectamos los mocks manualmente con reflexión porque usan @Autowired
        try {
            var repoField = UsuarioService.class.getDeclaredField("usuarioRepository");
            repoField.setAccessible(true);
            repoField.set(usuarioService, usuarioRepository);

            var rtField = UsuarioService.class.getDeclaredField("restTemplate");
            rtField.setAccessible(true);
            rtField.set(usuarioService, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException("Error inyectando mocks", e);
        }

        usuarioEjemplo = new Usuario(1L, "Juan Pérez", "juan@mail.com", "pass123", "Cliente", true);

        dtoEjemplo = UsuarioDto.builder()
                .idUsuario(1L)
                .nombre("Juan Pérez")
                .correo("juan@mail.com")
                .contraseña("pass123")
                .rol("Cliente")
                .activo(true)
                .build();
    }

    
    // crearUsuario
    @Test
    void testCrearUsuario_exitoso() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEjemplo);

        UsuarioDto resultado = usuarioService.crearUsuario(dtoEjemplo);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdUsuario());
        assertEquals("Juan Pérez", resultado.getNombre());
        assertEquals("juan@mail.com", resultado.getCorreo());
        assertEquals("Cliente", resultado.getRol());
        assertTrue(resultado.isActivo());
        verify(usuarioRepository).save(any(Usuario.class));
    }
    
    // listarUsuarios
    

    @Test
    void testListarUsuarios_devuelveListaCorrectamente() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioEjemplo));

        List<UsuarioDto> resultado = usuarioService.listarUsuarios();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Juan Pérez", resultado.get(0).getNombre());
        verify(usuarioRepository).findAll();
    }

    @Test
    void testListarUsuarios_listaVacia() {
        when(usuarioRepository.findAll()).thenReturn(List.of());

        List<UsuarioDto> resultado = usuarioService.listarUsuarios();

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

    // findById

    @Test
    void testFindById_encontrado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));

        UsuarioDto resultado = usuarioService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdUsuario());
        assertEquals("juan@mail.com", resultado.getCorreo());
        verify(usuarioRepository).findById(1L);
    }

    @Test
    void testFindById_noEncontrado_lanzaExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> usuarioService.findById(99L));
        verify(usuarioRepository).findById(99L);
    }
    
    // actualizarUsuario
    
    @Test
    void testActualizarUsuario_exitoso() {
        UsuarioDto dtoActualizado = UsuarioDto.builder()
                .nombre("Juan Actualizado")
                .correo("nuevo@mail.com")
                .contraseña("newpass")
                .rol("Administrador")
                .activo(false)
                .build();

        Usuario usuarioActualizado = new Usuario(1L, "Juan Actualizado", "nuevo@mail.com",
                "newpass", "Administrador", false);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioActualizado);

        UsuarioDto resultado = usuarioService.actualizarUsuario(1L, dtoActualizado);

        assertNotNull(resultado);
        assertEquals("Juan Actualizado", resultado.getNombre());
        assertEquals("nuevo@mail.com", resultado.getCorreo());
        assertEquals("Administrador", resultado.getRol());
        assertFalse(resultado.isActivo());
        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testActualizarUsuario_noExiste_lanzaExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> usuarioService.actualizarUsuario(99L, dtoEjemplo));
    }

    // eliminarUsuario
    
    @Test
    void testEliminarUsuario_exitoso() {
        doNothing().when(usuarioRepository).deleteById(1L);

        usuarioService.eliminarUsuario(1L);

        verify(usuarioRepository).deleteById(1L);
    }
    
    // totalPagosPorUsuario — sin pedidos retorna 0
    
    @Test
    void testTotalPagosPorUsuario_sinPedidos_retornaCero() {
        // restTemplate devuelve array vacío al pedir pedidos
        when(restTemplate.getForObject(anyString(), eq(java.util.Map[].class)))
                .thenReturn(new java.util.Map[0]);

        Double total = usuarioService.totalPagosPorUsuario(1L);

        assertEquals(0.0, total);
    }
}
