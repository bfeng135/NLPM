<template>
  <div class="report">
    <header-title>
      <template slot="title"> 日报管理 </template>
    </header-title>
    <div class="personSearch">
      <div style="margin-left:55px" class="searchBox">
        <el-date-picker
          v-model="startTime"
          type="date"
          placeholder="请选择开始日期"
          :picker-options="pickerOptions"
        />
        <el-date-picker
          v-model="endTime"
          type="date"
          placeholder="请选择结束日期"
          style="margin-left:5px"
          class="dailymargin"
          :picker-options="pickerOptions"
        />
        <el-select v-model="projectValue" placeholder="请选择项目" style="margin-left:5px" clearable filterable :disabled="reportloading" class="dailymargin" @change="selectProject">
          <el-option
            v-for="item in projectOptions"
            :key="item.id"
            :label="`${item.name}-${item.areaName}`"
            :value="item.id"
          />
        </el-select>
        <el-select v-if="role!=='EMPLOYEE'" v-model="projectReportPersonValue" placeholder="请选择人员" style="margin-left:5px" clearable filterable :disabled="reportloading" class="dailymargin">
          <el-option
            v-for="item in personOptions"
            :key="item.id"
            :label="item.nickname"
            :value="item.id"
          />
        </el-select>
        <el-button style="margin-left:5px" size="small" :disabled="reportloading" class="dailymargin serachbtn" type="success" @click="searchDailyReport">搜索</el-button>
      </div>
      <div style="margin-right:15px" class="btnBox">
        <!-- <el-button type="text" @click="handleAddDailyReport" :disabled="reportloading">
          <span class="el-icon-circle-plus" style="color:#13ce66;"></span>
          <span style="color:#606266;">新建日报</span>
        </el-button> -->
        <el-button type="text" :disabled="reportloading" @click="handleExportReport">
          <span class="el-icon-download" style="color:#13ce66;" />
          <span style="color:#606266;">导出日报</span>
        </el-button>
      </div>
    </div>
    <el-table
      v-loading="reportloading"
      :data="reportTableData"
      style="width: 100%"
    >
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-table :data="props.row.dayReportList" style="width: 100%">
            <el-table-column
              prop="projectName"
              label="项目名称"
              width="180"
            >
              <template slot-scope="scope">
                {{ scope.row.areaName }}-{{ scope.row.projectName }}
              </template>
            </el-table-column>
            <el-table-column
              prop="hours"
              label="项目时长"
              width="180"
            />
            <el-table-column
              prop="desc"
              label="项目描述"
            >
              <template slot-scope="scope">
                <span v-if="scope.row.desc!==null">{{ scope.row.desc }}</span>
                <span v-else>--</span>
              </template>
            </el-table-column>
          </el-table>
        </template>
      </el-table-column>
      <el-table-column
        type="index"
      >
        <template slot-scope="scope">
          <span>{{ (pageNo - 1) * pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="nickname"
        label="姓名"
      />
      <el-table-column
        prop="date"
        label="日期"
      >
        <template slot-scope="scope">
          <span>{{ scope.row.date|formatymd }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="关联区域"
      >
        <template slot-scope="scope">
          <span v-for="(item,index) in scope.row.dayReportList" :key="index">{{ item.areaName }}<br>
          </span>
        </template>
      </el-table-column>
      <el-table-column
        label="关联项目"
        width="200"
      >
        <template slot-scope="scope">
          <span v-for="(item,index) in scope.row.dayReportList" :key="index">
            <el-tooltip class="item" effect="dark" :content="item.projectName" placement="bottom">
              <span>{{ toJson(item.projectName) }}</span>
            </el-tooltip>
            <br>
          </span>
        </template>
      </el-table-column>
      <el-table-column
        label="工作时长"
      >
        <template slot-scope="scope">
          <span v-for="(item,index) in scope.row.dayReportList" :key="index">{{ item.hours }}<br>
          </span>
        </template>
      </el-table-column>
      <el-table-column
        prop="leaveVO.leaveHours"
        label="请假时长"
      >
        <template slot-scope="scope">
          <span v-if="scope.row.leaveVO.leaveHours!==null">{{ scope.row.leaveVO.leaveHours }}</span>
          <span v-else>--</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="leaveVO.desc"
        label="请假描述"
      >
        <template slot-scope="scope">
          <span v-if="scope.row.leaveVO.desc!==null">{{ scope.row.leaveVO.desc }}</span>
          <span v-else>--</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="118">
        <template slot-scope="scope">
          <el-button size="mini" title="编辑日报" icon="el-icon-edit-outline" :disabled="scope.row.isEdit&&userid==scope.row.userId&&readonly=='false'? false:true" type="warning" @click="handleEdit(scope.row)" />
          <!-- <el-button @click="handleDetail(scope.row)" type="success">日报详情</el-button> -->
          <el-button size="mini" title="删除日报" icon="el-icon-delete" type="danger" :disabled="scope.row.isEdit&&userid==scope.row.userId&&readonly=='false'? false:true" @click="handleDeletes(scope.row)" />
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      style="margin-top:10px;float:right"
      :current-page="pageNo"
      :page-sizes="[10, 20, 30]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
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
      readonly: localStorage.getItem('read-only'),
      reportTableData: [],
      startTime: (Date.parse(new Date(new Date().toLocaleDateString())) - 3600 * 1000 * 24 * 7),
      endTime: (Date.parse(new Date(new Date().toLocaleDateString()))),
      projectOptions: [],
      projectValue: null,
      pageNo: 1,
      pageSize: 10,
      total: 0,
      reportloading: false,
      reportSearch: null,
      projectReportPersonValue: null,
      personOptions: [],
      role: localStorage.getItem('role-code'),
      userid: localStorage.getItem('id'),
      deepTime: null,
      deepEndTime: null,
      exportTime: (Date.parse(new Date(new Date().toLocaleDateString())) - 3600 * 1000 * 24 * 7),
      exportEndTime: (Date.parse(new Date(new Date().toLocaleDateString())))
    }
  },
  watch: {
    startTime(val) {
      this.deepTime = val
      this.exportTime = this.getTime(val)
    },
    endTime(val) {
      this.deepEndTime = val
      this.exportEndTime = this.getTime(val)
    }
  },
  created() {
    // if (this.startTime < 1630425600000) {
    //   this.startTime = 1630425600000;
    // }
    this.getDailyReportList()
    this.getTime()
    // this.getManagerUser()
    // this.getProjectList()
    const role = localStorage.getItem('role-code')
    if (role == 'GROUP_MANAGER' || role == 'EMPLOYEE') {
      this.getAreaProject()
    } else {
      this.getPersonList()
      this.getProjectList()
    }
    if (role == 'GROUP_MANAGER') {
      this.getManagerUser()
    }
    // if(role!=='GROUP_MANAGER'|| role!=='EMPLOYEE'){

    // }
  },
  methods: {
    toJson(str) {
      return str.length > 9 ? str.substring(0, 9) + '...' : str
    },
    // setTime(val){
    //   let now = val
    //   var year = now.getFullYear(); //得到年份
    //   var month = now.getMonth(); //得到月份
    //   var date = now.getDate(); //得到日期
    //   month = month + 1;
    //   month = month.toString().padStart(2, "0");
    //   date = date.toString().padStart(2, "0");
    //   var defaultDate = `${year}-${month}-${date}`;
    //   // console.log(Date.parse(defaultDate)- 3600 * 1000 * 24 * 7)
    //   return Date.parse(new Date(new Date().toLocaleDateString()))
    //   // console.log(startDate)
    //   // return Date.parse(defaultDate)
    // },
    searchDailyReport() {
      const startdate = typeof this.startTime === 'number' ? this.startTime : this.getTime(this.deepTime)
      const enddate = typeof this.endTime === 'number' ? this.endTime : this.getTime(this.deepEndTime)
      if (startdate > enddate) {
        this.$message.error('结束日期必须大于起始日期')
      } else {
        this.pageNo = 1
        this.getDailyReportList()
      }
    },
    selectProject(data) {
      if (data) {
        this.$store.dispatch('person/getProjectAssignList', data).then(res => {
          this.personOptions = res
        })
      }
      if (this.role == 'AREA_MANAGER' || this.role == 'MANAGEMENT' || this.role == 'SUPER_ADMIN' || this.role == 'FINACE' || this.role == 'HR') {
        if (!data) {
          this.getPersonList()
        }
      } else if (this.role == 'GROUP_MANAGER') {
        if (!data) {
          this.getManagerUser()
        }
      }
    },
    // noneReport(){
    //   this.isNone = false
    //   const params = {
    //     currentPage: this.pageNo,
    //     endDate: this.getTime(this.endTime),
    //     pageSize: this.pageSize,
    //     projectId: this.projectValue,
    //     searchVal: this.reportSearch,
    //     startDate: this.getTime(this.startTime),
    //     userId: this.projectReportPersonValue
    //   }
    //   this.$store.dispatch('dailyreport/getNoneDayExchange',params).then(res=>{
    //     this.noneDailyReport = res.totalList
    //     this.total = res.total
    //   }).catch(err=>{})
    // },
    handleCurrentChange(val) {
      this.pageNo = val
      this.getDailyReportList()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getDailyReportList()
    },
    handleEdit(row) {
      this.$router.push({
        path: `/dailyreport/editreport/${row.id}`
      })
    },
    handleDetail(row) {
      this.$router.push({
        path: `/dailyreport/detailreport/${row.id}`
      })
    },
    getTime(time) {
      return Date.parse(time)
    },
    handleAddDailyReport() {
      this.$router.push('/dailyreport/createreport')
    },
    getPersonList() {
      const params = {
        areaId: 0,
        pageSize: 1000000,
        currentPage: 1,
        searchVal: null,
        nickname: '',
        projectId: null,
        roleId: null
      }
      const that = this
      that.$store
        .dispatch('person/getpseronList', params)
        .then((res) => {
          that.personOptions = res.totalList
        })
        .catch((err) => {
          that.loading = false
        })
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
      this.$store.dispatch('project/getProjectList', params).then(res => {
        this.projectOptions = res.totalList
      }).catch(err => {})
    },
    getAreaProject() {
      const params = {
        currentPage: 1,
        pageSize: 10000000,
        searchVal: null
      }
      this.$store.dispatch('project/getAssignUser', params).then(res => {
        this.projectOptions = res.totalList
      }).catch(err => {})
    },
    getManagerUser() {
      const id = localStorage.getItem('id')
      this.$store.dispatch('project/getManagerUser', id).then(res => {
        this.personOptions = res
      }).catch(err => {})
    },
    getDailyReportList() {
      this.isNone = true
      const params = {
        currentPage: this.pageNo,
        endDate: this.deepEndTime == null ? this.endTime : this.getTime(this.deepEndTime),
        pageSize: this.pageSize,
        projectId: this.projectValue,
        searchVal: this.reportSearch,
        startDate: this.deepTime == null ? this.startTime : this.getTime(this.deepTime),
        userId: this.projectReportPersonValue
      }
      this.reportloading = true
      this.$store.dispatch('dailyreport/getReportList', params).then(res => {
        this.reportTableData = res.totalList
        this.total = res.total
        this.reportloading = false
      }).catch(err => {})
    },
    handleDeletes(row) {
      this.$confirm(`此操作将删除该条日报, 是否继续?`, '删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        this.$store.dispatch('dailyreport/detleteReport', row.id).then(() => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          })
          this.getDailyReportList()
        }).catch(err => {
          this.$message.error(err)
        })
      })
    },
    handleExportReport() {
      const params = {
        currentPage: 1,
        endDate: this.exportEndTime,
        pageSize: 1000000,
        projectId: this.projectValue,
        searchVal: this.reportSearch,
        startDate: this.exportTime,
        userId: this.projectReportPersonValue
      }
      if (this.exportTime > this.exportEndTime) {
        this.$message.error('结束日期必须大于起始日期')
      } else {
        this.$store.dispatch('dailyreport/downloadDailyDetail', params).then(res => {
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
        }).catch(err => {})
      }
    }
  }

}
</script>

<style lang="scss">
.report{
  padding: 20px;
}
.report .personSearch{
  width: 100%;
  height: 50px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}
.report .el-input__inner{
  height: 32px;
}
.report .el-input .el-input__clear{
  margin-top: -4px;
}
.report .el-tag{
  border-radius: 20px;
}
.report .el-select__caret{
  display: flex;
  justify-content: center;
  align-items: center;
}
.report .el-input__icon {
  display: flex;
  align-items: center;
}
.report .el-input .el-input__clear{
}
@media only screen
and (min-device-width : 320px)
and (max-device-width : 500px){
  .el-date-editor.el-input, .el-date-editor.el-input__inner {
    width: 100%;
  }
  .personSearch{
    display: block !important;
  }
  .searchBox{
    margin-left:0 !important;
    width: 100% !important;
  }
  .report .personSearch{
  width: 100%;
  height: auto !important;
  }
  .btnBox{
    margin-left: 0 !important;
    text-align: right;
  }
  .el-button+.el-button {
    margin-left: 0px !important;
  }
  .dailymargin{
    margin-left: 0px !important;
    margin-top: 5px !important;
    width: 100% !important;
  }
  .serachbtn{
    background-color: #409eff;
    color: #fff;
  }
}
@media only screen
and (max-width : 1366px){
  .searchBox{
    margin-left:0 !important
  }
  .report .personSearch{
  width: 102% !important;
  height: auto !important;
  margin-left: -10px !important;
  }
  .btnBox{
    margin-right: 0 !important;
  }
}
</style>
