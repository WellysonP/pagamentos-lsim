package br.com.lsimteste.pagamentos.services;

import br.com.lsimteste.pagamentos.dtos.PagamentoDTO;
import br.com.lsimteste.pagamentos.entities.PagamentoEntity;
import br.com.lsimteste.pagamentos.enums.Status;
import br.com.lsimteste.pagamentos.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ModelMapper modelMapper;

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
}
