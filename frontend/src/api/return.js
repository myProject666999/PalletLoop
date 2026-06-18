import request from '@/utils/request'

export function page(params) {
  return request.get('/returns/page', { params })
}

export function getById(id) {
  return request.get(`/returns/${id}`)
}

export function returnEquipment(data) {
  return request.post('/returns', data)
}
