package com.llm.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.llm.platform.entity.Message;

import java.util.List;

public interface MessageService extends IService<Message> {
    
    List<Message> getMessagesByConversationId(Long conversationId);
    
    Message saveMessage(Long conversationId, String role, String content);
    
    void deleteMessagesByConversationId(Long conversationId);
}
