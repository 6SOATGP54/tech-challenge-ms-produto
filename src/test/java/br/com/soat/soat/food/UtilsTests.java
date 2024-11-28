package br.com.soat.soat.food;

import br.com.soat.soat.food.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UtilsTests {

    private RestTemplate restTemplateMock;

    @BeforeEach
    void setUp() {
        restTemplateMock = mock(RestTemplate.class);
    }

    @Test
    void deveFazerRequisicaoComSucesso() {
        try (MockedStatic<Utils> utilsMockedStatic = Mockito.mockStatic(Utils.class, Mockito.CALLS_REAL_METHODS)) {
            utilsMockedStatic.when(() -> Utils.request(
                    eq("http://example.com"),
                    eq(HttpMethod.GET),
                    isNull(),
                    eq(String.class)
            )).thenAnswer(invocation -> {
                HttpHeaders headers = new HttpHeaders();
                HttpEntity<Object> httpEntity = new HttpEntity<>(null, headers);

                when(restTemplateMock.exchange(
                        eq("http://example.com"),
                        eq(HttpMethod.GET),
                        eq(httpEntity),
                        eq(String.class)
                )).thenReturn(new ResponseEntity<>("response body", HttpStatus.OK));

                return new ResponseEntity<>("response body", HttpStatus.OK);
            });

            ResponseEntity<String> response = Utils.request(
                    "http://example.com",
                    HttpMethod.GET,
                    null,
                    String.class
            );

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("response body", response.getBody());
        }
    }

    @Test
    void deveRegistrarErroQuandoRequisicaoFalhar() {
        try (MockedStatic<Utils> utilsMockedStatic = Mockito.mockStatic(Utils.class, Mockito.CALLS_REAL_METHODS)) {
            utilsMockedStatic.when(() -> Utils.request(
                    eq("http://example.com"),
                    eq(HttpMethod.GET),
                    isNull(),
                    eq(String.class)
            )).thenAnswer(invocation -> {
                HttpHeaders headers = new HttpHeaders();
                HttpEntity<Object> httpEntity = new HttpEntity<>(null, headers);

                when(restTemplateMock.exchange(
                        eq("http://example.com"),
                        eq(HttpMethod.GET),
                        eq(httpEntity),
                        eq(String.class)
                )).thenThrow(new RuntimeException("Erro simulado"));

                Logger loggerMock = mock(Logger.class);
                doNothing().when(loggerMock).severe(anyString());

                return null;
            });

            ResponseEntity<String> response = Utils.request(
                    "http://example.com",
                    HttpMethod.GET,
                    null,
                    String.class
            );

            assertNull(response);
        }
    }
}
