<template>
  <div id="project-echarts">
    <el-row :gutter="32" style="margin-bottom:10px">
    <el-col :xs="24" :sm="24" :lg="24" class="card-panel-col">
        <div class="chart-wrapper">
           <project-echarts/>
        </div>
    </el-col>
    </el-row>
    <el-divider></el-divider>
    <header-title style="margin-right: 0px; margin-bottom: 5px">
      <template slot="title"> <span style="margin-left:10px !important">项目工时统计</span> </template>
      <template slot="title">
        <el-select
          v-model="areaValue"
          placeholder="请选择区域"
          style="margin-top: 5px;margin-left:5px"
          clearable 
          @change="areaChange" 
          :disabled="role=='AREA_MANAGER'||role=='GROUP_MANAGER'||role=='EMPlOYEE'? true:false" 
          filterable
        >
          <el-option
            v-for="item in areaOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          >
          </el-option>
        </el-select>
      </template>
    </header-title>
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="跨区工时占比图" name="first">
        <transition name="chartsFade">
          <pie :list="list" :key="menuKey" :title="seriesTitle" :height="height"/>
        </transition>
      </el-tab-pane>
      <el-tab-pane label="人员工时占比图" name="second">
        <transition name="chartsFade">
          <pie :list="userHoursList" :key="userHoursKey" :title="'项目人员工时占比图'" :height="height"/>
        </transition>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import pie from "@/components/Charts/pie.vue";
import headerTitle from "@/components/Title/index.vue";
import projectEcharts from './projectbar.vue'
import elementResizeDetectorMaker from 'element-resize-detector'
export default {
  components: {
    headerTitle,
    pie,
    projectEcharts
  },
  created() {
    
  },
  mounted(){
    this.getAreaList()
    this.getHeight()
  },
  data() {
    return {
      projectCharts: [],
      chart: null,
      list: [],
      areaOptions:[],
      areaValue:null,
      role:localStorage.getItem('role-code'),
      flags:true,
      menuKey:1,
      loading:false,
      areaName:null,
      seriesTitle:'跨区项目工时占比图',
      activeName: 'first',
      userHoursList:[],
      userHoursKey:1,
      accountTitle:'跨区项目工时占比图',
      height:null
    };
  },
  methods: {
    getHeight(){
        const heightResize = elementResizeDetectorMaker()
        heightResize.listenTo(document.getElementById("app"),(element)=>{
          this.height = element.offsetHeight - 138 + 'px'
        })
    },
    handleClick(){
      if(this.activeName === 'second'){
        ++this.userHoursKey
        ++this.menuKey
        this.getUserHoursChart()
        this.getHeight()
      }else{
        ++this.menuKey
        this.getHeight()
        this.getCharts(this.areaName)
      }
    },
    areaChange(data){
      if(this.activeName === 'first'){
        if(data){
          this.getCharts()
          ++this.menuKey
        }else{
          this.areaValue = null
          ++this.menuKey
          this.getCharts()
        }
      }
      if(this.activeName === 'second'){
        if(data){
          ++this.userHoursKey
          this.getUserHoursChart()
        }else{
          this.areaValue = null
          ++this.userHoursKey
          this.getUserHoursChart()
        }
      }
    },
    getAreaList(){
      const params = {
        golbalName: '',
        pageNumber: 0,
        pageSize: 0
      }
      this.$store.dispatch('area/getAreaList',params).then(res=>{
        const role = localStorage.getItem('role-code')
        const areaid = localStorage.getItem('area-id')
        if(role=='GROUP_MANAGER'){
          res.totalList.forEach(item=>{
            if(item.id == areaid){
              this.areaOptions.push(item)
              this.areaValue = item.id
              this.areaName = item.name
              this.getCharts(item.name)
              // this.getUserHoursChart()
            }
          })
        }else if(role=='EMPLOYEE'){
          res.totalList.forEach(item=>{
            if(item.id == areaid){
              this.areaOptions.push(item)
            }
          })
        }else if(role=='AREA_MANAGER'){
          res.totalList.forEach(item=>{
            if(item.id == areaid){
              this.areaOptions.push(item)
              this.areaValue = item.id
              this.areaName = item.name
              this.getCharts(item.name)
            }
          })
        }else if(role=='SUPER_ADMIN'||'HR'||'FINANCE'||'MANAGEMENT'){
          this.areaOptions = res.totalList;
          this.getCharts()
          // this.getUserHoursChart()
        }

      })
    },
    getCharts(name) {
      this.loading = true
      const params = {
        mainAreaId: this.areaValue,
      };
      this.$store
        .dispatch("project/getProjectCharts", params)
        .then((res) => {
          this.loading = false
          this.list = []
          res.data.forEach(item=>{
            if(item.enable==true||item.enable==null){
              this.list.push(item)
            }
          })
          for(let i = 0; i < this.list.length; i++) {
            if(this.list[i].resData.length == 1 && this.list[i].resData[0].name==name) {
              this.list.splice(i,1);
              i--;
            }
          }
        })
        .catch((err) => {
          this.loading = false
        });
    },
    getUserHoursChart(){
      const params = {
        mainAreaId:this.areaValue
      }
      this.$store.dispatch('project/getUserHoursChart',params).then(res=>{
        this.userHoursList = res
      }).catch(err=>{})
    }
  },
};
</script>

<style>
#project-echarts {
  padding: 20px;
  background-color: rgb(240, 242, 245);
}
.chartsFade-enter-active {
  transition: opacity 3s;
}
.chartsFade-enter,
.chartsFade-leave-to {
  opacity: 0;
}
.chart-wrapper {
  background: #fff;
  padding: 16px 16px 0;
  margin-bottom: 32px;
}
#project-echarts .content_title .title {
  margin-left: 5px !important;
}
</style>