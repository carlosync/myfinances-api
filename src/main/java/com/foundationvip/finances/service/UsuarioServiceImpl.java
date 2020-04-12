package com.foundationvip.finances.service;

import com.foundationvip.finances.model.Usuario;

public interface UsuarioServiceImpl {

    Usuario authenticate(String email, String senha);

    Usuario save(Usuario usuario);

    void validateEmail(String email);
}
