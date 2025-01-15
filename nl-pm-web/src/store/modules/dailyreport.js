
import { getReportList, getReportDetail, detleteReport, createReport, updateReport, getDayExchangeList, getDayExchangeDetail, getNoneDayExchange, downloadDailyDetail, getDailyDraftDetail, postDraftDetail, getLeaveList, downloadLeaveList, queryUserLeaveHours, queryOneSelfProjectTimeList } from '@/api/dailyreport'

const state = {
  date: null
}

const mutations = {
  SET_DATE: (state, date) => {
    state.date = date
  }
}

const actions = {
  getReportList({ commit }, data) {
    return new Promise((resolve, reject) => {
      getReportList((data)).then((res) => {
        resolve(res.data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  getReportDetail({ commit }, data) {
    return new Promise((resolve, reject) => {
      getReportDetail((data)).then((res) => {
        resolve(res.data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  detleteReport({ commit }, data) {
    return new Promise((resolve, reject) => {
      detleteReport((data)).then((res) => {
        resolve(res.data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  createReport({ commit }, data) {
    return new Promise((resolve, reject) => {
      createReport((data)).then((res) => {
        resolve(res.data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  updateReport({ commit }, data) {
    return new Promise((resolve, reject) => {
      updateReport((data)).then((res) => {
        resolve(res.data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  getDayExchangeList({ commit }, data) {
    return new Promise((resolve, reject) => {
      getDayExchangeList((data)).then((res) => {
        resolve(res.data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  getDayExchangeDetail({ commit }, data) {
    return new Promise((resolve, reject) => {
      getDayExchangeDetail((data)).then((res) => {
        resolve(res.data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  getNoneDayExchange({ commit }, data) {
    return new Promise((resolve, reject) => {
      getNoneDayExchange((data)).then((res) => {
        resolve(res.data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  downloadDailyDetail({ commit }, data) {
    return new Promise((resolve, reject) => {
      downloadDailyDetail((data)).then((res) => {
        resolve(res)
      }).catch(error => {
        reject(error)
      })
    })
  },
  getDailyDraftDetail({ commit }, data) {
    return new Promise((resolve, reject) => {
      getDailyDraftDetail((data)).then((res) => {
        resolve(res.data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  postDraftDetail({ commit }, data) {
    return new Promise((resolve, reject) => {
      postDraftDetail((data)).then((res) => {
        resolve(res.data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  getLeaveList({ commit }, data) {
    return new Promise((resolve, reject) => {
      getLeaveList((data)).then((res) => {
        resolve(res.data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  downloadLeaveList({ commit }, data) {
    return new Promise((resolve, reject) => {
      downloadLeaveList((data)).then((res) => {
        resolve(res)
      }).catch(error => {
        reject(error)
      })
    })
  },
  queryUserLeaveHours({ commit }, data) {
    return new Promise((resolve, reject) => {
      queryUserLeaveHours((data)).then((res) => {
        resolve(res.data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  queryOneSelfProjectTimeList({ commit }, data) {
    return new Promise((resolve, reject) => {
      queryOneSelfProjectTimeList((data)).then((res) => {
        resolve(res.data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  setDate({ commit }, data) {
    commit('SET_DATE', data)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

