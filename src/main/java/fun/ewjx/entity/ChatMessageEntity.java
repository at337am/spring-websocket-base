package fun.ewjx.entity;

import lombok.*;
import fun.ewjx.dto.MessageType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "messages") // 指定 MongoDB 中的集合名称
public class ChatMessageEntity {
    @Id
    private String id;
    
    private MessageType type;
    private String content;
    private String sender;
}