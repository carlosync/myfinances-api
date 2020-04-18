package com.foundationvip.finances.service;

import com.foundationvip.finances.model.Lancamento;
import com.foundationvip.finances.model.StatusLancamento;
import com.foundationvip.finances.repository.LancamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LancamentoService implements LancamentoImpl{

    @Autowired
    private LancamentoRepository repository;

    @Override
    public Lancamento save(Lancamento lancamento) {
        validateLancamento(lancamento);
        lancamento.setStatusLancamento(StatusLancamento.PENDENTE);
        return repository.save(lancamento);
    }

    @Override
    public Lancamento update(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        validateLancamento(lancamento);
        return repository.save(lancamento);
    }

    @Override
    public void delete(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        repository.delete(lancamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lancamento> search(Lancamento lancamentoFiltro) {
        Example example = Example.of(lancamentoFiltro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example);
    }

    @Override
    public void updateStatus(Lancamento lancamento, StatusLancamento statusLancamento) {
        lancamento.setStatusLancamento(statusLancamento);
        update(lancamento);
    }

    @Override
    public void validateLancamento(Lancamento lancamento) {
        if(StringUtils.hasText(lancamento.getDescricao())){
            throw new RegraNegocioException("Informe uma Descrição válida.");
        }
        if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12){
            throw new RegraNegocioException("Informe um Mês válido.");
        }
        if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4){
            throw new RegraNegocioException("Informe um Ano válido.");
        }
        if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null){
            throw new RegraNegocioException("Informe um Usuário.");
        }
        if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1){
            throw new RegraNegocioException("Informe um Valor válido.");
        }
        if(lancamento.getTipo() == null){
            throw new RegraNegocioException("Informe um Tipo de lançamento");
        }
    }

    @Override
    public Optional<Lancamento> obterPorId(Long id) {
        return repository.findById(id);
    }


}
