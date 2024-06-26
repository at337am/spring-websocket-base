package fun.ewjx.service;

import fun.ewjx.dto.FileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    // 从数据库获取文件
    FileDTO getFile(String fileName) throws Exception;

    // 保存文件到数据库
    void saveFile(MultipartFile file) throws IOException;
}
