package com.example.demo.imgur;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Component
public class ImgurHandler {
    @Value("${imgur.client-id}")
    private String clientId;

    @Value("${imgur.client-secret}")
    private String clientSecret;

    // Constants for Imgur API URLs
    private static final String IMGUR_UPLOAD_URL = "https://api.imgur.com/3/upload";
    private static final String IMGUR_DELETE_URL = "https://api.imgur.com/3/image/";

    private HttpHeaders makeHeaders(MediaType mediaType){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.set("Authorization", "Client-ID " + clientId);
        return headers;
    }
    @Async
    public CompletableFuture<Map<String, Object>> upload(MultipartFile file, String title, String description) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = makeHeaders(MediaType.MULTIPART_FORM_DATA);

        // Build the request body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource resource = new ByteArrayResource(file.getBytes());
        body.add("image", resource);
        body.add("title", title);
        body.add("description", description);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Make the API call and return a Map directly
        ResponseEntity<Map> response = restTemplate.postForEntity(
                IMGUR_UPLOAD_URL,
                requestEntity,
                Map.class  // ✅ Capture the full response as a Map
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Imgur upload failed with status: " + response.getStatusCode());
        }

        // Return the full response body as a Map
        return CompletableFuture.completedFuture(response.getBody());
    }

    public CompletableFuture<Map<String, Object>> deleteImageFromImgur(String deleteHash) throws IOException{
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = makeHeaders(MediaType.APPLICATION_JSON);

        ResponseEntity<Map> response = restTemplate.exchange(IMGUR_DELETE_URL + deleteHash, HttpMethod.DELETE, new HttpEntity<>(headers) ,Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Imgur deletion failed with status: " + response.getStatusCode());
        }

        return CompletableFuture.completedFuture(response.getBody());
    }
}
