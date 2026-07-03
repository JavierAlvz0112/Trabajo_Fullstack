package com.foodtruck.pedidos_service.service;

import com.foodtruck.pedidos_service.dto.PedidoDto;
import com.foodtruck.pedidos_service.exception.ResourceNotFoundException;
import com.foodtruck.pedidos_service.model.Pedidos;
import com.foodtruck.pedidos_service.repository.PedidoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository pedidoRepository;
    private final RestTemplate restTemplate;

    public PedidoService(PedidoRepository pedidoRepository, RestTemplate restTemplate) {
        this.pedidoRepository = pedidoRepository;
        this.restTemplate = restTemplate;
    }

    public PedidoDto crearPedido(PedidoDto dto) {
        log.info("Iniciando creación de pedido para usuario ID: {}", dto.getIdUsuario());
        Pedidos pedido = dto.toModel();
        if (pedido.getEstado() == null) {
            pedido.setEstado("Pendiente");
        }
        Pedidos guardado = pedidoRepository.save(pedido);
        log.info("Pedido creado exitosamente con ID: {}", guardado.getIdPedido());
        return PedidoDto.fromEntity(guardado);
    }

    public List<PedidoDto> listarPedidos(Long idUsuario) {
        if (idUsuario != null) {
            log.info("Listando pedidos para usuario ID: {}", idUsuario);
            return pedidoRepository.findByIdUsuario(idUsuario).stream()
                    .map(PedidoDto::fromEntity)
                    .collect(Collectors.toList());
        }
        log.info("Listando todos los pedidos");
        return pedidoRepository.findAll().stream()
                .map(PedidoDto::fromEntity)
                .collect(Collectors.toList());
    }

    public PedidoDto buscarPorId(long idPedido) {
        log.info("Buscando pedido con ID: {}", idPedido);
        Pedidos pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> {
                    log.error("Pedido ID {} no encontrado", idPedido);
                    return new ResourceNotFoundException("Pedido no encontrado con ID: " + idPedido);
                });
        log.info("Pedido {} encontrado", idPedido);
        return PedidoDto.fromEntity(pedido);
    }

    public PedidoDto actualizarPedido(Long idPedido, String nuevoEstado) {
        log.info("Actualizando estado de pedido ID: {} a '{}'", idPedido, nuevoEstado);
        Pedidos pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> {
                    log.error("Pedido ID {} no encontrado para actualizar", idPedido);
                    return new ResourceNotFoundException("Pedido no encontrado con ID: " + idPedido);
                });
        pedido.setEstado(nuevoEstado);
        Pedidos actualizado = pedidoRepository.save(pedido);
        log.info("Estado de pedido ID {} actualizado exitosamente", idPedido);
        return PedidoDto.fromEntity(actualizado);
    }

    public void eliminarPedido(Long idPedido) {
        log.warn("Eliminando pedido con ID: {}", idPedido);
        if (!pedidoRepository.existsById(idPedido)) {
            log.error("Pedido ID {} no encontrado para eliminar", idPedido);
            throw new ResourceNotFoundException("Pedido no encontrado con ID: " + idPedido);
        }
        pedidoRepository.deleteById(idPedido);
        log.info("Pedido ID {} eliminado exitosamente", idPedido);
    }

    public Double totalPorUsuario(Long idUsuario) {
        log.info("Calculando total de pedidos para usuario ID: {}", idUsuario);
        Double result = pedidoRepository.sumTotalByIdUsuario(idUsuario);
        Double total = result != null ? result : 0.0;
        log.info("Total calculado para usuario {}: {}", idUsuario, total);
        return total;
    }

    public List<PedidoDto> buscarPorRangoFechas(LocalDateTime from, LocalDateTime to) {
        log.info("Buscando pedidos entre {} y {}", from, to);
        return pedidoRepository.findByFechaBetween(from, to).stream()
                .map(PedidoDto::fromEntity)
                .toList();
    }

    public List<PedidoDto> listarPorUsuario(Long usuarioId) {
        log.info("Listando pedidos de usuario ID: {}", usuarioId);
        return pedidoRepository.findByIdUsuario(usuarioId).stream()
                .map(PedidoDto::fromEntity)
                .toList();
    }

    public List<PedidoDto> listarPorProducto(Long productoId) {
        log.info("Listando pedidos que contienen producto ID: {}", productoId);
        return pedidoRepository.findByDetalles_IdProducto(productoId).stream()
                .map(PedidoDto::fromEntity)
                .toList();
    }

    public Map<String, Object> enriquecerPedido(Long id) {
        log.info("Enriqueciendo pedido ID: {}", id);
        Pedidos pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));

        Object usuario = null;
        try {
            usuario = restTemplate.getForObject(
                "http://localhost:9094/api/usuarios/{id}", Map.class, pedido.getIdUsuario());
            log.info("Datos de usuario obtenidos para pedido {}", id);
        } catch (Exception e) {
            log.warn("No se pudo obtener usuario {}: {}", pedido.getIdUsuario(), e.getMessage());
        }

        Object pagos = null;
        try {
            pagos = restTemplate.getForObject(
                "http://localhost:9093/api/pagos?pedidoId={pedidoId}", Object.class, id);
            log.info("Datos de pagos obtenidos para pedido {}", id);
        } catch (Exception e) {
            log.warn("No se pudo obtener pagos para pedido {}: {}", id, e.getMessage());
        }

        return Map.of(
            "pedido", PedidoDto.fromEntity(pedido),
            "usuario", usuario != null ? usuario : Map.of(),
            "pagos", pagos != null ? pagos : List.of()
        );
    }
}

