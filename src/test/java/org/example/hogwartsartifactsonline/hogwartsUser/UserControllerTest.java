package org.example.hogwartsartifactsonline.hogwartsUser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hogwartsartifactsonline.hogwartsUser.dto.UserDto;
import org.example.hogwartsartifactsonline.system.StatusCode;
import org.example.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // turn off spring security
class UserControllerTest {
    
    @Value("${api.endpoint.base-url}")
    String baseUrl;
    
    @Autowired
    MockMvc mockMvc;
    
    @MockBean
    UserService userService;
    
    List<HogwartsUser> users;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.users = new ArrayList<>();
        HogwartsUser user1 = new HogwartsUser();
        user1.setUserId(1);
        user1.setUsername("Eren");
        user1.setPassword("blablabla");
        user1.setEnabled(true);
        user1.setRoles("admin user");
        this.users.add(user1);

        HogwartsUser user2 = new HogwartsUser();
        user2.setUserId(2);
        user2.setUsername("Armin");
        user2.setPassword("123456");
        user2.setEnabled(true);
        user2.setRoles("user");
        this.users.add(user2);
        
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindUserByIdSuccess() throws Exception {
        // Given
        when(this.userService.findBy(1)).thenReturn(this.users.get(0));
        
        // Act and Then
        this.mockMvc.perform(get(this.baseUrl + "/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("Eren"))
                .andExpect(jsonPath("$.data.roles").value("admin user"))
                .andExpect(jsonPath("$.data.enabled").value(true));
    }

    @Test
    void testFindByNotFound() throws Exception {
        // Given
        when(this.userService.findBy(1)).thenThrow(new ObjectNotFoundException("user", 1));

        // Act and Then
        this.mockMvc.perform(get(this.baseUrl + "/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 1 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testFindAllUsersSuccess() throws Exception {
        // Given
        when(this.userService.findAll()).thenReturn(this.users);

        // Act and Then
        this.mockMvc.perform(get(this.baseUrl + "/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data[0].userId").value(1))
                .andExpect(jsonPath("$.data[0].username").value("Eren"))
                .andExpect(jsonPath("$.data[1].userId").value(2))
                .andExpect(jsonPath("$.data[1].username").value("Armin"));

    }

    @Test
    void testAddUserSuccess() throws Exception {
        // Given
        UserDto newUserDto = new UserDto(null, "Reiner", true, "user");
        // Dto to json
        String json = this.objectMapper.writeValueAsString(newUserDto);

        HogwartsUser userSaved = new HogwartsUser();
        userSaved.setUserId(3);
        userSaved.setUsername("Reiner");
        userSaved.setPassword("OulaMonMDP");
        userSaved.setEnabled(true);
        userSaved.setRoles("user");

        when(this.userService.save(Mockito.any(HogwartsUser.class))).thenReturn(userSaved);

        // Act and Then
        this.mockMvc.perform(post(this.baseUrl + "/users").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.userId").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value("Reiner"))
                .andExpect(jsonPath("$.data.enabled").value(true))
                .andExpect(jsonPath("$.data.roles").value("user"));
    }

    @Test
    void testUpdateUserSucces() throws Exception {
        // Given
        UserDto userDto = new UserDto(null, "Lola", true, "user");
        String json = this.objectMapper.writeValueAsString(userDto);

        HogwartsUser userUpdated = new HogwartsUser();
        userUpdated.setUserId(1);
        userUpdated.setUsername("new name");
        userUpdated.setEnabled(true);
        userUpdated.setPassword("12345");
        userUpdated.setRoles("user");

        when(this.userService.update(eq(1), Mockito.any(HogwartsUser.class))).thenReturn(userUpdated);

        // Act and Then
        this.mockMvc.perform(put(this.baseUrl + "/users/1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("new name"));
    }

    @Test
    void testUpdateUserWithNonExistentUserId() throws Exception {
        // Given
        UserDto userDto = new UserDto(null, "new name", true, "user");
        String json = this.objectMapper.writeValueAsString(userDto);
        when(this.userService.update(eq(1), Mockito.any(HogwartsUser.class))).thenThrow(new ObjectNotFoundException("user", 1));

        // Act and Then
        this.mockMvc.perform(put(this.baseUrl + "/users/1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 1 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteUserSuccess() throws Exception {
        // Given
        doNothing().when(this.userService).delete(1);

        // Act and Then
        this.mockMvc.perform(delete(this.baseUrl + "/users/1").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteWithNonExistentUserId() throws Exception {
        // Given
        doThrow(new ObjectNotFoundException("user", 3)).when(this.userService).delete(3);

        // Act and Then
        this.mockMvc.perform(delete(this.baseUrl + "/users/3").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 3 :("))
                .andExpect(jsonPath("$.data").isEmpty());

    }
}