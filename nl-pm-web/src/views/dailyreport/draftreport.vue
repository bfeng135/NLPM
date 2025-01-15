<template>
  <div class="create-report">
    <header-title>
      <template slot="title"> 新建日报草稿 </template>
    </header-title>
    <div class="report-main">
      <el-card class="box-card">
        <div class="searchContent">
          <div class="searchBlock">
            <!-- <span style="margin-top:-20px;display:inline-block;margin-right:5px;color:red">*</span> -->
            <el-date-picker
              v-model="reportDateValue"
              type="date"
              placeholder="请选择日期(必填)"
              :picker-options="pickerOptions"
            />
          </div>
          <div style="float: right" class="searchBtn">
            <el-button :type="isleave?'danger':'success'" :disabled="readonly!=='false'" @click="leave">
              <span v-if="isleave">取消请假</span>
              <span v-else>添加请假</span>
            </el-button>
            <el-button type="success" :disabled="readonly!=='false'" @click="add">添加项目</el-button>
          </div>
        </div>
        <el-card v-if="isleave" class="reportcard-main leave">
          <el-form
            ref="ruleForm"
            :model="leaveForm"
            label-width="100px"
            class="demo-ruleForm"
            inline="true"
          >
            <el-form-item label="请假时长">
              <el-select
                v-model="leaveForm.leaveHours"
                placeholder="请选择请假时长(必填)"
                clearable
                filterable
              >
                <el-option
                  v-for="item in leaveTime"
                  :key="item.value"
                  :label="item.lable"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="请假描述">
              <el-input
                v-model="leaveForm.desc"
                placeholder="请输入请假描述"
                style="width:500px"
                maxlength="400"
              />
            </el-form-item>
          </el-form>
        </el-card>
        <el-card v-for="(v,i) in ruleForm" :key="i" class="reportcard-main workdayReport">
          <div v-if="i!==0" class="el-icon-close close" @click="close(i)" />
          <el-form
            ref="createReportRuleForm"
            :model="ruleForm"
            :rules="rules"
            label-width="100px"
            class="demo-ruleForm"
          >
            <el-form-item label="项目名称" class="inline">
              <el-select
                v-model="ruleForm[i].projectId"
                placeholder="请选择项目(必填)"
                clearable
                filterable
              >
                <el-option
                  v-for="item in reportProjects"
                  :key="item.id+'1'"
                  :label="`${item.name}-${item.areaName}`"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="工作时长" class="inline">
              <el-select
                v-model="ruleForm[i].hours"
                placeholder="请选择工作时长(必填)"
                clearable
                filterable
              >
                <el-option
                  v-for="item in reportTimes"
                  :key="item.value"
                  :label="item.lable"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="项目描述">
              <el-input
                v-model="ruleForm[i].desc"
                type="textarea"
                :rows="5"
                placeholder="请输入日报内容"
                style="width: 92%"
                maxlength="400"
              />
            </el-form-item>
          </el-form>
        </el-card>
        <div class="reportfooter">
          <el-button
            class="footerBtn draft"
            style="margin-left:10px"
            type="primary"
            :disabled="readonly!=='false'"
            :loading="createreportLoading"
            @click="submitdraft"
          >保存草稿</el-button>
          <el-button
            class="footerBtn"
            style="margin-bottom: 10px"
            type="success"
            :loading="createreportLoading"
            :disabled="readonly!=='false'"
            @click="submit('createReportRuleForm')"
          >正式提交日报</el-button>
          <el-button
            class="footerBtn"
            style="margin-bottom:10px"
            type="danger"
            :disabled="readonly!=='false'"
            :loading="createreportLoading"
            @click="refreshDraft"
          >清空草稿</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import headerTitle from '@/components/Title/index.vue'
import { mapGetters } from 'vuex'
export default {
  components: {
    headerTitle
  },
  computed: {
    ...mapGetters([
      'date'
    ])
  },
  data() {
    return {
      readonly: localStorage.getItem('read-only'),
      deepDeport: null,
      createreportLoading: false,
      ruleForm: [
        {
          projectId: null,
          hours: null,
          desc: null,
          areaName: null,
          projectName: null
        }
      ],
      leaveForm: [
        {
          leaveHours: null,
          desc: null
        }
      ],
      isleave: false,
      rules: {
        projectId: [
          { required: true, message: '请选择项目', trigger: 'blur' }
        ],
        hours: [
          { required: true, message: '请选择工作时长', trigger: 'blur' }
        ]
      },
      reportDateValue: null,
      reportProjects: [],
      pickerOptions: {
        disabledDate(date) {
          if (date.getTime() < 1630425600000) {
            return true
          }
          return false
        }
      },
      leaveTime: [
        {
          value: 0.5,
          lable: '0.5'
        },
        {
          value: 1,
          lable: '1'
        }, {
          value: 1.5,
          lable: '1.5'
        },
        {
          value: 2,
          lable: '2'
        },
        {
          value: 2.5,
          lable: '2.5'
        }, {
          value: 3,
          lable: '3'
        },
        {
          value: 3.5,
          lable: '3.5'
        },
        {
          value: 4,
          lable: '4'
        },
        {
          value: 4.5,
          lable: '4.5'
        }, {
          value: 5,
          lable: '5'
        },
        {
          value: 5.5,
          lable: '5.5'
        },
        {
          value: 6,
          lable: '6'
        }, {
          value: 6.5,
          lable: '6.5'
        },
        {
          value: 7,
          lable: '7'
        }, {
          value: 7.5,
          lable: '7.5'
        },
        {
          value: 8,
          lable: '8'
        }
      ],
      reportTimes: [
        {
          value: 0.5,
          lable: '0.5'
        },
        {
          value: 1,
          lable: '1'
        },
        {
          value: 1.5,
          lable: '1.5'
        },
        {
          value: 2,
          lable: '2'
        },
        {
          value: 2.5,
          lable: '2.5'
        },
        {
          value: 3,
          lable: '3'
        },
        {
          value: 3.5,
          lable: '3.5'
        },
        {
          value: 4,
          lable: '4'
        },
        {
          value: 4.5,
          lable: '4.5'
        },
        {
          value: 5,
          lable: '5'
        },
        {
          value: 5.5,
          lable: '5.5'
        },
        {
          value: 6,
          lable: '6'
        }, {
          value: 6.5,
          lable: '6.5'
        },
        {
          value: 7,
          lable: '7'
        }, {
          value: 7.5,
          lable: '7.5'
        },
        {
          value: 8,
          lable: '8'
        },
        {
          value: 8.5,
          lable: '8.5'
        }, {
          value: 9,
          lable: '9'
        },
        {
          value: 9.5,
          lable: '9.5'
        }, {
          value: 10,
          lable: '10'
        },
        {
          value: 10.5,
          lable: '10.5'
        },
        {
          value: 11,
          lable: '11'
        },
        {
          value: 11.5,
          lable: '11.5'
        }, {
          value: 12,
          lable: '12'
        },
        {
          value: 12.5,
          lable: '12.5'
        }, {
          value: 13,
          lable: '13'
        },
        {
          value: 13.5,
          lable: '13.5'
        }, {
          value: 14,
          lable: '14'
        },
        {
          value: 14.5,
          lable: '14.5'
        },
        {
          value: 15,
          lable: '15'
        },
        {
          value: 15.5,
          lable: '15.5'
        },
        {
          value: 16,
          lable: '16'
        }, {
          value: 16.5,
          lable: '16.5'
        },
        {
          value: 17,
          lable: '17'
        }, {
          value: 17.5,
          lable: '17.5'
        },
        {
          value: 18,
          lable: '18'
        }, {
          value: 18.5,
          lable: '18.5'
        },
        {
          value: 19,
          lable: '19'
        }, {
          value: 19.5,
          lable: '19.5'
        },
        {
          value: 20,
          lable: '20'
        }, {
          value: 20.5,
          lable: '20.5'
        },
        {
          value: 21,
          lable: '21'
        }, {
          value: 21.5,
          lable: '21.5'
        },
        {
          value: 22,
          lable: '22'
        }, {
          value: 22.5,
          lable: '22.5'
        },
        {
          value: 23,
          lable: '23'
        }, {
          value: 23.5,
          lable: '23.5'
        },
        {
          value: 24,
          lable: '24'
        }
      ]
    }
  },
  created() {
    this.getProjectList()
    this.getDraftDetail()
  },
  mounted() {
    // this.getDates()
  },
  methods: {
    formatymd(value) {
      const date = new Date(value)
      const y = date.getFullYear()
      let MM = date.getMonth() + 1
      MM = MM < 10 ? '0' + MM : MM
      let d = date.getDate()
      d = d < 10 ? '0' + d : d
      return y + '-' + MM + '-' + d + ' '
    },
    refreshDraft() {
      // this.ruleForm = [
      //   {
      //     projectId: null,
      //     hours: null,
      //     desc: null,
      //     areaName: null,
      //     projectName: null
      //   }
      // ]
      // this.reportDateValue = null
      // this.leaveForm = [
      //   {
      //     leaveHours:null,
      //     desc:''
      //   }
      // ]
      const params = {
        date: null,
        dayReportList: [
          {
            projectId: null,
            hours: null,
            desc: null,
            areaName: null,
            projectName: null
          }
        ],
        leaveVO: {
          desc: null,
          leaveHours: null
        }
      }
      this.$confirm('此操作将清空草稿, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('dailyreport/postDraftDetail', params).then(res => {
          this.$message.success('清空成功')
          this.createreportLoading = false
          this.reportDateValue = null
          if (res.leaveVO.leaveHours == null || res.leaveVO.desc == null) {
            this.isleave = false
          }
          this.getDraftDetail()
        }).catch(err => {
          this.createreportLoading = false
          this.$message.error(err)
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    submitdraft() {
      const params = {
        date: typeof this.reportDateValue === 'number' ? this.reportDateValue : this.getTime(this.reportDateValue),
        // date:this.reportDateValue,
        dayReportList: this.ruleForm,
        leaveVO: {
          leaveHours: this.leaveForm.leaveHours,
          desc: this.leaveForm.desc !== undefined ? this.leaveForm.desc : null
        }
      }
      if (this.isleave == false) {
        params.leaveVO = null
      }
      this.createreportLoading = true
      this.$store.dispatch('dailyreport/postDraftDetail', params).then(res => {
        this.$message.success('保存成功')
        if (res.date !== null) {
        //  this.reportDateValue = this.formatymd(res.date)
          this.reportDateValue = res.date
        } else {
          this.reportDateValue = null
        }
        this.createreportLoading = false
        // this.$router.push('/dailyreport/reporttable')
        // this.$router.push('/dailyreport/reporttable')
      }).catch(err => {
        this.createreportLoading = false
        this.$message.error(err)
      })
    },
    getDraftDetail() {
      this.$store.dispatch('dailyreport/getDailyDraftDetail').then(res => {
        if (res.dayReportList.length > 0) {
          this.ruleForm = res.dayReportList
        }
        if (res.date !== null) {
          // this.reportDateValue = this.formatymd(res.date)
          this.reportDateValue = res.date
        }
        this.leaveForm = res.leaveVO
        if (res.leaveVO.leaveHours !== null || res.leaveVO.desc !== null) {
          this.isleave = true
        }
      })
    },
    getProjectList() {
      const params = {
        currentPage: 1,
        pageSize: 10000000,
        searchVal: null
      }
      this.$store.dispatch('project/getAssignUser', params).then((res) => {
        this.reportProjects = res.totalList
      })
    },
    add() {
      this.ruleForm.push({
        projectId: null,
        hours: null,
        desc: null
      })
    },
    close(index) {
      this.ruleForm.splice(index, 1)
    },
    leave() {
      this.isleave = !this.isleave
      if (this.isleave == false) {
        this.leaveForm = [
          {
            leaveHours: null,
            desc: null
          }
        ]
      } else {
        this.leaveForm = [
          {
            leaveHours: null,
            desc: null
          }
        ]
      }
    },
    getTime(time) {
      return Date.parse(time)
    },
    getForm(val) {
      if (val[0].projectId !== null && val[0].hours !== null) {
        return val
      }
      if (val[0].projectId == null && val[0].hours == null) {
        return this.ruleForm = []
      }
    },
    submit() {
      const params = {
        // date: this.getTime(this.reportDateValue),
        date: typeof this.reportDateValue === 'number' ? this.reportDateValue : this.getTime(this.reportDateValue),
        dayReportList: this.ruleForm,
        leaveVO: {
          leaveHours: this.leaveForm.leaveHours,
          desc: this.leaveForm.desc !== undefined ? this.leaveForm.desc : null
        }
      }
      if (this.isleave == false) {
        params.leaveVO = null
      }
      this.createreportLoading = true
      this.$store.dispatch('dailyreport/createReport', params).then(res => {
        this.$message.success('创建成功')
        this.createreportLoading = false
        this.$store.state.dailyreport.date = null
        // this.$router.push('/dailyreport/reporttable')
        this.$router.push('/dailyreport/reporttable')
      }).catch(err => {
        this.createreportLoading = false
        this.$message.error(err)
      })
    }
  }
}
</script>

<style lang="scss">
.create-report {
  padding: 20px;
}
.report-main {
  margin-top: 15px;
  .searchContent {
    width: 80%;
    height: 50px;
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 0 auto;
    .searchBlock {
      flex: 1;
      margin: 0 auto;
    }
  }
}
.report-main .box-card {
  width: 100%;
}
.create-report .el-date-editor.el-input,
.el-date-editor.el-input__inner {
  width: 195px;
}
.reportfooter {
  width: 80%;
  margin: 0 auto;
  margin-top: 10px;
  text-align: right;
}
.reportcard-main {
  width: 80%;
  margin: 0 auto;
  margin-top: 10px;
  position: relative;
}
.close {
  width: 25px;
  height: 25px;
  // background: red;
  color:#909399;
  font-weight: bold;
  position: absolute;
  right: 5px;
  top: 10px;
  text-align: center;
  cursor: pointer;
}
.inline{
  display: inline-block;
}
</style>
