package back.server.mybox.Domain.controller;

import back.server.mybox.Domain.service.NcpService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/ncp")
@RestController
public class NcpController {
    private final NcpService ncpService;

    // 폴더 생성
    @PostMapping("/folder/{foldername}")
    public ResponseEntity<Map<String, Object>> createFolder(@PathVariable(value = "foldername")String foldername){
        Map<String, Object> response = new HashMap<>();
        String responseDto = ncpService.createFolder(foldername);
        response.put("data", responseDto);
        response.put("message", "success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 폴더 삭제
    @DeleteMapping("/folder/{foldername}")
    public ResponseEntity<Map<String, Object>> deleteFolder(@PathVariable(value = "foldername")String foldername){
        Map<String, Object> response = new HashMap<>();
        ncpService.deleteFolder(foldername);
        response.put("message", "success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
//
//    //자식 파일/폴더 리스트 조회
//    @GetMapping("/folder/{foldername}")
//    public ResponseEntity<Map<String, Object>> folderList(@PathVariable(value = "foldername")String foldername){
//        Map<String, Object> response = new HashMap<>();
//        UserResponseDto responseDto =  ncpService.folderList();
//        response.put("data", responseDto);
//        response.put("message", "success");
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    // 파일 업로드
    @PostMapping("/file")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam(value = "file") MultipartFile multipartFile) throws IOException{
        Map<String, Object> response = new HashMap<>();
        String responseDto = ncpService.uploadFile(multipartFile);
        response.put("data", responseDto);
        response.put("message", "success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 파일 삭제
    @DeleteMapping("/file/{filename}")
    public ResponseEntity<Map<String, Object>> deleteFile(@PathVariable(value = "filename")String filename){
        Map<String, Object> response = new HashMap<>();
        ncpService.deleteFile(filename);
        response.put("message", "success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 파일 다운로드
    @GetMapping("/file/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable(value = "filename")String filename) throws IOException{
        return ncpService.downloadFile(filename);
    }

//    @PutMapping("/create/{bucketName}")
//    public ResponseEntity<Map<String, Object>> upload(@PathVariable(value = "bucketName")String bucketName) throws IOException {
//        Map<String, Object> response = new HashMap<>();
//        ncpService.createBucket(bucketName);
//        response.put("data", "responseDto");
//        response.put("message", "success");
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @GetMapping("/bucket/list")
//    public ResponseEntity<Map<String, Object>> bucketsList(){
//        Map<String, Object> response = new HashMap<>();
//        ncpService.getBucketList();
//        response.put("data", "responseDto");
//        response.put("message", "success");
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
}
