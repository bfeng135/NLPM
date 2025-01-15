
import { getAreaList, getAreaDetail, detleteArea, updateArea, addArea, getAreaPerson, AssignAreaPerson,getAreaPersonList,getProjectNameAreaList,updateAreaStatus } from '@/api/area'

const state = {}

const mutations = {}

const actions = {
    getAreaList({ commit },data) {
      return new Promise((resolve, reject) => {
        getAreaList((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getAreaDetail({ commit },data) {
      return new Promise((resolve, reject) => {
        getAreaDetail((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    detleteArea({ commit },data) {
      return new Promise((resolve, reject) => {
        detleteArea((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    updateArea({ commit },data) {
      return new Promise((resolve, reject) => {
        updateArea((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    addArea({ commit },data) {
      return new Promise((resolve, reject) => {
        addArea((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getAreaPerson({ commit },data) {
      return new Promise((resolve, reject) => {
        getAreaPerson((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    AssignAreaPerson({ commit },data) {
      return new Promise((resolve, reject) => {
        AssignAreaPerson((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getAreaPersonList({ commit },data) {
      return new Promise((resolve, reject) => {
        getAreaPersonList((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getProjectNameAreaList({ commit },data) {
      return new Promise((resolve, reject) => {
        getProjectNameAreaList((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    updateAreaStatus({ commit },data) {
      return new Promise((resolve, reject) => {
        updateAreaStatus((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

