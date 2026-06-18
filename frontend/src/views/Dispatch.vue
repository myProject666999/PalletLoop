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
          <el-input v-model="searchForm.dispatchNo" placeholder="请输入发货单号" clearable />
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
        <el-table-column label="器具批次" min-width="120">
          <template #default="{ row }">
            {{ getEquipmentBatchNo(row.equipmentId) }}
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="发出数量" min-width="90" />
        <el-table-column prop="orderNo" label="关联订单号" min-width="140" />
        <el-table-column prop="dispatchDate" label="发货日期" min-width="110" />
        <el-table-column prop="expectedReturnDate" label="预计回收日期" min-width="120" />
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

    <el-dialog v-model="addDialogVisible" title="新增发货" width="600px" destroy-on-close>
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="110px">
        <el-form-item label="合作方" prop="partnerId">
          <el-select v-model="addForm.partnerId" placeholder="请选择合作方" style="width: 100%" filterable>
            <el-option v-for="item in partnerList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="器具类型" prop="typeId">
          <el-select v-model="addForm.typeId" placeholder="请选择器具类型" style="width: 100%" filterable @change="handleTypeChange">
            <el-option v-for="item in typeList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="器具批次" prop="equipmentId">
          <el-select v-model="addForm.equipmentId" placeholder="请选择器具批次" style="width: 100%" filterable>
            <el-option v-for="item in filteredEquipmentList" :key="item.id" :label="item.batchNo" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="发出数量" prop="quantity">
          <el-input-number v-model="addForm.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="关联订单号" prop="orderNo">
          <el-input v-model="addForm.orderNo" placeholder="请输入关联订单号" />
        </el-form-item>
        <el-form-item label="发货日期" prop="dispatchDate">
          <el-date-picker v-model="addForm.dispatchDate" type="date" placeholder="选择发货日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="预计回收日期" prop="expectedReturnDate">
          <el-date-picker v-model="addForm.expectedReturnDate" type="date" placeholder="选择预计回收日期" style="width: 100%" value-format="YYYY-MM-DD" />
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

    <el-dialog v-model="detailDialogVisible" title="发货详情" width="600px">
      <el-descriptions :column="2" border v-loading="detailLoading">
        <el-descriptions-item label="发货单号">{{ detailData.dispatchNo }}</el-descriptions-item>
        <el-descriptions-item label="合作方名称">{{ getPartnerName(detailData.partnerId) }}</el-descriptions-item>
        <el-descriptions-item label="器具类型">{{ getTypeName(detailData.typeId) }}</el-descriptions-item>
        <el-descriptions-item label="器具批次">{{ getEquipmentBatchNo(detailData.equipmentId) }}</el-descriptions-item>
        <el-descriptions-item label="发出数量">{{ detailData.quantity }}</el-descriptions-item>
        <el-descriptions-item label="关联订单号">{{ detailData.orderNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发货日期">{{ detailData.dispatchDate }}</el-descriptions-item>
        <el-descriptions-item label="预计回收日期">{{ detailData.expectedReturnDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detailData.operator || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { page, getById, dispatch } from '@/api/dispatch'
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
const detailData = ref({})
const searchForm = reactive({ partnerId: null, typeId: null, dispatchNo: '' })
const pagination = reactive({ current: 1, size: 10, total: 0 })

const addForm = reactive({
  partnerId: null,
  equipmentId: null,
  typeId: null,
  quantity: 1,
  orderNo: '',
  dispatchDate: '',
  expectedReturnDate: '',
  operator: '',
  remark: ''
})

const addRules = {
  partnerId: [{ required: true, message: '请选择合作方', trigger: 'change' }],
  equipmentId: [{ required: true, message: '请选择器具批次', trigger: 'change' }],
  typeId: [{ required: true, message: '请选择器具类型', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入发出数量', trigger: 'blur' }],
  dispatchDate: [{ required: true, message: '请选择发货日期', trigger: 'change' }]
}

const filteredEquipmentList = computed(() => {
  if (!addForm.typeId) return equipmentList.value
  return equipmentList.value.filter(e => e.typeId === addForm.typeId)
})

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

const fetchData = async () => {
  loading.value = true
  try {
    const res = await page({
      current: pagination.current,
      size: pagination.size,
      partnerId: searchForm.partnerId || undefined,
      typeId: searchForm.typeId || undefined,
      dispatchNo: searchForm.dispatchNo || undefined
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

const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

const handleReset = () => {
  searchForm.partnerId = null
  searchForm.typeId = null
  searchForm.dispatchNo = ''
  pagination.current = 1
  fetchData()
}

const handleTypeChange = () => {
  addForm.equipmentId = null
}

const handleAdd = () => {
  const today = new Date().toISOString().split('T')[0]
  Object.assign(addForm, {
    partnerId: null,
    equipmentId: null,
    typeId: null,
    quantity: 1,
    orderNo: '',
    dispatchDate: today,
    expectedReturnDate: '',
    operator: '',
    remark: ''
  })
  addDialogVisible.value = true
}

const handleAddSubmit = async () => {
  const valid = await addFormRef.value.validate().catch(() => false)
  if (!valid) return
  addSubmitLoading.value = true
  try {
    await dispatch({ ...addForm })
    ElMessage.success('新增发货成功')
    addDialogVisible.value = false
    fetchData()
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
  fetchData()
})
</script>
