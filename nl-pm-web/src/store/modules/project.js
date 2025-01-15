import { getProjectList,projectStatus,createProject,deleteProject,getAreaProjectPerson,updateProject,configPerson,getProjectPersonDetail,getProjectPerson,getProjectAssignPerson,getAssignUser,getManagerUser,getProjectBoardList,getProjectBoardUserList,downloadProject,getExportProjectList,ExportOneProjectList,getProjectCharts,getUserHoursChart} from '@/api/project'

const state = {}

const mutations = {}

const actions = {
    getProjectList({ commit },data) {
      return new Promise((resolve, reject) => {
        getProjectList((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    projectStatus({ commit },data) {
      return new Promise((resolve, reject) => {
        projectStatus((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    createProject({ commit },data) {
      return new Promise((resolve, reject) => {
        createProject((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    deleteProject({ commit },data) {
      return new Promise((resolve, reject) => {
        deleteProject((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getAreaProjectPerson({ commit },data) {
      return new Promise((resolve, reject) => {
        getAreaProjectPerson((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    updateProject({ commit },data) {
      return new Promise((resolve, reject) => {
        updateProject((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    configPerson({ commit },data) {
      return new Promise((resolve, reject) => {
        configPerson((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getProjectPersonDetail({ commit },data) {
      return new Promise((resolve, reject) => {
        getProjectPersonDetail((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getProjectPerson({ commit },data) {
      return new Promise((resolve, reject) => {
        getProjectPerson((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getProjectPerson({ commit },data) {
      return new Promise((resolve, reject) => {
        getProjectPerson((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getProjectAssignPerson({ commit },data) {
      return new Promise((resolve, reject) => {
        getProjectAssignPerson((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getAssignUser({commit},data){
      return new Promise((resolve, reject) => {
        getAssignUser((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getManagerUser({commit},data){
      return new Promise((resolve, reject) => {
        getManagerUser((data)).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getProjectBoardList({commit},data){
      return new Promise((resolve, reject) => {
        getProjectBoardList(data).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getProjectBoardUserList({commit}){
      return new Promise((resolve, reject) => {
        getProjectBoardUserList().then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    downloadProject({commit},data){
      return new Promise((resolve, reject) => {
        downloadProject(data).then((res) => {
          resolve(res)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getExportProjectList({commit},data){
      return new Promise((resolve, reject) => {
        getExportProjectList(data).then((res) => {
          resolve(res.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    ExportOneProjectList({commit},data){
      return new Promise((resolve, reject) => {
        ExportOneProjectList(data).then((res) => {
          resolve(res)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getProjectCharts({commit},data){
      return new Promise((resolve, reject) => {
        getProjectCharts(data).then((res) => {
          resolve(res)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getUserHoursChart({commit},data){
      return new Promise((resolve, reject) => {
        getUserHoursChart(data).then((res) => {
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

