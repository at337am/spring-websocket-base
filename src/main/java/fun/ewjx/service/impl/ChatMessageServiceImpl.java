package fun.ewjx.service.impl;

import fun.ewjx.dto.ChatMessageDTO;
import fun.ewjx.entity.ChatMessageEntity;
import fun.ewjx.repo.ChatMessageRepository;
import fun.ewjx.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository messageRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    // 回显历史所有消息
    public List<ChatMessageEntity> getAllChatMessages() {
        return messageRepository.findAll();
    }

    // 保存消息到数据库中
    public void saveChatMessage(ChatMessageDTO chatMessageDTO) {
        // 保存消息到 MongoDB
        ChatMessageEntity entity = ChatMessageEntity.builder()
                .type(chatMessageDTO.getType())
                .content(chatMessageDTO.getContent())
                .sender(chatMessageDTO.getSender())
                .build();
        messageRepository.save(entity);

        // 发送消息到订阅者
        messagingTemplate.convertAndSend("/topic/public", chatMessageDTO);
    }

    // 在 web socket 会话中添加用户名
    public void addUser(ChatMessageDTO chatMessageDTO, SimpMessageHeaderAccessor headerAccessor) {
        if (headerAccessor.getSessionAttributes() == null) {
            log.warn("Session attributes are null");
            return;
        }

        headerAccessor.getSessionAttributes().put("username", chatMessageDTO.getSender());
        log.info("Added user '{}' to session", chatMessageDTO.getSender());

        messagingTemplate.convertAndSend("/topic/public", chatMessageDTO);
        log.info("Message sent to /topic/public");
    }
}
