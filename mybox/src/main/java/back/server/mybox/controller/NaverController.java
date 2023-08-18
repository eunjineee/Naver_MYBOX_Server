package back.server.mybox.controller;

import back.server.mybox.service.NaverService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/ncp")
@RestController
public class NaverController {
    private final NaverService naverService;


    @PutMapping("/create/{bucketName}")
    public ResponseEntity<Map<String, Object>> upload(@PathVariable(value = "bucketName")String bucketName) throws IOException {
        Map<String, Object> response = new HashMap<>();
        naverService.createBucket(bucketName);
        response.put("message", "success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/bucket/list")
    public ResponseEntity<Map<String, Object>> bucketsList() throws IOException {
        Map<String, Object> response = new HashMap<>();
        naverService.getBucketList();
        response.put("message", "success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
//
//    @GetMapping("/download/{filename}")
//    public ResponseEntity<Map<String, Object>> download(@PathVariable(value = "filename") String filename){
//        System.out.println(filename);
//        Map<String, Object> response = new HashMap<>();
//        ResponseEntity<UrlResource> url = s3service.download(filename);
//        response.put("data", url);
//        response.put("message", "success");
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
}
