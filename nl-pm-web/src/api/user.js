import request from '@/utils/request'

export function nllogin(data) {
  return request({
    url: '/login',
    method: 'post',
    data
  })
}
export function getInfo(token) {
  return request({
    url: '/vue-admin-template/user/info',
    method: 'get',
    params: { token }
  })
}

export function logout(username) {
  return request({
    url: `/logout/${username}`,
    method: 'get',
  })
}
