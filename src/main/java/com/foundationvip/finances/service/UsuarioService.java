package com.foundationvip.finances.service;

import com.foundationvip.finances.model.Usuario;
import com.foundationvip.finances.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService implements UsuarioServiceImpl {

    @Autowired
    private UsuarioRepository repository;

    @Override
    public Usuario authenticate(String email, String senha) {
        Optional<Usuario> usuario = repository.findByEmail(email);
        if(!usuario.isPresent()){
            throw new AutheticateException("Usuário não encontrado para o email informado.");
        }
        if(!usuario.get().getSenha().equals(senha)){
            throw new AutheticateException("Senha inválida.");
        }
        return usuario.get();
    }

    @Override
    public Usuario save(Usuario usuario) {
        validateEmail(usuario.getEmail());
        return repository.save(usuario);
    }

    @Override
    public void validateEmail(String email) {
        boolean existe = repository.existsByEmail(email);
        if(existe){
           throw new RegraNegocioException("Usuário já está cadastrado no sistema.");
        }
    }
}
