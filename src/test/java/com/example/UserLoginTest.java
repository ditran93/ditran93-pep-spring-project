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

import com.example.entity.Account;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserLoginTest {
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
     * Sending an http request to POST localhost:8080/login with valid username and password
     * 
     * Expected Response:
     *  Status Code: 200
     *  Response Body: JSON representation of user object
     */
    @Test
    public void loginSuccessful() throws IOException, InterruptedException {
    	String json = "{\"accountId\":0,\"username\":\"testuser1\",\"password\":\"password\"}";
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(200, status);
        ObjectMapper om = new ObjectMapper();
        Account expectedResult = new Account(9999, "testuser1", "password");
        Account actualResult = om.readValue(response.body().toString(), Account.class);
        Assertions.assertEquals(expectedResult, actualResult);        
    }

    /**
     * Sending an http request to POST localhost:8080/login with invalid username
     * 
     * Expected Response:
     * 	Status Code: 401 
     */
    @Test
    public void loginInvalidUsername() throws IOException, InterruptedException {
    	String json = "{\"accountId\":9999,\"username\":\"testuser404\",\"password\":\"password\"}";
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(401, status, "Expected Status Code 401 - Actual Code was: " + status);
    }
    

    /**
     * Sending an http request to POST localhost:8080/login with invalid password
     * 
     * Expected Response:
     * 	Status Code: 401
     */
    @Test
    public void loginInvalidPassword() throws IOException, InterruptedException {
    	String json = "{\"accountId\":9999,\"username\":\"testuser1\",\"password\":\"pass404\"}";
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(401, status, "Expected Status Code 401 - Actual Code was: " + status);
    }
}
