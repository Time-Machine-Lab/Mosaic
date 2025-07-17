<script lang="ts" setup>
import { defineProps, ref, onMounted, computed } from "vue";
import { type Cube, AngelCubeStatusUpdateReq } from "../../api/plugin/pluginType";
import { updateAngelCubeStatus } from '../../api/plugin/pluginApi'
import totemIconUrl from '../../assets/icons/minecraft-totem3.png?url'
import grassIconUrl from '../../assets/icons/minecraft-grass.svg?url'
// 如果使用了消息组件，请导入
// import { ElMessage } from 'element-plus'

import MCLeverSwitch from './MCLeverSwitch.vue'

const dialog = ref(false)
const isHovered = ref(false)
const configDialog = ref(false)

// 添加状态相关的响应式数据
const isUpdatingStatus = ref(false)
const statusUpdateError = ref<string>('')
const showStatusError = ref(false)

const props = defineProps({
  cube: {
    type: Object as () => Cube,
    default: () => ({} as Cube)
  }
})

// 修改状态切换方法
const handleStatusToggle = async (cube: Cube) => {
  if (cube.model !== 'angle') return // 只有 Angel Cube 可以切换状态

  isUpdatingStatus.value = true
  statusUpdateError.value = ''
  showStatusError.value = false

  try {
    const currentStatus = cube.status
    const action = currentStatus === 'ACTIVE' ? 'STOP' : 'START'

    console.log(`尝试${action === 'START' ? '启动' : '停止'} Angel Cube: ${cube.id}`)

    const response = await updateAngelCubeStatus(cube.id, action)

    // 检查响应是否成功
    if (response.code === 200 && response.data?.success) {
      // 操作成功，更新本地状态
      cube.status = response.data.newStatus

      // 显示成功消息（根据你使用的UI库调整）
      console.log('✅ 状态更新成功:', response.data.message)
      // ElMessage.success(response.data.message) // 如果使用 Element Plus

      // 可以添加成功提示的其他方式
      showSuccessNotification(response.data.message)

    } else {
      // 操作失败
      const errorMsg = response.message || '状态更新失败'
      statusUpdateError.value = errorMsg
      showStatusError.value = true

      console.error('❌ 状态更新失败:', errorMsg)
      // ElMessage.error(errorMsg) // 如果使用 Element Plus

      showErrorNotification(errorMsg)
    }

  } catch (error: any) {
    // 网络错误或其他异常
    const errorMessage = error?.response?.data?.message || error?.message || '网络错误，请重试'
    statusUpdateError.value = errorMessage
    showStatusError.value = true

    console.error('❌ 状态更新异常:', error)
    // ElMessage.error(errorMessage) // 如果使用 Element Plus

    showErrorNotification(errorMessage)
  } finally {
    isUpdatingStatus.value = false

    // 3秒后隐藏错误提示
    if (showStatusError.value) {
      setTimeout(() => {
        showStatusError.value = false
      }, 3000)
    }
  }
}

// 成功提示函数（根据你的UI框架调整）
const showSuccessNotification = (message: string) => {
  // 这里可以实现你自己的成功提示逻辑
  // 比如使用 Vuetify 的 snackbar 或其他组件
  console.log('🎉', message)
}

// 错误提示函数（根据你的UI框架调整）
const showErrorNotification = (message: string) => {
  // 这里可以实现你自己的错误提示逻辑
  console.error('💥', message)
}

// 计算属性：格式化内存使用量
const formattedMemory = computed(() => {
  const bytes = props.cube.statistics?.memoryUsage || 0
  if (bytes < 1024) return `${bytes}B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)}KB`
  return `${(bytes / (1024 * 1024)).toFixed(1)}MB`
})

// 计算属性：获取状态样式类
const statusClass = computed(() => {
  return props.cube.status?.toLowerCase() || 'unknown'
})

// 计算属性：获取模式样式类
const modelClass = computed(() => {
  return props.cube.model?.toLowerCase() || 'default'
})

// 获取模式图标
const getModelIcon = (model: string) => {
  const iconMap = {
    angle: 'minecraft-totem',
    default: 'minecraft-grass',
  }
  return iconMap[model as keyof typeof iconMap] || 'mdi-package-variant'
}

// 获取状态图标
const getStatusIcon = (status: string) => {
  return status === 'ACTIVE' ? 'mdi-check-circle' : 'mdi-pause-circle'
}

onMounted(() => {
  // 组件挂载后的初始化逻辑
})
</script>

<template>
  <div class="cube-card-container">
    <v-card
        width="100%"
        class="desert-cube-card"
        :class="[
        `desert-cube-card--${statusClass}`,
        `desert-cube-card--${modelClass}`,
        { 'desert-cube-card--hovered': isHovered }
      ]"
        @mouseenter="isHovered = true"
        @mouseleave="isHovered = false"
        elevation="0"
    >
      <!-- 错误提示横幅 -->
      <v-alert
          v-if="showStatusError"
          type="error"
          class="status-error-alert"
          closable
          @click:close="showStatusError = false"
      >
        <strong>状态更新失败:</strong> {{ statusUpdateError }}
      </v-alert>

      <!-- 简化呼吸光环 -->
      <div
          v-if="statusClass === 'active'"
          class="breathing-aura"
      ></div>

      <!-- 卡片状态指示条 -->
      <div class="status-indicator" :class="`status-indicator--${statusClass}`"></div>

      <!-- 卡片头部 -->
      <template v-slot:title>
        <div class="cube-header">
          <div class="cube-icon-wrapper">
            <img
                v-if="props.cube.model === 'angle'"
                :src="totemIconUrl"
                alt="Minecraft Totem"
                class="minecraft-icon minecraft-totem"
                width="70"
                height="64"
            />
            <img
                v-else-if="props.cube.model === 'default'"
                :src="grassIconUrl"
                alt="Minecraft Grass"
                class="minecraft-icon minecraft-grass"
                width="70"
                height="64"
            />
            <v-icon
                v-else
                :icon="getModelIcon(props.cube.model)"
                size="36"
            />
            <div class="status-badge" :class="`status-badge--${statusClass}`">
              <!-- 显示加载状态或正常状态图标 -->
              <v-progress-circular
                  v-if="isUpdatingStatus"
                  indeterminate
                  size="20"
                  width="2"
                  color="white"
              ></v-progress-circular>
              <v-icon
                  v-else
                  :icon="getStatusIcon(props.cube.status)"
                  size="24"
              ></v-icon>
            </div>
          </div>

          <div class="cube-title-section">
            <div class="cube-name-row">
              <span class="cube-name">{{ props.cube.name }}</span>
              <div class="cube-badges">
                <v-chip
                    size="small"
                    class="version-chip"
                    :class="`version-chip--${modelClass}`"
                >
                  v{{ props.cube.version }}
                </v-chip>
                <v-chip
                    size="small"
                    class="model-chip"
                    :class="`model-chip--${modelClass}`"
                >
                  {{ props.cube.scope }}
                </v-chip>
              </div>
            </div>

            <div class="cube-id-section">
              <div class="cube-id-wrapper">
                <v-icon icon="mdi-tag-outline" size="18" class="id-icon"></v-icon>
                <span class="cube-id">{{ props.cube.id }}</span>
                <v-btn
                    icon="mdi-content-copy"
                    size="x-small"
                    variant="text"
                    class="copy-btn"
                    @click.stop="navigator.clipboard?.writeText(props.cube.id)"
                >
                </v-btn>
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- 卡片内容 -->
      <template v-slot:text>
        <div class="cube-content">
          <p class="cube-description">{{ props.cube.description }}</p>

          <div class="stats-grid">
            <div class="stat-item">
              <v-icon icon="mdi-package-variant" class="stat-icon"></v-icon>
              <div class="stat-content">
                <div class="stat-value">{{ props.cube.statistics?.totalExtensionPackages || 0 }}</div>
                <div class="stat-label">扩展包</div>
              </div>
            </div>

            <div class="stat-item">
              <v-icon icon="mdi-connection" class="stat-icon"></v-icon>
              <div class="stat-content">
                <div class="stat-value">{{ props.cube.statistics?.totalExtensionPoints || 0 }}</div>
                <div class="stat-label">扩展点</div>
              </div>
            </div>

            <div class="stat-item">
              <v-icon icon="mdi-memory" class="stat-icon"></v-icon>
              <div class="stat-content">
                <div class="stat-value">{{ formattedMemory }}</div>
                <div class="stat-label">内存</div>
              </div>
            </div>

            <div class="stat-item">
              <v-icon icon="mdi-speedometer" class="stat-icon"></v-icon>
              <div class="stat-content">
                <div class="stat-value">{{ (props.cube.statistics?.averagePriority || 0).toFixed(1) }}</div>
                <div class="stat-label">优先级</div>
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- 卡片操作区域 -->
      <v-card-actions class="cube-actions">
        <div class="action-buttons">
          <v-tooltip text="查看详情" location="top">
            <template v-slot:activator="{ props: tooltipProps }">
              <v-btn
                  v-bind="tooltipProps"
                  icon="mdi-information-outline"
                  class="details-btn icon-btn"
                  size="large"
                  @click="dialog = true"
              ></v-btn>
            </template>
          </v-tooltip>

          <v-tooltip text="插件配置" location="top">
            <template v-slot:activator="{ props: tooltipProps }">
              <v-btn
                  v-bind="tooltipProps"
                  icon="mdi-cog-outline"
                  class="config-btn icon-btn"
                  size="large"
                  @click="configDialog = true"
              ></v-btn>
            </template>
          </v-tooltip>
        </div>

        <!-- 使用拉杆组件 -->
        <div class="center-lever-area">
          <MCLeverSwitch
              v-if="props.cube.model === 'angle'"
              :status="props.cube.status"
              :is-loading="isUpdatingStatus"
              :disabled="isUpdatingStatus"
              @toggle="handleStatusToggle(props.cube)"
          />
        </div>

        <div class="cube-meta-info">
          <div class="meta-item">
            <v-icon icon="mdi-account-outline" size="16" class="meta-icon"></v-icon>
            <span class="meta-text">{{ props.cube.author || 'Unknown' }}</span>
          </div>
          <div class="meta-item">
            <v-icon icon="mdi-clock-outline" size="16" class="meta-icon"></v-icon>
            <span class="meta-text">{{ new Date(props.cube.lastUpdatedTime).toLocaleDateString('zh-CN') }}</span>
          </div>
        </div>
      </v-card-actions>
    </v-card>

    <!-- Details对话框组件 -->
    <CubeDetailsComponent v-model="dialog" :pluginData="props.cube"></CubeDetailsComponent>

    <!-- 配置对话框组件 -->
    <CubeConfigComponent v-model="configDialog" :pluginData="props.cube"></CubeConfigComponent>
  </div>
</template>

<style scoped lang="scss">
// 沙漠绿洲配色变量
$desert-sand:   #F4E4BC;        // 沙漠沙子色
$desert-dune:   #E6D3A3;        // 沙丘色
$oasis-green:   #4A9B8E;        // 绿洲绿色
$oasis-blue:    #6BB6B0;         // 绿洲蓝绿色
$palm-green:    #2D5A27;         // 棕榈绿
$sunset-orange: #D4A574;         // 日落橙
$clear-white:   #FEFEFE;         // 清爽白
$shadow-brown:  #8B7355;         // 阴影棕

// 圆角系统
$radius-large: 24px;          // 主卡片圆角
$radius-medium: 16px;         // 中等圆角
$radius-small: 12px;          // 小圆角
$radius-tiny: 8px;            // 微小圆角

.cube-card-container {
  margin-bottom: 1.5rem;
  position: relative;
}

.status-error-alert {
  margin: 1rem;
  border-radius: 12px !important;
  font-weight: 500;
}

// 加载状态下的状态徽章样式
.status-badge {
  &--loading {
    background: linear-gradient(135deg, #ff9800, #f57c00) !important;

    .v-progress-circular {
      color: white !important;
    }
  }
}

// 禁用状态下的拉杆样式
.center-lever-area {
  &--disabled {
    opacity: 0.7;
    pointer-events: none;
  }
}

.desert-cube-card {
  position: relative;
  // 增强立体背景层次
  background:
      linear-gradient(135deg,
          rgba(244, 228, 188, 0.25) 0%,
          rgba(230, 211, 163, 0.20) 20%,
          rgba(244, 228, 188, 0.15) 40%,
          rgba(212, 165, 116, 0.18) 60%,
          rgba(244, 228, 188, 0.12) 80%,
          rgba(230, 211, 163, 0.15) 100%) !important;

  // 增强立体效果
  backdrop-filter: blur(12px);
  border: 1px solid rgba(244, 228, 188, 0.45) !important;
  border-radius: $radius-large !important;
  overflow: visible;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);

  // 多层立体阴影系统
  box-shadow:
      0 20px 60px rgba(139, 115, 85, 0.12),
      0 12px 35px rgba(139, 115, 85, 0.08),
      0 6px 18px rgba(139, 115, 85, 0.06),
      0 2px 8px rgba(139, 115, 85, 0.04),
      inset 0 2px 0 rgba(255, 255, 255, 0.25),
      inset 0 -2px 0 rgba(139, 115, 85, 0.08),
      inset 0 0 1px rgba(255, 255, 255, 0.3) !important;

  // 多层边框光晕效果
  &::before {
    content: '';
    position: absolute;
    top: -2px;
    left: -2px;
    right: -2px;
    bottom: -2px;
    background: linear-gradient(135deg,
        rgba(244, 228, 188, 0.6),
        rgba(74, 155, 142, 0.4),
        rgba(244, 228, 188, 0.5),
        rgba(74, 155, 142, 0.3),
        rgba(244, 228, 188, 0.6));
    border-radius: $radius-large;
    z-index: -1;
    opacity: 0;
    transition: opacity 0.4s ease;
    filter: blur(1px);
  }

  // 添加第二层外光晕
  &::after {
    content: '';
    position: absolute;
    top: -6px;
    left: -6px;
    right: -6px;
    bottom: -6px;
    background: radial-gradient(ellipse at center,
        rgba(74, 155, 142, 0.15) 0%,
        rgba(244, 228, 188, 0.1) 40%,
        transparent 70%);
    border-radius: $radius-large;
    z-index: -2;
    opacity: 0;
    transition: opacity 0.4s ease;
    filter: blur(4px);
  }

  &:hover,
  &--hovered {
    transform: translateY(-12px) scale(1.015);

    // 悬浮时的增强立体阴影
    box-shadow:
        0 35px 80px rgba(139, 115, 85, 0.18),
        0 20px 50px rgba(139, 115, 85, 0.12),
        0 12px 30px rgba(139, 115, 85, 0.08),
        0 6px 15px rgba(139, 115, 85, 0.06),
        0 2px 8px rgba(139, 115, 85, 0.04),
        inset 0 3px 0 rgba(255, 255, 255, 0.35),
        inset 0 -3px 0 rgba(139, 115, 85, 0.12),
        inset 0 0 2px rgba(255, 255, 255, 0.4) !important;

    &::before {
      opacity: 1;
    }

    &::after {
      opacity: 1;
    }

    .icon-btn {
      transform: scale(1.12) rotate(3deg);
    }

    .cube-id-wrapper {
      transform: scale(1.02);
      box-shadow:
          0 6px 20px rgba(139, 115, 85, 0.2),
          inset 0 1px 0 rgba(255, 255, 255, 0.9);
    }

    .stats-grid {
      transform: translateY(-2px);
      box-shadow:
          0 8px 25px rgba(139, 115, 85, 0.15),
          inset 0 2px 0 rgba(255, 255, 255, 0.9),
          inset 0 -1px 0 rgba(139, 115, 85, 0.08);
    }
  }

  // ===== 优雅的 ACTIVE 状态呼吸效果 =====
  &--active {
    // ACTIVE状态的增强背景
    background:
        linear-gradient(145deg,
            rgba(248, 252, 250, 0.28) 0%,
            rgba(245, 251, 248, 0.22) 25%,
            rgba(242, 250, 246, 0.18) 50%,
            rgba(245, 251, 248, 0.20) 75%,
            rgba(248, 252, 250, 0.25) 100%) !important;

    // 温和的呼吸动画
    animation: gentleBreathe 4.5s ease-in-out infinite;

    &:hover,
    &.desert-cube-card--hovered {
      transform: translateY(-15px) scale(1.02);
      animation: gentleBreatheFast 3.5s ease-in-out infinite;
    }

    // 更柔和的内容动画
    .cube-header {
      animation: subtleHeaderPulse 4.5s ease-in-out infinite;
    }

    .stats-grid {
      animation: softStatsBreathing 4.5s ease-in-out infinite;
    }

    .status-badge--active {
      animation: gentleBadgePulse 3s ease-in-out infinite;
    }
  }

  // INACTIVE 状态保持简洁
  &--inactive {
    opacity: 0.92;

    &::before {
      background: linear-gradient(135deg,
          rgba(212, 165, 116, 0.5),
          rgba(230, 183, 135, 0.4),
          rgba(212, 165, 116, 0.5));
    }
  }

  // 模式变体
  &--singleton {
    .model-icon--singleton {
      color: $oasis-blue !important;
    }
  }

  &--property {
    .model-icon--property {
      color: $oasis-green !important;
    }
  }

  &--prototype {
    .model-icon--prototype {
      color: $palm-green !important;
    }
  }
}

// ===== 简化优雅的呼吸光环 =====
.breathing-aura {
  position: absolute;
  top: -15px;
  left: -15px;
  right: -15px;
  bottom: -15px;
  border-radius: $radius-large;
  pointer-events: none;
  z-index: -3;
  background: radial-gradient(circle,
      rgba(74, 155, 142, 0.12) 0%,
      rgba(107, 182, 176, 0.08) 40%,
      rgba(74, 155, 142, 0.05) 70%,
      transparent 100%);
  animation: simpleBreathingAura 4.5s ease-in-out infinite;
  filter: blur(8px);
}

// ===== 增强的呼吸动画 =====
@keyframes gentleBreathe {
  0%, 100% {
    box-shadow:
        0 22px 65px rgba(74, 155, 142, 0.12),
        0 12px 35px rgba(74, 155, 142, 0.08),
        0 6px 18px rgba(74, 155, 142, 0.06),
        0 2px 8px rgba(74, 155, 142, 0.04),
        inset 0 2px 0 rgba(255, 255, 255, 0.25),
        inset 0 -2px 0 rgba(139, 115, 85, 0.08),
        0 0 0 1px rgba(74, 155, 142, 0.15),
        0 0 15px rgba(74, 155, 142, 0.06);
    filter: brightness(1) saturate(1);
    transform: scale(1);
  }
  50% {
    box-shadow:
        0 32px 85px rgba(74, 155, 142, 0.18),
        0 18px 50px rgba(74, 155, 142, 0.12),
        0 10px 28px rgba(74, 155, 142, 0.08),
        0 4px 15px rgba(74, 155, 142, 0.06),
        inset 0 3px 0 rgba(255, 255, 255, 0.3),
        inset 0 -3px 0 rgba(139, 115, 85, 0.1),
        0 0 0 2px rgba(74, 155, 142, 0.25),
        0 0 25px rgba(74, 155, 142, 0.1),
        0 0 40px rgba(74, 155, 142, 0.08);
    filter: brightness(1.03) saturate(1.12);
    transform: scale(1.01);
  }
}

@keyframes gentleBreatheFast {
  0%, 100% {
    box-shadow:
        0 30px 75px rgba(74, 155, 142, 0.15),
        0 18px 45px rgba(74, 155, 142, 0.1),
        0 8px 22px rgba(74, 155, 142, 0.07),
        0 3px 12px rgba(74, 155, 142, 0.05),
        inset 0 3px 0 rgba(255, 255, 255, 0.3),
        inset 0 -3px 0 rgba(139, 115, 85, 0.09),
        0 0 0 2px rgba(74, 155, 142, 0.2),
        0 0 20px rgba(74, 155, 142, 0.08);
    filter: brightness(1.02) saturate(1.05);
    transform: translateY(-15px) scale(1.02);
  }
  50% {
    box-shadow:
        0 45px 95px rgba(74, 155, 142, 0.22),
        0 25px 60px rgba(74, 155, 142, 0.15),
        0 12px 35px rgba(74, 155, 142, 0.1),
        0 6px 20px rgba(74, 155, 142, 0.07),
        inset 0 4px 0 rgba(255, 255, 255, 0.35),
        inset 0 -4px 0 rgba(139, 115, 85, 0.12),
        0 0 0 3px rgba(74, 155, 142, 0.3),
        0 0 30px rgba(74, 155, 142, 0.12),
        0 0 50px rgba(74, 155, 142, 0.1);
    filter: brightness(1.05) saturate(1.1);
    transform: translateY(-15px) scale(1.06);
  }
}

@keyframes simpleBreathingAura {
  0%, 100% {
    opacity: 0.4;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.08);
  }
}

@keyframes subtleHeaderPulse {
  0%, 100% {
    filter: brightness(1) saturate(1);
    transform: scale(1);
  }
  50% {
    filter: brightness(1.03) saturate(1.05);
    transform: scale(1.005);
  }
}

@keyframes softStatsBreathing {
  0%, 100% {
    background: linear-gradient(135deg,
        rgba(255, 255, 255, 0.9),
        rgba(245, 252, 248, 0.85));
    box-shadow:
        0 6px 22px rgba(74, 155, 142, 0.1),
        inset 0 2px 0 rgba(255, 255, 255, 0.9),
        inset 0 -1px 0 rgba(139, 115, 85, 0.06);
    transform: scale(1);
  }
  50% {
    background: linear-gradient(135deg,
        rgba(255, 255, 255, 0.95),
        rgba(248, 254, 251, 0.9));
    box-shadow:
        0 8px 28px rgba(74, 155, 142, 0.15),
        inset 0 3px 0 rgba(255, 255, 255, 0.95),
        inset 0 -2px 0 rgba(139, 115, 85, 0.08),
        0 0 20px rgba(74, 155, 142, 0.06);
    transform: scale(1.005);
  }
}

@keyframes gentleBadgePulse {
  0%, 100% {
    box-shadow:
        0 4px 15px rgba(74, 155, 142, 0.3),
        0 0 20px rgba(74, 155, 142, 0.12);
    transform: scale(1);
  }
  50% {
    box-shadow:
        0 6px 22px rgba(74, 155, 142, 0.4),
        0 0 30px rgba(74, 155, 142, 0.18),
        0 0 45px rgba(74, 155, 142, 0.1);
    transform: scale(1.08);
  }
}

// 状态指示器优化
.status-indicator {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 5px;
  z-index: 2;
  border-radius: $radius-large $radius-large 0 0;

  &--active {
    background: linear-gradient(90deg, $oasis-green, $oasis-blue);
    height: 6px;
    animation: gentleStatusIndicatorBreathe 4.5s ease-in-out infinite;
    box-shadow:
        0 2px 8px rgba(74, 155, 142, 0.3),
        inset 0 1px 0 rgba(255, 255, 255, 0.4);

    // 更柔和的流动光效
    &::after {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg,
          transparent,
          rgba(255, 255, 255, 0.7),
          transparent);
      animation: subtleFlowingLight 6s ease-in-out infinite;
    }
  }

  &--inactive {
    background: linear-gradient(90deg, $sunset-orange, #E6B887);
    box-shadow:
        0 2px 8px rgba(212, 165, 116, 0.3),
        inset 0 1px 0 rgba(255, 255, 255, 0.3);
  }
}

@keyframes gentleStatusIndicatorBreathe {
  0%, 100% {
    box-shadow:
        0 2px 8px rgba(74, 155, 142, 0.3),
        inset 0 1px 0 rgba(255, 255, 255, 0.4);
    height: 6px;
  }
  50% {
    box-shadow:
        0 3px 12px rgba(74, 155, 142, 0.4),
        0 0 18px rgba(74, 155, 142, 0.25),
        inset 0 1px 0 rgba(255, 255, 255, 0.5);
    height: 7px;
  }
}

@keyframes subtleFlowingLight {
  0%, 100% {
    left: -100%;
    opacity: 0;
  }
  10% {
    opacity: 0.7;
  }
  90% {
    opacity: 0.7;
  }
  100% {
    left: 100%;
    opacity: 0;
  }
}

// 状态徽章优化
.status-badge {
  position: absolute;
  top: -10px;
  right: -10px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 5px solid $clear-white;
  box-shadow:
      0 4px 12px rgba(0, 0, 0, 0.2),
      inset 0 1px 0 rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;

  &--active {
    background: linear-gradient(135deg, $oasis-green, $oasis-blue);
    color: white;
    box-shadow:
        0 5px 18px rgba(74, 155, 142, 0.3),
        0 0 20px rgba(74, 155, 142, 0.15),
        inset 0 1px 0 rgba(255, 255, 255, 0.3);
  }

  &--inactive {
    background: linear-gradient(135deg, $sunset-orange, #E6B887);
    color: white;
    box-shadow:
        0 5px 18px rgba(212, 165, 116, 0.3),
        0 0 20px rgba(212, 165, 116, 0.15),
        inset 0 1px 0 rgba(255, 255, 255, 0.3);
  }
}

.cube-header {
  display: flex;
  align-items: flex-start;
  gap: 1.5rem;
  padding: 1rem 0;
}

.cube-icon-wrapper {
  position: relative;
  flex-shrink: 0;
  background:
      linear-gradient(135deg,
          rgba(255, 255, 255, 0.98) 0%,
          rgba(255, 255, 255, 0.92) 100%);
  border-radius: $radius-medium;
  padding: 0.8rem;
  box-shadow:
      0 6px 20px rgba(139, 115, 85, 0.18),
      0 2px 8px rgba(139, 115, 85, 0.1),
      inset 0 2px 0 rgba(255, 255, 255, 0.9),
      inset 0 -1px 0 rgba(139, 115, 85, 0.05);
  transition: all 0.4s ease;

  &:hover {
    box-shadow:
        0 8px 25px rgba(139, 115, 85, 0.25),
        0 4px 12px rgba(139, 115, 85, 0.15),
        inset 0 3px 0 rgba(255, 255, 255, 0.95),
        inset 0 -2px 0 rgba(139, 115, 85, 0.08);
    transform: translateY(-3px) rotate(-2deg) scale(1.02);
  }
}

.cube-title-section {
  flex: 1;
  min-width: 0;
}

.cube-name-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1.25rem;
  margin-bottom: 1rem;
}

.cube-name {
  font-size: 1.4rem !important;
  font-weight: 700 !important;
  color: $shadow-brown !important;
  line-height: 1.3;
  flex: 1;
  min-width: 0;
  letter-spacing: -0.025em;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.5);
}

.cube-badges {
  display: flex;
  gap: 0.75rem;
  flex-shrink: 0;
}

.version-chip {
  font-weight: 600 !important;
  border-radius: $radius-small !important;
  box-shadow: 0 2px 6px rgba(139, 115, 85, 0.15) !important;

  &--singleton {
    background: rgba(107, 182, 176, 0.25) !important;
    color: $oasis-blue !important;
  }

  &--property {
    background: rgba(74, 155, 142, 0.25) !important;
    color: $oasis-green !important;
  }

  &--prototype {
    background: rgba(45, 90, 39, 0.25) !important;
    color: $palm-green !important;
  }
}

.model-chip {
  font-weight: 500 !important;
  border-radius: $radius-small !important;
  box-shadow: 0 2px 6px rgba(139, 115, 85, 0.12) !important;

  &--singleton {
    background: rgba(107, 182, 176, 0.18) !important;
    color: $oasis-blue !important;
  }

  &--property {
    background: rgba(74, 155, 142, 0.18) !important;
    color: $oasis-green !important;
  }

  &--prototype {
    background: rgba(45, 90, 39, 0.18) !important;
    color: $palm-green !important;
  }
}

.cube-id-section {
  margin-top: 0.5rem;
}

.cube-id-wrapper {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  background:
      linear-gradient(135deg,
          rgba(255, 255, 255, 0.95) 0%,
          rgba(244, 228, 188, 0.85) 100%);
  padding: 0.8rem 1.1rem;
  border-radius: $radius-small;
  border: 2px solid rgba(230, 211, 163, 0.7);
  box-shadow:
      0 4px 12px rgba(139, 115, 85, 0.15),
      inset 0 2px 0 rgba(255, 255, 255, 0.9),
      inset 0 -1px 0 rgba(139, 115, 85, 0.05);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.5), transparent);
    transition: left 0.6s ease;
  }

  &:hover {
    background:
        linear-gradient(135deg,
            rgba(255, 255, 255, 1) 0%,
            rgba(244, 228, 188, 0.95) 100%);
    border-color: $oasis-green;
    box-shadow:
        0 6px 18px rgba(139, 115, 85, 0.2),
        inset 0 3px 0 rgba(255, 255, 255, 0.95),
        inset 0 -2px 0 rgba(139, 115, 85, 0.08);
    transform: scale(1.02);

    &::before {
      left: 100%;
    }

    .copy-btn {
      opacity: 1;
      transform: scale(1);
    }
  }
}

.id-icon {
  color: $oasis-green !important;
  flex-shrink: 0;
}

.cube-id {
  font-family: 'JetBrains Mono', 'Fira Code', 'SF Mono', 'Monaco', 'Inconsolata', 'Roboto Mono', 'Source Code Pro', 'Consolas', monospace;
  font-size: 1rem;
  font-weight: 600;
  color: $shadow-brown;
  background: transparent;
  border: none;
  padding: 0;
  letter-spacing: 0.05em;
  flex: 1;
  min-width: 0;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
  line-height: 1.4;
}

.copy-btn {
  opacity: 0;
  transform: scale(0.8);
  transition: all 0.3s ease;
  color: rgba(139, 115, 85, 0.6) !important;

  &:hover {
    color: $oasis-green !important;
    background: rgba(74, 155, 142, 0.1) !important;
  }
}

.cube-content {
  padding: 1rem 0;
}

.cube-description {
  font-size: 1rem !important;
  color: rgba(139, 115, 85, 0.85) !important;
  line-height: 1.7 !important;
  margin-bottom: 2rem !important;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-weight: 400;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
  background:
      linear-gradient(135deg,
          rgba(255, 255, 255, 0.88) 0%,
          rgba(244, 228, 188, 0.7) 100%);
  padding: 1.8rem;
  border-radius: $radius-medium;
  border: 2px solid rgba(230, 211, 163, 0.6);
  box-shadow:
      0 6px 18px rgba(139, 115, 85, 0.12),
      inset 0 2px 0 rgba(255, 255, 255, 0.9),
      inset 0 -1px 0 rgba(139, 115, 85, 0.05);
  transition: all 0.3s ease;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  text-align: center;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-3px);

    .stat-icon {
      transform: scale(1.12) rotate(5deg);
      box-shadow: 0 6px 18px rgba(74, 155, 142, 0.25);
    }
  }
}

.stat-icon {
  color: $oasis-green !important;
  background:
      linear-gradient(135deg,
          rgba(74, 155, 142, 0.18) 0%,
          rgba(74, 155, 142, 0.10) 100%);
  padding: 0.9rem;
  border-radius: $radius-small;
  flex-shrink: 0;
  transition: all 0.3s ease;
  box-shadow:
      0 4px 12px rgba(74, 155, 142, 0.18),
      inset 0 1px 0 rgba(255, 255, 255, 0.3);
}

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: 1.2rem;
  font-weight: 700;
  color: $shadow-brown;
  line-height: 1.2;
  letter-spacing: -0.02em;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.5);
}

.stat-label {
  font-size: 0.85rem;
  color: rgba(139, 115, 85, 0.75);
  margin-top: 0.25rem;
  font-weight: 500;
}

.cube-actions {
  background:
      linear-gradient(135deg,
          rgba(244, 228, 188, 0.35) 0%,
          rgba(230, 211, 163, 0.25) 100%) !important;
  border-top: 2px solid rgba(230, 211, 163, 0.5) !important;
  padding: 2.8rem 2.2rem !important;
  border-radius: 0 0 $radius-large $radius-large;
  min-height: 220px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  backdrop-filter: blur(6px);
  box-shadow: inset 0 2px 0 rgba(255, 255, 255, 0.2) !important;
}

.action-buttons {
  display: flex;
  gap: 1.25rem;
  flex-shrink: 0;
}

.icon-btn {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  border-radius: $radius-medium !important;

  &:hover {
    transform: translateY(-4px) scale(1.12);
  }

  &:active {
    transform: translateY(-2px) scale(1.05);
  }
}

.details-btn {
  background: linear-gradient(135deg, $oasis-green, $oasis-blue) !important;
  color: white !important;
  box-shadow:
      0 6px 20px rgba(74, 155, 142, 0.35),
      inset 0 1px 0 rgba(255, 255, 255, 0.3) !important;

  &:hover {
    box-shadow:
        0 8px 25px rgba(74, 155, 142, 0.45),
        inset 0 1px 0 rgba(255, 255, 255, 0.4) !important;
  }
}

.config-btn {
  border-color: rgba(139, 115, 85, 0.6) !important;
  color: $shadow-brown !important;
  background: rgba(139, 115, 85, 0.12) !important;
  box-shadow: 0 4px 12px rgba(139, 115, 85, 0.15) !important;

  &:hover {
    background: rgba(139, 115, 85, 0.18) !important;
    border-color: rgba(139, 115, 85, 0.8) !important;
    box-shadow: 0 8px 22px rgba(139, 115, 85, 0.25) !important;
  }
}

.cube-meta-info {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  text-align: right;
  flex-shrink: 0;
  min-width: 120px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  justify-content: flex-end;
  transition: all 0.3s ease;

  &:hover {
    transform: translateX(-3px);

    .meta-icon {
      color: $oasis-green !important;
    }
  }
}

.meta-icon {
  color: rgba(139, 115, 85, 0.6) !important;
  transition: color 0.3s ease;
}

.meta-text {
  font-size: 0.85rem;
  color: rgba(139, 115, 85, 0.85);
  font-weight: 500;
  letter-spacing: 0.02em;
}

// 响应式设计
@media (max-width: 768px) {
  .desert-cube-card {
    border-radius: $radius-medium !important;

    &::before {
      border-radius: $radius-medium;
    }
  }

  .breathing-aura {
    top: -12px;
    left: -12px;
    right: -12px;
    bottom: -12px;
  }

  .cube-name-row {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }

  .cube-name {
    font-size: 1.25rem !important;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 1.25rem;
    padding: 1.5rem;
    border-radius: $radius-small;
  }

  .cube-actions {
    flex-direction: column;
    gap: 1.5rem;
    align-items: stretch;
    padding: 1.5rem 1.8rem !important;
    border-radius: 0 0 $radius-medium $radius-medium;
  }

  .action-buttons {
    width: 100%;
    justify-content: center;
  }

  .cube-meta-info {
    text-align: center;
  }

  .meta-item {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .desert-cube-card {
    border-radius: $radius-small !important;

    &::before {
      border-radius: $radius-small;
    }
  }

  .breathing-aura {
    top: -10px;
    left: -10px;
    right: -10px;
    bottom: -10px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
    border-radius: $radius-tiny;
  }

  .action-buttons {
    flex-direction: row;
    justify-content: space-around;
  }

  .cube-header {
    gap: 1.25rem;
  }

  .cube-icon-wrapper {
    padding: 1rem;
    border-radius: $radius-small;
  }

  .cube-actions {
    border-radius: 0 0 $radius-small $radius-small;
  }
}
</style>