<template>
  <div class="dayexchange">
    <header-title>
      <template slot="title"> 休假管理 </template>
    </header-title>
    <div class="daySearch">
      <div style="margin-left:55px" class="dayexchangeSearchBox">
        <el-input v-model="search" style="width:200px" placeholder="请输入员工姓名" clearable class="dayexchangeinput" @keyup.enter.native="searchDayExchanget" />
        <el-select v-model="areaValue" placeholder="请选择区域" style="margin-left:5px" clearable filterable class="dayexchangeSearchBoxmargin dayexchangeinput" :disabled="role=='AREA_MANAGER'? true:false" @change="selectArea">
          <el-option
            v-for="item in areaOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
        <el-button style="margin-left:5px" size="small" class="dayexchangeSearchBoxmargin dayexchangeinput dayexsearchbtn" type="success" @click.native.prevent="searchDayExchanget">搜索</el-button>
      </div>
      <el-card shadow="never">
        <div style="margin-right: 15px" class="projectrexport">
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
            style="margin:0 5px 0 5px "
            class="projectreportsearch"
            :picker-options="pickerOptions"
          />
          <el-button type="text" :disabled="projectReportStartTime!==null&&projectReportEndTime!==null? false:true" @click="exportReport">
            <span
              class="el-icon-download"
              style="color: #13ce66; font-size: 16px"
            />
            <span style="color: #606266">导出报表</span>
          </el-button>
        </div>
      </el-card>
    </div>
    <el-table v-loading="dayloading" :data="dayexchangeData" style="width:100%">
      <el-table-column type="index">
        <template slot-scope="scope">
          <span>{{ (pageNo - 1) * pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="区域名称" prop="areaName" />
      <el-table-column label="员工姓名" prop="nickname">
        <template slot-scope="scope">
          <span style="color:blue;cursor: pointer;" @click="detail(scope.row)">{{ scope.row.nickname }}</span>
        </template>
      </el-table-column>
      <el-table-column label="请假时长" prop="leaveHour" />
    </el-table>
    <el-pagination
      :current-page="pageNo"
      :page-sizes="[10,20,30]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      style="float:right;margin-top:10px"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
    <detailchange :detailchange.sync="detailchange" :detail-data="detailData" />
  </div>

</template>

<script>
import { getTime } from '@/utils/handletime'
import headerTitle from '@/components/Title/index.vue'
import detailchange from './components/detailchange'
import pickerOptions from '@/mixins/index'
export default {
  components: {
    headerTitle,
    detailchange
  },
  mixins: [pickerOptions],
  data() {
    return {
      areaOptions: [],
      areaValue: null,
      personValue: null,
      personOptions: [],
      search: null,
      pageSize: 10,
      pageNo: 1,
      dayexchangeData: [],
      total: 0,
      detailchange: false,
      detailData: [],
      dayloading: false,
      role: localStorage.getItem('role-code'),
      projectReportStartTime: null,
      projectReportEndTime: null,
      deepAreaValue: []
    }
  },
  created() {
    this.getdayExchangeList()
    this.getAreaList()
  },
  methods: {
    selectArea(data) {
      this.deepAreaValue = []
      if (data) {
        this.deepAreaValue.push(data)
      }
    },
    exportReport() {
      const params = {
        areaId: this.deepAreaValue.length == 0 ? [null] : this.deepAreaValue,
        endTime: getTime(this.projectReportEndTime),
        startTime: getTime(this.projectReportStartTime),
        userId: [null]
      }
      this.$store.dispatch('dailyreport/downloadLeaveList', params).then(res => {
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
    },
    searchDayExchanget() {
      this.pageNo = 1
      this.getdayExchangeList()
    },
    detail(row) {
      this.detailchange = true
      this.$store.dispatch('dailyreport/getDayExchangeDetail', row.userId).then(res => {
        this.detailData = res
      })
    },
    getAreaList() {
      const params = {
        golbalName: '',
        pageNumber: 0,
        pageSize: 0
      }
      this.$store.dispatch('area/getAreaList', params).then(res => {
        const role = localStorage.getItem('role-code')
        const areaid = localStorage.getItem('area-id')
        if (role == 'GROUP_MANAGER') {
          res.totalList.forEach(item => {
            if (item.id == areaid) {
              this.areaOptions.push(item)
            }
          })
        } else if (role == 'EMPLOYEE') {
          res.totalList.forEach(item => {
            if (item.id == areaid) {
              this.areaOptions.push(item)
            }
          })
        } else if (role == 'AREA_MANAGER') {
          res.totalList.forEach(item => {
            if (item.id == areaid) {
              this.areaOptions.push(item)
              this.areaValue = item.id
              this.deepAreaValue.push(item.id)
            }
          })
        } else if (role == 'SUPER_ADMIN' || role == 'HR' || role == 'FINANCE' || role == 'MANAGEMENT') {
          this.areaOptions = res.totalList
        }
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getdayExchangeList()
    },
    handleCurrentChange(val) {
      this.pageNo = val
      this.getdayExchangeList()
    },
    getdayExchangeList() {
      const params = {
        areaId: this.areaValue,
        currentPage: this.pageNo,
        nickname: this.search,
        pageSize: this.pageSize,
        searchVal: null
      }
      this.dayloading = true
      this.$store.dispatch('dailyreport/getLeaveList', params).then(res => {
        console.log(res)
        this.dayexchangeData = res.totalList
        this.total = res.total
        this.dayloading = false
      }).catch(err => {})
    }
  }
}
</script>

<style lang="scss">
.dayexchange{
  padding: 20px
}
.daySearch{
  width: 100%;
  height: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}
.dayexchange .el-input__inner{
  height: 32px;
}
.dayexchange .el-input .el-input__clear{
  margin-top: -4px;
}
.dayexchange .el-tag{
  border-radius: 20px;
}
.dayexchange .el-select__caret{
  display: flex;
  justify-content: center;
  align-items: center;
}
.dayexchange .el-input__icon {
  display: flex;
  align-items: center;
}
@media screen and (max-width : 1366px){
  .dayexchangeSearchBox{
    margin-left: 0px !important;
  }
}
@media only screen
and (min-device-width : 320px)
and (max-device-width : 500px){
  .daySearch{
    width: 100%;
    height: auto !important;
    display: block !important
  }
  .dayexchangeSearchBox{
    margin-left: 0 !important;
  }
  .dayexchangeSearchBoxmargin{
    margin-left: 0px !important;
  }
  .areainput{
    width: 200px !important;
  }
  .dayexchangeinput{
    width: 100% !important;
    margin-bottom: 5px !important;
  }
  .dayexsearchbtn{
    background-color: #409eff;
    color: #fff;
  }
}
</style>
