package com.nimble.gatewaypagamento;

import com.nimble.gatewaypagamento.mapper.UsuarioMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class GatewayPagamentoApplicationTests {

    @MockBean
    private UsuarioMapper usuarioMapper;

    @Test
    void contextLoads() {
    }

}
