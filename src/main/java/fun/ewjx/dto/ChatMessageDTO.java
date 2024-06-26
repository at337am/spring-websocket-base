package fun.ewjx.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDTO {
    private MessageType type;
    private String content;
    private String sender;
}
