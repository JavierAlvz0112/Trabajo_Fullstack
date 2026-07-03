package com.foodtruck.pedidos_service.dto;

import com.foodtruck.pedidos_service.model.Pedidos;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoDto {
    private Long idPedido;
    private Long idUsuario;
    private Double total;
    private String estado;
    private Date fecha;
    private List<DetallePedidoDto> detalles;

    public Pedidos toModel() {
        Pedidos pedidos = new Pedidos();
        pedidos.setIdPedido(this.idPedido);
        pedidos.setIdUsuario(this.idUsuario);
        pedidos.setFecha(this.fecha != null ? this.fecha : new Date());
        pedidos.setEstado(this.estado);
        pedidos.setTotal(this.total);
        return pedidos;
    }

    public static PedidoDto fromEntity(Pedidos pedidos) {
        PedidoDto dto = new PedidoDto();
        dto.setIdPedido(pedidos.getIdPedido());
        dto.setIdUsuario(pedidos.getIdUsuario());
        dto.setFecha(pedidos.getFecha());
        dto.setEstado(pedidos.getEstado());
        dto.setTotal(pedidos.getTotal());
        return dto;
    }
    
}
