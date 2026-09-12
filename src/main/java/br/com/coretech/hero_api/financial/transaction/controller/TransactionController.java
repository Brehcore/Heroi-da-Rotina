package br.com.coretech.hero_api.financial.transaction.controller;

import br.com.coretech.hero_api.financial.transaction.service.TransactionService;
import br.com.coretech.hero_api.financial.transaction.dto.ConvertTokensDTO;
import br.com.coretech.hero_api.financial.transaction.dto.DeductTokensRequestDTO;
import br.com.coretech.hero_api.financial.transaction.dto.TransactionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Transações", description = "Responsável por realizar operações de transações nas carteiras dos menores")
@RestController
@RequestMapping("/api/transactions")
@CrossOrigin("*")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Buscar Histórico", description = "Retorna o histórico financeiro do menor")
    @GetMapping("/minor/{minorId}/transactions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TransactionDTO>> getMinorTransactionalHistory(@PathVariable Long minorId,
                                                                             @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TransactionDTO> transactions = transactionService.getMinorTransactionalHistory(minorId, pageable);
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Adicionar fichas", description = "Monitor adiciona fichas à carteira do menor.")
    @PostMapping("/minor/{minorId}/deposit-tokens")
    @PreAuthorize( "hasRole('MONITOR')")
    public ResponseEntity<Void> depositTokens(@PathVariable Long minorId, @RequestParam Integer amount, @RequestParam String motive) {
        transactionService.tokenDeposit(minorId, amount, motive);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remover fichas", description = "Monitor remove fichas da carteira do menor.")
    @PostMapping("/minor/{minorId}/deduct-tokens")
    @PreAuthorize("hasRole('MONITOR')")
    public ResponseEntity<Void> deductToken(@PathVariable Long minorId, @Valid @RequestBody DeductTokensRequestDTO dto) {
        transactionService.tokenDeduct(minorId, dto.getAmount(), dto.getMotive());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Converter fichas",
            description = "Converte uma quantidade informada de fichas do menor em dinheiro no cofre."
    )
    @PostMapping("/minor/{minorId}/convert")
    @PreAuthorize("hasRole('MONITOR')")
    public ResponseEntity<Void> convertTokens(
            @PathVariable Long minorId,
            @Valid @RequestBody ConvertTokensDTO dto
    ) {
        transactionService.convertTokensToMoney(minorId, dto.tokensToConvert());
        return ResponseEntity.ok().build();
    }
}
