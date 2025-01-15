// import router, { constantRoutes} from "@/router";
import router from "../../router"
import { logout, getInfo, nllogin } from '@/api/user'
import { 
  getToken, 
  setToken, 
  removeToken, 
  getName, 
  setName,
  removeName, 
  setUserName, 
  getUserName,
  removeUserName,
  setUserID,
  getUserID,
  removeUserID,
  getUserAreaID,
  serUserAreaID,
  removeUserAreaID,
  setRole,
  getRole,
  removeRole,
  setReadOnly,
  getReadOnly,
  removeReadOnly} from '@/utils/auth'
import { resetRouter,normalRoutes,adminRoutes,constantRoutes} from '@/router'


const getDefaultState = () => {
  return {
    token: getToken(),
    name: getName(),
    avatar:'',
    username:getUserName(),
    areaId:getUserAreaID(),
    id:getUserID(),
    role:getRole(),
    routes:[],
    names:'',
    readonly:getReadOnly()
  }
}

const state = getDefaultState()

const mutations = {
  RESET_STATE: (state) => {
    Object.assign(state, getDefaultState())
  },
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_NAME: (state, name) => {
    state.name = name
    setName(name)
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  },
  SET_USERNAME:(state,username)=>{
    state.username = username
    setUserName(username)
  },
  SET_AREAID:(state,areaId)=>{
    state.areaId = areaId
    serUserAreaID(areaId)
  },
  SET_ID:(state,id)=>{
    state.id = id
    setUserID(id)
  },
  SET_ROLE:(state, role) => {
    state.role = role
    setRole(role)
  },
  SET_ROUTES: (state, routes) => {
    state.routes = constantRoutes.concat(routes);
  },
  SET_NAMES:(state,name)=>{
    state.names = name
  },
  SET_READONLY:(state,readonly)=>{
    state.readonly = readonly
    setReadOnly(readonly)
  }
}

const actions = {
  // user login
  nllogin({ commit }, userInfo) {
    const { username, password, areaId } = userInfo
    return new Promise((resolve, reject) => {
      nllogin({areaId:areaId, username: username.trim(), password: password }).then(response => {
        const { data } = response
        commit('SET_TOKEN', data.token)
        commit('SET_NAME', data.nickname)
        commit('SET_NAMES', data.nickname)
        commit('SET_USERNAME',data.username)
        commit('SET_AREAID',data.areaId)
        commit('SET_ID',data.id)
        commit('SET_ROLE',data.roleCode)
        commit('SET_READONLY',data.readonly)
        setToken(data.token)
        resolve(response.data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  // get user info
  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      getInfo(state.token).then(response => {
        const { data } = response
        if (!data) {
          return reject('Verification failed, please Login again.')
        }

        const { name, avatar } = data
        
        commit('SET_NAME', name)
        commit('SET_AVATAR', avatar)
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // user logout
  logout({ commit },data) {
    return new Promise((resolve, reject) => {
      logout((data)).then((res) => {
        removeToken() // must remove  token  first
        resetRouter()
        removeName()
        removeUserName()
        removeUserID()
        removeUserAreaID()
        removeRole()
        removeReadOnly()
        commit('RESET_STATE')
        resolve(res.data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // remove token
  resetToken({ commit }) {
    return new Promise(resolve => {
      removeToken() // must remove  token  first
      commit('RESET_STATE')
      resolve()
    })
  },
  updateName({ commit },data) {
    return new Promise(resolve => {
      commit('SET_NAME', data)
      resolve()
    })
  },
  init({ commit }) {
    return new Promise(resolve => {
      const arr = state.role == "SUPER_ADMIN"?adminRoutes:normalRoutes
      router.addRoutes(arr);
      commit("SET_ROUTES", arr);
      resolve(arr);
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

