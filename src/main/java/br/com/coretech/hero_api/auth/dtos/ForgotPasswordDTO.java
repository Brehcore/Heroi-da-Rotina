package br.com.coretech.hero_api.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Dados necessários para solicitar o envio do link de recuperação de senha")
public class ForgotPasswordDTO {

    @Schema(description = "E-mail cadastrado do usuário que deseja recuperar o acesso", example = "marido@email.com")
    private String email;
}
