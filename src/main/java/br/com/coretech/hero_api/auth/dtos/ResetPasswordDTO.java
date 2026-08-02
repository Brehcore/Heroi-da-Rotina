package br.com.coretech.hero_api.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Dados para efetivar a troca da senha utilizando o token de segurança")
public class ResetPasswordDTO {

    @Schema(description = "Token único gerado pelo sistema e enviado no link do e-mail", example = "123e4567-e89b-12d3-a456-426614174000")
    private String token;

    @Schema(description = "A nova senha escolhida pelo usuário", example = "Heroi@2026")
    private String newPassword;
}
