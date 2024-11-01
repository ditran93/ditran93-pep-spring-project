package com.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdateMessageTest {
	ApplicationContext app;
    HttpClient webClient;
    ObjectMapper objectMapper;

    /**
     * Before every test, reset the database, restart the Javalin app, and create a new webClient and ObjectMapper
     * for interacting locally on the web.
     * @throws InterruptedException
     */
    @BeforeEach
    public void setUp() throws InterruptedException {
        webClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
        String[] args = new String[] {};
        app = SpringApplication.run(SocialMediaApp.class, args);
        Thread.sleep(500);
    }

    @AfterEach
    public void tearDown() throws InterruptedException {
    	Thread.sleep(500);
    	SpringApplication.exit(app);
    }
    
    /**
     * Sending an http request to PATCH localhost:8080/messages/9999 (message id exists in db) and successful message text
     * 
     * Expected Response:
     *  Status Code: 200
     *  Response Body: 1 (one row modified)
     */
    @Test
    public void updateMessageSuccessful() throws IOException, InterruptedException {
    	String json = "{\"messageText\": \"text changed\"}";
        HttpRequest postMessageRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/messages/9999"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postMessageRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
        int status = response.statusCode();
        Assertions.assertEquals(200, status, "Expected Status Code 200 - Actual Code was: " + status);
        Integer actualResult = objectMapper.readValue(response.body().toString(), Integer.class);
        Assertions.assertTrue(actualResult.equals(1), "Expected to modify 1 row, but actually modified " + actualResult + " rows.");
    }


    /**
     * Sending an http request to PATCH localhost:8080/messages/100 (message does not exist in database)
     * 
     * Expected Response:
     *  Status Code: 400
     */
    @Test
    public void updateMessageMessageNotFound() throws IOException, InterruptedException {
    	String json = "{\"messageText\": \"text changed\"}";
        HttpRequest postMessageRequest = HttpRequest.newBuilder()
        		.uri(URI.create("http://localhost:8080/messages/5050"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postMessageRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(400, status, "Expected Status Code 400 - Actual Code was: " + status);
        System.out.println(response.body());
    }


    /**
     * Sending an http request to PATCH localhost:8080/messages/9999 with empty message string 
     * 
     * Expected Response:
     *  Status Code: 400
     */
    @Test
    public void updateMessageMessageStringEmpty() throws IOException, InterruptedException {
    	String json = "{\"messageText\": \"\"}";
        HttpRequest postMessageRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/messages/9999"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postMessageRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(400, status, "Expected Status Code 400 - Actual Code was: " + status);
    }


    /**
     * Sending an http request to PATCH localhost:8080/messages/9999 with message string over 255 characters 
     * 
     * Expected Response:
     *  Status Code: 400
     */
    @Test
    public void updateMessageMessageTooLong() throws IOException, InterruptedException {
    	String json = "{\"messageText\": \"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\"}";
        HttpRequest postMessageRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/messages/9999"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postMessageRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(400, status, "Expected Status Code 400 - Actual Code was: " + status);
    }
}
