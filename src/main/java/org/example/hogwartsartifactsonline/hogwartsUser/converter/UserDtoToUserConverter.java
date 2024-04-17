package org.example.hogwartsartifactsonline.hogwartsUser.converter;

import org.example.hogwartsartifactsonline.hogwartsUser.HogwartsUser;
import org.example.hogwartsartifactsonline.hogwartsUser.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDtoToUserConverter implements Converter<UserDto, HogwartsUser> {
    @Override
    public HogwartsUser convert(UserDto source) {
        HogwartsUser user = new HogwartsUser();
        user.setUserId(source.userId());
        user.setUsername(source.username());
        user.setEnabled(source.enabled());
        user.setRoles(source.roles());
        return user;
    }
}
