import request from '@/utils/request'

export function page(params) {
  return request.get('/dispatches/page', { params })
}

export function getById(id) {
  return request.get(`/dispatches/${id}`)
}

export function dispatch(data) {
  return request.post('/dispatches', data)
}
