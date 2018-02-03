package io.rocketbase.commons.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.rocketbase.commons.dto.PageableResult;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.ParameterizedType;

public abstract class AbstractBaseCrudRestResource<Data, Edit> extends AbstractRestResource {

    protected RestTemplate restTemplate;

    @Getter
    protected Class<Data> responseClass;

    @Autowired
    public AbstractBaseCrudRestResource(RestTemplate restTemplate, ObjectMapper objectMapper) {
        super(objectMapper);
        restTemplate.setErrorHandler(new NoopResponseErrorHandler());
        this.restTemplate = restTemplate;
        responseClass = (Class<Data>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }


    @SneakyThrows
    protected PageableResult<Data> find(UriComponentsBuilder uriBuilder) {
        ResponseEntity<String> response = restTemplate.exchange(uriBuilder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(createHeaderWithLanguage()),
                String.class);
        return renderResponse(response, createPagedTypeReference());
    }

    @SneakyThrows
    protected Data getById(UriComponentsBuilder uriBuilder) {
        ResponseEntity<String> response = restTemplate.exchange(uriBuilder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(createHeaderWithLanguage()),
                String.class);
        return renderResponse(response, responseClass);
    }

    @SneakyThrows
    protected Data create(UriComponentsBuilder uriBuilder, Edit edit) {
        ResponseEntity<String> response = restTemplate.exchange(uriBuilder.toUriString(),
                HttpMethod.POST,
                createHttpEntity(edit),
                String.class);
        return renderResponse(response, responseClass);
    }

    @SneakyThrows
    protected Data update(UriComponentsBuilder uriBuilder, Edit edit) {
        ResponseEntity<String> response = restTemplate.exchange(uriBuilder.toUriString(),
                HttpMethod.PUT,
                createHttpEntity(edit),
                String.class);
        return renderResponse(response, responseClass);
    }

    protected void delete(UriComponentsBuilder uriBuilder) {
        ResponseEntity<Void> response = restTemplate.exchange(uriBuilder.toUriString(),
                HttpMethod.DELETE,
                null, Void.class);
    }

    protected HttpEntity<Edit> createHttpEntity(Edit edit) {
        HttpEntity<Edit> entity = new HttpEntity<>(edit, createHeaderWithLanguage());
        return entity;
    }

    protected abstract TypeReference<PageableResult<Data>> createPagedTypeReference();

}
