package com.foundationvip.finances.api;

import com.foundationvip.finances.model.Lancamento;
import com.foundationvip.finances.service.LancamentoService;
import com.foundationvip.finances.service.RegraNegocioException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Api(value = "API Rest - Lancamentos")
@CrossOrigin(origins = "*")
public class LancamentoResource {

    @Autowired
    private LancamentoService lancamentoService;

    @PostMapping("/lancamentos")
    @ApiOperation(value = "Recurso para salvar o lancamento")
    public ResponseEntity save(@RequestBody Lancamento lancamento) {
        try {
            Lancamento lancamentoSalvo = lancamentoService.save(lancamento);
            return new ResponseEntity(lancamentoSalvo, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity update(@PathVariable Long id, @RequestBody Lancamento lancamento) {
        return lancamentoService.obterPorId(id).map(entity -> {
            try {
                Lancamento lancamentoUpdate = lancamento;
                lancamentoUpdate.setId(entity.getId());
                lancamentoService.update(lancamentoUpdate);
                return ResponseEntity.ok(lancamentoUpdate);
            } catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado", HttpStatus.BAD_REQUEST));

    }
}
