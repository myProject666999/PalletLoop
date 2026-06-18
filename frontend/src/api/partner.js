import request from '@/utils/request'

export function page(params) {
  return request.get('/partners/page', { params })
}

export function list() {
  return request.get('/partners/list')
}

export function getById(id) {
  return request.get(`/partners/${id}`)
}

export function save(data) {
  return request.post('/partners', data)
}

export function update(data) {
  return request.put('/partners', data)
}

export function remove(id) {
  return request.delete(`/partners/${id}`)
}
