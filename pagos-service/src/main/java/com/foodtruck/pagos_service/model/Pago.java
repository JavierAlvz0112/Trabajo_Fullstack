package com.foodtruck.pagos_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;

    @Column(nullable = false)
    private Long idPedido; // referencia al pedido

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false, length = 50)
    private String metodoPago; // tarjeta, efectivo, transferencia

    @Column(nullable = false)
    private String estado; // pendiente, completado, fallido

    @Column(nullable = false)
    private Date fechaPago = new Date();
}
