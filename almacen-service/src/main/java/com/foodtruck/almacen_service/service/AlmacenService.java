package com.foodtruck.almacen_service.service;

import com.foodtruck.almacen_service.dto.AlmacenDto;
import com.foodtruck.almacen_service.exception.BadRequestException;
import com.foodtruck.almacen_service.exception.ResourceNotFoundException;
import com.foodtruck.almacen_service.model.Almacen;
import com.foodtruck.almacen_service.repository.AlmacenRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AlmacenService {

    private static final Logger log = LoggerFactory.getLogger(AlmacenService.class);

    private final AlmacenRepository almacenRepository;
    private final RestTemplate restTemplate;

    public AlmacenService(AlmacenRepository almacenRepository, RestTemplate restTemplate) {
        this.almacenRepository = almacenRepository;
        this.restTemplate = restTemplate;
    }

    public AlmacenDto registrarStock(AlmacenDto dto) {
        log.info("Registrando stock para producto ID: {}", dto.getIdProducto());
        if (dto.getIdProducto() == null)
            throw new BadRequestException("El ID del producto es obligatorio");
        if (dto.getStock() == null || dto.getStock() < 0)
            throw new BadRequestException("El stock no puede ser negativo");
        Almacen almacen = almacenRepository.save(dto.toModel());
        log.info("Stock registrado con ID: {}", almacen.getIdAlmacen());
        return AlmacenDto.fromEntity(almacen);
    }

    public List<AlmacenDto> listarStock() {
        log.info("Listando todo el stock del almacen");
        return almacenRepository.findAll().stream()
                .map(AlmacenDto::fromEntity)
                .collect(Collectors.toList());
    }

    public AlmacenDto obtenerPorId(Long id) {
        log.info("Buscando registro de almacen ID: {}", id);
        Almacen almacen = almacenRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Almacen ID {} no encontrado", id);
                    return new ResourceNotFoundException("Registro no encontrado con ID: " + id);
                });
        return AlmacenDto.fromEntity(almacen);
    }

    public List<AlmacenDto> obtenerPorProducto(Long idProducto) {
        log.info("Buscando stock del producto ID: {}", idProducto);
        return almacenRepository.findByIdProducto(idProducto).stream()
                .map(AlmacenDto::fromEntity)
                .collect(Collectors.toList());
    }

    public AlmacenDto actualizarStock(Long id, AlmacenDto dto) {
        log.info("Actualizando stock ID: {}", id);
        Almacen almacen = almacenRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Almacen ID {} no encontrado para actualizar", id);
                    return new ResourceNotFoundException("Registro no encontrado con ID: " + id);
                });
        if (dto.getStock() != null && dto.getStock() < 0)
            throw new BadRequestException("El stock no puede ser negativo");
        almacen.setStock(dto.getStock() != null ? dto.getStock() : almacen.getStock());
        almacen.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion() : almacen.getDescripcion());
        almacenRepository.save(almacen);
        log.info("Stock ID {} actualizado exitosamente", id);
        return AlmacenDto.fromEntity(almacen);
    }

    public void eliminarStock(Long id) {
        log.warn("Eliminando registro de almacen ID: {}", id);
        if (!almacenRepository.existsById(id))
            throw new ResourceNotFoundException("Registro no encontrado con ID: " + id);
        almacenRepository.deleteById(id);
        log.info("Registro ID {} eliminado exitosamente", id);
    }

    public Map enriquecerStock(Long id) {
        log.info("Enriqueciendo stock ID: {}", id);
        Almacen almacen = almacenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado con ID: " + id));
        Object producto = null;
        try {
            producto = restTemplate.getForObject(
                "http://localhost:9095/api/productos/{id}", Map.class, almacen.getIdProducto());
            log.info("Datos de producto obtenidos para almacen {}", id);
        } catch (Exception e) {
            log.warn("No se pudo obtener producto {}: {}", almacen.getIdProducto(), e.getMessage());
        }
        return Map.of(
            "almacen", AlmacenDto.fromEntity(almacen),
            "producto", producto != null ? producto : Map.of()
        );
    }
}