package br.com.coretech.hero_api.dtos;

import br.com.coretech.hero_api.enums.RoleUsuario;
import lombok.Data;

@Data
public class UsuarioCreateDTO {
    private String nome;
    private String email;
    private String senha;
    private RoleUsuario role; // MONITOR ou MENOR
    private Long familiaId; // Para vincular a uma família existente (ou null se for criar nova)
}