<template>
  <div class="create-report">
    <header-title>
      <template slot="title"> 新建日报 </template>
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
              @change="changeDate"
            />
          </div>
          <div style="float: right" class="searchBtn">
            <el-button :type="isleave?'danger':'success'" class="displays" :disabled="readonly!=='false'" @click="leave">
              <span v-if="isleave">取消请假</span>
              <span v-else>添加请假</span>
            </el-button>
            <el-button type="success" class="displays" :disabled="readonly!=='false'" @click="add">添加项目</el-button>
          </div>
        </div>
        <el-card v-if="isleave" class="reportcard-main leave" style="position: relative;">
          <el-form
            ref="ruleForm"
            :model="leaveForm"
            :rules="leaverules"
            label-width="100px"
            class="demo-ruleForm"
            inline="true"
          >
            <el-form-item label="请假时长">

              <el-select
                v-model="leaveForm.leaveTime"
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
                style="width:250px"
              >
                <el-option
                  v-for="item in reportProjects"
                  :key="item.id+'1'"
                  :label="`${item.name}-${item.areaName}`"
                  :value="item.id"
                >
                  <!-- <el-tooltip placement="bottom"> -->
                  <!-- <div slot="content">{{item.areaName}}-{{item.name}}</div> -->
                  <span>{{ item.name }}-{{ item.areaName }}</span>
                <!-- </el-tooltip> -->
                </el-option>
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
            class="footerBtn"
            style="margin-bottom: 10px;"
            type="success"
            :loading="createreportLoading"
            :disabled="readonly!=='false'"
            @click="submit('createReportRuleForm')"
          >提交日报</el-button>
          <el-button
            class="footerBtn draft"
            style="margin-bottom: 10px"
            type="danger"
            :disabled="readonly!=='false'"
            :loading="createreportLoading"
            @click="submitDraft"
          >保存至草稿</el-button>

        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import headerTitle from '@/components/Title/index.vue'
import { mapGetters } from 'vuex'
import pickerOptions from '@/mixins/index'
export default {
  components: {
    headerTitle
  },
  computed: {
    ...mapGetters([
      'date'
    ])
  },
  mixins: [pickerOptions],
  data() {
    return {
      readonly: localStorage.getItem('read-only'),
      deepDeport: null,
      createreportLoading: false,
      deepTime: null,
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
          leaveTime: null,
          desc: ''
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
      // pickerOptions: {
      //   disabledDate(date) {
      //     if (date.getTime() < 1630425600000) {
      //       return true
      //     }
      //     return false
      //   }
      // },
      reportProjects: [],
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
    // this.getDailyReportDetail()
    this.getDraftDetail()
  },
  mounted() {
    this.getDates()
  },
  methods: {
    // getDailyReportDetail(){
    //   const id = this.$route.params.id
    //   this.$store.dispatch('dailyreport/getReportDetail',id).then(res=>{
    //     this.deepTime = res.date
    //   })
    // },
    changeDate(data) {
      if (data) {
        this.$store.state.dailyreport.date = null
      }
    },
    getDraftDetail() {
      this.$store.dispatch('dailyreport/getDailyDraftDetail').then(res => {
        this.deepTime = res.date
      })
    },
    submitDraft() {
      const date = this.$store.state.dailyreport.date
      const params = {
        date: date == null ? this.getTime(this.reportDateValue) : date,
        dayReportList: this.ruleForm,
        leaveVO: {
          leaveHours: this.leaveForm.leaveTime,
          desc: this.leaveForm.desc !== undefined ? this.leaveForm.desc : null
        }
      }
      if (this.isleave == false) {
        params.leaveVO = null
      }
      if (this.deepTime !== null) {
        this.$confirm('您是否要保存至草稿?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$confirm('您有草稿内容，是否覆盖草稿?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.$store.dispatch('dailyreport/postDraftDetail', params).then(res => {
              this.$message({
                type: 'success',
                message: '保存成功!'
              })
              this.$router.push('/dailyreport/draftreport')
            })
          }).catch(() => {
          })
        })
      } else {
        this.$confirm('您是否要保存至草稿?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$store.dispatch('dailyreport/postDraftDetail', params).then(res => {
            this.$message({
              type: 'success',
              message: '保存成功!'
            })
            this.$router.push('/dailyreport/draftreport')
          })
        })
      }
    },
    getDates() {
    //   this.$bus.$on('nonedate',(date)=>{
    //      console.log(date)
    // })
    // console.log(this.$route.params.code)
      const code = this.$route.params.code
      const dates = this.$store.state.dailyreport.date
      if (dates && code) {
        this.reportDateValue = dates
      } else {
        this.reportDateValue = null
      }
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
            leaveTime: null,
            desc: ''
          }
        ]
      } else {
        this.leaveForm = [
          {
            leaveTime: null,
            desc: ''
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
      const date = this.$store.state.dailyreport.date
      // let code = this.$route.params.code
      // let deepDeport = null
      // if(this.ruleForm[0].projectId =="" && this.ruleForm[0].hours==""||this.ruleForm[0].projectId==null && this.ruleForm[0].hours==null){
      //   this.deepDeport = []
      // }else if(this.ruleForm[0].projectId =="" ||this.ruleForm[0].hours==""){
      //   this.deepDeport = []
      // }else{
      //   this.deepDeport = this.ruleForm
      // }
      const params = {
        date: date == null ? this.getTime(this.reportDateValue) : date,
        dayReportList: this.ruleForm,
        leaveVO: {
          leaveHours: this.leaveForm.leaveTime,
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
        // this.$router.push('/dailyreport/reporttable')
        this.$router.push('/dailyreport/reporttable')
        this.$store.state.dailyreport.date = null
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
.el-select-dropdown__wrap {
  max-height: 350px;
}
.inline{
  display: inline-block;
}
</style>
