package fun.ewjx.repo;

import fun.ewjx.entity.FileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FileRepository extends MongoRepository<FileEntity, String> {
    Optional<FileEntity> findByFileName(String fileName);
}