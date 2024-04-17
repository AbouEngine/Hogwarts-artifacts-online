package org.example.hogwartsartifactsonline.hogwartsUser.converter;

import org.example.hogwartsartifactsonline.hogwartsUser.HogwartsUser;
import org.example.hogwartsartifactsonline.hogwartsUser.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserDtoConverter implements Converter<HogwartsUser, UserDto> {
    @Override
    public UserDto convert(HogwartsUser source) {
        return new UserDto(source.getUserId(), source.getUsername(), source.getEnabled(), source.getRoles());
    }
}
