package fun.ewjx.controller;

import fun.ewjx.dto.FileDTO;
import fun.ewjx.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        try {
            FileDTO fileDTO = fileService.getFile(fileName);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileDTO.getFileName() + "\"")
                    .body(fileDTO.getFileData());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(("File download failed: " + fileName).getBytes());
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            fileService.saveFile(file);
            return ResponseEntity.status(HttpStatus.OK).body(file.getOriginalFilename());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("上传失败: " + file.getOriginalFilename());
        }
    }

}
