package com.foundationvip.finances.api;

import com.foundationvip.finances.dto.AtualizaStatusDTO;
import com.foundationvip.finances.dto.LancamentoDTO;
import com.foundationvip.finances.model.Lancamento;
import com.foundationvip.finances.model.StatusLancamento;
import com.foundationvip.finances.model.TipoLancamento;
import com.foundationvip.finances.model.Usuario;
import com.foundationvip.finances.repository.LancamentoRepository;
import com.foundationvip.finances.service.LancamentoService;
import com.foundationvip.finances.service.RegraNegocioException;
import com.foundationvip.finances.service.UsuarioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@Api(value = "API Rest - Lançamentos")
@CrossOrigin(origins = "*")
public class LancamentoResource {

    @Autowired
    private LancamentoService lancamentoService;
    @Autowired
    private UsuarioService usuarioService;

    @ApiOperation(value = "Recurso para salvar o lancamento")
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity save(@Valid @RequestBody LancamentoDTO lancamentoDTO) {
        try {
            Lancamento lancamentoSalvo = converter(lancamentoDTO);
            lancamentoSalvo = lancamentoService.save(lancamentoSalvo);
            return ResponseEntity.ok(lancamentoSalvo);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation(value = "Recurso para atualizar o lancamento")
    @PutMapping("{id}")
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody LancamentoDTO lancamentoDTO) {
        return lancamentoService.obterPorId(id).map(entity -> {
            try {
                Lancamento lancamentoUpdate = converter(lancamentoDTO);
                lancamentoUpdate.setId(entity.getId());
                lancamentoService.update(lancamentoUpdate);
                return ResponseEntity.ok(lancamentoUpdate);
            } catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado", HttpStatus.BAD_REQUEST));
    }

    @ApiOperation(value = "Recurso para deletar o lancamento")
    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable("id") Long id){
        return lancamentoService.obterPorId(id).map(entity ->{
            lancamentoService.delete(entity);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado", HttpStatus.BAD_REQUEST));
    }

    @PutMapping("{id}/atualiza-status")
    public ResponseEntity updateStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto){
        return lancamentoService.obterPorId(id).map(entity ->{
            StatusLancamento statusSelecionado  = StatusLancamento.valueOf(dto.getStatus());
            if (statusSelecionado == null){
                return ResponseEntity.badRequest().body("Não foi possível atualizar Status");
            }
            try{
                entity.setStatus(statusSelecionado);
                lancamentoService.update(entity);
                return ResponseEntity.ok(entity);
            }catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado", HttpStatus.BAD_REQUEST));
    }

    @ApiOperation(value = "Recurso para buscar lancamentos com filtros")
    @GetMapping
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

    private Lancamento converter(LancamentoDTO dto){
        Lancamento lancamento = new Lancamento();
        BeanUtils.copyProperties(dto, lancamento);
        Usuario usuario = usuarioService.obterPorId(dto.getUsuario())
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado"));
        lancamento.setUsuario(usuario);
        if(dto.getTipo() != null){
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        }
        if(dto.getStatus() != null){
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        }
        return lancamento;
    }
}
