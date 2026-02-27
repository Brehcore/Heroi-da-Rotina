package br.com.coretech.hero_api.users.dtos;

import lombok.Data;

import java.util.List;

@Data
public class FamilyResponseDTO {

    private Long id;
    private String familyName;
    private List<UserResponseDTO> members;
}
