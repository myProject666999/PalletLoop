import request from '@/utils/request'

export function page(params) {
  return request.get('/return/page', { params })
}

export function getById(id) {
  return request.get(`/return/${id}`)
}

export function returnEquipment(data) {
  return request.post('/return', data)
}
