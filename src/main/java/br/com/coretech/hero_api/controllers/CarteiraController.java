package br.com.coretech.hero_api.controllers;

import br.com.coretech.hero_api.dtos.CarteiraResponseDTO;
import br.com.coretech.hero_api.entities.Carteira;
import br.com.coretech.hero_api.mappers.HeroMapper;
import br.com.coretech.hero_api.repositories.CarteiraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carteiras")
@CrossOrigin("*") // Libera acesso para o Angular
public class CarteiraController {

    @Autowired
    private CarteiraRepository carteiraRepository;

    @Autowired
    private HeroMapper heroMapper;

    // Busca saldo e dados da carteira do menor
    @GetMapping("/menor/{menorId}")
    public ResponseEntity<CarteiraResponseDTO> buscarCarteira(@PathVariable Long menorId) {
        return carteiraRepository.findByMenorId(menorId)
                .map(heroMapper::toCarteiraDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // OBS: O histórico de transações (extrato) já é complexo.
    // TODO: riar um endpoint separado depois se a lista de transações ficar muito grande dentro do objeto Carteira.
}