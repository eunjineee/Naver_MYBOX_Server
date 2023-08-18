package back.server.mybox.user;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {
    private final UserService userService;
    @PostMapping("/join")
    public ResponseEntity<Map<String, Object>> join(@RequestBody UserRequestDto requestDto){
        Map<String, Object> response = new HashMap<>();
        UserResponseDto responseDto = userService.join(requestDto);
        response.put("data", responseDto);
        response.put("message", "success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/myinfo/{userId}")
    public ResponseEntity<Map<String, Object>> userMyInfo(@PathVariable(value = "userId") Long userId){
        Map<String, Object> response = new HashMap<>();
        UserResponseDto responseDto = userService.userInfo(userId);
        response.put("data", responseDto);
        response.put("message", "success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
