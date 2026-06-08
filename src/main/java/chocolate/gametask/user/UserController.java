package chocolate.gametask.user;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/api/v1/users/")
@RestController
public class UserController {
    private final UserService userService;

}
