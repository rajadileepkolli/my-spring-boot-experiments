package com.example.restclient.bootrestclient.services;

import com.example.restclient.bootrestclient.exception.MyCustomClientException;
import java.net.URI;
import java.util.Map;
import java.util.function.Function;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

@Service
@Retryable(
        retryFor = {HttpServerErrorException.class},
        maxAttempts = 2,
        backoff = @Backoff(delay = 5000))
public class HttpClientService {

    private final RestClient restClient;

    public HttpClientService(RestClient restClient) {
        this.restClient = restClient;
    }

    <T> T callAndFetchResponse(
            Function<UriBuilder, URI> uriFunction,
            HttpMethod httpMethod,
            @Nullable Object body,
            Class<T> bodyType) {
        return callServer(uriFunction, httpMethod, null, body, bodyType, null);
    }

    <T> T callAndFetchResponse(
            Function<UriBuilder, URI> uriFunction,
            HttpMethod httpMethod,
            @Nullable Map<String, String> headers,
            Class<T> bodyType) {
        return callServer(uriFunction, httpMethod, headers, null, bodyType, null);
    }

    <T> T callAndFetchResponse(
            Function<UriBuilder, URI> uriFunction,
            HttpMethod httpMethod,
            @Nullable Object body,
            ParameterizedTypeReference<T> bodyType) {
        return callServer(uriFunction, httpMethod, null, body, null, bodyType);
    }

    private <T> T callServer(
            Function<UriBuilder, URI> uriFunction,
            HttpMethod httpMethod,
            Map<String, String> headers,
            Object body,
            Class<T> bodyType,
            ParameterizedTypeReference<T> typeReferenceBodyType) {
        RestClient.RequestBodySpec uri = restClient.method(httpMethod).uri(uriFunction);
        if (!CollectionUtils.isEmpty(headers)) {
            uri.headers(
                    httpHeader ->
                            headers.keySet().forEach(key -> httpHeader.add(key, headers.get(key))));
        }
        if (body != null) {
            uri.body(body);
        }
        RestClient.ResponseSpec responseSpec =
                uri.retrieve()
                        .onStatus(
                                HttpStatusCode::is4xxClientError,
                                (request, response) -> {
                                    throw new MyCustomClientException(
                                            response.getStatusCode(), response.getHeaders());
                                });
        if (bodyType != null) {
            return responseSpec.body(bodyType);
        } else {
            return responseSpec.body(typeReferenceBodyType);
        }
    }
}
