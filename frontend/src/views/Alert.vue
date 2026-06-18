<template>
  <div>
    <el-card shadow="never">
      <el-row :gutter="16" align="middle">
        <el-col :span="6">
          <el-select v-model="selectedPartnerId" placeholder="请选择合作方" clearable style="width: 100%" @change="fetchData">
            <el-option label="全部合作方" :value="null" />
            <el-option v-for="item in partnerList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="fetchData">
            <el-icon><Refresh /></el-icon>刷新
          </el-button>
        </el-col>
        <el-col :span="12" style="text-align: right">
          <el-badge :value="tableData.length" :max="999" class="overdue-badge">
            <el-tag type="danger" size="large">超期未还总数</el-tag>
          </el-badge>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never" style="margin-top: 16px">
      <el-table :data="tableData" stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="dispatchNo" label="发货单号" min-width="140" />
        <el-table-column label="合作方名称" min-width="120">
          <template #default="{ row }">
            {{ getPartnerName(row.partnerId) }}
          </template>
        </el-table-column>
        <el-table-column label="器具类型" min-width="100">
          <template #default="{ row }">
            {{ getTypeName(row.typeId) }}
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="发出数量" min-width="90" />
        <el-table-column prop="dispatchDate" label="发货日期" min-width="110" />
        <el-table-column prop="expectedReturnDate" label="预计回收日期" min-width="120" />
        <el-table-column label="超期天数" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getOverdueTagType(row.overdueDays)">
              {{ row.overdueDays }} 天
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="warning" link @click="handleRemind(row)">催还</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-alert
      title="预警说明"
      type="warning"
      :closable="false"
      style="margin-top: 16px"
      show-icon
    >
      <template #default>
        系统自动检测预计回收日期早于今日且尚未完全回收的发货记录。超期天数 = 今日日期 - 预计回收日期。
      </template>
    </el-alert>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOverdue, getByPartner } from '@/api/alert'
import { list as getPartnerList } from '@/api/partner'
import { list as getTypeList } from '@/api/equipmentType'

const loading = ref(false)
const tableData = ref([])
const partnerList = ref([])
const typeList = ref([])
const selectedPartnerId = ref(null)

const getPartnerName = (partnerId) => {
  const partner = partnerList.value.find(p => p.id === partnerId)
  return partner ? partner.name : '-'
}

const getTypeName = (typeId) => {
  const type = typeList.value.find(t => t.id === typeId)
  return type ? type.name : '-'
}

const getOverdueTagType = (days) => {
  if (days > 30) return 'danger'
  if (days > 15) return 'warning'
  return 'info'
}

const fetchPartnerList = async () => {
  const res = await getPartnerList()
  partnerList.value = res || []
}

const fetchTypeList = async () => {
  const res = await getTypeList()
  typeList.value = res || []
}

const fetchData = async () => {
  loading.value = true
  try {
    let res
    if (selectedPartnerId.value) {
      res = await getByPartner(selectedPartnerId.value)
    } else {
      res = await getOverdue()
    }
    tableData.value = res || []
  } finally {
    loading.value = false
  }
}

const handleRemind = (row) => {
  const partnerName = getPartnerName(row.partnerId)
  const typeName = getTypeName(row.typeId)
  ElMessageBox.confirm(
    `确认向【${partnerName}】发送催还通知？涉及器具：${typeName} x ${row.quantity}，已超期 ${row.overdueDays} 天`,
    '催还确认',
    {
      confirmButtonText: '确认发送',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('催还通知已发送')
  }).catch(() => {})
}

onMounted(() => {
  fetchPartnerList()
  fetchTypeList()
  fetchData()
})
</script>

<style scoped>
.overdue-badge :deep(.el-badge__content) {
  top: 8px;
}
</style>
