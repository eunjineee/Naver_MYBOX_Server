package back.server.mybox.Domain.controller;


import back.server.mybox.Domain.dto.UserRequestDto;
import back.server.mybox.Domain.dto.UserResponseDto;
import back.server.mybox.Domain.service.UserService;
import back.server.mybox.jwt.dto.TokenRequestDto;
import back.server.mybox.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
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

    @PutMapping("/logout/{userId}")
    public ResponseEntity<Map<String, Object>> uesrLogout(@PathVariable(value = "userId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        userService.userLogout(userId);
        response.put("message", "success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 토큰 갱신 accessToken, refreshToken을 갱신하여 전달
    @GetMapping("/refresh/{username}")
    public Map<String,String> refreshToken(@PathVariable("username") String username, @RequestHeader("refreshToken") String refreshToken,
                                           HttpServletResponse response){
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        TokenRequestDto tokenRequestDto = jwtService.validRefreshToken(username, refreshToken);
        Map<String, String> jsonResponse = jwtService.recreateTokenResponse(tokenRequestDto);
        return jsonResponse;
    }
}
