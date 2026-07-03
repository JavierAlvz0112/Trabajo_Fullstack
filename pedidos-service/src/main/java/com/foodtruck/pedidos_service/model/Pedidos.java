package com.foodtruck.pedidos_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedidos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    @Column(nullable = false)
    private Long idUsuario; 

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false, length = 40)
    private String estado; // pendiente, pagado, entregado

    @Column(nullable = false)
    private Date fecha = new Date();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles;
}

