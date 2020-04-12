package com.foundationvip.finances.api;

import com.foundationvip.finances.model.Usuario;
import com.foundationvip.finances.service.AutheticateException;
import com.foundationvip.finances.service.RegraNegocioException;
import com.foundationvip.finances.service.UsuarioService;
import com.foundationvip.finances.service.UsuarioServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Api(value = "API Rest Usuários")
@CrossOrigin(origins = "*")
public class UsuarioResource {

    @Autowired
    private UsuarioService service;

    @PostMapping("/usuarios")
    @ApiOperation(value = "Recurso para salvar o usuário")
    public ResponseEntity save(@RequestBody Usuario usuario){
        try {
            Usuario usuarioSalvo = service.save(usuario);
            return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
        }catch (RegraNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/autenticar")
    @ApiOperation(value = "Recurso para autenticar o usuário na api")
    public ResponseEntity autenticar(@RequestBody Usuario usuario){
        try {
            Usuario usuarioAutenticado = service.authenticate(usuario.getEmail(), usuario.getSenha());
            return ResponseEntity.ok("Usuário logado com sucesso");
        }catch (AutheticateException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
