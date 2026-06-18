import request from '@/utils/request'

export function page(params) {
  return request.get('/equipment-types/page', { params })
}

export function list() {
  return request.get('/equipment-types/list')
}

export function getById(id) {
  return request.get(`/equipment-types/${id}`)
}

export function save(data) {
  return request.post('/equipment-types', data)
}

export function update(data) {
  return request.put('/equipment-types', data)
}

export function remove(id) {
  return request.delete(`/equipment-types/${id}`)
}
