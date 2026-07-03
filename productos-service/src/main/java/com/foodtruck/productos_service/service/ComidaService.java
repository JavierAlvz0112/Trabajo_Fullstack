package com.foodtruck.productos_service.service;

import com.foodtruck.productos_service.dto.ComidaDto;
import com.foodtruck.productos_service.exception.BadRequestException;
import com.foodtruck.productos_service.exception.ResourceNotFoundException;
import com.foodtruck.productos_service.model.Comida;
import com.foodtruck.productos_service.repository.ComidaRepository;

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
public class ComidaService {

    private static final Logger log = LoggerFactory.getLogger(ComidaService.class);

    private final ComidaRepository comidaRepository;
    private final RestTemplate restTemplate;

    public ComidaService(ComidaRepository comidaRepository, RestTemplate restTemplate) {
        this.comidaRepository = comidaRepository;
        this.restTemplate = restTemplate;
    }

    public ComidaDto crearComida(ComidaDto dto) {
        log.info("Creando comida: {}", dto.getNombre());
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new BadRequestException("El nombre de la comida es obligatorio");
        if (dto.getPrecio() == null || dto.getPrecio() <= 0)
            throw new BadRequestException("El precio debe ser mayor a cero");
        Comida comida = comidaRepository.save(dto.toModel());
        log.info("Comida creada con ID: {}", comida.getIdComida());
        return ComidaDto.fromModel(comida);
    }

    public List<ComidaDto> listarComidas() {
        log.info("Listando todas las comidas");
        return comidaRepository.findAll().stream()
                .map(ComidaDto::fromModel)
                .collect(Collectors.toList());
    }

    public ComidaDto obtenerComidaPorId(Long id) {
        log.info("Buscando comida con ID: {}", id);
        Comida comida = comidaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Comida ID {} no encontrada", id);
                    return new ResourceNotFoundException("Comida no encontrada con ID: " + id);
                });
        return ComidaDto.fromModel(comida);
    }

    public ComidaDto actualizarComida(Long id, ComidaDto dto) {
        log.info("Actualizando comida ID: {}", id);
        Comida comida = comidaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Comida ID {} no encontrada para actualizar", id);
                    return new ResourceNotFoundException("Comida no encontrada con ID: " + id);
                });
        comida.setNombre(dto.getNombre());
        comida.setDescripcion(dto.getDescripcion());
        comida.setIngredientes(dto.getIngredientes());
        comida.setInfoNutricional(dto.getInfoNutricional());
        comida.setPrecio(dto.getPrecio());
        comida.setTipo(dto.getTipo());
        comidaRepository.save(comida);
        log.info("Comida ID {} actualizada exitosamente", id);
        return ComidaDto.fromModel(comida);
    }

    public void eliminarComida(Long id) {
        log.warn("Eliminando comida ID: {}", id);
        if (!comidaRepository.existsById(id))
            throw new ResourceNotFoundException("Comida no encontrada con ID: " + id);
        comidaRepository.deleteById(id);
        log.info("Comida ID {} eliminada exitosamente", id);
    }

    public Map obtenerInventario(Long comidaId) {
        log.info("Consultando inventario para comida ID: {}", comidaId);
        try {
            Map resp = restTemplate.getForObject(
                "http://localhost:9091/api/almacen?productoId={id}", Map.class, comidaId);
            return resp != null ? resp : Collections.emptyMap();
        } catch (Exception e) {
            log.warn("No se pudo obtener inventario para comida {}: {}", comidaId, e.getMessage());
            return Collections.emptyMap();
        }
    }

    public List<Map> obtenerPedidosPorProducto(Long productoId) {
        log.info("Consultando pedidos para comida ID: {}", productoId);
        try {
            Map[] resp = restTemplate.getForObject(
                "http://localhost:9092/api/pedidos?productoId={id}", Map[].class, productoId);
            return resp != null ? Arrays.asList(resp) : Collections.emptyList();
        } catch (Exception e) {
            log.warn("No se pudo obtener pedidos para comida {}: {}", productoId, e.getMessage());
            return Collections.emptyList();
        }
    }
}
