import request from '@/utils/request'

export function getAreaList(data) {
    return request({
      url: '/area/getAreaList',
      method: 'post',
      data
    })
}
export function getAreaDetail(params) {
  return request({
    url: '/area/getAreaInfo',
    method: 'get',
    params
  })
}
export function addArea(data) {
  return request({
    url: '/area/addArea',
    method: 'post',
    data
  })
}
export function detleteArea(params) {
  return request({
    url: '/area/deleteArea',
    method: 'DELETE',
    params
  })
}
export function updateArea(data) {
  return request({
    url: '/area/updateArea',
    method: 'put',
    data
  })
}
export function getAreaPerson(id) {
  return request({
    url: `/area/${id}/otherArea/userList`,
    method: 'get',
  })
}
export function AssignAreaPerson(data) {
  return request({
    url: '/area/assignUserToArea',
    method: 'post',
    data
  })
}
export function getAreaPersonList(areaId) {
  return request({
    url: `/user/allUser/withoutArea/${areaId}`,
    method: 'get',
  })
}
export function getProjectNameAreaList(data) {
  return request({
    url: '/area/projectName/areaList',
    method: 'post',
    data
  })
}
export function updateAreaStatus(data) {
  return request({
    url: '/area/updateStatus',
    method: 'put',
    data
  })
}
