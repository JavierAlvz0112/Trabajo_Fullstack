package com.foodtruck.productos_service.dto;

import com.foodtruck.productos_service.model.Comida;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComidaDto {
    private Long idComida;
    private String nombre;
    private String descripcion;
    private String ingredientes;
    private String infoNutricional;
    private Double precio;
    private String tipo;

    public Comida toModel() {
        return Comida.builder()
                .idComida(this.idComida)
                .nombre(this.nombre)
                .descripcion(this.descripcion)
                .ingredientes(this.ingredientes)
                .infoNutricional(this.infoNutricional)
                .precio(this.precio)
                .tipo(this.tipo)
                .build();
    }

    public static ComidaDto fromModel(Comida comida) {
        return ComidaDto.builder()
                .idComida(comida.getIdComida())
                .nombre(comida.getNombre())
                .descripcion(comida.getDescripcion())
                .ingredientes(comida.getIngredientes())
                .infoNutricional(comida.getInfoNutricional())
                .precio(comida.getPrecio())
                .tipo(comida.getTipo())
                .build();
    }
}
