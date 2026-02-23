package com.llm.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.llm.platform.entity.Conversation;

import java.util.List;

public interface ConversationService extends IService<Conversation> {
    
    List<Conversation> listRecentConversations();
    
    Conversation createConversation(String title);
    
    void deleteConversation(Long id);
}
