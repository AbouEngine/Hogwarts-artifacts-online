package org.example.hogwartsartifactsonline.hogwartsUser;

import jakarta.validation.Valid;
import org.example.hogwartsartifactsonline.hogwartsUser.converter.UserDtoToUserConverter;
import org.example.hogwartsartifactsonline.hogwartsUser.converter.UserToUserDtoConverter;
import org.example.hogwartsartifactsonline.hogwartsUser.dto.UserDto;
import org.example.hogwartsartifactsonline.system.Result;
import org.example.hogwartsartifactsonline.system.StatusCode;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/users")
public class UserController {

    private final UserService userService;
    private final UserToUserDtoConverter userToUserDtoConverter;
    private final UserDtoToUserConverter userDtoToUserConverter;

    public UserController(UserService userService, UserToUserDtoConverter userToUserDtoConverter, UserDtoToUserConverter userDtoToUserConverter) {
        this.userService = userService;
        this.userToUserDtoConverter = userToUserDtoConverter;
        this.userDtoToUserConverter = userDtoToUserConverter;
    }

    @GetMapping("/{userId}")
    public Result findUserById(@PathVariable Integer userId) {
        HogwartsUser userFound = this.userService.findBy(userId);
        UserDto userDtoFound = this.userToUserDtoConverter.convert(userFound);
        return new Result(true, StatusCode.SUCCESS, "Find One Success", userDtoFound);
    }

    @GetMapping
    public Result findAllUsers() {
        List<HogwartsUser> usersFound = this.userService.findAll();
        List<UserDto> userDtoList = usersFound.stream()
                .map(this.userToUserDtoConverter::convert)
                .toList();
        return new Result(true, StatusCode.SUCCESS, "Find All Success", userDtoList);
    }

    @PostMapping
    public Result addUser(@Valid @RequestBody UserDto newUserDto) {
        HogwartsUser newUser = this.userDtoToUserConverter.convert(newUserDto);
        HogwartsUser userAdded = this.userService.save(newUser);
        UserDto userAddedDto = this.userToUserDtoConverter.convert(userAdded);
        return new Result(true, StatusCode.SUCCESS, "Add Success", userAddedDto);
    }

    @PutMapping("/{userId}")
    public Result updateUser(@PathVariable Integer userId, @Valid @RequestBody UserDto userDtoUpdated) {
        HogwartsUser user = this.userDtoToUserConverter.convert(userDtoUpdated);
        HogwartsUser userUpdated = this.userService.update(userId, user);
        UserDto userDto = this.userToUserDtoConverter.convert(userUpdated);
        return new Result(true, StatusCode.SUCCESS, "Update Success", userDto);
    }

    @DeleteMapping("/{userId}")
    public Result deleteUser(@PathVariable Integer userId) {
        this.userService.delete(userId);
        return new Result(true, StatusCode.SUCCESS, "Delete Success");
    }
}
