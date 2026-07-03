package com.foodtruck.productos_service.service;

import com.foodtruck.productos_service.dto.BebidaDto;
import com.foodtruck.productos_service.exception.BadRequestException;
import com.foodtruck.productos_service.exception.ResourceNotFoundException;
import com.foodtruck.productos_service.model.Bebida;
import com.foodtruck.productos_service.repository.BebidaRepository;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
 
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
 
@Service
public class BebidaService {
 
    private static final Logger log = LoggerFactory.getLogger(BebidaService.class);
 
    private final BebidaRepository bebidaRepository;
    private final RestTemplate restTemplate;
 
    public BebidaService(BebidaRepository bebidaRepository, RestTemplate restTemplate) {
        this.bebidaRepository = bebidaRepository;
        this.restTemplate = restTemplate;
    }
 
    public BebidaDto crearBebida(BebidaDto dto) {
        log.info("Creando bebida: {}", dto.getNombre());
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new BadRequestException("El nombre de la bebida es obligatorio");
        if (dto.getPrecio() == null || dto.getPrecio() <= 0)
            throw new BadRequestException("El precio debe ser mayor a cero");
        Bebida bebida = bebidaRepository.save(dto.toModel());
        log.info("Bebida creada con ID: {}", bebida.getIdBebida());
        return BebidaDto.fromModel(bebida);
    }
 
    public List<BebidaDto> listarBebidas() {
        log.info("Listando todas las bebidas");
        return bebidaRepository.findAll().stream()
                .map(BebidaDto::fromModel)
                .collect(Collectors.toList());
    }
 
    public BebidaDto obtenerBebidaPorId(Long id) {
        log.info("Buscando bebida con ID: {}", id);
        Bebida bebida = bebidaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Bebida ID {} no encontrada", id);
                    return new ResourceNotFoundException("Bebida no encontrada con ID: " + id);
                });
        return BebidaDto.fromModel(bebida);
    }
 
    public BebidaDto actualizarBebida(Long id, BebidaDto dto) {
        log.info("Actualizando bebida ID: {}", id);
        Bebida bebida = bebidaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Bebida ID {} no encontrada para actualizar", id);
                    return new ResourceNotFoundException("Bebida no encontrada con ID: " + id);
                });
        bebida.setNombre(dto.getNombre());
        bebida.setDescripcion(dto.getDescripcion());
        bebida.setIngredientes(dto.getIngredientes());
        bebida.setInfoNutricional(dto.getInfoNutricional());
        bebida.setPrecio(dto.getPrecio());
        bebida.setSellos(dto.getSellos());
        bebida.setSabor(dto.getSabor());
        bebida.setTamaño(dto.getTamaño());
        bebidaRepository.save(bebida);
        log.info("Bebida ID {} actualizada exitosamente", id);
        return BebidaDto.fromModel(bebida);
    }
 
    public void eliminarBebida(Long id) {
        log.warn("Eliminando bebida ID: {}", id);
        if (!bebidaRepository.existsById(id))
            throw new ResourceNotFoundException("Bebida no encontrada con ID: " + id);
        bebidaRepository.deleteById(id);
        log.info("Bebida ID {} eliminada exitosamente", id);
    }
 
    public Map obtenerInventario(Long bebidaId) {
        log.info("Consultando inventario para bebida ID: {}", bebidaId);
        try {
            Map resp = restTemplate.getForObject(
                "http://localhost:9091/api/almacen?productoId={id}", Map.class, bebidaId);
            return resp != null ? resp : Collections.emptyMap();
        } catch (Exception e) {
            log.warn("No se pudo obtener inventario para bebida {}: {}", bebidaId, e.getMessage());
            return Collections.emptyMap();
        }
    }
 
    public List<Map> obtenerPedidosPorProducto(Long productoId) {
        log.info("Consultando pedidos para bebida ID: {}", productoId);
        try {
            Map[] resp = restTemplate.getForObject(
                "http://localhost:9092/api/pedidos?productoId={id}", Map[].class, productoId);
            return resp != null ? Arrays.asList(resp) : Collections.emptyList();
        } catch (Exception e) {
            log.warn("No se pudo obtener pedidos para bebida {}: {}", productoId, e.getMessage());
            return Collections.emptyList();
        }
    }
}