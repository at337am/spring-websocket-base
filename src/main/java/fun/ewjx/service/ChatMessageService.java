package fun.ewjx.service;

import fun.ewjx.dto.ChatMessageDTO;
import fun.ewjx.entity.ChatMessageEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.List;

public interface ChatMessageService {

    // 回显历史所有消息
    List<ChatMessageEntity> getAllChatMessages();

    // 保存消息到数据库中
    void saveChatMessage(ChatMessageDTO chatMessageDTO);

    // 在 web socket 会话中添加用户名
    void addUser(ChatMessageDTO chatMessageDTO, SimpMessageHeaderAccessor headerAccessor);
}
