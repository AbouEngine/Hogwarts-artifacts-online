package org.example.hogwartsartifactsonline.hogwartsUser.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;

public record UserDto(@Null Integer userId,
                      @NotEmpty(message = "username is required") String username,
                      Boolean enabled,
                      @NotEmpty(message = "role is required") String roles) {
}
