package org.example.hogwartsartifactsonline.hogwartsUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<HogwartsUser, Integer> {
    Optional<HogwartsUser> findByUsername(String username);
}
