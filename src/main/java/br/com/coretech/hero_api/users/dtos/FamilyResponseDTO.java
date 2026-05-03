package br.com.coretech.hero_api.users.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "Dados de retorno da família, incluindo seus integrantes")
public class FamilyResponseDTO {

    @Schema(description = "ID único da família", example = "1")
    private Long id;

    @Schema(description = "Nome da família", example = "Os Incríveis")
    private String familyName;

    @Schema(description = "Lista de usuários que pertencem a esta família")
    private List<UserResponseDTO> members;

    @Schema(description = "URL da foto de perfil gerada dinamicamente", example = "https://api.dicebear.com/8.x/bottts/svg?seed=Nome")
    private String profilePictureUrl;
}