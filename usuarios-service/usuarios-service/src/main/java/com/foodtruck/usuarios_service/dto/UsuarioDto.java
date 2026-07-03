package com.foodtruck.usuarios_service.dto;

import com.foodtruck.usuarios_service.model.Usuario;
import com.foodtruck.usuarios_service.dto.UsuarioDto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDto {
    private Long idUsuario;
    private String nombre;
    private String correo;
    private String contraseña;
    private String rol;
    private boolean activo;

public Usuario toModel() {
    Usuario usuario = new Usuario();
    usuario.setIdUsuario(this.idUsuario);
    usuario.setNombre(this.nombre);
    usuario.setCorreo(this.correo);
    usuario.setContraseña(this.contraseña);
    usuario.setRol(this.rol);
    usuario.setActivo(this.activo);
    return usuario;
}

public static UsuarioDto fromEntity(Usuario usuario) {
    UsuarioDto dto = new UsuarioDto();
    dto.setIdUsuario(usuario.getIdUsuario());
    dto.setNombre(usuario.getNombre());
    dto.setCorreo(usuario.getCorreo());
    dto.setContraseña(usuario.getContraseña());
    dto.setRol(usuario.getRol());
    dto.setActivo(usuario.isActivo());
    return dto;
}

}
