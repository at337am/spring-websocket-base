package fun.ewjx.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "files")
public class FileEntity {
    @Id
    private String id;

    @Indexed(unique = true) // 添加索引，并设为唯一
    private String fileName;
    private byte[] fileData;
}
