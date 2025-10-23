package com.nimble.gatewaypagamento.exception.handler;

import com.nimble.gatewaypagamento.exception.usuario.UsuarioNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveRetornarBadRequestParaUsuarioExceptions() {
        UsuarioNaoEncontradoException ex = new UsuarioNaoEncontradoException("Usu?rio n?o encontrado");

        ResponseEntity<Map<String, Object>> response = handler.handleUsuarioException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Usu?rio n?o encontrado", response.getBody().get("erro"));
    }

    @Test
    void deveTratarErrosDeValidacao() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError error1 = new FieldError("obj", "cpf", "CPF inv?lido");
        FieldError error2 = new FieldError("obj", "email", "E-mail inv?lido");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("CPF inv?lido", response.getBody().get("cpf"));
        assertEquals("E-mail inv?lido", response.getBody().get("email"));
    }
}