package com.nimble.gatewaypagamento.exception.handler;

import com.nimble.gatewaypagamento.exception.CpfOriginadorDestinatarioIguaisException;
import com.nimble.gatewaypagamento.exception.SenhaIncorretaException;
import com.nimble.gatewaypagamento.exception.UsuarioJaCadastradoException;
import com.nimble.gatewaypagamento.exception.UsuarioNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            UsuarioNaoEncontradoException.class,
            SenhaIncorretaException.class,
            UsuarioJaCadastradoException.class,
            CpfOriginadorDestinatarioIguaisException.class
    })
    public ResponseEntity<Map<String, Object>> handleUsuarioException(RuntimeException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("erro", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
