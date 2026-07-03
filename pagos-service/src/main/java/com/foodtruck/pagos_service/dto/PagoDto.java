package com.foodtruck.pagos_service.dto;

import com.foodtruck.pagos_service.model.Pago;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoDto {
    private Long idPago;
    private Long idPedido;
    private Double monto;
    private String metodoPago;
    private String estado;
    private Date fechaPago;

    public Pago toModel() {
       Pago pago = new Pago();
        pago.setIdPago(this.idPago);
        pago.setIdPedido(this.idPedido);
        pago.setMonto(this.monto);
        pago.setFechaPago(this.fechaPago != null ? this.fechaPago : new Date());
        pago.setMetodoPago(this.metodoPago);
        pago.setEstado(this.estado);
        return pago;
    }

    public static PagoDto fromEntity(Pago pago) {
       PagoDto dto = new PagoDto();
        dto.setIdPago(pago.getIdPago());
        dto.setIdPedido(pago.getIdPedido());
        dto.setMonto(pago.getMonto());
        dto.setFechaPago(pago.getFechaPago());
        dto.setMetodoPago(pago.getMetodoPago());
        dto.setEstado(pago.getEstado());
        return dto;
    }

}
