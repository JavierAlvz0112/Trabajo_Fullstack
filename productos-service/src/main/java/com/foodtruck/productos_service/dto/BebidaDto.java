package com.foodtruck.productos_service.dto;

import com.foodtruck.productos_service.model.Bebida;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BebidaDto {
    private Long idBebida;
    private String nombre;
    private String descripcion;
    private String ingredientes;
    private String infoNutricional;
    private Double precio;
    private String sellos;
    private String sabor;
    private String tamaño;

    public Bebida toModel() {
        return Bebida.builder()
                .idBebida(this.idBebida)
                .nombre(this.nombre)
                .descripcion(this.descripcion)
                .ingredientes(this.ingredientes)
                .infoNutricional(this.infoNutricional)
                .precio(this.precio)
                .sellos(this.sellos)
                .sabor(this.sabor)
                .tamaño(this.tamaño)
                .build();
    }

    public static BebidaDto fromModel(Bebida bebida) {
        return BebidaDto.builder()
                .idBebida(bebida.getIdBebida())
                .nombre(bebida.getNombre())
                .descripcion(bebida.getDescripcion())
                .ingredientes(bebida.getIngredientes())
                .infoNutricional(bebida.getInfoNutricional())
                .precio(bebida.getPrecio())
                .sellos(bebida.getSellos())
                .sabor(bebida.getSabor())
                .tamaño(bebida.getTamaño())
                .build();
    }
}
