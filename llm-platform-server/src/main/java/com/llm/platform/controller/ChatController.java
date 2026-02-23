package com.llm.platform.controller;

import com.llm.platform.entity.Conversation;
import com.llm.platform.service.ChatService;
import com.llm.platform.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {
    
    private final ChatService chatService;
    private final ConversationService conversationService;
    
    /**
     * 流式聊天接口
     * 如果 conversationId 为空，则自动创建新会话
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody Map<String, Object> request) {
        String message = request.get("message").toString();
        Long conversationId = null;
        
        // 获取或创建会话ID
        if (request.get("conversationId") != null && !request.get("conversationId").toString().isEmpty()) {
            conversationId = Long.valueOf(request.get("conversationId").toString());
        }
        
        // 如果没有会话ID，创建新会话
        if (conversationId == null) {
            // 会话标题
            String title = message.length() > 20 ? message.substring(0, 20) + "..." : message;
            Conversation conversation = conversationService.createConversation(title);
            conversationId = conversation.getId();
            log.info("创建新的会话, 会话ID: {}, 会话标题: {}", conversationId, title);
        }
        log.info("发起AI请求, 会话ID: {}, 会话内容: {}", conversationId, message);
        return chatService.streamChat(conversationId, message);
    }
    
    /**
     * 获取当前会话ID（用于前端首次创建会话后获取ID）
     */
    @PostMapping("/init")
    public Mono<Conversation> initConversation(@RequestBody Map<String, String> request) {
        String message = request.getOrDefault("message", "新对话");
        String title = message.length() > 20 ? message.substring(0, 20) + "..." : message;
        return Mono.just(conversationService.createConversation(title));
    }
}
