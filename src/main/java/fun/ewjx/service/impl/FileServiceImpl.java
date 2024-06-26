package fun.ewjx.service.impl;

import fun.ewjx.dto.FileDTO;
import fun.ewjx.entity.FileEntity;
import fun.ewjx.repo.FileRepository;
import fun.ewjx.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    public void saveFile(MultipartFile file) throws IOException {
        FileEntity entity = FileEntity.builder()
                .fileName(file.getOriginalFilename())
                .fileData(file.getBytes())
                .build();
        fileRepository.save(entity);
    }

    public FileDTO getFile(String fileName) throws Exception {
        FileEntity fileEntity = fileRepository.findByFileName(fileName)
                .orElseThrow(() -> new Exception("File not found: " + fileName));

        FileDTO fileDTO = new FileDTO();
        fileDTO.setFileName(fileEntity.getFileName());
        fileDTO.setFileData(fileEntity.getFileData());

        return fileDTO;
    }
}
