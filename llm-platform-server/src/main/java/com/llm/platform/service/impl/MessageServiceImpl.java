package com.llm.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llm.platform.entity.Message;
import com.llm.platform.mapper.MessageMapper;
import com.llm.platform.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    
    @Override
    public List<Message> getMessagesByConversationId(Long conversationId) {
        return baseMapper.selectByConversationId(conversationId);
    }
    
    @Override
    public Message saveMessage(Long conversationId, String role, String content) {
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setRole(role);
        message.setContent(content);
        save(message);
        return message;
    }
    
    @Override
    public void deleteMessagesByConversationId(Long conversationId) {
        lambdaUpdate()
                .eq(Message::getConversationId, conversationId)
                .remove();
    }
}
