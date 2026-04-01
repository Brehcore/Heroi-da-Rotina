package br.com.coretech.hero_api.users.services;

import br.com.coretech.hero_api.mappers.HeroMapper;
import br.com.coretech.hero_api.users.dtos.FamilyCreateDTO;
import br.com.coretech.hero_api.users.dtos.FamilyResponseDTO;
import br.com.coretech.hero_api.users.dtos.UserResponseDTO;
import br.com.coretech.hero_api.users.entities.Family;
import br.com.coretech.hero_api.users.entities.User;
import br.com.coretech.hero_api.users.repositories.FamilyRepository;
import br.com.coretech.hero_api.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FamilyService {

    private final HeroMapper heroMapper;
    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;

    @Transactional
    public FamilyResponseDTO createFamily(FamilyCreateDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User loggedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));


        Family family = new Family();
        family.setFamilyName(dto.getFamilyName());
        Family savedFamily = familyRepository.save(family);

        if (loggedUser.getFamilies() == null) {
            loggedUser.setFamilies(new ArrayList<>());
        }

        loggedUser.getFamilies().add(savedFamily);

        userRepository.save(loggedUser);

        return heroMapper.toFamilyDTO(savedFamily);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> listMembers(Long familyId) {
        return userRepository.findAllByFamiliesId(familyId)
                .stream()
                .map(heroMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    public List<FamilyResponseDTO> getMyFamilies(String email) {
        User user = userRepository.findByEmailWithFamilies(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (user.getFamilies() == null) {
            return new ArrayList<>();
        }

        return user.getFamilies().stream()
                .map(heroMapper::toFamilyDTO)
                .collect(Collectors.toList());
    }
}
