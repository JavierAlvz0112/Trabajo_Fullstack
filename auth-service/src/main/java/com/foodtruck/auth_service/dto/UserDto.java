package com.foodtruck.auth_service.dto;

import lombok.Data;

@Data
public class UserDto {
    private String rut;
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private String password;
    private Long roleId;
}
