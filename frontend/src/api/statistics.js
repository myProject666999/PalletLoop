import request from '@/utils/request'

export function getOverview() {
  return request.get('/statistics/overview')
}

export function getPartnerStats(partnerId) {
  return request.get(`/statistics/partner/${partnerId}`)
}

export function getAllPartnerStats() {
  return request.get('/statistics/partners')
}

export function exportPartnerStats(partnerId) {
  return request.get('/statistics/export', {
    params: { partnerId },
    responseType: 'blob'
  })
}
