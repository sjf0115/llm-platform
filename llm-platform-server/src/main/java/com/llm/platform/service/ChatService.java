package com.llm.platform.service;

import reactor.core.publisher.Flux;

public interface ChatService {
    
    Flux<String> streamChat(Long conversationId, String message);
    
    Flux<String> streamChatWithHistory(Long conversationId, String message);
}
