
import { getpseronList, editStatus, findUserInfo, deleteUser, getRoleUser,addUser,findRole,initPasswprd,editUser,editEmail,updatePassword,getProjectAssignList,getUserBoardAreaUser,removeAssociated,getAssociatedArea,getOtherAreaUser,getProjectUserList} from '@/api/person'

const state = {}

const mutations = {}

const actions = {
    getpseronList({ commit },data) {
      return new Promise((resolve, reject) => {
        getpseronList((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    editStatus({ commit },data) {
      return new Promise((resolve, reject) => {
        editStatus((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    findUserInfo({ commit },data) {
      return new Promise((resolve, reject) => {
        findUserInfo((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    deleteUser({ commit },data) {
      return new Promise((resolve, reject) => {
        deleteUser((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getRoleUser({ commit },data) {
      return new Promise((resolve, reject) => {
        getRoleUser((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    addUser({ commit },data) {
      return new Promise((resolve, reject) => {
        addUser((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    findRole({ commit },data) {
      return new Promise((resolve, reject) => {
        findRole((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    initPasswprd({ commit },data) {
      return new Promise((resolve, reject) => {
        initPasswprd((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    editUser({ commit },data) {
      return new Promise((resolve, reject) => {
        editUser((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    editEmail({ commit },data) {
      return new Promise((resolve, reject) => {
        editEmail((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    updatePassword({ commit },data) {
      return new Promise((resolve, reject) => {
        updatePassword((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getProjectAssignList({ commit },data) {
      return new Promise((resolve, reject) => {
        getProjectAssignList((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getUserBoardAreaUser({ commit }) {
      return new Promise((resolve, reject) => {
        getUserBoardAreaUser().then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    removeAssociated({ commit },data) {
      return new Promise((resolve, reject) => {
        removeAssociated(data).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getAssociatedArea({ commit },data) {
      return new Promise((resolve, reject) => {
        getAssociatedArea(data).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getOtherAreaUser({ commit },data) {
      return new Promise((resolve, reject) => {
        getOtherAreaUser(data).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getProjectUserList({ commit },data) {
      return new Promise((resolve, reject) => {
        getProjectUserList(data).then((res) => {
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

