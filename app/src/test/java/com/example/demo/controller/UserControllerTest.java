package com.example.demo.controller;

import com.example.demo.dto.request.PhoneRequest;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }
    @Test
    public void testSignUp_Success() throws Exception {
        List<PhoneRequest> phonesRequest = new ArrayList<>();
        phonesRequest.add(new PhoneRequest(111111, 1900, "54"));
        phonesRequest.add(new PhoneRequest(222222, 1900, "54"));
        UserRequest validUserRequest = new UserRequest("Lucia", "lucia@mail.com", "Clave12Pru", phonesRequest);
        UserResponse userResponse = new UserResponse(validUserRequest, UUID.randomUUID(), LocalDateTime.now(), null, "eydhdhdfc", true);

        when(userService.createUser(any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserRequest)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.user.name").value("Lucia"));
    }

    @Test
    public void testSignUp_InvalidEmail() throws Exception {
        List<PhoneRequest> phonesRequest = new ArrayList<>();
        phonesRequest.add(new PhoneRequest(111111, 1900, "54"));
        UserRequest invalidEmailRequest = new UserRequest("Lucia", "lucia-sarasa", "Clave12Pru", phonesRequest);

        mockMvc.perform(post("/api/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEmailRequest)))
                .andExpect(status().isBadRequest())
                .andDo(print());

    }

    @Test
    public void testSignUp_InvalidPassword() throws Exception {
        UserRequest invalidEmailRequest = new UserRequest("Lucia", "lucia@mail.com", "Clave1234Pru", new ArrayList<>());

        mockMvc.perform(post("/api/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEmailRequest)))
                .andExpect(status().isBadRequest())
                .andDo(print());

    }

    @Test
    public void testSignUp_OptionalFields() throws Exception {

        List<PhoneRequest> phonesRequest = new ArrayList<>();
        phonesRequest.add(new PhoneRequest(0, 0, ""));
        UserRequest optionalFieldsRequest = new UserRequest("", "lucia@mail.com", "Clave12Pru", phonesRequest);

        mockMvc.perform(post("/api/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(optionalFieldsRequest)))
                .andExpect(status().isCreated())
                .andDo(print());

    }

    @Test
    public void testSignUp_RequiredFields() throws Exception {

        List<PhoneRequest> phonesRequest = new ArrayList<>();
        phonesRequest.add(new PhoneRequest(0, 0, ""));
        UserRequest optionalFieldsRequest = new UserRequest("Lucia", "", "Clave12Pru", phonesRequest);

        mockMvc.perform(post("/api/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(optionalFieldsRequest)))
                .andExpect(status().isBadRequest())
                .andDo(print());

    }

    @Test
    public void testLogin_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users/login"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value("Token not found"));
    }

}
