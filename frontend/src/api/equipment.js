import request from '@/utils/request'

export function page(params) {
  return request.get('/equipments/page', { params })
}

export function getById(id) {
  return request.get(`/equipments/${id}`)
}

export function save(data) {
  return request.post('/equipments', data)
}

export function update(data) {
  return request.put('/equipments', data)
}
