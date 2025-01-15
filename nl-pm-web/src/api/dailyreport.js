import request from '@/utils/request'

export function getReportList(data) {
  return request({
    url: '/dayReport/list',
    method: 'post',
    data
  })
}
export function getReportDetail(id) {
  return request({
    url: `/dayReport/detail/${id}`,
    method: 'get'
  })
}
export function detleteReport(id) {
  return request({
    url: `/dayReport/delete/${id}`,
    method: 'delete'
  })
}
export function createReport(data) {
  return request({
    url: '/dayReport/create',
    method: 'post',
    data
  })
}
export function updateReport(data) {
  return request({
    url: '/dayReport/update',
    method: 'put',
    data
  })
}
export function getDayExchangeList(data) {
  return request({
    url: '/dayExchange/list',
    method: 'post',
    data
  })
}
export function getDayExchangeDetail(id) {
  return request({
    url: `/dayExchange/detail/${id}`,
    method: 'get'
  })
}
export function getNoneDayExchange(data) {
  return request({
    url: '/dayReport/NoneDayReport',
    method: 'post',
    data
  })
}
export function downloadDailyDetail(data) {
  return request({
    url: '/downloadDayReportForm/downDayReportFormDetails',
    method: 'post',
    data,
    responseType: 'blob'
  })
}
export function getDailyDraftDetail(data) {
  return request({
    url: '/dayReport/viewDraft',
    method: 'get',
    data
  })
}
export function postDraftDetail(data) {
  return request({
    url: '/dayReport/saveDraft',
    method: 'post',
    data
  })
}
export function getLeaveList(data) {
  return request({
    url: '/dayExchange/leave/list',
    method: 'post',
    data
  })
}
export function downloadLeaveList(data) {
  return request({
    url: '/dayExchange/downDayExchangeForm',
    method: 'post',
    data,
    responseType: 'blob'
  })
}
export function queryUserLeaveHours(data) {
  return request({
    url: '/statistics/queryUserWorkLeaveHours',
    method: 'post',
    data
  })
}
export function queryOneSelfProjectTimeList(data) {
  return request({
    url: '/statistics/queryOneSelfProjectTimeList',
    method: 'post',
    data
  })
}
