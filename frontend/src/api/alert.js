import request from '@/utils/request'

export function getOverdue() {
  return request.get('/alerts/overdue')
}

export function getByPartner(partnerId) {
  return request.get(`/alerts/overdue/${partnerId}`)
}
