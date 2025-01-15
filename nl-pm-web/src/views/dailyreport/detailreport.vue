<template>
  <div class="create-report">
    <header-title>
      <template slot="title"> 日报详情 </template>
    </header-title>
    <div class="report-main">
      <el-card class="box-card">
        <div class="searchContent">
          <div class="searchBlock">
            <el-date-picker
              v-model="reportDateValue"
              type="date"
              placeholder="请选择日期"
              @change="getDatechange"
              :disabled="true"
            >
            </el-date-picker>
          </div>
          <!-- <div style="float: right">
            <el-button @click="leave" :type="isleave?'danger':'success'">
              <span v-if="isleave">取消请假</span>
              <span v-else>添加请假</span>
            </el-button>
            <el-button @click="add" type="success">添加项目</el-button>
          </div> -->
        </div>
        <el-card class="reportcard-main" v-if="isleave">
          <el-form
            :model="leaveForm"
            ref="ruleForm"
            label-width="100px"
            class="demo-ruleForm"
            inline="true"
          >
            <el-form-item label="请假时长">
              <el-select
                v-model="leaveForm.leaveHours"
                placeholder="请选择请假时长"
                clearable
                :disabled="true">
                <el-option
                  v-for="item in leaveTime"
                  :key="item.value"
                  :label="item.lable"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="请假描述">
              <el-input
                placeholder="请输入请假描述"
                v-model="leaveForm.desc"
                style="width:500px"
                :disabled="true"
              >
              </el-input>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card class="reportcard-main" v-for="(v,i) in ruleForm" :key="i">
          <div class="el-icon-close close" @click="close(i)" v-if="i!==0"></div>
          <el-form
            :model="ruleForm"
            :rules="rules"
            ref="ruleForm"
            label-width="100px"
            class="demo-ruleForm"
          >
            <el-form-item label="项目名称" prop="projectId" class="inline">
              <el-select
                v-model="ruleForm[i].projectId"
                placeholder="请选择项目"
                clearable
                :disabled="true"
              >
                <el-option
                  v-for="item in reportProjects"
                  :key="item.id+'1'"
                  :label="item.name"
                  :value="item.id"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="工作时长" prop="projectId" class="inline">
              <el-select
                v-model="ruleForm[i].hours"
                placeholder="请选择工作时长"
                clearable
                :disabled="true">
                <el-option
                  v-for="item in reportTimes"
                  :key="item.value"
                  :label="item.lable"
                  :value="item.value"
                 >
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="项目描述">
              <el-input
                type="textarea"
                :rows="5"
                placeholder="请输入日报内容"
                v-model="ruleForm[i].desc"
                style="width: 92%"
                :disabled="true"
              >
              </el-input>
            </el-form-item>
          </el-form>
        </el-card>
        <div class="reportfooter">
          <el-button style="float: right; margin-bottom: 10px" type="success" @click="back"
            >返回</el-button
          >
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import headerTitle from "@/components/Title/index.vue";
export default {
  components: {
    headerTitle,
  },
  data() {
    return {
      ruleForm: [
        {
          projectId: null,
          hours: null,
          desc: null,
        }
      ],
      leaveForm:[
        {
          leaveHours:'',
          desc:''
        }
      ],
      isleave:false,
      rules: {
        projectId: [
          { required: true, message: "请输入活动名称", trigger: "blur" },
        ],
      },
      reportDateValue: "",
      reportProjects:[],
      leaveTime:[
        {
          value: 0.5,
          lable: "0.5",
        },
        {
          value: 1,
          lable: "1",
        },{
          value: 1.5,
          lable: "1.5",
        },
        {
          value: 2,
          lable: "2",
        },
        {
          value: 2.5,
          lable: "2.5",
        },{
          value: 3,
          lable: "3",
        },
        {
          value: 3.5,
          lable: "3.5",
        },
        {
          value: 4,
          lable: "4",
        },
        {
          value: 4.5,
          lable: "4.5",
        },{
          value: 5,
          lable: "5",
        },
        {
          value: 5.5,
          lable: "5.5",
        },
        {
          value: 6,
          lable: "6",
        },{
          value: 6.5,
          lable: "6.5",
        },
        {
          value: 7,
          lable: "7",
        },{
          value: 7.5,
          lable: "7.5",
        },
        {
          value: 8,
          lable: "8",
        },
      ],
      reportTimes:[
        {
          value: 0.5,
          lable: "0.5",
        },
        {
          value: 1,
          lable: "1",
        },
        {
          value: 1.5,
          lable: "1.5",
        },
        {
          value: 2,
          lable: "2",
        },
        {
          value: 2.5,
          lable: "2.5",
        },
        {
          value: 3,
          lable: "3",
        },
        {
          value: 3.5,
          lable: "3.5",
        },
        {
          value: 4,
          lable: "4",
        },
        {
          value: 4.5,
          lable: "4.5",
        },
        {
          value: 5,
          lable: "5",
        },
        {
          value: 5.5,
          lable: "5.5",
        },
        {
          value: 6,
          lable: "6",
        },{
          value: 6.5,
          lable: "6.5",
        },
        {
          value: 7,
          lable: "7",
        },{
          value: 7.5,
          lable: "7.5",
        },
        {
          value: 8,
          lable: "8",
        },
        {
          value: 8.5,
          lable: "8.5",
        },{
          value: 9,
          lable: "9",
        },
        {
          value: 9.5,
          lable: "9.5",
        },{
          value: 10,
          lable: "10",
        },
        {
          value: 10.5,
          lable: "10.5",
        },
        {
          value: 11,
          lable: "11",
        },
        {
          value: 11.5,
          lable: "11.5",
        },{
          value: 12,
          lable: "12",
        },
        {
          value: 12.5,
          lable: "12.5",
        },{
          value: 13,
          lable: "13",
        },
        {
          value: 13.5,
          lable: "13.5",
        },{
          value: 14,
          lable: "14",
        },
        {
          value: 14.5,
          lable: "14.5",
        },{
          value: 15,
          lable: "15.5",
        },
        {
          value: 16,
          lable: "16",
        },{
          value: 16.5,
          lable: "16.5",
        },
        {
          value: 17,
          lable: "17",
        },{
          value: 17.5,
          lable: "17.5",
        },
        {
          value: 18,
          lable: "18",
        },{
          value: 18.5,
          lable: "18.5",
        },
        {
          value: 19,
          lable: "19",
        },{
          value: 19.5,
          lable: "19.5",
        },
        {
          value: 20,
          lable: "20",
        },{
          value: 20.5,
          lable: "20.5",
        },
        {
          value: 21,
          lable: "21",
        },{
          value: 21.5,
          lable: "21.5",
        },
        {
          value: 22,
          lable: "22",
        },{
          value: 22.5,
          lable: "22.5",
        },
        {
          value: 23,
          lable: "23",
        },{
          value: 23.5,
          lable: "23.5",
        },
        {
          value: 24,
          lable: "24",
        },
      ]
    };
  },
  created(){
    // this.getProjectList()
    this.getDailyReportDetail()
  },
  methods:{
      getDatechange(data){
        if(data){
          this.reportDateValue = this.getTime(data)
        }
      },
    getDailyReportDetail(){
      const id = this.$route.params.id
      this.$store.dispatch('dailyreport/getReportDetail',id).then(res=>{
        this.ruleForm = res.dayReportList
        this.reportDateValue = res.date
        this.leaveForm = res.leaveVO
        if(res.leaveVO.leaveHours!==null){
          this.isleave = true
        }
      })
    },
    // getProjectList() {
    //   const role = localStorage.getItem('role-code')
    //   const areaid = localStorage.getItem('area-id')
    //   const params = {
    //     areaId: role == 'AREA_MANAGER'? Number(areaid):0,
    //     currentPage: 1,
    //     desc: "",
    //     managerId: null,
    //     name: "",
    //     pageSize: 10,
    //     searchVal: "",
    //     userId: null,
    //   };
    //   this.$store.dispatch("project/getProjectList", params).then((res) => {
    //     this.reportProjects = res.totalList;
    //   });
    // },
    // getAreaProjectList() {
    //   this.$store.dispatch("project/getProjectList", params).then((res) => {
    //     this.reportProjects = res.totalList;
    //   });
    // },
    back() {
      this.$router.back()
    }
  }
};
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
  background: red;
  color: #fff;
  font-weight: bold;
  position: absolute;
  right: 5px;
  top: 5px;
  text-align: center;
  cursor: pointer;
}
.inline{
  display: inline-block;
}
</style>