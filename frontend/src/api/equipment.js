import request from '@/utils/request'

export function page(params) {
  return request.get('/equipment/page', { params })
}

export function getById(id) {
  return request.get(`/equipment/${id}`)
}

export function save(data) {
  return request.post('/equipment', data)
}

export function update(data) {
  return request.put('/equipment', data)
}
