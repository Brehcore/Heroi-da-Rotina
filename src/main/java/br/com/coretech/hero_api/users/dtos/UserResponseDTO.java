package br.com.coretech.hero_api.users.dtos;

import br.com.coretech.hero_api.users.enums.UserRole;
import lombok.Data;

@Data
public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private Long familyId;
    private String familyName; // Útil para exibir no aplicativo sem buscar outra entidade
}
