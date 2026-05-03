package br.com.coretech.hero_api.users.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Dados para registro público de um novo Monitor")
public class UserRegisterDTO {

    @Schema(description = "Nome completo do usuário", example = "Peter Parker")
    private String name;

    @Schema(description = "E-mail de acesso (deve ser único)", example = "peter.parker@example.com")
    private String email;

    @Schema(description = "Senha de acesso", example = "Spider@2026")
    private String password;

    @Schema(description = "URL do avatar escolhido no front-end", example = "https://api.dicebear.com/8.x/bottts/svg?seed=Lucas")
    private String profilePictureUrl;
}