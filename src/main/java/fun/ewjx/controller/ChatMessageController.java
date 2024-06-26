package fun.ewjx.controller;

import fun.ewjx.dto.ChatMessageDTO;
import fun.ewjx.entity.ChatMessageEntity;
import fun.ewjx.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService messageService;

    // 接收发送消息的请求
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDTO chatMessageDTO) {
        messageService.saveChatMessage(chatMessageDTO);
    }

    // 接收添加用户的请求
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessageDTO chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        messageService.addUser(chatMessage, headerAccessor);
    }

    // 接收历史消息的请求
    @GetMapping("/api/messages")
    public List<ChatMessageEntity> getAllMessages() {
        return messageService.getAllChatMessages();
    }
}
