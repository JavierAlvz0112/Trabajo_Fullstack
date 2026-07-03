package com.foodtruck.productos_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comida")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComida;

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

    @Column(nullable = false, length = 20)
    private String tipo;
}
