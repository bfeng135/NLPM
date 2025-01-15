<template>
  <div class="nonereport">
    <header-title>
      <template slot="title"> 未填写日报查询 </template>
    </header-title>
    <div class="personSearch">
      <div style="margin-left: 55px" class="nonereportBox">
        <el-date-picker
          v-model="startTime"
          type="date"
          placeholder="请选择开始日期"
          value-format="timestamp"
          :picker-options="pickerOptions"
        />
        <el-date-picker
          v-model="endTime"
          type="date"
          placeholder="请选择结束日期"
          style="margin-left: 5px"
          class="nonedailymargin"
          :picker-options="EndPickerOptions"
        />
        <el-button
          style="margin-left: 5px"
          size="small"
          class="nonedailymargin nonereportsearch"
          type="success"
          @click="searchNoneReport"
        >搜索</el-button>
      </div>
    </div>
    <el-table v-loading="noneReportLoading" :data="noneDailyReport" style="width: 100%">
      <el-table-column type="index">
        <template slot-scope="scope">
          <span>{{ (pageNo - 1) * pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="date" label="日期" width="180">
        <template slot-scope="scope">
          <span>{{ scope.row.date | formatymd }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="userList" label="人员列表">
        <template slot-scope="scope">
          <span v-for="(item, index) in scope.row.userList" :key="index">
            【{{ item.nickname }}】
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button size="mini" :disabled="scope.row.isReport && readonly=='false'? false:true" @click="toReport(scope.row)">补日报</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      style="margin-top: 10px; float: right"
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
      startTime: 1630425600000,
      endTime: new Date((Date.parse(new Date(new Date().toLocaleDateString())) - 3600 * 1000 * 24 * 1)),
      projectOptions: [],
      projectValue: null,
      projectReportPersonValue: null,
      personOptions: [],
      pageNo: 1,
      pageSize: 10,
      total: 0,
      noneDailyReport: [],
      role: localStorage.getItem('role-code'),
      userId: localStorage.getItem('id'),
      noneReportLoading: false,
      EndPickerOptions: {
        disabledDate(date) {
          const zero = new Date().setHours(0, 0, 0, 0)
          if (date.getTime() > zero) {
            return true
          }
          return false
        }
      }
    }
  },
  created() {
    const startDate = this.getTime(this.startTime)
    if (startDate < 1630425600000) {
      this.startTime = new Date(1630425600000)
    }
    this.getNnoneReport()
  },
  methods: {
    searchNoneReport() {
      this.pageNo = 1
      this.getNnoneReport()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getNnoneReport()
    },
    handleCurrentChange(val) {
      this.pageNo = val
      this.getNnoneReport()
    },
    getTime(time) {
      return Date.parse(time)
    },
    toReport(row) {
      this.$router.push({ name: 'DailyReportCreate', params: { code: 1000 }})
      this.$store.dispatch('dailyreport/setDate', row.date)
      this.$bus.$emit('nonedate', row.date)
    },
    getNnoneReport() {
      if (this.getTime(this.startTime) > this.getTime(this.endTime)) {
        this.$message.error('结束日期必须大于起始日期')
      } else {
        this.isNone = false
        const params = {
          currentPage: this.pageNo,
          endDate: this.getTime(this.endTime),
          pageSize: this.pageSize,
          projectId: null,
          searchVal: this.reportSearch,
          startDate: this.startTime,
          userId: null
        }
        this.noneReportLoading = true
        this.$store
          .dispatch('dailyreport/getNoneDayExchange', params)
          .then((res) => {
            this.noneReportLoading = false
            this.noneDailyReport = res.totalList
            this.total = res.total
            this.noneDailyReport.map(item => {
              if (item.userList.length > 0) {
                item.userList.map(item1 => {
                  if (item1.userId == this.userId) {
                    item.isReport = true
                  }
                })
              }
              return this.noneDailyReport
            })
          })
          .catch((err) => {
            this.noneReportLoading = false
          })
      }
    }
  }
}
</script>

<style>
.nonereport {
  padding: 20px;
}
.nonereport .personSearch {
  width: 100%;
  height: 50px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}
.nonereport .el-input__inner {
  height: 32px;
}
.nonereport .el-input .el-input__clear {
  margin-top: -4px;
}
.nonereport .el-tag {
  border-radius: 20px;
}
.nonereport .el-select__caret {
  display: flex;
  justify-content: center;
  align-items: center;
}
.nonereport .el-input__icon {
  display: flex;
  align-items: center;
}
@media only screen
and (min-device-width : 320px)
and (max-device-width : 500px){
  .searchBox{
    margin-left:0 !important
  }
  .nonereport  .personSearch{
  width: 100%;
  height: auto !important;
  }
  .nonereportBox{
    margin-left: 0 !important;
  }
  .nonedailymargin{
    margin-left: 0px !important;
    margin-top: 5px !important;
    width: 100%;
  }
  .nonereportsearch{
    background-color: #409eff;
    color: #fff;
  }
}
</style>
