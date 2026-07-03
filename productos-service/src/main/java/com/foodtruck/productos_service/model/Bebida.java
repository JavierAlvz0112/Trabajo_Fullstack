package com.foodtruck.productos_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bebida")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bebida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBebida;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 300)
    private String descripcion;

    @Column(columnDefinition = "TEXT")
    private String ingredientes;

    @Column(length = 500)
    private String infoNutricional;

    @Column(nullable = false)
    private Double precio;

    @Column(length = 50)
    private String sellos;

    @Column(length = 50)
    private String sabor;

    @Column(length = 20)
    private String tamaño;
}
