
import { getComplexList, getReportFormProject, downComplexReportForm, downReportForm, downProjectName, downProjectCoo } from '@/api/report'

const state = {}

const mutations = {}

const actions = {
  getComplexList({ commit }, data) {
    return new Promise((resolve, reject) => {
      getComplexList((data)).then((res) => {
        resolve(res.data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  getReportFormProject({ commit }, data) {
    return new Promise((resolve, reject) => {
      getReportFormProject((data)).then((res) => {
        resolve(res.data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  downComplexReportForm({ commit }, data) {
    return new Promise((resolve, reject) => {
      downComplexReportForm((data)).then((res) => {
        resolve(res)
      }).catch(error => {
        reject(error)
      })
    })
  },
  downReportForm({ commit }, data) {
    return new Promise((resolve, reject) => {
      downReportForm((data)).then((res) => {
        resolve(res)
      }).catch(error => {
        reject(error)
      })
    })
  },
  downProjectName({ commit }, data) {
    return new Promise((resolve, reject) => {
      downProjectName((data)).then((res) => {
        resolve(res)
      }).catch(error => {
        reject(error)
      })
    })
  },
  downProjectCoo({ commit }, data) {
    return new Promise((resolve, reject) => {
      downProjectCoo((data)).then((res) => {
        resolve(res)
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

