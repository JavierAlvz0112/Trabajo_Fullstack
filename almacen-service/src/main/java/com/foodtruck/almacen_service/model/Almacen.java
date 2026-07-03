package com.foodtruck.almacen_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "almacen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Almacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAlmacen;

    @Column(nullable = false)
    private Long idProducto;

    @Column(nullable = false)
    private Integer stock;

    @Column
    private String descripcion;
}
