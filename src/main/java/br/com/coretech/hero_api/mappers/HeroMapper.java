package br.com.coretech.hero_api.mappers;

import br.com.coretech.hero_api.dtos.CarteiraResponseDTO;
import br.com.coretech.hero_api.dtos.TransacaoDTO;
import br.com.coretech.hero_api.dtos.UsuarioResponseDTO;
import br.com.coretech.hero_api.entities.Carteira;
import br.com.coretech.hero_api.entities.TransacaoDinheiro;
import br.com.coretech.hero_api.entities.TransacaoFicha;
import br.com.coretech.hero_api.entities.Usuario;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class HeroMapper {

    public UsuarioResponseDTO toUsuarioDTO(Usuario usuario) {
        if (usuario == null) return null;

        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setRole(usuario.getRole());

        if (usuario.getFamilia() != null) {
            dto.setFamiliaId(usuario.getFamilia().getId());
            dto.setFamiliaNome(usuario.getFamilia().getNomeFamilia());
        }
        return dto;
    }

    public CarteiraResponseDTO toCarteiraDTO(Carteira carteira) {
        if (carteira == null) return null;

        CarteiraResponseDTO dto = new CarteiraResponseDTO();
        dto.setId(carteira.getId());
        dto.setSaldoFichas(carteira.getSaldoFichas());
        dto.setSaldoDinheiro(carteira.getSaldoDinheiro());

        if (carteira.getMenor() != null) {
            dto.setMenorId(carteira.getMenor().getId());
            dto.setMenorNome(carteira.getMenor().getNome());
        }
        return dto;
    }

    // Mapper genérico para histórico (Fichas)
    public TransacaoDTO toTransacaoDTO(TransacaoFicha tx) {
        TransacaoDTO dto = new TransacaoDTO();
        dto.setId(tx.getId());
        dto.setTipo(tx.getTipo());
        dto.setMotivo(tx.getMotivo());
        dto.setData(tx.getData());
        dto.setValorFormatado(tx.getValor() + " Fichas"); // Formata para leitura humana
        return dto;
    }

    // Mapper genérico para histórico (Dinheiro)
    public TransacaoDTO toTransacaoDTO(TransacaoDinheiro tx) {
        TransacaoDTO dto = new TransacaoDTO();
        dto.setId(tx.getId());
        dto.setTipo(tx.getTipo());
        dto.setMotivo(tx.getMotivo());
        dto.setData(tx.getData());
        dto.setValorFormatado("R$ " + String.format("%.2f", tx.getValor())); // Formata dinheiro
        return dto;
    }
}