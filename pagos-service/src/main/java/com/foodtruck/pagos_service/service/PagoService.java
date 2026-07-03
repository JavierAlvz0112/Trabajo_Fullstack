package com.foodtruck.pagos_service.service;

import com.foodtruck.pagos_service.dto.PagoDto;
import com.foodtruck.pagos_service.exception.ResourceNotFoundException;
import com.foodtruck.pagos_service.model.Pago;
import com.foodtruck.pagos_service.repository.PagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PagoService {
    private static final Logger log = LoggerFactory.getLogger(PagoService.class);

    @Autowired 
    private PagoRepository pagoRepository;

    private final RestTemplate restTemplate;
    
    public PagoService(PagoRepository pagoRepository, RestTemplate restTemplate) {
        this.pagoRepository = pagoRepository;
        this.restTemplate = restTemplate;
    }

    public PagoDto registrarPago(PagoDto dto) {
        log.info("Registrando nuevo pago para pedido {} con monto {}", dto.getIdPedido(), dto.getMonto());

        Pago pago = dto.toModel();
        Pago savedPago = pagoRepository.save(pago);
        log.info("Pago registrado con ID {}", savedPago.getIdPago());
        return PagoDto.fromEntity(savedPago);
    }

  
    public PagoDto obtenerTotalPorPedido(Long idPedido) {
        log.info("Obteniendo total de pagos para pedido {}", idPedido);

        List<Pago> pagos = pagoRepository.findByIdPedido(idPedido);
        Double total = pagos.stream().mapToDouble(Pago::getMonto).sum();
        
        log.info("Total de pagos para pedido {}: {}", idPedido, total);
        return PagoDto.builder().idPedido(idPedido).monto(total).build();
    }

      public List<PagoDto> listarTodosLosPagos() {
        log.info("Listando todos los pagos registrados");
        return pagoRepository.findAll()
                .stream()
                .map(PagoDto::fromEntity)
                .collect(Collectors.toList());
    }

    public PagoDto actualizarPago(Long id, PagoDto dto) {
        log.info("Actualizando pago {} para pedido {}", id, dto.getIdPedido());
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Pago con ID {} no encontrado para actualización", id);
                    return new RuntimeException("Pago no encontrado");
                });

        pago.setIdPedido(dto.getIdPedido());
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago (dto.getMetodoPago());
        pago.setEstado(dto.getEstado());
        pago.setFechaPago(dto.getFechaPago() != null ? dto.getFechaPago() : pago.getFechaPago());

        Pago pagoActualizado = pagoRepository.save(pago);
        log.info("Pago {} actualizado exitosamente", id);
        return PagoDto.fromEntity(pagoActualizado);
    }

    public PagoDto obtenerPagoPorId(Long id) {
        log.info("Buscando pago con ID: {}", id);
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));
        return PagoDto.fromEntity(pago);
    }

    public void eliminarPago(Long id) {
        log.info("Eliminando pago {}", id);
        pagoRepository.deleteById(id);
    }

    public java.util.List<PagoDto> buscarPorPedido(Long pedidoId) {
        return pagoRepository.findByIdPedido(pedidoId).stream().map(PagoDto::fromEntity).toList();
    }

    public Double totalPorPedido(Long pedidoId) {
        log.info("Calculando total de pagos para pedido {}", pedidoId);
        Double sum = pagoRepository.sumMontoByPedido(pedidoId);
        return sum != null ? sum : 0.0;
    }

    public java.util.List<PagoDto> buscarPorEstado(String estado) {
        return pagoRepository.findByEstado(estado).stream().map(PagoDto::fromEntity).toList();
    }

    public java.util.List<PagoDto> buscarPorRangoFechas(LocalDateTime from, LocalDateTime to) {
        java.util.Date fromDate = java.util.Date.from(from.atZone(java.time.ZoneId.systemDefault()).toInstant());
        java.util.Date toDate = java.util.Date.from(to.atZone(java.time.ZoneId.systemDefault()).toInstant());
        return pagoRepository.findByFechaPagoBetween(fromDate, toDate).stream().map(PagoDto::fromEntity).toList();
    }

    public Map<String, Object> enriquecerPago(Long id) {
        Pago pago = pagoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));
        log.info("Enriqueciendo pago {} llamando a pedidos-service", id);
        Object pedido = null;
        try {
            pedido = restTemplate.getForObject("http://localhost:9092/api/pedidos/{id}", Map.class, pago.getIdPedido());
        } catch (Exception e) {
            log.warn("No se pudo obtener pedido {}: {}", pago.getIdPedido(), e.getMessage());
        }
        return Map.of("pago", PagoDto.fromEntity(pago), "pedido", pedido);
    }
}

