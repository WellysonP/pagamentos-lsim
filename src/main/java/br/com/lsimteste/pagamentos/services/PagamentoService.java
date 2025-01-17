package br.com.lsimteste.pagamentos.services;

import br.com.lsimteste.pagamentos.dtos.PagamentoDTO;
import br.com.lsimteste.pagamentos.entities.PagamentoEntity;
import br.com.lsimteste.pagamentos.enums.Status;
import br.com.lsimteste.pagamentos.http.PedidoClient;
import br.com.lsimteste.pagamentos.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PedidoClient pedido;

    public Page<PagamentoDTO> obterPagamentos(Pageable pageable) {
        return pagamentoRepository
                .findAll(pageable)
                .map(pagamento -> modelMapper.map(pagamento, PagamentoDTO.class));
    }

    public PagamentoDTO obterPorId(Long id) {
        PagamentoEntity pagamentoEntity = pagamentoRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException());

        return modelMapper.map(pagamentoEntity, PagamentoDTO.class);
    }

    public PagamentoDTO criarPagamento(PagamentoDTO pagamentoDTO) {
        PagamentoEntity pagamentoEntity = modelMapper.map(pagamentoDTO, PagamentoEntity.class);
        pagamentoEntity.setStatus(Status.CRIADO);
        pagamentoRepository.save(pagamentoEntity);

        return modelMapper.map(pagamentoEntity, PagamentoDTO.class);
    }

    public PagamentoDTO atualizarPagamento(Long id, PagamentoDTO pagamentoDTO) {
        PagamentoEntity pagamentoEntity = modelMapper.map(pagamentoDTO, PagamentoEntity.class);
        pagamentoEntity.setId(id);
        pagamentoRepository.save(pagamentoEntity);

        return modelMapper.map(pagamentoEntity, PagamentoDTO.class);
    }

    public void deletarPagamento(Long id) {
        pagamentoRepository.deleteById(id);
    }

    public void confirmarPagamento(Long id){
        Optional<PagamentoEntity> pagamento = pagamentoRepository.findById(id);

        if (!pagamento.isPresent()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO);
        pagamentoRepository.save(pagamento.get());
        pedido.atualizaPagamento(pagamento.get().getPedidoId());
    }

    public void alterarStatus(Long id) {
        Optional<PagamentoEntity> pagamento = pagamentoRepository.findById(id);

        if (!pagamento.isPresent()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO_SEM_INTEGRACAO);
        pagamentoRepository.save(pagamento.get());
    }
}
