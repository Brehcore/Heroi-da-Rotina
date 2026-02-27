package br.com.coretech.hero_api.users.controllers;

import br.com.coretech.hero_api.jwt.services.JwtService;
import br.com.coretech.hero_api.users.dtos.TokenResponseDTO;
import br.com.coretech.hero_api.users.dtos.UserLoginDTO;
import br.com.coretech.hero_api.users.entities.User;
import br.com.coretech.hero_api.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @RequestMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody UserLoginDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );
        User User = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        String jwt = jwtService.generateToken(User);

        return ResponseEntity.ok(new TokenResponseDTO(jwt));
    }
}
