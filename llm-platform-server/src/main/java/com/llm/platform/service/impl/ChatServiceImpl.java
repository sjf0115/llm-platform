package com.llm.platform.service.impl;

import com.llm.platform.service.ChatService;
import com.llm.platform.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatClient chatClient;
    private final MessageService messageService;

    @Override
    public Flux<String> streamChat(Long conversationId, String message) {
        return streamChatWithHistory(conversationId, message);
    }

    // Todo: 事务
    @Override
    public Flux<String> streamChatWithHistory(Long conversationId, String message) {
        // 保存用户消息
        messageService.saveMessage(conversationId, "user", message);
        
        // 流式返回(带记忆)
        StringBuilder sb = new StringBuilder();
        Flux<String> flux = chatClient
                .prompt()
                // 通过监听器传入会话ID，实现对话记忆
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(message)
                .stream()
                .content()
                .doOnNext(str -> {
                    sb.append(str);
                    log.info("AI回复: {}", str);})
                .doOnComplete(() -> {
                    // 保存AI回复
                    messageService.saveMessage(conversationId, "assistant", sb.toString());
                    log.info("Chat completed - conversationId: {}, response length: {}", conversationId, sb.length());
                })
                .doOnError(error -> {
                    log.error("Error during AI streaming - conversationId: {}", conversationId, error);
                });
        return flux;
    }
}
