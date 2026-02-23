package com.llm.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llm.platform.entity.Conversation;
import com.llm.platform.mapper.ConversationMapper;
import com.llm.platform.service.ConversationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation> implements ConversationService {
    
    @Override
    public List<Conversation> listRecentConversations() {
        return lambdaQuery()
                .orderByDesc(Conversation::getUpdatedAt)
                .list();
    }
    
    @Override
    public Conversation createConversation(String title) {
        Conversation conversation = new Conversation();
        conversation.setTitle(title);
        save(conversation);
        return conversation;
    }
    
    @Override
    public void deleteConversation(Long id) {
        removeById(id);
    }
}
