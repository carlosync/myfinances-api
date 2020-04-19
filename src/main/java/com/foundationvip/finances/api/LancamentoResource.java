package com.foundationvip.finances.api;

import com.foundationvip.finances.model.Lancamento;
import com.foundationvip.finances.model.Usuario;
import com.foundationvip.finances.service.LancamentoService;
import com.foundationvip.finances.service.RegraNegocioException;
import com.foundationvip.finances.service.UsuarioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Api(value = "API Rest - Lancamentos")
@CrossOrigin(origins = "*")
public class LancamentoResource {

    @Autowired
    private LancamentoService lancamentoService;
    @Autowired
    private UsuarioService usuarioService;

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
    @ApiOperation(value = "Recurso para atualizar o lancamento")
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

    @DeleteMapping("{id}")
    @ApiOperation(value = "Recurso para deletar o lancamento")
    public ResponseEntity delete(@PathVariable Long id){
        return lancamentoService.obterPorId(id).map(entity ->{
            lancamentoService.delete(entity);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado", HttpStatus.BAD_REQUEST));
    }

    @GetMapping
    @ApiOperation(value = "Recurso para buscar lancamentos com filtros")
    public ResponseEntity search(
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam(value = "usuario") Long IdUsuario){

        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);

        Optional<Usuario> usuario = usuarioService.obterPorId(IdUsuario);
        if(!usuario.isPresent()){
            return ResponseEntity.badRequest().body("Consulta com erro: Usuário não foi localizado");
        }else{
            lancamentoFiltro.setUsuario(usuario.get());
        }
        List<Lancamento> lancamentos = lancamentoService.search(lancamentoFiltro);
        return ResponseEntity.ok(lancamentos);
    }
}
