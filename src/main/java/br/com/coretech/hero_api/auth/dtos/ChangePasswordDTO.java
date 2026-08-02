package br.com.coretech.hero_api.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Dados necessários para mudança de senha - Usuário autenticado")
public class ChangePasswordDTO {

    @Schema(description = "Antiga senha do usuário", example = "senha@123")
    private String oldPassword;

    @Schema(description = "Nova senha do usuário", example = "novaSenha@123")
    private String newPassword;
}
