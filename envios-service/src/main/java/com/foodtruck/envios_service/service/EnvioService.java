package com.foodtruck.envios_service.service;

import com.foodtruck.envios_service.dto.EnvioDto;
import com.foodtruck.envios_service.exception.BadRequestException;
import com.foodtruck.envios_service.exception.ResourceNotFoundException;
import com.foodtruck.envios_service.model.Envio;
import com.foodtruck.envios_service.repository.EnvioRepository;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
 
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
 
@Service
public class EnvioService {
 
    private static final Logger log = LoggerFactory.getLogger(EnvioService.class);
 
    private final EnvioRepository envioRepository;
    private final RestTemplate restTemplate;
 
    public EnvioService(EnvioRepository envioRepository, RestTemplate restTemplate) {
        this.envioRepository = envioRepository;
        this.restTemplate = restTemplate;
    }
 
    public EnvioDto crearEnvio(EnvioDto dto) {
        log.info("Creando envio para pedido ID: {}", dto.getIdPedido());
        if (dto.getIdPedido() == null)
            throw new BadRequestException("El ID del pedido es obligatorio");
        if (dto.getDireccion() == null || dto.getDireccion().isBlank())
            throw new BadRequestException("La dirección es obligatoria");
        Envio envio = envioRepository.save(dto.toModel());
        log.info("Envio creado con ID: {}", envio.getIdEnvio());
        return EnvioDto.fromEntity(envio);
    }
 
    public List<EnvioDto> listarEnvios() {
        log.info("Listando todos los envios");
        return envioRepository.findAll().stream()
                .map(EnvioDto::fromEntity)
                .collect(Collectors.toList());
    }
 
    public EnvioDto obtenerEnvioPorId(Long id) {
        log.info("Buscando envio con ID: {}", id);
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Envio ID {} no encontrado", id);
                    return new ResourceNotFoundException("Envio no encontrado con ID: " + id);
                });
        return EnvioDto.fromEntity(envio);
    }
 
    public List<EnvioDto> buscarPorPedido(Long idPedido) {
        log.info("Buscando envios del pedido ID: {}", idPedido);
        return envioRepository.findByIdPedido(idPedido).stream()
                .map(EnvioDto::fromEntity)
                .collect(Collectors.toList());
    }
 
    public List<EnvioDto> buscarPorEstado(String estado) {
        log.info("Buscando envios con estado: {}", estado);
        return envioRepository.findByEstado(estado).stream()
                .map(EnvioDto::fromEntity)
                .collect(Collectors.toList());
    }
 
    public EnvioDto actualizarEstado(Long id, String nuevoEstado) {
        log.info("Actualizando estado de envio ID: {} a '{}'", id, nuevoEstado);
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Envio ID {} no encontrado para actualizar", id);
                    return new ResourceNotFoundException("Envio no encontrado con ID: " + id);
                });
        envio.setEstado(nuevoEstado);
        envioRepository.save(envio);
        log.info("Estado de envio ID {} actualizado exitosamente", id);
        return EnvioDto.fromEntity(envio);
    }
 
    public EnvioDto actualizarEnvio(Long id, EnvioDto dto) {
        log.info("Actualizando envio ID: {}", id);
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envio no encontrado con ID: " + id));
        envio.setDireccion(dto.getDireccion());
        envio.setEstado(dto.getEstado());
        envio.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now());
        envioRepository.save(envio);
        log.info("Envio ID {} actualizado exitosamente", id);
        return EnvioDto.fromEntity(envio);
    }
 
    public void eliminarEnvio(Long id) {
        log.warn("Eliminando envio ID: {}", id);
        if (!envioRepository.existsById(id))
            throw new ResourceNotFoundException("Envio no encontrado con ID: " + id);
        envioRepository.deleteById(id);
        log.info("Envio ID {} eliminado exitosamente", id);
    }
 
    public Map enriquecerEnvio(Long id) {
        log.info("Enriqueciendo envio ID: {}", id);
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envio no encontrado con ID: " + id));
        Object pedido = null;
        try {
            pedido = restTemplate.getForObject(
                "http://localhost:9092/api/pedidos/{id}", Map.class, envio.getIdPedido());
            log.info("Datos de pedido obtenidos para envio {}", id);
        } catch (Exception e) {
            log.warn("No se pudo obtener pedido {}: {}", envio.getIdPedido(), e.getMessage());
        }
        return Map.of(
            "envio", EnvioDto.fromEntity(envio),
            "pedido", pedido != null ? pedido : Map.of()
        );
    }
}
 