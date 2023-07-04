package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;


    @Test
    void getAllUsersWhenInvokedOkBody() {
        List<UserDto> expectedUsers = List.of(new UserDto());
        Mockito.when(userService.getAllUsers()).thenReturn(expectedUsers);

        ResponseEntity<List<UserDto>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUsers, response.getBody());
    }




    // @Autowired
    // private UserController userController;

   /* @Test
    public void contextLoads() throws Exception {
        assertThat(userController).isNotNull();
    }

    @Test
    public void createUserTest() {
        UserDto user = UserDto.builder().email("user444@user.com").name("Марк").build();
        UserDto user2 = userController.saveNewUser(user).getBody();
        assertNotNull(user2.getId());
        assertEquals(user2.getName(), user.getName());
    }

    @Test
    public void updateUserTest() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("name", "Марк Юрьевич");
        long id = 1L;
        UserDto user = UserDto.builder().email("user444@user.com").name("Марк").build();
        userController.saveNewUser(user).getBody();
        UserDto user3 = userController.updateUser(id, fields).getBody();
        assertEquals(user3.getName(), "Марк Юрьевич");
        userController.delete(1L);
    }
*/

}