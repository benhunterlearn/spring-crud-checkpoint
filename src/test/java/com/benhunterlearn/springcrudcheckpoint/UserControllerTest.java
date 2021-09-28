package com.benhunterlearn.springcrudcheckpoint;

import com.benhunterlearn.springcrudcheckpoint.model.User;
import com.benhunterlearn.springcrudcheckpoint.model.UserDto;
import com.benhunterlearn.springcrudcheckpoint.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Rollback
@Transactional
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    private final UserRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    private User firstUser;
    private User secondUser;

    @Autowired
    public UserControllerTest(UserRepository repository) {
        this.repository = repository;
    }

    @BeforeEach
    public void setupRepository() {
        this.firstUser = this.repository.save(new User("first@mail.com", "secret"));
        this.secondUser = this.repository.save(new User("second@mail.com", "notsosecret"));
    }

    @Test
    public void getAllUsersFromRepository() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/users")
                .accept(MediaType.APPLICATION_JSON);
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email", is(firstUser.getEmail())))
                .andExpect(jsonPath("$[0].id", is(firstUser.getId().intValue())));
    }

    @Test
    public void createUserWithValidData() throws Exception {
        UserDto inputUser = new UserDto("john@example.com", "something-secret");
        RequestBuilder request = MockMvcRequestBuilders.post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(inputUser));
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email", is(inputUser.getEmail())))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    public void getUserById() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/users/" + firstUser.getId())
                .accept(MediaType.APPLICATION_JSON);
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstUser.getId().intValue())))
                .andExpect(jsonPath("$.email", is(firstUser.getEmail())))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    public void patchUpdateUserEmailById() throws Exception {
        UserDto userPatchInformation = new UserDto().setEmail("new@different.com");

        RequestBuilder request = MockMvcRequestBuilders.patch("/users/" + secondUser.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(userPatchInformation));
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(secondUser.getId().intValue())))
                .andExpect(jsonPath("$.email", is(userPatchInformation.getEmail())))
                .andExpect(jsonPath("$.password").doesNotExist());
        String expectedPassword = this.repository.findById(secondUser.getId()).get().getPassword();
        assertEquals(secondUser.getPassword(), expectedPassword);
    }

    @Test
    public void patchUpdateUserPasswordById() throws Exception {
        UserDto userPatchInformation = new UserDto().setPassword("terrible plain text");
        RequestBuilder request = MockMvcRequestBuilders.patch("/users/" + secondUser.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userPatchInformation));
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(secondUser.getId().intValue())))
                .andExpect(jsonPath("$.email", is(secondUser.getEmail())))
                .andExpect(jsonPath("$.password").doesNotExist());
        String expectedPassword = userPatchInformation.getPassword();
        String actualPassword = this.repository.findById(secondUser.getId()).get().getPassword();
        assertEquals(expectedPassword, actualPassword);
    }

    @Test
    public void patchUpdateUserEmailAndPasswordById() throws Exception {
        UserDto userPatchInformation = new UserDto().setEmail("verynewemail@email.place").setPassword("123");
        RequestBuilder request = MockMvcRequestBuilders.patch("/users/" + secondUser.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(userPatchInformation));
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(secondUser.getId().intValue())))
                .andExpect(jsonPath("$.email", is(userPatchInformation.getEmail())))
                .andExpect(jsonPath("$.password").doesNotExist());
        String expectedPassword = userPatchInformation.getPassword();
        String actualPassword = this.repository.findById(secondUser.getId()).get().getPassword();
        assertEquals(expectedPassword, actualPassword);
    }

    @Test
    public void deleteUserByIdRendersCountOfUsersInRepository() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.delete("/users/" + firstUser.getId())
                .accept(MediaType.APPLICATION_JSON);
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").isNumber());
        assertTrue(this.repository.findById(firstUser.getId()).isEmpty());
    }

    @Test
    public void authenticateWithValidPassword() throws Exception {
        UserDto validUser = new UserDto()
                .setEmail(firstUser.getEmail())
                .setPassword(firstUser.getPassword());
        RequestBuilder request = MockMvcRequestBuilders.post("/users/authenticate")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(validUser));
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated", is(true)))
                .andExpect(jsonPath("$.user.id", is(firstUser.getId().intValue())))
                .andExpect(jsonPath("$.user.email", is(firstUser.getEmail())));
    }

    @Test
    public void authenticateFailsWithInvalidPassword() throws Exception {
        UserDto validUser = new UserDto()
                .setEmail(firstUser.getEmail())
                .setPassword("fake news");
        RequestBuilder request = MockMvcRequestBuilders.post("/users/authenticate")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(validUser));
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated", is(false)))
                .andExpect(jsonPath("$.user").doesNotExist());
    }
}
