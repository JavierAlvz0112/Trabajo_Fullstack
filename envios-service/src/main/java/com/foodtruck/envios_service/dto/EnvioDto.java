package com.foodtruck.envios_service.dto;
import com.foodtruck.envios_service.model.Envio;
import lombok.*;
import java.time.LocalDateTime;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioDto {
 
    private Long idEnvio;
    private Long idPedido;
    private String direccion;
    private String estado;
    private LocalDateTime fecha;
 
    public static EnvioDto fromEntity(Envio e) {
        return EnvioDto.builder()
                .idEnvio(e.getIdEnvio())
                .idPedido(e.getIdPedido())
                .direccion(e.getDireccion())
                .estado(e.getEstado())
                .fecha(e.getFecha())
                .build();
    }
 
    public Envio toModel() {
        return new Envio(null, idPedido, direccion,
                estado != null ? estado : "pendiente",
                fecha != null ? fecha : LocalDateTime.now());
    }
}
