import Vue from 'vue'
import Vuex from 'vuex'
import getters from './getters'
import app from './modules/app'
import settings from './modules/settings'
import user from './modules/user'
import person from './modules/person'
import area from './modules/area'
import role from './modules/role'
import system from './modules/system'
import project from './modules/project'
import dailyreport from './modules/dailyreport'
import calendar from './modules/calendar'
import report from './modules/report'
import permission from './modules/permission'
Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    app,
    settings,
    user,
    person,
    area,
    role,
    system,
    project,
    dailyreport,
    calendar,
    report,
    permission
  },
  getters
})

export default store
