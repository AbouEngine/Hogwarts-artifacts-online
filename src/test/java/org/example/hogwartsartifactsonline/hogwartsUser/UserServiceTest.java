package org.example.hogwartsartifactsonline.hogwartsUser;

import org.example.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    List<HogwartsUser> users;

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

        HogwartsUser user3 = new HogwartsUser();
        user3.setUserId(3);
        user3.setUsername("Mikasa");
        user3.setPassword("motDePasse");
        user3.setEnabled(true);
        user3.setRoles("user");
        this.users.add(user3);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindBySuccess() {
        // Given
        HogwartsUser user = new HogwartsUser();
        user.setUserId(1);
        user.setUsername("Eren");
        user.setEnabled(true);
        user.setPassword("12345");
        when(this.userRepository.findById(1)).thenReturn(Optional.of(user));

        // Act
        HogwartsUser userFound = this.userService.findBy(1);

        // Then
        assertThat(userFound.getUserId()).isEqualTo(user.getUserId());
        assertThat(userFound.getUsername()).isEqualTo(user.getUsername());
        assertThat(userFound.getPassword()).isEqualTo(user.getPassword());
        assertThat(userFound.getEnabled()).isEqualTo(user.getEnabled());
        verify(this.userRepository, times(1)).findById(1);
    }

    @Test
    void testFindByIdNotFound() {
        // Given
        when(this.userRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.empty());

        // Act
        Throwable throwable = assertThrows(ObjectNotFoundException.class, () -> {
            this.userService.findBy(2);
        });

        // Then
        assertThat(throwable).isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find user with Id 2 :(");
        verify(this.userRepository, times(1)).findById(2);
    }

    @Test
    void testFindAllSuccess() {
        // Given
        when(this.userRepository.findAll()).thenReturn(this.users);

        // Act
        List<HogwartsUser> usersFound = this.userService.findAll();

        // Then
        assertThat(usersFound.size()).isEqualTo(this.users.size());
        assertThat(usersFound.get(0)).isEqualTo(this.users.get(0));
        verify(this.userRepository, times(1)).findAll();
    }

    @Test
    void testSaveSuccess() {
        // Given
        HogwartsUser user = new HogwartsUser();
        user.setUserId(1);
        user.setUsername("Eren");
        user.setPassword("mtdp");
        user.setEnabled(true);
        user.setRoles("admin user");
        when(this.userRepository.save(user)).thenReturn(user);

        // Act
        HogwartsUser userAdded = this.userService.save(user);

        // Then
        assertThat(userAdded.getUserId()).isEqualTo(user.getUserId());
        assertThat(userAdded.getUsername()).isEqualTo(user.getUsername());
        assertThat(userAdded.getPassword()).isEqualTo(user.getPassword());
        assertThat(userAdded.getRoles()).isEqualTo(user.getRoles());
        verify(this.userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateSuccess() {
        // Given
        HogwartsUser oldUser = new HogwartsUser();
        oldUser.setUserId(1);
        oldUser.setUsername("Eren");
        oldUser.setPassword("mtdp");
        oldUser.setEnabled(true);
        oldUser.setRoles("admin user");

        HogwartsUser update = new HogwartsUser();
        update.setUsername("Grisha");
        update.setEnabled(true);
        update.setRoles("user");

        when(this.userRepository.findById(1)).thenReturn(Optional.of(oldUser));
        when(this.userRepository.save(oldUser)).thenReturn(oldUser);

        // Act
        HogwartsUser userUpdated = this.userService.update(1, update);

        // Then
        assertThat(userUpdated.getUserId()).isEqualTo(1);
        assertThat(userUpdated.getUsername()).isEqualTo(update.getUsername());
        assertThat(userUpdated.getRoles()).isEqualTo(update.getRoles());

        verify(this.userRepository, times(1)).findById(1);
        verify(this.userRepository, times(1)).save(oldUser);
    }
    @Test
    public void testUpdateNotFound() {
        // Given
        HogwartsUser update = new HogwartsUser();
        update.setUserId(1);
        update.setUsername("Mikasa");
        when(this.userRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        assertThrows(ObjectNotFoundException.class, () -> {
            this.userService.update(1, update);
        });

        // Then
        verify(this.userRepository, times(1)).findById(1);
    }

    @Test
    void testDeleteSuccess() {
        // Given
        HogwartsUser user = new HogwartsUser();
        user.setUserId(2);
        user.setUsername("Eren");
        user.setEnabled(true);
        user.setPassword("2020202");
        user.setRoles("user");
        when(this.userRepository.findById(2)).thenReturn(Optional.of(user));
        doNothing().when(this.userRepository).deleteById(2);

        // Act
        this.userService.delete(2);

        // Then
        verify(this.userRepository, times(1)).findById(2);
        verify(this.userRepository, times(1)).deleteById(2);
    }

    @Test
    void testDeleteWithNonExistentUserId() {
        // Given
        when(this.userRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.empty());

        // Act
        assertThrows(ObjectNotFoundException.class, () -> {
            this.userService.delete(1);
        });

        // Then
        verify(this.userRepository, times(1)).findById(1);
    }
}