<template>
  <div
    id="pie"
    ref="workHoursPie"
    v-loading="chartloading"
    :class="className"
    :style="{height:height,width:width}"
    element-loading-text="数据加载中..."
    element-loading-spinner="el-icon-loading"
  />
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from '@/views/dashboard/components/charts/mixins/resize'
export default {
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
      default: '350px'
    },
    dataList: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      chart: null,
      roleCode: localStorage.getItem('role-code'),
      chartloading: false
    }
  },
  watch: {
    dataList(val) {
      if (val) {
        this.initChart()
      }
    }
  },
  mounted() {
    setTimeout(() => {
      this.initChart()
    }, 1000)
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    // this.chart.dispose()
    // this.chart = null
  },
  methods: {
    initChart() {
      if (this.chart != null && this.chart !== '' && this.chart !== undefined) {
        this.chart.dispose()
      }
      this.chart = echarts.init(this.$refs.workHoursPie, 'macarons')
      const metadata = []
      const datasource = []
      const domId = document.getElementById('pie')
      if (this.dataList && this.dataList.length > 0) {
        for (const item of this.dataList) {
          metadata.push(item.name)
          datasource.push({ value: item.value, name: item.name })
        }
      } else {
        domId.innerHTML = `<h4 style="font-size:20px;text-align:center">暂无数据</h4>`
      }
      this.chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)',
          backgroundColor: 'rgba(50,50,50,0.6)',
          borderWidth: 0,
          textStyle: {
            color: '#ffffff'
          },
          position: 'top'
        },
        legend: {
          left: 'center',
          bottom: '10',
          data: metadata,
          type: 'scroll',
          formatter: function(value) {
            return value.length > 14 ? value.substring(0, 14) + '...' : value
          }
        },
        series: [
          {
            name: '人员工时统计',
            type: 'pie',
            roseType: 'radius',
            radius: [15, 95],
            center: ['50%', '38%'],
            data: datasource,
            animationEasing: 'cubicInOut',
            animationDuration: 2600
          }
        ]
      })
    }
  }
}
</script>
