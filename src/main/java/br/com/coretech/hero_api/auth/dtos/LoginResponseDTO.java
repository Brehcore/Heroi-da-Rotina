package br.com.coretech.hero_api.auth.dtos;

import br.com.coretech.hero_api.users.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private String token;
    private Long id;
    private String name;
    private UserRole role;
}