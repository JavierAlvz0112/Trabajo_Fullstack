package com.foodtruck.usuarios_service.service;

import com.foodtruck.usuarios_service.dto.UsuarioDto;
import com.foodtruck.usuarios_service.model.Usuario;
import com.foodtruck.usuarios_service.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Service
public class UsuarioService {
    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RestTemplate restTemplate;
    // 1. listar
    public List<UsuarioDto> listarUsuarios() {
        log.info("Solicitando la lista de todos los usuarios registrados.");
        List<Usuario> usuarios = usuarioRepository.findAll();
        log.info("Usuarios encontrados en la base de datos: {}", usuarios.size());
        return usuarios.stream()
                .map(UsuarioDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 2. crear
    public UsuarioDto crearUsuario(UsuarioDto dto) {
        log.info("Iniciando la creación de un nuevo usuario con correo: {}", dto.getCorreo());
        Usuario usuario = dto.toModel();
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("Usuario creado exitosamente con ID asignado: {}", usuarioGuardado.getIdUsuario());
        return UsuarioDto.fromEntity(usuarioGuardado);
    }

    // 3. ACTUALIZAR 
    public UsuarioDto actualizarUsuario(Long idUsuario, UsuarioDto dto) {
        log.info("Actualizando usuario con ID: {}", idUsuario);
        Usuario existing = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> {
                    log.error("Error al actualizar: Usuario ID {} no existe.", idUsuario);
                    return new RuntimeException("Usuario no encontrado");
                }); 
      
        existing.setNombre(dto.getNombre());
        existing.setCorreo(dto.getCorreo());
        existing.setContraseña(dto.getContraseña());
        existing.setRol(dto.getRol());
        existing.setActivo(dto.isActivo());
        
        Usuario usuarioActualizado = usuarioRepository.save(existing);
        log.info("Usuario con ID {} actualizado exitosamente.", idUsuario);
        return UsuarioDto.fromEntity(usuarioActualizado);
    }
    // 4. buscar por el id
    public UsuarioDto findById(Long idUsuario) {
        log.info("Buscando usuario con ID: {}", idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> {
                    log.error("Error al buscar: Usuario ID {} no existe.", idUsuario);
                    return new RuntimeException("Usuario no encontrado");
                });
        return UsuarioDto.fromEntity(usuario);
    }

    // 5. ELIMINAR
    public void eliminarUsuario(Long idUsuario) {
        log.info("Alerta: Se eliminará de forma permanente el usuario con ID: {}", idUsuario);
        usuarioRepository.deleteById(idUsuario);
        log.info("Usuario con ID {} eliminado exitosamente.", idUsuario);
    }
    // 6. conexion con pedidos-services 
    public List<Map> obtenerPedidosDeUsuario(Long usuarioId) {
        String url = "http://localhost:9092/api/pedidos?usuarioId=" + usuarioId;
        log.info("Solicitando pedidos para el usuario ID: {} desde URL: {}", url);
        try {
            Map[] resp = restTemplate.getForObject(url, Map[].class);
            if (resp != null) {
                log.info("Pedidos recibidos para usuario ID {}: {}", usuarioId, resp.length);
                return java.util.Arrays.asList(resp);
            } 
            log.info("PEDIDOS-SERVICE respondió vacío (null) para el usuario ID: {}", usuarioId);
            return java.util.Collections.emptyList();
        } catch (Exception e) {
            log.error("Error al conectar con PEDIDOS-SERVICE para usuario ID {}: {}", usuarioId, e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    // 7. Conexión con pagos-service
    public Double totalPagosPorUsuario(Long usuarioId) {
        log.info("calculando el total acumulado de pagos para el ususario ID: {}", usuarioId);
        List<Map> pedidos = obtenerPedidosDeUsuario(usuarioId);
        double total = 0.0;
        if (pedidos.isEmpty()) {
            log.info("No se encontraron pedidos para el usuario ID: {}. Total de pagos será 0.", usuarioId);
            return total;
        }
        for (Map p : pedidos) {
            Object idPedido = p.get("idPedido");
            if (idPedido != null) {
                String urlPagos = "http://localhost:9093/api/pagos/total-por-pedido?pedidoId=" + idPedido;
                try {
                    log.info("Solicitando total de pagos para el pedido ID: {} desde URL: {}", idPedido, urlPagos);
                    Double suma = restTemplate.getForObject(urlPagos, Double.class);
                    if (suma != null) total += suma;
                } catch (Exception e) {
                    log.error("Error al conectar con PAGOS-SERVICE para el pedido ID: {}", idPedido, e);
                }
            }
        }
        log.info("Total acumulado de pagos para el usuario ID {}: {}", usuarioId, total);
        return total;
    }

}
