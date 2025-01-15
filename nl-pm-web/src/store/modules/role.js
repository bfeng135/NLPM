import { getRoleList } from '@/api/role'

const state = {}

const mutations = {}

const actions = {
    getRoleList({ commit },data) {
      return new Promise((resolve, reject) => {
        getRoleList((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

