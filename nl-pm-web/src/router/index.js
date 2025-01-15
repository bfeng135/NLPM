import Vue from 'vue'
import Router from 'vue-router'
// import store from '../store/index'
Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'/'el-icon-x' the icon show in the sidebar
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },

  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },

  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [{
      path: 'dashboard',
      name: 'Dashboard',
      component: () => import('@/views/dashboard/index'),
      meta: { title: '主页', icon: 'dashboard' }
    }]
  },

  {
    path: '/role',
    component: Layout,
    children: [
      {
        path: 'table',
        name: 'Table',
        component: () => import('@/views/role/index'),
        meta: { title: '角色管理', icon: 'el-icon-s-help' }
      }
    ]
  },

  {
    path: '/area',
    component: Layout,
    // hidden: getRole(role),
    children: [
      {
        path: 'index',
        name: 'Area',
        component: () => import('@/views/area/index'),
        meta: { title: '区域管理', icon: 'el-icon-location-information' }
      }
    ]
  },
  {
    path: '/calendar',
    component: Layout,
    children: [
      {
        path: 'index',
        name: 'Calendar',
        component: () => import('@/views/calendar/index'),
        meta: { title: '万年历', icon: 'el-icon-date' }
      }
    ]
  },
  {
    path: '/dailyreport',
    component: Layout,
    redirect: '/dailyreport/reporttable',
    name: 'DailyReport',
    meta: {
      title: '日报管理',
      icon: 'form'
    },
    children: [
      {
        path: 'reporttable',
        name: 'DailyReportList',
        component: () => import('@/views/dailyreport/index'),
        meta: { title: '日报查询', icon: 'el-icon-document' }
      },
      {
        path: 'createreport',
        name: 'DailyReportCreate',
        // hidden:true,
        component: () => import('@/views/dailyreport/createreport'),
        meta: { title: '新建日报', icon: 'form' }
      },
      {
        path: 'draftreport',
        name: 'DraftReport',
        hidden: false,
        component: () => import('@/views/dailyreport/draftreport'),
        meta: { title: '日报草稿', icon: 'draft' }
      },
      {
        path: 'editreport/:id',
        name: 'DailyReportEdit',
        hidden: true,
        component: () => import('@/views/dailyreport/editreport'),
        meta: { title: '编辑日报', icon: 'form' }
      },
      {
        path: 'detailreport/:id',
        name: 'DailyReportDetail',
        hidden: true,
        component: () => import('@/views/dailyreport/detailreport'),
        meta: { title: '日报详情', icon: 'form' }
      },
      {
        path: 'nonereport',
        name: 'NoneReport',
        hidden: false,
        component: () => import('@/views/dailyreport/nonereport'),
        meta: { title: '未填写日报查询', icon: 'el-icon-circle-close' }
      },
      {
        path: 'workhourstotal',
        name: 'WorkHoursTotal',
        component: () => import('@/views/dailyreport/workhourstotal'),
        meta: { title: '人员工时统计', icon: 'piechart' }
      }
    ]
  },
  {
    path: '/person',
    component: Layout,
    redirect: '/person/index',
    name: 'person',
    meta: {
      title: '人员管理',
      icon: 'peoples'
    },
    children: [
      // {
      //   path: 'charts',
      //   name: 'Person',
      //   component: () => import('@/views/person/personecharts'),
      //   meta: { title: '人员工时看板', icon: 'piechart' },
      //   hidden:true
      // },
      {
        path: 'personcharts',
        name: 'PersonCharts',
        component: () => import('@/views/person/personprojectchart'),
        meta: { title: '人员工时看板', icon: 'piechart' }
      },
      {
        path: 'index',
        name: 'PersonList',
        component: () => import('@/views/person/index'),
        meta: { title: '人员列表管理', icon: 'peoples' }
      }

    ]
  },
  {
    path: '/leave',
    component: Layout,
    redirect: '/leave/dayexchange',
    name: 'leave',
    meta: {
      title: '请假管理',
      icon: 'leaves'
    },
    children: [
      {
        path: 'dayeEchange',
        name: 'DayeEchange',
        component: () => import('@/views/dayexchange/index'),
        meta: { title: '倒休管理', icon: 'daoxiu' }
      },
      {
        path: 'index',
        name: 'Leave',
        component: () => import('@/views/dayexchange/leave'),
        meta: { title: '休假管理', icon: 'xiujia' }
      },
      {
        path: 'leaveCharts',
        name: 'LeaveCharts',
        component: () => import('@/views/dayexchange/leaveCharts'),
        meta: { title: '休假看板', icon: 'piechart' }
      }
    ]
  },
  {
    path: '/leaves',
    component: Layout,
    redirect: '/leave/dayexchange',
    name: 'leave',
    meta: {
      title: '休假管理',
      icon: 'leaves'
    },
    children: [
      {
        path: 'leaveCharts',
        name: 'LeaveCharts',
        component: () => import('@/views/dayexchange/leaveCharts'),
        meta: { title: '休假时间看板', icon: 'piechart' }
      },
      {
        path: 'index',
        name: 'Leaves',
        component: () => import('@/views/dayexchange/leave'),
        meta: { title: '休假列表管理', icon: 'xiujia' }
      }
    ]
  },
  // {
  //   path: '',
  //   component: Layout,
  //   children: [
  //     {
  //       path: 'index',
  //       name: 'DayeEchange',
  //       component: () => import('@/views/dayexchange/index'),
  //       meta: { title: '倒休管理', icon: 'form' }
  //     }
  //   ]
  // },
  {
    path: '/project',
    component: Layout,
    redirect: '/project/list',
    name: 'project',
    meta: {
      title: '项目管理',
      icon: 'el-icon-menu'
    },
    children: [
      {
        path: 'echarts',
        name: 'ProjectCharts',
        component: () => import('@/views/project/components/projectecharts'),
        meta: { title: '项目工时看版', icon: 'piechart' }
      },
      {
        path: 'index',
        name: 'Project',
        component: () => import('@/views/project/index'),
        meta: { title: '项目列表管理', icon: 'el-icon-menu' }
      }
    ]
  },
  {
    path: '/rpa',
    component: Layout,
    children: [
      {
        path: 'index',
        name: 'Rpa',
        component: () => import('@/views/system/projectname'),
        meta: { title: '项目字典管理', icon: 'education' }
      }
    ]
  },
  {
    path: '/system',
    component: Layout,
    redirect: '/system/index',
    name: 'System',
    meta: { title: '系统管理', icon: 'el-icon-setting' },
    children: [
      {
        path: 'index',
        name: 'Email',
        component: () => import('@/views/system/index'),
        meta: { title: '系统名称管理', icon: 'el-icon-edit' },
        subMenuHiddenRole: ['HR', 'MANAGEMENT']
      },
      {
        path: 'projectname',
        name: 'ProjectName',
        component: () => import('@/views/system/projectname'),
        meta: { title: '项目字典管理', icon: 'education' },
        subMenuHiddenRole: ['HR', 'MANAGEMENT']
      },
      {
        path: 'editableDate',
        name: 'EditableDate',
        component: () => import('@/views/system/editableDate'),
        meta: { title: '限制编辑日期', icon: 'el-icon-s-claim' }
      },
      {
        path: 'email',
        name: 'Emails',
        component: () => import('@/views/system/email'),
        meta: { title: '系统邮箱查询', icon: 'el-icon-message' },
        subMenuHiddenRole: ['HR', 'MANAGEMENT']
      }
    ]
  },
  {
    path: '/report',
    component: Layout,
    redirect: '/report/complexreport',
    name: 'Report',
    meta: {
      title: '报表管理',
      icon: 'excel'
    },
    children: [
      {
        path: 'complexreport',
        name: 'Complexreport',
        component: () => import('@/views/report/complexreport'),
        meta: { title: '复杂报表查询', icon: 'complexreport' }
      },
      {
        path: 'projectreport',
        name: 'ProjectReport',
        component: () => import('@/views/report/projectreport'),
        meta: { title: '单报表查询', icon: 'projectreport' }
      },
      {
        path: 'project-cooperation',
        name: 'ProjectCooperation',
        component: () => import('@/views/report/project-cooperation'),
        meta: { title: '区域协作统计报表', icon: 'el-icon-menu' }
      }
    ]
  },
  {
    path: '/profile',
    component: Layout,
    redirect: '/profile/index',
    hidden: true,
    children: [
      {
        path: 'index',
        component: () => import('@/views/profile/index'),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user', noCache: true }
      }
    ]
  },
  {
    path: '/projectlist',
    component: Layout,
    children: [
      {
        path: 'index',
        name: 'ProjectList',
        component: () => import('@/views/project/index'),
        meta: { title: '项目列表管理', icon: 'el-icon-menu' }
      }
    ]
  },
  {
    path: '/personlist',
    component: Layout,
    children: [
      {
        path: 'index',
        name: 'Personlist',
        component: () => import('@/views/person/index'),
        meta: { title: '人员管理', icon: 'el-icon-menu' }
      }
    ]
  },
  {
    path: '/personprojectchart',
    component: Layout,
    children: [
      {
        path: 'index',
        name: 'PersonProjectChart',
        component: () => import('@/views/person/personprojectchart'),
        meta: { title: '人员项目工时看板', icon: 'piechart' }
      }
    ]
  },

  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]
const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
