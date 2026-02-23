import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'
import { createParser, type EventSourceParser } from 'eventsource-parser'

const API_BASE_URL = 'http://localhost:8888/api'

interface Conversation {
  id: number
  title: string
  createdAt: string
  updatedAt: string
}

interface Message {
  role: 'user' | 'assistant'
  content: string
}

export const useChatStore = defineStore('chat', () => {
  // State
  const conversations = ref<Conversation[]>([])
  const currentConversationId = ref<number | null>(null)
  const messages = ref<Message[]>([])

  // Getters
  const currentConversation = computed(() => {
    return conversations.value.find(c => c.id === currentConversationId.value)
  })

  // Actions
  const loadConversations = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/conversations`)
      conversations.value = response.data
    } catch (error) {
      console.error('Failed to load conversations:', error)
    }
  }

  const createConversation = async (title: string): Promise<Conversation> => {
    try {
      const response = await axios.post(`${API_BASE_URL}/conversations`, { title })
      const conversation = response.data
      conversations.value.unshift(conversation)
      currentConversationId.value = conversation.id
      messages.value = []
      return conversation
    } catch (error) {
      console.error('Failed to create conversation:', error)
      throw error
    }
  }

  const deleteConversation = async (id: number) => {
    try {
      await axios.delete(`${API_BASE_URL}/conversations/${id}`)
      conversations.value = conversations.value.filter(c => c.id !== id)
      if (currentConversationId.value === id) {
        currentConversationId.value = null
        messages.value = []
      }
    } catch (error) {
      console.error('Failed to delete conversation:', error)
      throw error
    }
  }

  const setCurrentConversation = async (id: number) => {
    currentConversationId.value = id
    await loadMessages(id)
  }

  const loadMessages = async (conversationId: number) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/conversations/${conversationId}/messages`)
      messages.value = response.data.map((msg: any) => ({
        role: msg.role,
        content: msg.content
      }))
    } catch (error) {
      console.error('Failed to load messages:', error)
      messages.value = []
    }
  }

  const sendMessage = async (content: string): Promise<void> => {
    // 添加用户消息到列表
    messages.value.push({ role: 'user', content })

    try {
      // 使用 SSE 接收流式响应
      // 如果没有 currentConversationId，后端会自动创建新会话
      const requestBody: any = {
        message: content
      }
      
      // 如果有会话ID，则传递；否则后端会自动创建
      if (currentConversationId.value) {
        requestBody.conversationId = currentConversationId.value
      }

      const response = await fetch(`${API_BASE_URL}/chat/stream`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestBody)
      })

      // 从响应头中获取会话ID（后端在创建新会话后会返回）
      // 注意：由于 SSE 流式响应，我们需要在流结束后刷新会话列表

      const reader = response.body?.getReader()
      const decoder = new TextDecoder()
      let assistantMessage = ''

      // 添加空的助手消息
      messages.value.push({ role: 'assistant', content: '' })
      const assistantIndex = messages.value.length - 1

      // 创建 SSE 解析器
      const parser = createParser({
        onEvent: (event: any) => {
          const data = event.data
          if (data === '[DONE]') return
          
          assistantMessage += data
          if (messages.value[assistantIndex]) {
            messages.value[assistantIndex].content = assistantMessage
          }
        }
      })

      if (reader) {
        while (true) {
          const { done, value } = await reader.read()
          if (done) break

          const chunk = decoder.decode(value, { stream: true })
          // 使用专业的 SSE 解析器处理数据
          parser.feed(chunk)
        }
        // 解析完成
        parser.reset()
      }
      
      // 流式响应结束后，刷新会话列表（获取新创建的会话ID）
      await loadConversations()
      
      // 如果没有当前会话ID，设置为最新创建的会话
      if (!currentConversationId.value && conversations.value.length > 0) {
        currentConversationId.value = conversations.value[0]!.id
      }
    } catch (error) {
      console.error('Failed to send message:', error)
      // 移除失败的消息（用户消息和空的AI消息）
      const lastMsg = messages.value[messages.value.length - 1]
      if (messages.value.length >= 2 && lastMsg && lastMsg.role === 'assistant') {
        messages.value.splice(-2, 2)
      } else {
        messages.value.pop()
      }
      throw error
    }
  }

  return {
    conversations,
    currentConversationId,
    messages,
    currentConversation,
    loadConversations,
    createConversation,
    deleteConversation,
    setCurrentConversation,
    sendMessage
  }
})
