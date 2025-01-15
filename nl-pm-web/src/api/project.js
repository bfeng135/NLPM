import request from '@/utils/request'

export function getProjectList(data) {
    return request({
      url: '/project/list',
      method: 'post',
      data
    })
}
export function projectStatus(id) {
  return request({
    url: `/project/openOrClose/${id}`,
    method: 'get',
  })
}
export function createProject(data) {
  return request({
    url: '/project/create',
    method: 'post',
    data
  })
}
export function updateProject(data) {
  return request({
    url: '/project/update',
    method: 'post',
    data
  })
}
export function deleteProject(id) {
  return request({
    url: `/project/delete/${id}`,
    method: 'delete',
  })
}
export function getAreaProjectPerson(params) {
  return request({
    url: '/user/byProjectAndArea',
    method: 'get',
    params
  })
}
export function configPerson(data) {
  return request({
    url: '/project/assignEmployees',
    method: 'post',
    data
  })
}
export function getProjectPersonDetail(id) {
  return request({
    url: `/project/${id}`,
    method: 'get',
  })
}
export function getProjectPerson(params) {
  return request({
    url: '/user/byProjectAndArea',
    method: 'get',
    params
  })
}
export function getProjectAssignPerson(id) {
  return request({
    url: `/user/searchUserWhoCanBeAssignToProjectByAreaId/${id}`,
    method: 'get',
  })
}
export function getAssignUser(data) {
  return request({
    url: '/project/list/assignUser',
    method: 'post',
    data
  })
}
export function getManagerUser(id) {
  return request({
    url: `/user/searchUserByManagerId/${id}`,
    method: 'get',
  })
}
export function getProjectBoardList(params) {
  return request({
    url: 'project/board/list',
    method: 'get',
    params
  })
}
export function getProjectBoardUserList() {
  return request({
    url: 'project/board/projectUserlist',
    method: 'get',
  })
}
export function downloadProject(data) {
  return request({
    url: '/project/downProjectReportForm',
    method: 'post',
    data,
    responseType: 'blob',
  })
}
export function getExportProjectList(data) {
  return request({
    url: '/reportForm/getReportFormByProjectByProject',
    method: 'post',
    data,
  })
}
export function ExportOneProjectList(data) {
  return request({
    url: '/downloadReportForm/downReportFormDetailsByProject',
    method: 'post',
    data,
    responseType: 'blob',
  })
}
export function getProjectCharts(data) {
  return request({
    url: '/project/distributionChart',
    method: 'post',
    data,
  })
}
export function getUserHoursChart(data) {
  return request({
    url: '/project/userHoursChart',
    method: 'post',
    data,
  })
}
