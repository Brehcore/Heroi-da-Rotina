package br.com.coretech.hero_api.users.controllers;

import br.com.coretech.hero_api.mappers.HeroMapper;
import br.com.coretech.hero_api.users.dtos.FamilyCreateDTO;
import br.com.coretech.hero_api.users.dtos.FamilyResponseDTO;
import br.com.coretech.hero_api.users.dtos.UserResponseDTO;
import br.com.coretech.hero_api.users.repositories.UserRepository;
import br.com.coretech.hero_api.users.services.FamilyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/families")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyService familyService;
    private UserRepository userRepository;
    private HeroMapper heroMapper;

    @PostMapping
    public ResponseEntity<FamilyResponseDTO> createFamily(@RequestBody FamilyCreateDTO dto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        FamilyResponseDTO newFamily = familyService.createFamily(dto, userEmail);

        return ResponseEntity.status(HttpStatus.CREATED).body(newFamily);
    }

    // GET: Listar todos os membros de uma família (Para o Dashboard do Monitor no Angular)
    @GetMapping("/familia/{familiaId}")
    public ResponseEntity<List<UserResponseDTO>> familyList(@PathVariable Long familyId) {
        List<UserResponseDTO> members = userRepository.findAllByFamilyId(familyId)
                .stream()
                .map(heroMapper::toUserDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(members);
    }
}
