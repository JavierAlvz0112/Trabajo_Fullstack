package com.foodtruck.almacen_service.dto;

import com.foodtruck.almacen_service.model.Almacen;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlmacenDto {

    private Long idAlmacen;
    private Long idProducto;
    private Integer stock;
    private String descripcion;

    public static AlmacenDto fromEntity(Almacen a) {
        return AlmacenDto.builder()
                .idAlmacen(a.getIdAlmacen())
                .idProducto(a.getIdProducto())
                .stock(a.getStock())
                .descripcion(a.getDescripcion())
                .build();
    }

    public Almacen toModel() {
        return new Almacen(null, idProducto,
                stock != null ? stock : 0,
                descripcion);
    }
}