package br.com.coretech.hero_api.users.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Dados para criação de um novo grupo familiar")
public class FamilyCreateDTO {

    @Schema(description = "Nome do clã ou grupo familiar", example = "Família Pimentel")
    private String familyName;

    @Schema(description = "URL da foto de perfil gerada dinamicamente", example = "https://api.dicebear.com/8.x/bottts/svg?seed=Nome")
    private String profilePictureUrl;
}