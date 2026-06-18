import request from '@/utils/request'

export function page(params) {
  return request.get('/dispatch/page', { params })
}

export function getById(id) {
  return request.get(`/dispatch/${id}`)
}

export function dispatch(data) {
  return request.post('/dispatch', data)
}
