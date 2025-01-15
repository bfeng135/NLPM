<template>
  <div class="project-bar">
    <div class="screenBars">
      <header-title class="headerTitle">
      <template slot="title"> 
        <span class="title-margin" v-if="this.roleCode=='SUPER_ADMIN'|| this.roleCode=='MANAGEMENT' || this.roleCode=='FINANCE'">全部项目工时消耗统计</span>
        <span class="title-margin" v-else>当前区域项目工时消耗统计</span>
      </template>
      <template slot="title">
        <el-select
          v-model="barValue"
          placeholder="请选择项目进行筛选"
          style="margin-top: 5px;margin-left:5px;width:400px"
          class="projectBarSelect"
          clearable
          filterable
          multiple
          collapse-tags
        >
          <el-option
            v-for="item in barProjectList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          >
          </el-option>
        </el-select>
        <el-button type="success" style="margin-left:5px" @click="getDataSource">筛选</el-button>
      </template>
    </header-title>
    </div>
    <div
    v-loading="chartloading"
    :class="className"
    :style="{height:height,width:width}"
    id="bar"
    element-loading-text="数据加载中..."
    element-loading-spinner="el-icon-loading"
    ref="myEchart1">
    <h3 style="text-align:center;font-size:20px">{{Txt}}</h3>
  </div>
  </div>
    
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from '@/views/dashboard/components/charts/mixins/resize'
import headerTitle from "@/components/Title/index.vue";
const animationDuration = 6000
const color = '#36a3f7'
const decalColor = 'rgba(51, 51, 51, 0.2)'

export default {
  components: {
    headerTitle,
  },
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '600px'
    }
  },
  data() {
    return {
      chart: null,
      chartloading: false,
      roleCode: localStorage.getItem('role-code'),
      Txt:'',
      autoHeight:null,
      barValue:[],
      barProjectList:[],
      deepBarList:[]
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.getDataSource()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart(data) {
      this.chart = echarts.init(this.$refs.myEchart1, 'macarons')
      const metadata = []
      const datasource = {
        name: (this.roleCode === 'HR') ? '人员总计' : '工时总计',
        type: 'bar',
        stack: 'vistors',
        barWidth: '55%',
        data: [],
        calculable: true,
        animationDuration
      }
      const domId = document.getElementById('bar')
      if (data && data.length > 0) {
        for (const item of data) {
          metadata.push(item.name)
          datasource.data.push(item.value)
          // let data = {}
          // if(item.id>item.value){
          //   data = {
          //     value:item.value,
          //     itemStyle:{
          //       color:'red'
          //     }
          //   }
          // }else{
          //   data = {
          //     value:item.value,
          //     itemStyle:{
          //       color:'#36a3f7'
          //     }
          //   }
          // }
          // datasource.data.push(data)
        }
      }else{
        domId.innerHTML = `<h4 style="font-size:20px;text-align:center">暂无数据</h4>`
      }

      this.chart.setOption({
        // title: {
        //   text: (this.roleCode=='SUPER_ADMIN'|| this.roleCode=='MANAGEMENT' || this.roleCode=='FINANCE' )? '全部项目工时消耗统计':'当前区域项目工时消耗统计',
        //   subtext: ''
        // },
        dataZoom: [
          {
            id: 'dataZoomX',
            type: 'slider',
            xAxisIndex: [0],
            filterMode: 'filter',
            bottom: 10,
            handleSize: 8,
            show:true,
          },
        ],
        tooltip: {
          trigger: 'axis',
          formatter(params){
            const role = localStorage.getItem('role-code')
            const item = params[0];
            if(role=='HR'){
              return `
                ${item.name}<br/>
                人员总计: ${item.data}<br/>
               `;
            }else{
              //  return `
              //   ${item.name}<br/>
              //   人/时: ${item.data.value} 小时<br/>
              //   人/天: ${(item.data.value/8).toFixed(2)} 天<br/>
              //   人/月: ${(item.data.value/8/22).toFixed(2)} 月
              //  `;
              return `
                ${item.name}<br/>
                人/时: ${item.data} 小时<br/>
                人/天: ${(item.data/8).toFixed(2)} 天<br/>
                人/月: ${(item.data/8/22).toFixed(2)} 月
               `;
            }
            
          },
          axisPointer: { // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
          },
          backgroundColor: 'rgba(50,50,50,0.6)',
          borderWidth: 0,
          textStyle: {
            color: '#ffffff'
          }
        },
        grid: {
          top: 35,
          left: '2%',
          right: '2%',
          bottom: 50,
          containLabel: true
        },
        xAxis: [{
          type: 'category',
          data: metadata,
          show: true,
          axisTick: {
            alignWithLabel: true
          },
          axisLabel: {
            rotate: 20,
            formatter:function(value)
              {  
                return value.length > 14 ? value.substring(0,14) + "..." : value;
              }
          }
        }],
        yAxis: [{
          type: 'value',
          show: true,
          axisTick: {
            show: false
          }
        }],
        series: datasource,
        color: color,
        aria: {
          label: {
            description: (this.roleCode === 'HR') ? '这是区域人员详情柱状图表' : '这是工时消耗统计柱状图表',
            enabled: true
          },
          enabled: true,
          decal: {
            show: true,
            decals: [{
              color: decalColor,
              dashArrayX: [1, 0],
              dashArrayY: [2, 5],
              symbolSize: 1,
              rotation: Math.PI / 6
            }]
          }
        }
      })
    },
    getDataSource() {
      this.chartloading = true
      const params = {
        pjNumber:0
      }
      this.$store
        .dispatch('project/getProjectBoardList', params)
        .then((res) => {
          this.chartloading = false
          this.barProjectList = res
          this.deepBarList = []
          if(this.barValue.length>0){
            this.barValue.forEach(item=>{
              res.forEach(element=>{
                if(item==element.id){
                  this.deepBarList.push(element)
                }
              })
            })
          }else{
            this.deepBarList = res
          }
          this.initChart(this.deepBarList)
        })
        .catch(() => {
          this.Txt = '服务端错误，请检查网络连接或联系管理员'
          this.chartloading = false
        })
    }
  }
}
</script>

<style>
.project-bar{
  height:700px
}
.screenBars{
  margin-bottom: 10px;
}
.headerTitle{
  margin-right: 0px; 
  margin-bottom: 5px;
  margin-left:0 !important
}
.title-margin{
  margin-left: -5px;
}
.project-bar .content_title{
  border-bottom: none !important;
}

@media only screen
and (min-device-width : 330px)
and (max-device-width : 500px){
  .project-bar .projectBarSelect{
    width: 200px !important;
  }
}
</style>