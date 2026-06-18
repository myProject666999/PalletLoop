<template>
  <el-container style="height: 100vh">
    <el-aside width="220px">
      <div class="logo-area">
        <el-icon :size="24"><Box /></el-icon>
        <span>器具循环管理</span>
      </div>
      <el-menu
        :default-active="route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataBoard /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/equipment-types">
          <el-icon><Menu /></el-icon>
          <span>器具类型</span>
        </el-menu-item>
        <el-menu-item index="/equipments">
          <el-icon><Box /></el-icon>
          <span>器具台账</span>
        </el-menu-item>
        <el-menu-item index="/partners">
          <el-icon><User /></el-icon>
          <span>合作方管理</span>
        </el-menu-item>
        <el-menu-item index="/dispatches">
          <el-icon><Van /></el-icon>
          <span>发货登记</span>
        </el-menu-item>
        <el-menu-item index="/returns">
          <el-icon><Finished /></el-icon>
          <span>回收登记</span>
        </el-menu-item>
        <el-menu-item index="/alerts">
          <el-icon><Bell /></el-icon>
          <span>超期预警</span>
        </el-menu-item>
        <el-menu-item index="/statistics">
          <el-icon><TrendCharts /></el-icon>
          <span>统计分析</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <span class="header-title">托盘与周转箱循环管理系统</span>
        <span class="header-user">
          <el-icon><UserFilled /></el-icon>
          管理员
        </span>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<style scoped>
.el-aside {
  background-color: #304156;
  overflow: hidden;
}

.logo-area {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.el-menu {
  border-right: none;
}

.el-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.header-user {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #606266;
}

.el-main {
  background-color: #f0f2f5;
  overflow-y: auto;
}
</style>
