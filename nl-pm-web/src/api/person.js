import request from '@/utils/request'

export function getpseronList(data) {
  return request({
    url: '/user/list',
    method: 'post',
    data
  })
}
export function editStatus(data) {
  return request({
    url: `/user/editUserStatus/${data}`,
    method: 'get'
  })
}
export function findUserInfo(userId) {
  return request({
    url: `/user/findUserInfo/${userId}`,
    method: 'get'
  })
}
export function deleteUser(Id) {
  return request({
    url: `/user/delUser/${Id}`,
    method: 'DELETE'
  })
}

export function getRoleUser(data) {
  return request({
    url: '/user/searchUserByRoleOrArea',
    method: 'post',
    data
  })
}
export function addUser(data) {
  return request({
    url: '/user/add',
    method: 'post',
    data
  })
}
export function findRole() {
  return request({
    url: '/role/findAllRoleByDifferentUser',
    method: 'get'
  })
}
export function initPasswprd(data) {
  return request({
    url: '/user/initPassword',
    method: 'put',
    data
  })
}
export function editUser(data) {
  return request({
    url: '/user/editUserInfo',
    method: 'put',
    data
  })
}
export function editEmail(userId) {
  return request({
    url: `/user/editEmailNotice/${userId}`,
    method: 'get'
  })
}
export function updatePassword(data) {
  return request({
    url: '/user/updatePassword',
    method: 'put',
    data
  })
}
export function getProjectAssignList(id) {
  return request({
    url: `/user/searchUserByProjectId/${id}`,
    method: 'get'
  })
}
export function getUserBoardAreaUser() {
  return request({
    url: '/user/board/areaUser',
    method: 'get'
  })
}
export function removeAssociated(data) {
  return request({
    url: `/project/removeAssociated/${data.userId}/${data.areaId}`,
    method: 'delete'
  })
}
export function getAssociatedArea(id) {
  return request({
    url: `/project/searchAssociatedArea/${id}`,
    method: 'get'
  })
}
export function getOtherAreaUser(data) {
  return request({
    url: '/user/associated/otherAreaUser',
    method: 'post',
    data
  })
}

export function getProjectUserList(data) {
  return request({
    url: '/statistics/queryUserProjectTimeList',
    method: 'post',
    data
  })
}
