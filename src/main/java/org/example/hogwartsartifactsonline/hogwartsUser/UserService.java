package org.example.hogwartsartifactsonline.hogwartsUser;

import jakarta.transaction.Transactional;
import org.example.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public HogwartsUser findBy(Integer userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user",userId));
    }

    public List<HogwartsUser> findAll() {
        return this.userRepository.findAll();
    }

    public HogwartsUser save(HogwartsUser user) {
        // Encrypt plain text password before saving to the DB
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        return this.userRepository.save(user);
    }

    public HogwartsUser update(Integer id, HogwartsUser user) {
        return this.userRepository.findById(id)
                .map(oldUser -> {
                    oldUser.setUsername(user.getUsername());
                    oldUser.setEnabled(user.getEnabled());
                    oldUser.setRoles(user.getRoles());
                    return this.userRepository.save(oldUser);
                })
                .orElseThrow(() -> new ObjectNotFoundException("user", id));
    }

    public void delete(Integer userId) {
        this.userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("user", userId));
        this.userRepository.deleteById(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username)
                .map(MyUserPrincipal::new) // map(user -> new MyUserPrincipal(user))
                .orElseThrow(() -> new UsernameNotFoundException("username " + username + " is not found"));
    }
}
