package br.com.lsimteste.pagamentos.controller;

import br.com.lsimteste.pagamentos.dtos.PagamentoDTO;
import br.com.lsimteste.pagamentos.services.PagamentoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;

@RestController
@RequestMapping("pagamentos")
@Tag(name = "Pagamentos", description = "Endpoints para gerenciamento de pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @Operation(summary = "Listar pagamentos", description = "Retorna uma lista paginada de pagamentos")
    @GetMapping
    public Page<PagamentoDTO> listar(@PageableDefault(size = 10) Pageable pageable) {
        return pagamentoService.obterPagamentos(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pagamento por ID", description = "Retorna um pagamento específico pelo seu ID")
    public ResponseEntity<PagamentoDTO> buscar(@PathVariable @NotNull Long id) {
        PagamentoDTO pagamentoDTO = pagamentoService.obterPorId(id);

        return ResponseEntity.ok(pagamentoDTO);
    }

    @PostMapping
    @Operation(summary = "Criar pagamento", description = "Cria um novo pagamento")
    public ResponseEntity<PagamentoDTO> salvar(@Valid @RequestBody PagamentoDTO pagamentoDTO, UriComponentsBuilder uriBuilder) {
        PagamentoDTO pagamento = pagamentoService.criarPagamento(pagamentoDTO);

        URI endereco = uriBuilder.path("/pagamentos/{id}").buildAndExpand(pagamento.getId()).toUri();

        return ResponseEntity.created(endereco).body(pagamento);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pagamento", description = "Atualiza um pagamento existente")
    public ResponseEntity<PagamentoDTO> atualizar(@PathVariable @NotNull Long id, @RequestBody @Valid PagamentoDTO pagamentoDTO) {
        PagamentoDTO pagamento = pagamentoService.atualizarPagamento(id, pagamentoDTO);

        return ResponseEntity.ok(pagamento);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover pagamento", description = "Remove um pagamento existente")
    public ResponseEntity<PagamentoDTO> remover(@PathVariable @NotNull Long id) {
        pagamentoService.deletarPagamento(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirmar")
    @CircuitBreaker(name = "atualizaPedido", fallbackMethod = "pagamentoAutorizadoComIntegracaoPendente")
    public void confirmarPagamento(@PathVariable @NotNull Long id){
        pagamentoService.confirmarPagamento(id);
    }

    public void pagamentoAutorizadoComIntegracaoPendente(Long id, Exception e){
        pagamentoService.alterarStatus(id);
    }

}
