package br.com.lsimteste.pagamentos.controller;

import br.com.lsimteste.pagamentos.dtos.PagamentoDTO;
import br.com.lsimteste.pagamentos.services.PagamentoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @GetMapping
    public Page<PagamentoDTO> listar(@PageableDefault(size = 10) Pageable pageable) {
        return pagamentoService.obterPagamentos(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoDTO> buscar(@PathVariable @NotNull Long id) {
        PagamentoDTO pagamentoDTO = pagamentoService.obterPorId(id);

        return ResponseEntity.ok(pagamentoDTO);
    }

    @PostMapping
    public ResponseEntity<PagamentoDTO> salvar(@Valid @RequestBody PagamentoDTO pagamentoDTO, UriComponentsBuilder uriBuilder) {
        PagamentoDTO pagamento = pagamentoService.criarPagamento(pagamentoDTO);

        URI endereco = uriBuilder.path("/pagamentos/{id}").buildAndExpand(pagamento.getId()).toUri();

        return ResponseEntity.created(endereco).body(pagamento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagamentoDTO> atualizar(@PathVariable @NotNull Long id, @RequestBody @Valid PagamentoDTO pagamentoDTO) {
        PagamentoDTO pagamento = pagamentoService.atualizarPagamento(id, pagamentoDTO);

        return ResponseEntity.ok(pagamento);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PagamentoDTO> remover(@PathVariable @NotNull Long id) {
        pagamentoService.deletarPagamento(id);

        return ResponseEntity.noContent().build();
    }
}
