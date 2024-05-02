package org.example.hogwartsartifactsonline.security;

import org.example.hogwartsartifactsonline.hogwartsUser.HogwartsUser;
import org.example.hogwartsartifactsonline.hogwartsUser.MyUserPrincipal;
import org.example.hogwartsartifactsonline.hogwartsUser.converter.UserToUserDtoConverter;
import org.example.hogwartsartifactsonline.hogwartsUser.dto.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final JwtProvider jwtProvider;
    private final UserToUserDtoConverter userToUserDtoConverter;

    public AuthService(JwtProvider jwtProvider, UserToUserDtoConverter userToUserDtoConverter) {
        this.jwtProvider = jwtProvider;
        this.userToUserDtoConverter = userToUserDtoConverter;
    }

    public Map<String, Object> createLoginInfo(Authentication authentication) {
        // Create user Info
        MyUserPrincipal principal = (MyUserPrincipal) authentication.getPrincipal();
        HogwartsUser hogwartsUser = principal.getHogwartsUser();
        UserDto userDto = this.userToUserDtoConverter.convert(hogwartsUser);

        // Create Jwt Token
        String token = this.jwtProvider.createToken(authentication);
        Map<String, Object> loginResultmap = new HashMap<>();
        loginResultmap.put("userInfo", userDto);
        loginResultmap.put("token", token);
        return loginResultmap;
    }
}
