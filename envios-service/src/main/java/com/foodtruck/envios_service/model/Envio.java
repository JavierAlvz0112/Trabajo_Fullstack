package com.foodtruck.envios_service.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "envios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Envio {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEnvio;
 
    @Column(nullable = false)
    private Long idPedido;
 
    @Column(nullable = false)
    private String direccion;
 
    @Column(nullable = false)
    private String estado;
 
    @Column(nullable = false)
    private LocalDateTime fecha;
}
 