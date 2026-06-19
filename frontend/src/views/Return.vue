<template>
  <div>
    <el-card shadow="never">
      <el-row :gutter="16" align="middle">
        <el-col :span="4">
          <el-select v-model="searchForm.partnerId" placeholder="请选择合作方" clearable style="width: 100%">
            <el-option v-for="item in partnerList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.typeId" placeholder="请选择器具类型" clearable style="width: 100%">
            <el-option v-for="item in typeList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-input v-model="searchForm.returnNo" placeholder="请输入回收单号" clearable />
        </el-col>
        <el-col :span="10">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never" style="margin-top: 16px">
      <el-table :data="tableData" stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="returnNo" label="回收单号" min-width="140" />
        <el-table-column prop="dispatchNo" label="关联发货单号" min-width="140" />
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
        <el-table-column label="器具批次" min-width="120">
          <template #default="{ row }">
            {{ getEquipmentBatchNo(row.equipmentId) }}
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="回收数量" min-width="90" />
        <el-table-column prop="damaged" label="损坏数量" min-width="90" />
        <el-table-column prop="returnDate" label="回收日期" min-width="110" />
        <el-table-column prop="operator" label="操作人" min-width="100" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; display: flex; justify-content: flex-end">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <el-dialog v-model="addDialogVisible" title="新增回收" width="600px" destroy-on-close>
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="110px">
        <el-form-item label="关联发货单" prop="dispatchId">
          <el-select v-model="addForm.dispatchId" placeholder="请选择发货单" style="width: 100%" filterable @change="handleDispatchChange">
            <el-option v-for="item in dispatchList" :key="item.id" :label="getDispatchLabel(item)" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="合作方" prop="partnerId">
          <el-select v-model="addForm.partnerId" placeholder="请选择合作方" style="width: 100%" disabled>
            <el-option v-for="item in partnerList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="器具类型" prop="typeId">
          <el-select v-model="addForm.typeId" placeholder="请选择器具类型" style="width: 100%" disabled>
            <el-option v-for="item in typeList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="器具批次" prop="equipmentId">
          <el-select v-model="addForm.equipmentId" placeholder="请选择器具批次" style="width: 100%" disabled>
            <el-option v-for="item in equipmentList" :key="item.id" :label="item.batchNo" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="可回收数量">
          <el-tag type="info" effect="plain">最大可回收数量：{{ addForm.dispatchId ? maxReturnable : '请先选择发货单' }}</el-tag>
        </el-form-item>
        <el-form-item label="回收数量" prop="quantity" v-if="addForm.dispatchId">
          <el-input-number v-model="addForm.quantity" :min="1" :max="maxReturnable" style="width: 100%" @change="handleQuantityChange" />
        </el-form-item>
        <el-form-item label="损坏数量" prop="damaged" v-if="addForm.dispatchId">
          <el-input-number v-model="addForm.damaged" :min="0" :max="addForm.quantity" style="width: 100%" />
        </el-form-item>
        <el-form-item label="回收数量" v-else>
          <el-input placeholder="请先选择关联发货单" disabled style="width: 100%" />
        </el-form-item>
        <el-form-item label="回收日期" prop="returnDate">
          <el-date-picker v-model="addForm.returnDate" type="date" placeholder="选择回收日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="操作人" prop="operator">
          <el-input v-model="addForm.operator" placeholder="请输入操作人" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="addForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddSubmit" :loading="addSubmitLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="回收详情" width="600px">
      <el-descriptions :column="2" border v-loading="detailLoading">
        <el-descriptions-item label="回收单号">{{ detailData.returnNo }}</el-descriptions-item>
        <el-descriptions-item label="关联发货单号">{{ detailData.dispatchNo }}</el-descriptions-item>
        <el-descriptions-item label="合作方名称">{{ getPartnerName(detailData.partnerId) }}</el-descriptions-item>
        <el-descriptions-item label="器具类型">{{ getTypeName(detailData.typeId) }}</el-descriptions-item>
        <el-descriptions-item label="器具批次">{{ getEquipmentBatchNo(detailData.equipmentId) }}</el-descriptions-item>
        <el-descriptions-item label="回收数量">{{ detailData.quantity }}</el-descriptions-item>
        <el-descriptions-item label="损坏数量">{{ detailData.damaged || 0 }}</el-descriptions-item>
        <el-descriptions-item label="回收日期">{{ detailData.returnDate }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detailData.operator || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { page, getById, returnEquipment } from '@/api/return'
import { page as getDispatchPage } from '@/api/dispatch'
import { list as getPartnerList } from '@/api/partner'
import { list as getTypeList } from '@/api/equipmentType'
import { page as getEquipmentPage } from '@/api/equipment'

const loading = ref(false)
const addSubmitLoading = ref(false)
const detailLoading = ref(false)
const addDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const addFormRef = ref(null)
const tableData = ref([])
const partnerList = ref([])
const typeList = ref([])
const equipmentList = ref([])
const dispatchList = ref([])
const detailData = ref({})
const maxReturnable = ref(0)
const searchForm = reactive({ partnerId: null, typeId: null, returnNo: '' })
const pagination = reactive({ current: 1, size: 10, total: 0 })

const addForm = reactive({
  dispatchId: null,
  partnerId: null,
  equipmentId: null,
  typeId: null,
  quantity: 1,
  damaged: 0,
  returnDate: '',
  operator: '',
  remark: ''
})

const addRules = {
  dispatchId: [{ required: true, message: '请选择关联发货单', trigger: 'change' }],
  partnerId: [{ required: true, message: '请选择合作方', trigger: 'change' }],
  equipmentId: [{ required: true, message: '请选择器具批次', trigger: 'change' }],
  typeId: [{ required: true, message: '请选择器具类型', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入回收数量', trigger: 'blur' }],
  returnDate: [{ required: true, message: '请选择回收日期', trigger: 'change' }]
}

const getPartnerName = (partnerId) => {
  const partner = partnerList.value.find(p => p.id === partnerId)
  return partner ? partner.name : '-'
}

const getTypeName = (typeId) => {
  const type = typeList.value.find(t => t.id === typeId)
  return type ? type.name : '-'
}

const getEquipmentBatchNo = (equipmentId) => {
  const equipment = equipmentList.value.find(e => e.id === equipmentId)
  return equipment ? equipment.batchNo : '-'
}

const getDispatchLabel = (item) => {
  const partnerName = getPartnerName(item.partnerId)
  const typeName = getTypeName(item.typeId)
  const remaining = item.remainingQuantity != null ? item.remainingQuantity : item.quantity
  return `${item.dispatchNo} - ${partnerName} - ${typeName} (剩余: ${remaining})`
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await page({
      current: pagination.current,
      size: pagination.size,
      partnerId: searchForm.partnerId || undefined,
      typeId: searchForm.typeId || undefined,
      returnNo: searchForm.returnNo || undefined
    })
    tableData.value = res.records || []
    pagination.total = res.total || 0
  } finally {
    loading.value = false
  }
}

const fetchPartnerList = async () => {
  const res = await getPartnerList()
  partnerList.value = res || []
}

const fetchTypeList = async () => {
  const res = await getTypeList()
  typeList.value = res || []
}

const fetchEquipmentList = async () => {
  const res = await getEquipmentPage({ current: 1, size: 1000 })
  equipmentList.value = res.records || []
}

const fetchDispatchList = async () => {
  const res = await getDispatchPage({ current: 1, size: 1000 })
  dispatchList.value = (res.records || []).filter(d => {
    const remaining = d.remainingQuantity != null ? d.remainingQuantity : d.quantity
    return remaining > 0
  })
}

const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

const handleReset = () => {
  searchForm.partnerId = null
  searchForm.typeId = null
  searchForm.returnNo = ''
  pagination.current = 1
  fetchData()
}

const handleDispatchChange = (dispatchId) => {
  const dispatch = dispatchList.value.find(d => d.id === dispatchId)
  if (dispatch) {
    addForm.partnerId = dispatch.partnerId
    addForm.equipmentId = dispatch.equipmentId
    addForm.typeId = dispatch.typeId
    maxReturnable.value = dispatch.remainingQuantity != null ? dispatch.remainingQuantity : dispatch.quantity
    addForm.quantity = Math.min(1, maxReturnable.value)
    if (addForm.damaged > addForm.quantity) {
      addForm.damaged = addForm.quantity
    }
  } else {
    addForm.partnerId = null
    addForm.equipmentId = null
    addForm.typeId = null
    maxReturnable.value = 0
    addForm.quantity = 1
    addForm.damaged = 0
  }
}

const handleQuantityChange = (val) => {
  if (addForm.damaged > val) {
    addForm.damaged = val
  }
}

const handleAdd = () => {
  const today = new Date().toISOString().split('T')[0]
  Object.assign(addForm, {
    dispatchId: null,
    partnerId: null,
    equipmentId: null,
    typeId: null,
    quantity: 1,
    damaged: 0,
    returnDate: today,
    operator: '',
    remark: ''
  })
  maxReturnable.value = 0
  addDialogVisible.value = true
}

const handleAddSubmit = async () => {
  const valid = await addFormRef.value.validate().catch(() => false)
  if (!valid) return
  addSubmitLoading.value = true
  try {
    await returnEquipment({ ...addForm })
    ElMessage.success('新增回收成功')
    addDialogVisible.value = false
    fetchData()
    fetchDispatchList()
  } finally {
    addSubmitLoading.value = false
  }
}

const handleDetail = async (row) => {
  detailLoading.value = true
  detailDialogVisible.value = true
  try {
    const res = await getById(row.id)
    detailData.value = res || {}
  } finally {
    detailLoading.value = false
  }
}

onMounted(() => {
  fetchPartnerList()
  fetchTypeList()
  fetchEquipmentList()
  fetchDispatchList()
  fetchData()
})
</script>
