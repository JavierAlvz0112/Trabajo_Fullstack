package com.foodtruck.pedidos_service.dto;

import com.foodtruck.pedidos_service.model.DetallePedido;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePedidoDto {
    private Long idDetalle;
    private Long idProducto;
    private Integer cantidad;
    private Double subtotal;

    public DetallePedido toModel() {
        return DetallePedido.builder()
                .idDetalle(this.idDetalle)
                .idProducto(this.idProducto)
                .cantidad(this.cantidad)
                .subtotal(this.subtotal)
                .build();
    }

    public static DetallePedidoDto fromModel(DetallePedido detalle) {
        return DetallePedidoDto.builder()
                .idDetalle(detalle.getIdDetalle())
                .idProducto(detalle.getIdProducto())
                .cantidad(detalle.getCantidad())
                .subtotal(detalle.getSubtotal())
                .build();
    }
}
