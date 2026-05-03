package br.com.coretech.hero_api.users.controllers;

import br.com.coretech.hero_api.users.dtos.FamilyCreateDTO;
import br.com.coretech.hero_api.users.dtos.FamilyResponseDTO;
import br.com.coretech.hero_api.users.dtos.UserResponseDTO;
import br.com.coretech.hero_api.users.services.FamilyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Família", description = "Gerenciamento da família")
@RestController
@RequestMapping("/api/families")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyService familyService;

    @Operation(summary = "Criar família", description = "Criação de uma nova família.")
    @PostMapping
    @PreAuthorize( "hasRole('MONITOR')")
    public ResponseEntity<FamilyResponseDTO> createFamily(@RequestBody FamilyCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(familyService.createFamily(dto));
    }

    @Operation(summary = "Listar membros", description = "Listar todos os membros de uma família.")
    @GetMapping("/family/{familyId}")
    public ResponseEntity<List<UserResponseDTO>> familyList(@PathVariable Long familyId) {
        return ResponseEntity.ok(familyService.listMembers(familyId));
    }

    @Operation(summary = "Listar minhas famílias", description = "Retorna todas as famílias às quais o monitor pertence.")
    @GetMapping("/me")
    @PreAuthorize( "hasRole('MONITOR')")
    public ResponseEntity<List<FamilyResponseDTO>> getMyFamilies() {
        // Pega o e-mail do token JWT atual
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        List<FamilyResponseDTO> families = familyService.getMyFamilies(userEmail);
        return ResponseEntity.ok(families);
    }
}
