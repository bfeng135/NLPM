import request from '@/utils/request'

export function getComplexList(data) {
  return request({
    url: '/reportForm/getComplexReportForm',
    method: 'post',
    data
  })
}
export function getReportFormProject(data) {
  return request({
    url: '/reportForm/getReportFormByProject',
    method: 'post',
    data
  })
}
export function downComplexReportForm(data) {
  return request({
    url: '/downloadReportForm/downComplexReportForm',
    method: 'post',
    data,
    responseType: 'blob'
  })
}
export function downReportForm(data) {
  return request({
    url: '/downloadReportForm/downReportFormDetails',
    method: 'post',
    data,
    responseType: 'blob'
  })
}
export function downProjectName(data) {
  return request({
    url: '/systemProject/downSystemProject',
    method: 'post',
    data,
    responseType: 'blob'
  })
}
export function downProjectCoo(data) {
  return request({
    url: '/statistics/area/cost/download',
    method: 'post',
    data,
    responseType: 'blob'
  })
}

