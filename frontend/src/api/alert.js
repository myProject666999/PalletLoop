import request from '@/utils/request'

export function getOverdue() {
  return request.get('/alert/overdue')
}

export function getByPartner(partnerId) {
  return request.get(`/alert/partner/${partnerId}`)
}
