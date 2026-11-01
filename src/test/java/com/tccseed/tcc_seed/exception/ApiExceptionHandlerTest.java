package com.tccseed.tcc_seed.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiExceptionHandlerTest {
    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void retornaErrosDeValidacaoPorCampoMantendoPrimeiraMensagem() {
        Object alvo = new Object();
        BeanPropertyBindingResult resultado = new BeanPropertyBindingResult(alvo, "request");
        resultado.addError(new FieldError("request", "email", "Formato inválido"));
        resultado.addError(new FieldError("request", "email", "Outra mensagem"));
        resultado.addError(new FieldError("request", "nome", null, false, null, null, null));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, resultado);

        var response = handler.handleInvalidRequest(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Map.of("status", 400, "errors", Map.of("email", "Formato inválido", "nome", "Valor inválido.")),
                response.getBody());
    }

    @Test
    void retornaMensagemGenericaParaArgumentoInvalido() {
        var response = handler.handleInvalidArgument();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Map.of("status", 400, "error", "Requisição inválida."), response.getBody());
    }
}
