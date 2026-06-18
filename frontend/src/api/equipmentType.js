import request from '@/utils/request'

export function page(params) {
  return request.get('/equipment-type/page', { params })
}

export function list() {
  return request.get('/equipment-type/list')
}

export function getById(id) {
  return request.get(`/equipment-type/${id}`)
}

export function save(data) {
  return request.post('/equipment-type', data)
}

export function update(data) {
  return request.put('/equipment-type', data)
}

export function remove(id) {
  return request.delete(`/equipment-type/${id}`)
}
