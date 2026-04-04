package br.com.coretech.hero_api.users.dtos;

import br.com.coretech.hero_api.users.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Dados para cadastro de um novo usuário (Monitor ou Menor)")
public class UserCreateDTO {

    @Schema(description = "Nome completo do usuário", example = "Peter Parker")
    private String name;

    @Schema(description = "E-mail de acesso (deve ser único)", example = "peter.parker@example.com")
    private String email;

    @Schema(description = "Senha de acesso", example = "Spider@2026")
    private String password;

    @Schema(description = "Papel do usuário no sistema", example = "MONITOR")
    private UserRole role;

    @Schema(description = "ID da família à qual o usuário será vinculado. Pode ser nulo se a família for criada posteriormente.",
            example = "1", nullable = true)
    private Long familyId;
}