<template>
  <div class="chat-container">
    <!-- 左侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isSidebarCollapsed }">
      <div class="sidebar-header">
        <div class="logo" @click="startNewChat" style="cursor: pointer;">
          <span class="logo-icon">🤖</span>
          <span class="logo-text" v-show="!isSidebarCollapsed">通义千问</span>
        </div>
        <el-button 
          class="collapse-btn" 
          :icon="isSidebarCollapsed ? Expand : Fold" 
          circle 
          text
          @click="toggleSidebar"
        />
      </div>

      <!-- 新建对话按钮 -->
      <div class="new-chat-section">
        <el-button 
          type="primary" 
          class="new-chat-btn"
          :class="{ collapsed: isSidebarCollapsed }"
          @click="startNewChat"
        >
          <el-icon><Plus /></el-icon>
          <span v-show="!isSidebarCollapsed">开启新对话</span>
        </el-button>
      </div>

      <!-- 历史会话列表 -->
      <div class="conversation-list" v-show="!isSidebarCollapsed">
        <!-- 今天 -->
        <div class="time-group" v-if="todayConversations.length > 0">
          <div class="time-label">今天</div>
          <div 
            v-for="conv in todayConversations" 
            :key="conv.id"
            class="conversation-item"
            :class="{ active: currentConversationId === conv.id }"
            @click="selectConversation(conv.id)"
          >
            <el-icon><ChatLineRound /></el-icon>
            <span class="conv-title">{{ conv.title }}</span>
            <el-dropdown trigger="click" @command="handleCommand($event, conv.id)">
              <el-icon class="more-icon"><MoreFilled /></el-icon>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="delete">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>

        <!-- 昨天 -->
        <div class="time-group" v-if="yesterdayConversations.length > 0">
          <div class="time-label">昨天</div>
          <div 
            v-for="conv in yesterdayConversations" 
            :key="conv.id"
            class="conversation-item"
            :class="{ active: currentConversationId === conv.id }"
            @click="selectConversation(conv.id)"
          >
            <el-icon><ChatLineRound /></el-icon>
            <span class="conv-title">{{ conv.title }}</span>
            <el-dropdown trigger="click" @command="handleCommand($event, conv.id)">
              <el-icon class="more-icon"><MoreFilled /></el-icon>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="delete">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>

        <!-- 7天内 -->
        <div class="time-group" v-if="weekConversations.length > 0">
          <div class="time-label">7天内</div>
          <div 
            v-for="conv in weekConversations" 
            :key="conv.id"
            class="conversation-item"
            :class="{ active: currentConversationId === conv.id }"
            @click="selectConversation(conv.id)"
          >
            <el-icon><ChatLineRound /></el-icon>
            <span class="conv-title">{{ conv.title }}</span>
            <el-dropdown trigger="click" @command="handleCommand($event, conv.id)">
              <el-icon class="more-icon"><MoreFilled /></el-icon>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="delete">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>

      <!-- 底部用户信息 -->
      <div class="sidebar-footer" v-show="!isSidebarCollapsed">
        <div class="user-info">
          <el-avatar :size="32" :icon="UserFilled" />
          <span class="username">用户</span>
        </div>
      </div>
    </aside>

    <!-- 主聊天区域 -->
    <main class="chat-main">
      <!-- 消息列表 -->
      <div class="messages-container" ref="messagesContainer">
        <!-- 欢迎语 -->
        <div v-if="messages.length === 0" class="welcome-section">
          <div class="welcome-icon">🤖</div>
          <h2 class="welcome-text">今天有什么可以帮到你？</h2>
          <p class="welcome-subtext">输入消息开始新对话</p>
        </div>

        <!-- 消息列表 -->
        <div v-else class="messages-list">
          <div 
            v-for="(msg, index) in messages" 
            :key="index"
            class="message-item"
            :class="msg.role"
          >
            <div class="message-avatar">
              <el-avatar 
                :size="36" 
                :icon="msg.role === 'user' ? UserFilled : ChatDotRound"
                :class="msg.role"
              />
            </div>
            <div class="message-content">
              <div class="message-text" v-html="renderMarkdown(msg.content)"></div>
            </div>
          </div>
          
          <!-- 加载中 -->
          <div v-if="isLoading" class="message-item assistant">
            <div class="message-avatar">
              <el-avatar :size="36" :icon="ChatDotRound" class="assistant" />
            </div>
            <div class="message-content">
              <div class="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="input-section">
        <div class="input-container">
          <!-- 输入框 -->
          <div class="input-wrapper">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="1"
              placeholder="给通义千问发送消息"
              resize="none"
              @keydown.enter.prevent="handleEnter"
            />
          </div>
          
          <!-- 底部工具栏 -->
          <div class="input-toolbar">
            <div class="toolbar-left">
              <el-button 
                class="action-btn"
                :class="{ active: isDeepThinking }"
                @click="isDeepThinking = !isDeepThinking"
              >
                <el-icon><Lightning /></el-icon>
                <span>深度思考</span>
              </el-button>
              <el-button 
                class="action-btn"
                :class="{ active: isWebSearch }"
                @click="isWebSearch = !isWebSearch"
              >
                <el-icon><Search /></el-icon>
                <span>联网搜索</span>
              </el-button>
            </div>
            <div class="toolbar-right">
              <el-button circle text :icon="Paperclip" class="attach-btn" />
              <el-button 
                type="primary" 
                circle 
                :icon="Promotion"
                :disabled="!inputMessage.trim() || isLoading"
                @click="sendMessage"
                class="send-btn"
              />
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useChatStore } from '@/stores/chat'
import { marked } from 'marked'

interface Conversation {
  id: number
  title: string
  createdAt: string
  updatedAt: string
}
import {
  Plus,
  ChatLineRound,
  MoreFilled,
  UserFilled,
  ChatDotRound,
  Search,
  Paperclip,
  Promotion,
  Fold,
  Expand,
  Lightning
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const chatStore = useChatStore()

// 状态
const isSidebarCollapsed = ref(false)
const inputMessage = ref('')
const isLoading = ref(false)
const isDeepThinking = ref(false)
const isWebSearch = ref(false)
const messagesContainer = ref<HTMLElement>()

// 计算属性
const currentConversationId = computed(() => chatStore.currentConversationId)
const messages = computed(() => chatStore.messages)
const conversations = computed(() => chatStore.conversations)

const todayConversations = computed(() => {
  return conversations.value.filter((conv: Conversation) => isToday(conv.updatedAt))
})

const yesterdayConversations = computed(() => {
  return conversations.value.filter((conv: Conversation) => isYesterday(conv.updatedAt))
})

const weekConversations = computed(() => {
  return conversations.value.filter((conv: Conversation) => isWithinWeek(conv.updatedAt))
})

// 方法
const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
}

const isToday = (dateStr: string) => {
  const date = new Date(dateStr)
  const today = new Date()
  return date.toDateString() === today.toDateString()
}

const isYesterday = (dateStr: string) => {
  const date = new Date(dateStr)
  const yesterday = new Date()
  yesterday.setDate(yesterday.getDate() - 1)
  return date.toDateString() === yesterday.toDateString()
}

const isWithinWeek = (dateStr: string) => {
  const date = new Date(dateStr)
  const weekAgo = new Date()
  weekAgo.setDate(weekAgo.getDate() - 7)
  return date > weekAgo && !isToday(dateStr) && !isYesterday(dateStr)
}

const createNewConversation = async () => {
  const conversation = await chatStore.createConversation('新对话')
  router.push(`/chat/${conversation.id}`)
}

// 开启新对话（不创建会话，只清空当前状态）
const startNewChat = () => {
  chatStore.currentConversationId = null
  chatStore.messages = []
  router.push('/chat')
}

const selectConversation = (id: number) => {
  chatStore.setCurrentConversation(id)
  router.push(`/chat/${id}`)
}

const handleCommand = async (command: string, id: number) => {
  if (command === 'delete') {
    try {
      await ElMessageBox.confirm('确定要删除这个对话吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await chatStore.deleteConversation(id)
      if (currentConversationId.value === id) {
        router.push('/chat')
      }
      ElMessage.success('删除成功')
    } catch {
      // 取消删除
    }
  }
}

const renderMarkdown = (content: string) => {
  if (!content) return ''
  // 配置 marked 选项
  marked.setOptions({
    breaks: true,  // 支持换行
    gfm: true      // 支持 GitHub 风格 Markdown
  })
  return marked(content)
}

const handleEnter = (e: KeyboardEvent) => {
  if (!e.shiftKey) {
    sendMessage()
  }
}

const sendMessage = async () => {
  const message = inputMessage.value.trim()
  if (!message || isLoading.value) return

  inputMessage.value = ''
  isLoading.value = true

  try {
    // 发送消息，如果没有会话ID，后端会自动创建
    await chatStore.sendMessage(message)
    
    // 如果是新会话，更新URL
    if (currentConversationId.value) {
      router.push(`/chat/${currentConversationId.value}`)
    }
  } catch (error) {
    ElMessage.error('发送消息失败')
  } finally {
    isLoading.value = false
    scrollToBottom()
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 监听消息变化，自动滚动
watch(messages, scrollToBottom, { deep: true })

onMounted(async () => {
  await chatStore.loadConversations()
  
  const conversationId = route.params.id
  if (conversationId) {
    chatStore.setCurrentConversation(Number(conversationId))
  }
})
</script>

<style scoped>
.chat-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  width: 100%;
  height: 100%;
  background: linear-gradient(180deg, #f5f9ff 0%, #ffffff 50%, #f0f7ff 100%);
  color: #1a1a1a;
  overflow: hidden;
}

/* 侧边栏 */
.sidebar {
  width: 260px;
  min-width: 260px;
  height: 100%;
  background: #ffffff;
  border-right: 1px solid #e8f0fe;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease, min-width 0.3s ease;
  flex-shrink: 0;
}

.sidebar.collapsed {
  width: 64px;
  min-width: 64px;
}

.sidebar-header {
  padding: 14px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-icon {
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #1677ff 0%, #0056b3 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  color: #1677ff;
}

.collapse-btn {
  color: #5b6b79;
  transition: color 0.2s;
}

.collapse-btn:hover {
  color: #1a1a1a;
}

.new-chat-section {
  padding: 0 16px 16px;
  flex-shrink: 0;
}

.new-chat-btn {
  width: 100%;
  height: 40px;
  background: #f0f7ff;
  border: 1px solid #e8f0fe;
  border-radius: 8px;
  color: #1677ff;
  font-size: 14px;
  font-weight: 500;
  justify-content: flex-start;
  gap: 8px;
  transition: all 0.2s;
}

.new-chat-btn:hover {
  background: #e8f0fe;
  border-color: #1677ff30;
}

.new-chat-btn.collapsed {
  justify-content: center;
  padding: 12px;
}

.conversation-list {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 0 12px;
  min-height: 0;
}

.time-group {
  margin-bottom: 20px;
}

.time-label {
  font-size: 11px;
  font-weight: 500;
  color: #9ca3af;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  padding: 8px 12px;
  margin-bottom: 4px;
}

.conversation-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
  margin-bottom: 4px;
}

.conversation-item:hover {
  background: #f0f7ff;
}

.conversation-item.active {
  background: #e8f0fe;
  border: 1px solid #1677ff30;
}

.conv-title {
  flex: 1;
  font-size: 13px;
  color: #4b5563;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.conversation-item.active .conv-title {
  color: #1677ff;
  font-weight: 500;
}

.more-icon {
  opacity: 0;
  transition: opacity 0.2s;
  color: #9ca3af;
  padding: 4px;
  border-radius: 4px;
}

.more-icon:hover {
  background: #e8f0fe;
  color: #1677ff;
}

.conversation-item:hover .more-icon {
  opacity: 1;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid #e8f0fe;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.username {
  font-size: 14px;
  color: #4b5563;
  font-weight: 500;
}

/* 主聊天区域 */
.chat-main {
  flex: 1;
  min-width: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: transparent;
  overflow: hidden;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 20px;
  min-height: 0;
}

.welcome-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  gap: 16px;
  padding-bottom: 120px;
}

.welcome-icon {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #1677ff 0%, #0056b3 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  box-shadow: 0 4px 20px rgba(22, 119, 255, 0.2);
}

.welcome-text {
  font-size: 24px;
  font-weight: 500;
  color: #1a1a1a;
  letter-spacing: -0.3px;
}

.welcome-subtext {
  font-size: 14px;
  color: #9ca3af;
  margin-top: 8px;
}

.messages-list {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.message-item {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar .el-avatar {
  background: #f0f7ff;
  border: 1px solid #e8f0fe;
}

.message-avatar .el-avatar.assistant {
  background: linear-gradient(135deg, #1677ff 0%, #0056b3 100%);
  border: none;
}

.message-content {
  max-width: 80%;
  padding: 16px 20px;
  border-radius: 16px;
  background: #f8fafc;
  border: 1px solid #d1d5db;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.message-item.user .message-content {
  background: #1677ff;
  border: none;
  box-shadow: 0 2px 8px rgba(22, 119, 255, 0.2);
}

.message-text {
  font-size: 15px;
  line-height: 1.7;
  color: #1f2937;
}

.message-item.user .message-text {
  color: #ffffff;
}

.message-text :deep(p) {
  margin: 0 0 12px 0;
}

.message-text :deep(p:last-child) {
  margin-bottom: 0;
}

.message-text :deep(pre) {
  background: #f8fafc;
  padding: 16px;
  border-radius: 10px;
  overflow-x: auto;
  border: 1px solid #e8f0fe;
  margin: 12px 0;
}

.message-text :deep(code) {
  font-family: 'JetBrains Mono', 'Courier New', monospace;
  font-size: 13px;
  background: #f0f7ff;
  padding: 2px 6px;
  border-radius: 4px;
  color: #1677ff;
}

.message-text :deep(pre code) {
  background: transparent;
  padding: 0;
  color: #374151;
}

.message-text :deep(ul), .message-text :deep(ol) {
  margin: 12px 0;
  padding-left: 24px;
}

.message-text :deep(li) {
  margin: 6px 0;
}

/* Markdown 样式优化 */
.message-text :deep(h1),
.message-text :deep(h2),
.message-text :deep(h3),
.message-text :deep(h4) {
  margin: 16px 0 12px 0;
  font-weight: 600;
  color: #1f2937;
}

.message-text :deep(h1) { font-size: 20px; }
.message-text :deep(h2) { font-size: 18px; }
.message-text :deep(h3) { font-size: 16px; }

.message-text :deep(strong) {
  font-weight: 600;
  color: #1f2937;
}

.message-text :deep(em) {
  font-style: italic;
}

.message-text :deep(blockquote) {
  border-left: 4px solid #1677ff;
  padding-left: 16px;
  margin: 12px 0;
  color: #4b5563;
}

.message-text :deep(hr) {
  border: none;
  border-top: 1px solid #e5e7eb;
  margin: 16px 0;
}

.message-text :deep(table) {
  border-collapse: collapse;
  width: 100%;
  margin: 12px 0;
}

.message-text :deep(th),
.message-text :deep(td) {
  border: 1px solid #e5e7eb;
  padding: 8px 12px;
  text-align: left;
}

.message-text :deep(th) {
  background: #f8fafc;
  font-weight: 600;
}

.typing-indicator {
  display: flex;
  gap: 6px;
  padding: 12px 0;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #6b7280;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.4;
  }
  30% {
    transform: translateY(-8px);
    opacity: 1;
  }
}

/* 输入区域 */
.input-section {
  padding: 16px 24px 24px;
  background: transparent;
  flex-shrink: 0;
}

.input-container {
  max-width: 800px;
  margin: 0 auto;
  background: #ffffff;
  border-radius: 16px;
  padding: 12px 16px;
  border: 1px solid #e8f0fe;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: all 0.2s;
}

.input-container:focus-within {
  border-color: #1677ff40;
  box-shadow: 0 4px 20px rgba(22, 119, 255, 0.1);
}

.input-wrapper {
  position: relative;
  margin-bottom: 8px;
}

.input-wrapper :deep(.el-textarea__inner) {
  background: transparent;
  border: none;
  color: #1a1a1a;
  font-size: 15px;
  padding: 8px 0;
  box-shadow: none;
  line-height: 1.6;
  resize: none;
  min-height: 24px !important;
}

.input-wrapper :deep(.el-textarea__inner::placeholder) {
  color: #9ca3af;
}

.input-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toolbar-left {
  display: flex;
  gap: 8px;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  background: transparent;
  border: none;
  color: #6b7280;
  padding: 6px 12px;
  font-size: 13px;
  border-radius: 6px;
  transition: all 0.2s;
  height: 32px;
}

.action-btn:hover {
  background: #f0f7ff;
  color: #4b5563;
}

.action-btn.active {
  background: #e8f0fe;
  color: #1677ff;
}

.action-btn .el-icon {
  margin-right: 4px;
  font-size: 14px;
}

.attach-btn {
  color: #9ca3af;
  transition: all 0.2s;
}

.attach-btn:hover {
  color: #6b7280;
  background: #f0f7ff;
}

.send-btn {
  background: #1677ff;
  color: #ffffff;
  border: none;
  transition: all 0.2s;
}

.send-btn:hover {
  background: #4096ff;
}

.send-btn:disabled {
  background: #e8f0fe;
  color: #9ca3af;
}
</style>
