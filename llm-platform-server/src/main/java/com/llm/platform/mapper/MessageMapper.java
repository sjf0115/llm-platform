package com.llm.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.llm.platform.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    
    @Select("SELECT * FROM message WHERE conversation_id = #{conversationId} AND deleted = 0 ORDER BY created_at ASC")
    List<Message> selectByConversationId(@Param("conversationId") Long conversationId);
}
