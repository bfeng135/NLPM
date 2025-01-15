<template>
  <div id="person-project-charts">
    <header-title style="margin-right: 0px; margin-bottom: 5px">
      <template slot="title">
        <span style="margin-left: 10px !important">人员工时看板</span>
      </template>
      <template slot="title">
        <el-select
          v-model="areaValue"
          placeholder="请选择区域"
          style="margin-top: 5px; margin-left: 5px"
          class="personSelectValue"
          clearable
          :disabled="
            role == 'AREA_MANAGER' ||
              role == 'GROUP_MANAGER' ||
              role == 'EMPlOYEE'
              ? true
              : false
          "
          filterable
        >
          <el-option
            v-for="item in areaOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </template>
      <template slot="title">
        <div style="display:inline-block;margin:0 8px 0  8px">日期选择</div>
        <el-date-picker
          v-model="personChartsStartValue"
          type="date"
          placeholder="选择开始日期"
          class="startTime"
          :picker-options="pickerOptions"
        />
        <el-date-picker
          v-model="personChartsEndValue"
          type="date"
          style="margin-left:5px;margin-right:5px"
          class="endTime"
          placeholder="选择结束日期"
          :picker-options="pickerOptions"
        />
      </template>
      <template slot="title" style="display:flex">
        <div style="display:inline-block;margin:0 8px 0  8px">人员状态</div>
        <el-radio-group v-model="radio">
          <el-radio :label="null">全部</el-radio>
          <el-radio :label="false">离开</el-radio>
          <el-radio :label="true">在职</el-radio>
        </el-radio-group>
      </template>
      <template slot="title">
        <el-button type="success" style="margin-left:10px" @click="search">搜索</el-button>
      </template>
    </header-title>
    <pie :key="menuKey" :list="userPersonList" :height="height" :title="'人员项目工时占比图'" />
  </div>
</template>

<script>
import headerTitle from '@/components/Title/index.vue'
import pie from '@/components/Charts/pie.vue'
import elementResizeDetectorMaker from 'element-resize-detector'
import pickerOptions from '@/mixins/index'
export default {
  components: {
    headerTitle,
    pie
  },
  mixins: [pickerOptions],
  data() {
    return {
      areaOptions: [],
      areaValue: null,
      role: localStorage.getItem('role-code'),
      projectOptions: [],
      projectValue: [],
      userPersonList: [],
      menuKey: 1,
      radio: true,
      personChartsStartValue: null,
      personChartsEndValue: null,
      height: null
    }
  },
  created() {
    this.getAreaList()
    this.getUserProjectList()
  },
  mounted() {
    this.getHeight()
  },
  methods: {
    search() {
      this.getUserProjectList()
      ++this.menuKey
      this.getHeight()
    },
    getHeight() {
      const heightResize = elementResizeDetectorMaker()
      heightResize.listenTo(document.getElementById('app'), (element) => {
        this.$nextTick(() => {
          this.height = element.offsetHeight - 138 + 'px'
        })
      })
    },
    getAreaList() {
      const params = {
        golbalName: '',
        pageNumber: 0,
        pageSize: 0
      }
      this.$store.dispatch('area/getAreaList', params).then((res) => {
        this.areaList = res.totalList
        const role = localStorage.getItem('role-code')
        const areaid = localStorage.getItem('area-id')
        if (role == 'GROUP_MANAGER') {
          res.totalList.forEach((item) => {
            if (item.id == areaid) {
              this.areaOptions.push(item)
              this.areaValue = item.id
              this.areaName = item.name
            }
          })
        } else if (role == 'EMPLOYEE') {
          res.totalList.forEach((item) => {
            if (item.id == areaid) {
              this.areaOptions.push(item)
            }
          })
        } else if (role == 'AREA_MANAGER') {
          res.totalList.forEach((item) => {
            if (item.id == areaid) {
              this.areaOptions.push(item)
              this.areaValue = item.id
            }
          })
        } else if (role == 'SUPER_ADMIN' || 'HR' || 'FINANCE' || 'MANAGEMENT') {
          this.areaOptions = res.totalList
        }
      })
    },
    getStartTime(date) {
      return Date.parse(date)
    },
    getEndTime(date) {
      return (Date.parse(date) - 1) + 24 * 60 * 60 * 1000
    },
    getUserProjectList() {
      const params = {
        areaId: this.areaValue == '' ? null : this.areaValue,
        startTime: this.personChartsStartValue == null ? null : this.getStartTime(this.personChartsStartValue),
        endTime: this.personChartsEndValue == null ? null : this.getEndTime(this.personChartsEndValue),
        statusFlag: this.radio
      }
      this.$store.dispatch('person/getProjectUserList', params).then(res => {
        this.userPersonList = []
        const list = []
        res.forEach(item => {
          list.push({
            name: item.nickname,
            resData: item.projectTimeList
          })
        })
        for (let i = 0; i < list.length; i++) {
          for (let j = 0; j < list[i].resData.length; j++) {
            list[i].resData[j].value = list[i].resData[j].hours
            delete list[i].resData[j].areaId
            delete list[i].resData[j].areaName
            delete list[i].resData[j].createTime
            delete list[i].resData[j].desc
            delete list[i].resData[j].enable
            delete list[i].resData[j].forceDescFlag
            delete list[i].resData[j].hours
            delete list[i].resData[j].id
            delete list[i].resData[j].managerId
            delete list[i].resData[j].managerName
            delete list[i].resData[j].systemProjectId
            delete list[i].resData[j].updateTime
          }
        }
        this.userPersonList = list
      }).catch(err => {})
    }
  }
}
</script>

<style>
#person-project-charts {
  padding: 20px;
  background-color: rgb(240, 242, 245);
}
#person-project-charts .content_title .title {
    margin-left: 5px !important;
}
@media only screen and (max-width:1500px) {
  #person-project-charts .el-date-editor.el-input, .el-date-editor.el-input__inner {
  width: 162px
}
}
@media only screen and (max-width:1366px) {
  #person-project-charts .personSelectValue {
    width: 160px !important
  }
}
@media only screen
and (min-device-width : 330px)
and (max-device-width : 500px){
  #person-project-charts .el-select{
    display: block !important;
    width: 100% !important;
  }
  #person-project-charts .el-input {
    display: block !important;
    width: 95% !important;
  }
  .startTime{
    margin-left: 5px !important;
  }
  .endTime{
    margin-top: 5px;
  }
  #person-project-charts .el-button{
    margin-left: 5px !important;
    width: 95% !important;
    margin-bottom: 5px !important;
  }
}
</style>
