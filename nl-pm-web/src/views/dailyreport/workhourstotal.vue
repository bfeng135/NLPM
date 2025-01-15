<template>
  <div class="work-hours-total-container">
    <div style="margin-bottom:10px">
      <el-date-picker
        v-model="workHoursStartTime"
        type="date"
        placeholder="请选择日期日期"
        value-format="timestamp"
        :picker-options="pickerOptions"
      />
      <el-date-picker
        v-model="workHoursEndTime"
        type="date"
        style="margin-left:10px"
        placeholder="请选择结束日期"
        :picker-options="pickerOptions"
        @change="changeEndTime"
      />
      <el-button type="success" size="medium" style="margin-left:10px;height:40px" @click="searchWorkHours">搜索</el-button>
      <el-button type="success" size="medium" style="margin-left:10px;height:40px" @click="searchAll">全部</el-button>
    </div>
    <el-row :gutter="32" class="panel-group">
      <el-col :xs="24" :sm="24" :lg="8" class="card-panel-col">
        <div class="chart-wrapper" style="height:380px">
          <pie :key="pieKey" :data-list="pieList" />
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="16" class="card-panel-col">
        <div class="chart-wrapper" style="height:380px">
          <bar :key="barKey" :data-list="pieList" />
        </div>
      </el-col>
    </el-row>
    <el-table :data="workList" style="100%">
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-table :data="props.row.resReportDetailVOS" style="100%">
            <el-table-column
              label="时长"
              prop="hours"
            />
            <el-table-column
              label="描述"
              prop="desc"
            >
              <template slot-scope="scope">
                <span v-if="scope.row.desc!==null && scope.row.desc!==''">{{ scope.row.desc }}</span>
                <span v-else>--</span>
              </template>
            </el-table-column>
            <el-table-column
              label="日期"
              prop="reportDate"
            >
              <template slot-scope="scope">
                <span>{{ scope.row.reportDate|formatymd }}</span>
              </template>
            </el-table-column>
          </el-table>
        </template>
      </el-table-column>
      <el-table-column
        label="区域名称"
        prop="areaName"
      />
      <el-table-column
        label="项目名称"
        prop="projectName"
      />
      <el-table-column
        label="总时长"
        prop="hours"
      />
    </el-table>
  </div>
</template>

<script>
import pie from './components/pie.vue'
import bar from './components/bar.vue'
import pickerOptions from '@/mixins/index'
export default {
  components: {
    pie,
    bar
  },
  mixins: [pickerOptions],
  data() {
    return {
      workHoursStartTime: (Date.parse(new Date(new Date().toLocaleDateString())) - 3600 * 1000 * 24 * 7),
      workHoursEndTime: (Date.parse(new Date(new Date(new Date().toLocaleDateString()).getTime() + 24 * 60 * 60 * 1000 - 1))),
      workList: [],
      pieList: [],
      pieKey: 1,
      barKey: 1
    }
  },
  created() {
    this.getWorkHoursTotalList()
  },
  methods: {
    searchWorkHours() {
      this.getWorkHoursTotalList()
      ++this.pieKey
      ++this.barKey
    },
    changeEndTime(val) {
      this.workHoursEndTime = (Date.parse(val) - 1) + 24 * 60 * 60 * 1000
    },
    searchAll() {
      this.workHoursStartTime = 1630425600000
      this.workHoursEndTime = (Date.parse(new Date(new Date(new Date().toLocaleDateString()).getTime() + 24 * 60 * 60 * 1000 - 1)))
      this.getWorkHoursTotalList()
    },
    getWorkHoursTotalList() {
      const params = {
        endTime: this.workHoursEndTime,
        startTime: this.workHoursStartTime
      }
      this.$store.dispatch('dailyreport/queryOneSelfProjectTimeList', params).then(res => {
        this.workList = res
        this.pieList = []
        this.workList.forEach(item => {
          this.pieList.push({
            name: item.projectName,
            value: item.hours
          })
        })
      })
    }
  }
}
</script>

<style>
.work-hours-total-container{
  background-color: #f0f2f5;
  min-height: calc(100vh - 50px);
  height: 100%;
  padding: 20px;
}
.panel-group {
  margin: 5px auto 5px;
}
.chart-wrapper {
  background: #fff;
  padding: 16px 16px 0;
  margin-bottom: 32px;
}
</style>
