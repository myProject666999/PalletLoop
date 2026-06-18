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

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span style="font-weight: 600">合作方器具结存</span>
          </template>
          <el-table :data="partnerStatsList" stripe style="width: 100%" max-height="400">
            <el-table-column prop="partnerName" label="合作方" min-width="100" />
            <el-table-column prop="typeName" label="器具类型" min-width="100" />
            <el-table-column prop="dispatched" label="累计发出" min-width="80" />
            <el-table-column prop="returned" label="累计回收" min-width="80" />
            <el-table-column prop="outstanding" label="在外结存" min-width="80" />
            <el-table-column prop="damaged" label="损坏数量" min-width="80" />
            <el-table-column label="损耗率(%)" min-width="90">
              <template #default="{ row }">
                {{ row.lossRate != null ? row.lossRate : '0.00' }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span style="font-weight: 600">超期预警概览</span>
          </template>
          <el-table :data="overdueList" stripe style="width: 100%" max-height="400">
            <el-table-column prop="dispatchNo" label="发货单号" min-width="120" />
            <el-table-column prop="partnerName" label="合作方" min-width="100" />
            <el-table-column prop="typeName" label="器具类型" min-width="100" />
            <el-table-column prop="quantity" label="发出数量" min-width="80" />
            <el-table-column prop="overdueDays" label="超期天数" min-width="80">
              <template #default="{ row }">
                <el-tag type="danger">{{ row.overdueDays }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOverview, getAllPartnerStats } from '@/api/statistics'
import { getOverdue } from '@/api/alert'

const loading = ref(false)
const overview = ref({})
const partnerStatsList = ref([])
const overdueList = ref([])

const fetchData = async () => {
  loading.value = true
  try {
    const [overviewData, statsData, overdueData] = await Promise.all([
      getOverview(),
      getAllPartnerStats(),
      getOverdue()
    ])
    overview.value = overviewData || {}
    partnerStatsList.value = statsData || []
    overdueList.value = (overdueData || []).slice(0, 5)
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
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
