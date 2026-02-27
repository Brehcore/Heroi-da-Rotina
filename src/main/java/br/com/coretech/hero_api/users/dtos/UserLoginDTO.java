package br.com.coretech.hero_api.users.dtos;

import lombok.Data;

@Data
public class UserLoginDTO {

    private String email;
    private String password;
}
