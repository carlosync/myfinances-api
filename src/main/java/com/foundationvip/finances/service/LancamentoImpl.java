package com.foundationvip.finances.service;

import com.foundationvip.finances.model.Lancamento;
import com.foundationvip.finances.model.StatusLancamento;

import java.util.List;
import java.util.Optional;

public interface LancamentoImpl {

    Lancamento save(Lancamento lancamento);

    Lancamento update(Lancamento lancamento);

    void delete(Lancamento lancamento);

    List<Lancamento> search(Lancamento lancamento);

    void updateStatus(Lancamento lancamento, StatusLancamento statusLancamento);

    void validateLancamento(Lancamento lancamento);

    Optional<Lancamento> obterPorId(Long id);
}
