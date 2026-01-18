package br.com.coretech.hero_api.dtos;

import br.com.coretech.hero_api.enums.RoleUsuario;
import lombok.Data;

@Data
public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private RoleUsuario role;
    private Long familiaId;
    private String familiaNome; // Útil para exibir no app sem buscar outra entidade
}