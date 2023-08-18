package back.server.mybox.controller;

import back.server.mybox.dto.UserRequestDto;
import back.server.mybox.dto.UserResponseDto;
import back.server.mybox.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/file")
@RestController
public class S3Controller {
    private final S3Service s3service;


    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(@RequestParam(value = "file")MultipartFile multipartFile) throws IOException {
        System.out.println(multipartFile);
        Map<String, Object> response = new HashMap<>();
        String upload_url = s3service.saveFile(multipartFile);
        response.put("data", upload_url);
        response.put("message", "success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Map<String, Object>> download(@PathVariable(value = "filename") String filename){
        System.out.println(filename);
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<UrlResource> url = s3service.download(filename);
        response.put("data", url);
        response.put("message", "success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
