import request from '@/utils/request'

export function page(params) {
  return request.get('/partner/page', { params })
}

export function list() {
  return request.get('/partner/list')
}

export function getById(id) {
  return request.get(`/partner/${id}`)
}

export function save(data) {
  return request.post('/partner', data)
}

export function update(data) {
  return request.put('/partner', data)
}

export function remove(id) {
  return request.delete(`/partner/${id}`)
}
