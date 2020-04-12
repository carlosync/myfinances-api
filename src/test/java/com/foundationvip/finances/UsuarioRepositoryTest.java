package com.foundationvip.finances;

import com.foundationvip.finances.model.Usuario;
import com.foundationvip.finances.repository.UsuarioRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository repository;
    @Autowired
    TestEntityManager entityManager;

    /*  três elementos para um teste */
    @Test
    public void checkExistEmail(){
        // cenário
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário");
        usuario.setEmail("teste@gmail.com");
        entityManager.persist(usuario);

        // ação
        boolean resultado = repository.existsByEmail("teste@gmail.com");

        //verificação
        Assertions.assertThat(resultado).isTrue();

    }

    @Test
    public void mustTurnBackFalse() {
        boolean result = repository.existsByEmail("teste@gmail.com");

        Assertions.assertThat(result).isFalse();

    }
}
