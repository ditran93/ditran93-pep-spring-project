package com.example;

import com.example.controller.SocialMediaController;
import com.example.entity.Account;
import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;
import com.example.service.AccountService;
import com.example.service.MessageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class SpringTest {
    ApplicationContext applicationContext;

    /**
     * Retrieve the applicationContext for your app by starting it up.
     */
    @BeforeEach
    public void setUp(){
        String[] args = new String[] {};
        applicationContext = SpringApplication.run(SocialMediaApp.class, args);
    }
    /**
     * Reset the applicationContext after each test.
     */
    @AfterEach
    public void tearDown(){
        SpringApplication.exit(applicationContext);
    }

    /**
     * Retrieve the SocialMediaController as a bean.
     * The SocialMediaController must be a bean in order for this test to pass.
     */
    @Test
    public void getSocialMediaControllerBean(){
        SocialMediaController bean = applicationContext.getBean(SocialMediaController.class);
        Assertions.assertNotNull(bean);
    }
    /**
     * Retrieve the AccountService as a bean.
     * The AccountService must be a bean in order for this test to pass.
     */
    @Test
    public void getAccountServiceBean(){
        AccountService bean = applicationContext.getBean(AccountService.class);
        Assertions.assertNotNull(bean);
    }
    /**
     * Retrieve the MessageService as a bean.
     * The MessageService must be a bean in order for this test to pass.
     */
    @Test
    public void getMessageServiceBean(){
        MessageService bean = applicationContext.getBean(MessageService.class);
        Assertions.assertNotNull(bean);
    }
    /**
     * Retrieve the AccountRepository as a bean.
     * The AccountRepository must be a bean in order for this test to pass.
     */
    @Test
    public void getAccountRepositoryBean(){
        AccountRepository bean = applicationContext.getBean(AccountRepository.class);
        Assertions.assertNotNull(bean);
    }
    /**
     * Retrieve the MessageRepository as a bean.
     * The MessageRepository must be a bean in order for this test to pass.
     */
    @Test
    public void getMessageRepositoryBean(){
        MessageRepository bean = applicationContext.getBean(MessageRepository.class);
        Assertions.assertNotNull(bean);
    }
    /**
     * After retrieving the AccountRepository bean, it should exhibit the functionality of a JPARepository
     * for an "Account" entity.
     */
    @Test
    public void accountRepositoryIsRepositoryTest() throws ReflectiveOperationException {
        AccountRepository repository = applicationContext.getBean(AccountRepository.class);
        Method[] repositoryMethods = repository.getClass().getMethods();
        Method saveMethod = null;
        Method findAllMethod = null;
        String expectedUsername = "ted";
        String expectedPassword = "password123";
        Account testAccount = new Account(expectedUsername, expectedPassword);
        for(Method m : repositoryMethods){
            System.out.println(m.getName());
            if(m.getName().equals("save") && m.getParameterCount() == 1){
                saveMethod = m;
            }else if(m.getName().equals("findAll") && m.getParameterCount() == 0){
                findAllMethod = m;
            }
        }
        if(saveMethod == null || findAllMethod == null){
            Assertions.fail("The save / findAll methods were not found. Ensure that AccountRepository properly " +
                    "extends JPARepository.");
        }
        List<Account> accountList1 = (List<Account>) findAllMethod.invoke(repository, new Object[]{});
        System.out.println(accountList1);
        Assertions.assertTrue(accountList1.size() == 4, "There should be no accounts in the " +
                "JPARepository on startup.");
        Account actualAccount = (Account) saveMethod.invoke(repository, testAccount);
        Assertions.assertEquals(actualAccount.getUsername(), expectedUsername);
        List<Account> accountList2 = (List<Account>) findAllMethod.invoke(repository, new Object[]{});
        Assertions.assertTrue(accountList2.size() > 4, "The account should be addable to the " +
                "JPARepository.");
    }
    /**
     * After retrieving the MessageRepository bean, it should exhibit the functionality of a JPARepository
     * for a "Message" entity.
     */
    @Test
    public void messageRepositoryIsRepositoryTest() throws ReflectiveOperationException{
        MessageRepository repository = applicationContext.getBean(MessageRepository.class);
        Method[] repositoryMethods = repository.getClass().getMethods();
        Method saveMethod = null;
        Method findAllMethod = null;
        int expectedPostedBy = 9999;
        String expectedText = "ted test 1";
        long expectedTimePosted = 999999999999L;
        Message testMessage = new Message(expectedPostedBy, expectedText, expectedTimePosted);
        for(Method m : repositoryMethods){
            System.out.println(m.getName());
            if(m.getName().equals("save") && m.getParameterCount() == 1){
                saveMethod = m;
            }else if(m.getName().equals("findAll") && m.getParameterCount() == 0){
                findAllMethod = m;
            }
        }
        if(saveMethod == null || findAllMethod == null){
            Assertions.fail("The save / findAll methods were not found. Ensure that MessageRepository properly " +
                    "extends JPARepository.");
        }
        List<Account> accountList1 = (List<Account>) findAllMethod.invoke(repository, new Object[]{});
        System.out.println(accountList1);
        Assertions.assertTrue(accountList1.size() == 3, "There should be no messages in the " +
                "JPARepository on startup.");
        Message actualMessage = (Message) saveMethod.invoke(repository, testMessage);
        Assertions.assertEquals(actualMessage.getMessageText(), expectedText);
        List<Account> accountList2 = (List<Account>) findAllMethod.invoke(repository, new Object[]{});
        Assertions.assertTrue(accountList2.size() > 3, "The message should be addable to the " +
                "JPARepository.");
    }
    /**
     * Verify the functionality of Spring MVC, independently of the project requirement endpoints, by sending a request
     * to an arbitrary endpoint.
     */
    @Test
    public void default404Test() throws IOException, InterruptedException {
        HttpClient webClient = HttpClient.newHttpClient();
        int random = (int) (Math.random()*100000);
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/arbitrary"+random))
                .build();
        HttpResponse<String> response = webClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(404, status);
        String body = response.body().toString();
        Assertions.assertTrue(body.contains("timestamp"));
        Assertions.assertTrue(body.contains("status"));
        Assertions.assertTrue(body.contains("error"));
        Assertions.assertTrue(body.contains("path"));
    }
}
