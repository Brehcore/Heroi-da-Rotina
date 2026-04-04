package br.com.coretech.hero_api.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto de requisição para autenticação de usuários")
public class LoginRequestDTO {

    @Schema(description = "Endereço de e-mail cadastrado do usuário", example = "heroidarotina@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    private String email;

    @Schema(description = "Senha de acesso do usuário", example = "Senha@123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "A senha é obrigatória")
    private String password;
}
