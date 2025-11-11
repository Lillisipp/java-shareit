package ru.practicum.shareit.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Test
    void get_WithValidParameters_ShouldReturnResponse() {
        BaseClient client = new BaseClient(restTemplate);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Success");

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class), any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = client.get("/test", 1L, Map.of("param", "value"));

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Success", result.getBody());
    }

    @Test
    void post_WithValidBody_ShouldReturnResponse() {
        BaseClient client = new BaseClient(restTemplate);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Created");

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = client.post("/test", 1L, "request body");

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Created", result.getBody());
    }

    @Test
    void post_WithParametersAndBody_ShouldReturnResponse() {
        BaseClient client = new BaseClient(restTemplate);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Created");

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class), any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = client.post("/test", 1L, Map.of("param", "value"), "body");

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void put_WithValidBody_ShouldReturnResponse() {
        BaseClient client = new BaseClient(restTemplate);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Updated");

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = client.put("/test", 1L, "request body");

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void patch_WithValidBody_ShouldReturnResponse() {
        BaseClient client = new BaseClient(restTemplate);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Patched");

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = client.patch("/test", 1L, "request body");

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void patch_WithParametersAndBody_ShouldReturnResponse() {
        BaseClient client = new BaseClient(restTemplate);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Patched");

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class), any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = client.patch("/test", 1L, Map.of("param", "value"), "body");

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void delete_ShouldReturnResponse() {
        BaseClient client = new BaseClient(restTemplate);
        ResponseEntity<Object> expectedResponse = ResponseEntity.noContent().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = client.delete("/test", 1L);

        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    void delete_WithParameters_ShouldReturnResponse() {
        BaseClient client = new BaseClient(restTemplate);
        ResponseEntity<Object> expectedResponse = ResponseEntity.noContent().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class), any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = client.delete("/test", 1L, Map.of("param", "value"));

        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    void get_WithoutUserId_ShouldReturnResponse() {
        BaseClient client = new BaseClient(restTemplate);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Success");

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = client.get("/test");

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void post_WithoutUserId_ShouldReturnResponse() {
        BaseClient client = new BaseClient(restTemplate);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Created");

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = client.post("/test", "request body");

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void patch_WithoutUserId_ShouldReturnResponse() {
        BaseClient client = new BaseClient(restTemplate);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Patched");

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = client.patch("/test", "request body");

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void patch_WithUserIdOnly_ShouldReturnResponse() {
        BaseClient client = new BaseClient(restTemplate);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Patched");

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = client.patch("/test", 1L);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}