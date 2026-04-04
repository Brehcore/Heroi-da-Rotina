package br.com.coretech.hero_api.auth.dtos;

import br.com.coretech.hero_api.users.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto de resposta contendo os dados de autenticação e perfil do usuário")
public class LoginResponseDTO {

    @Schema(description = "Token JWT para autenticação nas rotas protegidas",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "ID único do usuário no banco de dados",
            example = "1")
    private Long id;

    @Schema(description = "Nome completo do usuário",
            example = "Tony Stark")
    private String name;

    @Schema(description = "Nível de permissão do usuário no sistema",
            example = "ADMIN")
    private UserRole role;
}