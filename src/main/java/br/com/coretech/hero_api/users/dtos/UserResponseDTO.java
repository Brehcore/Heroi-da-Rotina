package br.com.coretech.hero_api.users.dtos;

import br.com.coretech.hero_api.users.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Dados detalhados do perfil do usuário")
public class UserResponseDTO {

    @Schema(description = "ID único do usuário", example = "5")
    private Long id;

    @Schema(description = "Nome completo do usuário", example = "Peter Parker")
    private String name;

    @Schema(description = "E-mail cadastrado", example = "peter.parker@example.com")
    private String email;

    @Schema(description = "Papel ou nível de acesso", example = "MENOR")
    private UserRole role;

    @Schema(description = "ID da família vinculada", example = "1")
    private Long familyId;

    @Schema(description = "Nome da família vinculada para facilitar exibição no front-end", example = "Vingadores")
    private String familyName;
}