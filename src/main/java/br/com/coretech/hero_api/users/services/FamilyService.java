package br.com.coretech.hero_api.users.services;

import br.com.coretech.hero_api.mappers.HeroMapper;
import br.com.coretech.hero_api.users.dtos.FamilyCreateDTO;
import br.com.coretech.hero_api.users.dtos.FamilyResponseDTO;
import br.com.coretech.hero_api.users.entities.Family;
import br.com.coretech.hero_api.users.entities.User;
import br.com.coretech.hero_api.users.repositories.FamilyRepository;
import br.com.coretech.hero_api.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FamilyService {

    private final HeroMapper heroMapper;
    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;

    @Transactional
    public FamilyResponseDTO createFamily(FamilyCreateDTO dto, String userEmail) {
        User loggedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (loggedUser.getFamily() != null) {
            throw new RuntimeException("Este usuário já pertence a uma família!");
        }

        Family family = new Family();
        family.setFamilyName(dto.getFamilyName());
        Family saveFamily = familyRepository.save(family);

        loggedUser.setFamily(saveFamily);
        userRepository.save(loggedUser);

        return heroMapper.toFamilyDTO(saveFamily);

    }

}
