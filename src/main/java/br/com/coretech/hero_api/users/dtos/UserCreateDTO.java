package br.com.coretech.hero_api.users.dtos;

import br.com.coretech.hero_api.users.enums.UserRole;
import lombok.Data;

@Data
public class UserCreateDTO {
    private String name;
    private String email;
    private String password;
    private UserRole role; // MONITOR ou MENOR
    private Long familyId; // Para vincular a uma família existente (ou null se for criar nova)
}