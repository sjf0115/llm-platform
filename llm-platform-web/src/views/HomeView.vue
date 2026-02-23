<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-left">
        <div class="brand">
          <div class="brand-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" fill="#1677ff"/>
              <path d="M2 17L12 22L22 17" stroke="#1677ff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 12L12 17L22 12" stroke="#1677ff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <span class="brand-text">通义千问</span>
        </div>
      </div>
      <div class="header-right">
        <el-button text class="lang-btn">EN</el-button>
      </div>
    </header>

    <!-- 顶部公告栏 -->
    <div class="announcement-bar">
      <span class="announcement-icon">🎉</span>
      <span class="announcement-text">
        通义千问-Turbo 正式版发布，强化 Agent 能力，融入思考推理，在网页端、APP 和 API 全面上线，点击查看详情。
      </span>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- Logo 区域 -->
      <div class="logo-section">
        <h1 class="logo-text">通义千问</h1>
        <p class="slogan">探索未至之境</p>
      </div>

      <!-- 入口卡片 -->
      <div class="entry-cards">
        <div class="entry-card" @click="startChat">
          <div class="card-icon">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="card-content">
            <h3 class="card-title">开始对话</h3>
            <p class="card-desc">与通义千问-Turbo 免费对话</p>
            <p class="card-desc">体验全新旗舰模型</p>
          </div>
        </div>

        <div class="entry-card" @click="showApiDocs">
          <div class="card-icon api-icon">
            <el-icon><Document /></el-icon>
          </div>
          <div class="card-content">
            <h3 class="card-title">API 开放平台</h3>
            <p class="card-desc">调用通义千问最新模型</p>
            <p class="card-desc">快速集成、流畅体验</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ChatDotRound, Document } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()

const startChat = () => {
  router.push('/chat')
}

const showApiDocs = () => {
  ElMessage.info('API 文档功能开发中...')
}
</script>

<style scoped>
.home-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(180deg, #f5f9ff 0%, #ffffff 50%, #f0f7ff 100%);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部导航栏 */
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 40px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-icon {
  width: 32px;
  height: 32px;
}

.brand-icon svg {
  width: 100%;
  height: 100%;
}

.brand-text {
  font-size: 20px;
  font-weight: 600;
  color: #1677ff;
  letter-spacing: -0.5px;
}

.header-right {
  display: flex;
  align-items: center;
}

.lang-btn {
  font-size: 14px;
  color: #5b6b79;
  font-weight: 500;
}

/* 公告栏 */
.announcement-bar {
  margin-top: 60px;
  background: rgba(255, 255, 255, 0.8);
  padding: 12px 24px;
  text-align: center;
  border-bottom: 1px solid #e8f0fe;
  backdrop-filter: blur(10px);
}

.announcement-icon {
  margin-right: 8px;
}

.announcement-text {
  color: #5b6b79;
  font-size: 14px;
}

/* 主内容区 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 40px 40px;
  width: 100%;
  min-width: 100%;
  overflow-y: auto;
  box-sizing: border-box;
}

.logo-section {
  text-align: center;
  margin-bottom: 80px;
}

.logo-text {
  font-size: 80px;
  font-weight: 700;
  background: linear-gradient(135deg, #1677ff 0%, #0056b3 50%, #1677ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 20px 0;
  letter-spacing: -3px;
  animation: shimmer 3s ease-in-out infinite;
  background-size: 200% 200%;
}

@keyframes shimmer {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.slogan {
  font-size: 26px;
  color: #5b6b79;
  margin: 0;
  font-weight: 400;
  letter-spacing: 2px;
}

/* 入口卡片 */
.entry-cards {
  display: flex;
  gap: 32px;
  flex-wrap: wrap;
  justify-content: center;
  width: 100%;
  max-width: 1200px;
}

.entry-card {
  width: 360px;
  padding: 36px;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: flex-start;
  gap: 24px;
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.entry-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 40px rgba(22, 119, 255, 0.15);
  border-color: rgba(22, 119, 255, 0.1);
}

.card-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #1677ff 0%, #0056b3 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(22, 119, 255, 0.3);
}

.card-icon.api-icon {
  background: linear-gradient(135deg, #00b4db 0%, #0083b0 100%);
  box-shadow: 0 4px 12px rgba(0, 180, 219, 0.3);
}

.card-icon .el-icon {
  font-size: 28px;
  color: #ffffff;
}

.card-content {
  flex: 1;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 10px 0;
}

.card-desc {
  font-size: 14px;
  color: #5b6b79;
  margin: 0 0 6px 0;
  line-height: 1.6;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header {
    padding: 0 20px;
  }
  
  .logo-text {
    font-size: 52px;
    letter-spacing: -2px;
  }
  
  .slogan {
    font-size: 20px;
    letter-spacing: 1px;
  }
  
  .entry-cards {
    flex-direction: column;
    align-items: center;
    gap: 20px;
  }
  
  .entry-card {
    width: 100%;
    max-width: 340px;
    padding: 28px;
  }
  
  .main-content {
    padding: 20px;
  }
}
</style>
