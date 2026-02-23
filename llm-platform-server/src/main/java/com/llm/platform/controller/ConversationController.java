package com.llm.platform.controller;

import com.llm.platform.entity.Conversation;
import com.llm.platform.entity.Message;
import com.llm.platform.service.ConversationService;
import com.llm.platform.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConversationController {
    
    private final ConversationService conversationService;
    private final MessageService messageService;

    // 列出所有会话
    @GetMapping("/conversations")
    public List<Conversation> listConversations() {
        return conversationService.listRecentConversations();
    }

    // 创建会话
    @PostMapping("/conversations")
    public Conversation createConversation(@RequestBody Map<String, String> request) {
        String title = request.getOrDefault("title", "新对话");
        return conversationService.createConversation(title);
    }

    // 根据指定ID删除会话
    @DeleteMapping("/conversations/{id}")
    public void deleteConversation(@PathVariable Long id) {
        conversationService.deleteConversation(id);
        messageService.deleteMessagesByConversationId(id);
    }

    // 根据指定ID查询会话消息
    @GetMapping("/conversations/{id}/messages")
    public List<Message> getMessages(@PathVariable Long id) {
        return messageService.getMessagesByConversationId(id);
    }

    // 保存会话消息
    @PostMapping("/conversations/{id}/messages")
    public Message saveMessage(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String role = request.get("role");
        String content = request.get("content");
        return messageService.saveMessage(id, role, content);
    }
}
