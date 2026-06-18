<template>
  <div v-loading="loading">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon blue"><el-icon :size="40"><Box /></el-icon></div>
            <div class="stat-info">
              <div class="stat-number">{{ overview.totalEquipment || 0 }}</div>
              <div class="stat-label">器具总量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon green"><el-icon :size="40"><Check /></el-icon></div>
            <div class="stat-info">
              <div class="stat-number">{{ overview.totalInStock || 0 }}</div>
              <div class="stat-label">在库数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon orange"><el-icon :size="40"><Truck /></el-icon></div>
            <div class="stat-info">
              <div class="stat-number">{{ overview.totalOutStock || 0 }}</div>
              <div class="stat-label">在外数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon red"><el-icon :size="40"><Warning /></el-icon></div>
            <div class="stat-info">
              <div class="stat-number">{{ overview.totalOverdue || 0 }}</div>
              <div class="stat-label">超期未还</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 20px">
      <template #header>
        <el-row :gutter="16" align="middle">
          <el-col :span="6">
            <span style="font-weight: 600">合作方详细统计</span>
          </el-col>
          <el-col :span="6">
            <el-select v-model="selectedPartnerId" placeholder="请选择合作方" clearable style="width: 100%" @change="fetchPartnerStats">
              <el-option label="全部合作方" :value="null" />
              <el-option v-for="item in partnerList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-col>
          <el-col :span="12" style="text-align: right">
            <el-button type="primary" @click="handleExport">
              <el-icon><Download /></el-icon>导出
            </el-button>
          </el-col>
        </el-row>
      </template>
      <el-table
        :data="partnerStatsList"
        stripe
        v-loading="statsLoading"
        style="width: 100%"
        show-summary
        :summary-method="getSummaries"
      >
        <el-table-column prop="partnerName" label="合作方名称" min-width="120" />
        <el-table-column prop="typeName" label="器具类型" min-width="100" />
        <el-table-column prop="dispatched" label="累计发出" min-width="90" />
        <el-table-column prop="returned" label="累计回收" min-width="90" />
        <el-table-column prop="damaged" label="损坏数量" min-width="90" />
        <el-table-column label="在外结存" min-width="90">
          <template #default="{ row }">
            <el-tag :type="getOutstandingTagType(row.outstanding)">
              {{ row.outstanding }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="turnoverCount" label="周转次数" min-width="90" />
        <el-table-column label="损耗率(%)" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getLossRateTagType(row.lossRate)">
              {{ row.lossRate != null ? row.lossRate : '0.00' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOverview, getPartnerStats, getAllPartnerStats } from '@/api/statistics'
import { list as getPartnerList } from '@/api/partner'

const loading = ref(false)
const statsLoading = ref(false)
const overview = ref({})
const partnerStatsList = ref([])
const partnerList = ref([])
const selectedPartnerId = ref(null)

const getOutstandingTagType = (outstanding) => {
  if (outstanding > 100) return 'warning'
  return 'success'
}

const getLossRateTagType = (lossRate) => {
  const rate = Number(lossRate) || 0
  if (rate > 10) return 'danger'
  if (rate > 5) return 'warning'
  return 'success'
}

const fetchOverview = async () => {
  loading.value = true
  try {
    const res = await getOverview()
    overview.value = res || {}
  } finally {
    loading.value = false
  }
}

const fetchPartnerList = async () => {
  const res = await getPartnerList()
  partnerList.value = res || []
}

const fetchPartnerStats = async () => {
  statsLoading.value = true
  try {
    if (selectedPartnerId.value) {
      const res = await getPartnerStats(selectedPartnerId.value)
      partnerStatsList.value = res || []
    } else {
      const res = await getAllPartnerStats()
      partnerStatsList.value = res || []
    }
  } finally {
    statsLoading.value = false
  }
}

const handleExport = () => {
  ElMessage.info('导出功能开发中')
}

const getSummaries = ({ columns, data }) => {
  const sums = []
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '合计'
      return
    }
    if (['dispatched', 'returned', 'damaged', 'outstanding'].includes(column.property)) {
      const values = data.map(item => Number(item[column.property]) || 0)
      if (!values.every(value => isNaN(value))) {
        sums[index] = values.reduce((prev, curr) => prev + curr, 0)
      } else {
        sums[index] = '-'
      }
      return
    }
    sums[index] = '-'
  })
  return sums
}

onMounted(async () => {
  await fetchPartnerList()
  fetchOverview()
  fetchPartnerStats()
})
</script>

<style scoped>
.stat-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 70px;
  height: 70px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-icon.blue {
  background: linear-gradient(135deg, #409eff, #79bbff);
}

.stat-icon.green {
  background: linear-gradient(135deg, #67c23a, #95d475);
}

.stat-icon.orange {
  background: linear-gradient(135deg, #e6a23c, #eebe77);
}

.stat-icon.red {
  background: linear-gradient(135deg, #f56c6c, #f89898);
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}
</style>
