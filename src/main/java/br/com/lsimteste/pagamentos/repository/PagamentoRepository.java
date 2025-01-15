package br.com.lsimteste.pagamentos.repository;

import br.com.lsimteste.pagamentos.entities.PagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<PagamentoEntity, Long> {
}
