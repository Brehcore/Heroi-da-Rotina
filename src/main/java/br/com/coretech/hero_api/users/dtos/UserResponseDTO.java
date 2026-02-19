package br.com.coretech.hero_api.users.dtos;

import br.com.coretech.hero_api.users.RoleUsuario;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private RoleUsuario role;
    private Long familiaId;
    private String familiaNome; // Útil para exibir no app sem buscar outra entidade
}