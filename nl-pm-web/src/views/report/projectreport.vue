<template>
  <div class="project-report">
    <header-title>
      <template slot="title"> 单报表查询 </template>
    </header-title>
    <div class="projectReportSearch">
      <div style="margin-left: 55px" class="projectReportSearchBox">
        <el-select
          v-model="areaValue"
          placeholder="请选择区域"
          style="margin-left: 5px"
          clearable
          collapse-tags
          multiple
          :disabled="role=='AREA_MANAGER'? true:false"
          filterable
          class="projectreportsearch"
        >
          <el-option
            v-for="item in areaOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
        <el-select
          v-model="projectValue"
          placeholder="请选择项目(必填)"
          style="margin-left: 5px"
          clearable
          filterable
          class="projectreportsearch"
        >
          <el-option
            v-for="item in projectOptions"
            :key="item.id"
            :label="item.name"
            :value="item.name"
          />
        </el-select>

        <el-date-picker
          v-model="projectReportStartTime"
          type="date"
          placeholder="选择查询开始日期(必填)"
          style="margin-left: 5px"
          class="projectreportsearch"
          :picker-options="pickerOptions"
        />
        <el-date-picker
          v-model="projectReportEndTime"
          type="date"
          placeholder="选择查询结束日期(必填)"
          style="margin-left: 5px"
          class="projectreportsearch"
          :picker-options="pickerOptions"
        />
        <el-button
          style="margin-left: 5px"
          size="small"
          :disabled="projectValue!==''&&projectReportStartTime!==null&&projectReportEndTime!==null?false:true"
          class="projectreportsearch pReportSearchBtn"
          type="success"
          @click="getProjectReportList"
        >搜索</el-button>
      </div>
      <div style="margin-right: 15px" class="projectrexport">
        <el-button type="text" :disabled="projectValue!==''&&projectReportStartTime!==null&&projectReportEndTime!==null? false:true" @click="exportReport">
          <span
            class="el-icon-download"
            style="color: #13ce66; font-size: 16px"
          />
          <span style="color: #606266">导出报表</span>
        </el-button>
      </div>
    </div>
    <el-table v-loading="projectReportLoading" :data="projectReportTableData" style="width: 100%">
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-table :data="props.row.resReportFormByTimeVOS" style="width:100%">
            <el-table-column label="工作日" prop="workDay" />
            <el-table-column label="工作时长" prop="workTime" />
          </el-table>
        </template>
      </el-table-column>
      <el-table-column type="index" />
      <el-table-column label="项目名称" prop="projectName" />
      <el-table-column label="姓名" prop="nickname" />
      <el-table-column label="总时长" prop="userTotalTime" />
    </el-table>
  </div>
</template>

<script>
import headerTitle from '@/components/Title/index.vue'
import pickerOptions from '@/mixins/index'
export default {
  components: {
    headerTitle
  },
  mixins: [pickerOptions],
  data() {
    return {
      projectReportTableData: [],
      projectReportStartTime: null,
      projectReportEndTime: null,
      projectValue: '',
      projectOptions: [],
      areaValue: [],
      areaOptions: [],
      projectReportLoading: false,
      role: localStorage.getItem('role-code'),
      isDisabled: true
    }
  },
  watch: {
    // projectReportStartTime(val){
    //   if(val&&this.projectReportEndTime!==''){
    //     this.isDisabled = false
    //   }
    // },
    // projectReportEndTime(val){
    //   console.log(val)
    // }
    projectValue(val) {
      console.log(val)
    }
  },
  created() {
    this.getProjectReportList()
    this.getAreaList()
    this.getProjectList()
  },
  mounted() {
  },
  methods: {
    getTime(time) {
      return Date.parse(time)
    },
    unique(arr) {
      const res = new Map()
      this.projectOptions = arr.filter((arr) => !res.has(arr.name) && res.set(arr.name, 1))
    },
    getAreaList() {
      const params = {
        golbalName: '',
        pageNumber: 0,
        pageSize: 0
      }

      this.$store.dispatch('area/getAreaList', params).then((res) => {
        // this.areaOptions = res.totalList;
        const role = localStorage.getItem('role-code')
        const areaid = localStorage.getItem('area-id')
        if (role == 'AREA_MANAGER') {
          res.totalList.forEach(item => {
            if (item.id == areaid) {
              this.areaOptions.push(item)
              this.areaValue.push(item.id)
            }
          })
        } else {
          this.areaOptions = res.totalList
        }
      }).catch(err => {})
    },
    getProjectList() {
      const params = {
        areaId: null,
        currentPage: 1,
        desc: '',
        managerId: null,
        name: '',
        pageSize: 1000000,
        searchVal: '',
        userId: null
      }
      this.$store.dispatch('project/getProjectList', params).then((res) => {
        this.projectOptions = res.totalList
        this.unique(this.projectOptions)
      }).catch(err => {})
    },
    getProjectReportList() {
      const params = {
        areaId: this.areaValue,
        endTime: this.getTime(this.projectReportEndTime),
        currentProjectName: this.projectValue,
        startTime: this.getTime(this.projectReportStartTime)
      }
      if (this.getTime(this.projectReportStartTime) > this.getTime(this.projectReportEndTime)) {
        this.$message.error('结束日期必须大于起始日期')
      } else {
        this.projectReportLoading = true
        this.$store
          .dispatch('report/getReportFormProject', params)
          .then((res) => {
            this.projectReportLoading = false
            this.projectReportTableData = res
          }).catch(err => {
            this.projectReportTableData = []
            this.projectReportLoading = false
          // this.$message.error(err)
          })
      }
    },
    exportReport() {
      if (this.getTime(this.projectReportStartTime) > this.getTime(this.projectReportEndTime)) {
        this.$message.error('结束日期必须大于起始日期')
      } else if (this.projectValue == '') {
        this.$message.error('请选择项目')
      } else {
        const params = {
          areaId: this.areaValue,
          endTime: this.getTime(this.projectReportEndTime),
          currentProjectName: this.projectValue,
          projectName: null,
          startTime: this.getTime(this.projectReportStartTime)
        }
        this.$store.dispatch('report/downReportForm', params).then(res => {
          const contentDisposition = res.headers['content-disposition']
          const fileName = decodeURI(contentDisposition.substring(contentDisposition.indexOf('=') + 1))
          const url = window.URL.createObjectURL(new Blob([res.data], { type: 'application/vnd.ms-excel;charset=UTF-8' }))
          const a = document.createElement('a')
          a.style.display = 'none'
          a.href = url
          a.setAttribute('download', fileName)
          document.body.appendChild(a)
          a.click()
          document.body.removeChild(a)
          window.URL.revokeObjectURL(url)
          this.$message.success('下载成功')
        }).catch(err => {
        })
      }
    }
  }
}
</script>

<style>
.project-report {
  padding: 20px;
}
.projectReportSearch {
  display: flex;
  width: 100%;
  height: 50px;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}
.project-report .el-input__inner {
  height: 32px;
}
.project-report .el-input .el-input__clear {
  margin-top: -4px;
}
.project-report .el-select__caret {
  display: flex;
  justify-content: center;
  align-items: center;
}
.project-report .el-input__icon {
  display: flex;
  align-items: center;
}
@media only screen
and (min-device-width : 320px)
and (max-device-width : 500px){
  .projectReportSearch{
  width: 100%;
  height: auto !important;
  display: block !important;
  }
  .projectReportSearchBox{
    margin-left: 0 !important;
  }
  .projectreportsearch{
    margin-left: 0px !important;
    width: 100% !important;
    margin-top: 5px;
  }
  .projectrexport{
    text-align: right;
    margin-right: 0px !important;
  }
  .pReportSearchBtn{
    background-color: #409eff;
    color: #fff;
  }
}
</style>
