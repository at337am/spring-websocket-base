package fun.ewjx.config;

import fun.ewjx.dto.ChatMessageDTO;
import fun.ewjx.dto.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;


@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    // 监听 WebSocket 连接断开事件
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        // 使用 StompHeaderAccessor 包装事件消息，方便访问 STOMP 协议的头信息
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // 获取断开连接的用户名，从 WebSocket 会话属性中获取
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        // 如果用户名不为 null，则记录用户断开连接的信息
        if (username != null) {
            log.info("user disconnected: {}", username);

            // 创建一个离开消息（ChatMessageDTO），标记用户离开聊天室
            var chatMessage = ChatMessageDTO.builder()
                    .type(MessageType.LEAVE)
                    .sender(username)
                    .build();

            // 将离开消息发送到 "/topic/public" 主题，通知订阅者有用户离开
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}
