package br.com.coretech.hero_api.auth.services;

import br.com.coretech.hero_api.auth.dtos.LoginRequestDTO;
import br.com.coretech.hero_api.auth.dtos.LoginResponseDTO;
import br.com.coretech.hero_api.jwt.services.JwtService;
import br.com.coretech.hero_api.users.entities.User;
import br.com.coretech.hero_api.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public LoginResponseDTO login(LoginRequestDTO request) {
        // 1. O Spring Security verifica se o e-mail e senha estão corretos
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Se a senha está correta, buscamos o usuário completo no banco
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 3. Geramos o Token JWT
        String jwtToken = jwtService.generateToken(user);

        // 4. Retornamos o DTO completo com o Token e as informações do usuário!
        return new LoginResponseDTO(
                jwtToken,
                user.getId(),
                user.getName(),
                user.getRole() // A ROLE VAI AQUI!
        );
    }
}