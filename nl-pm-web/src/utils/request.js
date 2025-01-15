import axios from 'axios'
import { Message,MessageBox } from 'element-ui'
import store from '@/store'
import router from '@/router';
import { getToken } from '@/utils/auth'

// create an axios instance
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 50000 // request timeout
})

// request interceptor
service.interceptors.request.use(
  config => {
    // do something before request is sent
    if(store.getters.token && config.url !== '/login'){
      config.headers.Authorization = ['Bearer ', getToken()].join('');
    }
    if (store.getters.token) {
      // let each request carry token
      // ['X-Token'] is a custom headers key
      // please modify it according to the actual situation
      config.headers['X-Token'] = getToken()
    }
    return config
  },
  error => {
    // do something with request error
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  response => {
    const res = response.data
    if(response.status == 200){
      return response
    }
  },
  error => {
    if(error.response==undefined){
      Message({
        message: '服务器端错误,请联系管理员',
        type: 'error',
        showClose: true,
        duration: 3000,
      })
    }
    if(error.response.status==401){
      Message({
        message: error.response.data.errorMessage || '凭证已过期，请重新登录',
        type: 'error',
        showClose: true,
        duration: 3000,
      })
      setTimeout(()=>{
        store.dispatch("user/resetToken").then(() => {
          // location.reload();
          router.push('/login')
        });
      },1000)
    }
    if(error.response.status==500){
      Message({
        message: error.response.data.errorMessage || '所选参数查询暂无数据',
        type: 'error',
        showClose: true,
        duration: 3000,
      })
    }
    console.log('err' + error) // for debug
    // Message({
    //   message: error.message,
    //   type: 'error',
    //   duration: 5 * 1000
    // })
    return Promise.reject(error)
  }
)

export default service
