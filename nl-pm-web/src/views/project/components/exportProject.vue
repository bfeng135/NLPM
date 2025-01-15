<template>
  <div class="export-project">
    <el-dialog
    :title='"项目单报表详细"+"-"+exportProjectRowData.name'
    :visible.sync="exportProjectDialogVisible"
    width="100%"
    @open="openExporProject"
    @close="closeExportProject">
    <div slot="title" class="header-title">
      <span class="project-title">项目单报表详细-{{titleName}}</span>
      <el-button type="danger" @click="closeExportProject" size="small">返 回</el-button>
    </div>
    <span>
    <div class="expportProjectSearch">
      <div class="projectReportSearchBox">
        <el-select v-model="selectProject"  clearable filterable  placeholder="请选择项目(必填)" @change="selectChangeProject">
          <el-option
          v-for="(item, index) in singleProjectList"
          :key="index"
          :label="item.name"
          :value="item.name">
          </el-option>
        </el-select>
        <el-select
          v-model="areaValue"
          placeholder="请选择区域"
          style="margin-left: 5px"
          clearable
          collapse-tags
          multiple
          filterable
          class="projectreportsearch"
        >
          <el-option
            v-for="item in areaOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          >
          </el-option>
        </el-select>
        <!-- <el-select
          v-model="projectValue"
          placeholder="请选择项目(必填)"
          style="margin-left: 5px"
          clearable
          filterable
          :disabled="true"
          class="projectreportsearch"
        >
        </el-select> -->
        
        <el-date-picker
          v-model="projectReportStartTime"
          type="date"
          placeholder="选择查询开始日期(必填)"
          style="margin-left: 5px"
          class="projectreportsearch"
          @change="selectStartDate"
        >
        </el-date-picker>
        <el-date-picker
          v-model="projectReportEndTime"
          type="date"
          placeholder="选择查询结束日期(必填)"
          style="margin-left: 5px"
          class="projectreportsearch"
          @change="selectEndDate"
        >
        </el-date-picker>
        <el-button style="margin-left: 5px" @click="searchExportProjectList" size="small"
        :disabled="selectProject!==''&&projectReportStartTime!==null&&projectReportEndTime!==null?false:true"
        class="projectreportsearch pReportSearchBtn"
          >搜索</el-button
        >
      </div>
      <div style="margin-right: 15px" class="projectrexport">
        <span style="margin-right:15px;font-size:14px">
           <span style="font-weight:bold">时间占比:</span>
           {{percent}}%
        </span>
        <span style="margin-right:15px;font-size:14px">
           <span style="font-weight:bold">当前时长:</span>
           {{currentTime}}
        </span>
        <span style="margin-right:15px;font-size:14px">
           <span style="font-weight:bold">总时长:</span>
           {{totalTime}}
        </span>
        <br/>
        <el-button type="text" @click="exportReport" :disabled="projectValue!==''&&projectReportStartTime!==null&&projectReportEndTime!==null? false:true" style="float:right;margin-right:15px">
          <span
            class="el-icon-download"
            style="color: #13ce66; font-size: 16px"
          ></span>
          <span style="color: #606266">导出报表</span>
        </el-button>
      </div>
    </div>
    <div style="width: 100%;height:100%">
      <el-table :data="exportProjectReportTableData" style="width: 100%;height:100%" v-loading="projectReportLoading">
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-table :data="props.row.resReportFormByTimeVOS" style="width:100%">
              <el-table-column label="工作日" prop="workDay"></el-table-column>
              <el-table-column label="工作时长" prop="workTime"></el-table-column>
              <el-table-column label="工作描述" prop="projectDesc">
                <template slot-scope="scope">
                  <span v-if="scope.row.projectDesc!==null">{{scope.row.projectDesc}}</span>
                  <span v-else>--</span>
                </template>
              </el-table-column>
            </el-table>
          </template>
        </el-table-column>
        <el-table-column type="index"></el-table-column>
        <el-table-column label="项目名称" prop="projectName"> </el-table-column>
        <el-table-column label="姓名" prop="nickname"> </el-table-column>
        <el-table-column label="区域" prop="areaName"> </el-table-column>
        <el-table-column label="总时长" prop="userTotalTime"> </el-table-column>
      </el-table>
      
      <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pageNo"
      :page-sizes="[10, 20, 30]"
      style="margin-top:10px;text-align:right"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>
    </div>
    <!-- <el-scrollbar style="height:100vh"> -->
    
    <!-- </el-scrollbar> -->
    </span>
    <!-- <span slot="footer" class="dialog-footer">
        <el-button @click="exportProjectDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="exportProjectDialogVisible = false">确 定</el-button>
    </span> -->
</el-dialog>
  </div>
</template>

<script>
import {getTime} from '@/utils/handletime'
export default {
    props:{
      exportproject:{
        type:Boolean,
        default:false
      },
      exportProjectRowData:{
        type:Object,
        default:()=>{}
      },
      singleProjectList:{
        type:Array,
        default:()=>[]
      }
    },
    data() {
      return {
        exportProjectDialogVisible: false,
        areaValue:[],
        projectValue:null,
        exportProjectReportTableData:[],
        projectReportStartTime:(Date.parse(new Date(new Date().toLocaleDateString())) - 3600 * 1000 * 24 * 7),
        projectReportEndTime:(Date.parse(new Date(new Date().toLocaleDateString()))),
        areaOptions:[],
        totalTime:0,
        total:0,
        pageSize:10,
        pageNo:1,
        projectReportLoading:false,
        deepStartTime:null,
        deepEndTime:null,
        percent:0,
        currentTime:0,
        title:'项目单报表详细',
        list:[],
        selectProject:null,
        projectOptions:[],
        titleName:null
      };
    },
    watch:{
      exportproject(val){
        this.exportProjectDialogVisible = val 
      },
      exportProjectRowData(val){
        this.selectProject = val.name
        this.titleName = val.name
      },
      selectProject(val){
        if(val){
          this.areaValue = []
          this.total = 0
          this.pageNo = 1
          this.exportProjectReportTableData = []
          this.getProjectNameAreaList()
        } 
      }
    },
    methods:{
      selectChangeProject(data){
        if(data){
          this.titleName = data
          this.getExportProjectList()
        }
      },
      selectStartDate(data){
        this.deepStartTime = getTime(data)
      },
      selectEndDate(data){
        this.deepEndTime = getTime(data)
      },
      closeExportProject(){
        this.$emit('update:exportproject',false)
        this.areaValue = [],
        this.projectValue = null,
        this.titleName = this.exportProjectRowData.name
        this.selectProject = this.exportProjectRowData.name
        this.exportProjectReportTableData = [],
        this.projectReportStartTime = (Date.parse(new Date(new Date().toLocaleDateString())) - 3600 * 1000 * 24 * 7),
        this.projectReportEndTime = (Date.parse(new Date(new Date().toLocaleDateString()))),
        this.totalTime = 0,
        this.total = 0
      },
      searchExportProjectList(){
        if(this.projectReportStartTime>this.projectReportEndTime){
          this.$message.error('结束日期必须大于起始日期')
        }else{
          this.pageNo = 1
          this.titleName = this.selectProject
          this.getExportProjectList()
        }
      },
      handleSizeChange(val){
        this.pageSize = val;
        this.getExportProjectList()
      },
      handleCurrentChange(val){
        this.pageNo = val
        this.getExportProjectList()
      },
      exportReport(){
        const params = {
            areaId: this.areaValue,
            endTime: this.deepEndTime==null? this.projectReportEndTime:this.deepEndTime,
            currentProjectName:this.selectProject,
            currentPage:this.pageNo,
            pageSize:this.pageSize,
            projectName: null,
            startTime: this.deepStartTime==null? this.projectReportStartTime:this.deepStartTime,
        }
        if(this.projectReportStartTime>this.projectReportEndTime){
            this.$message.error('结束日期必须大于起始日期')
        }else{
        this.$store.dispatch('project/ExportOneProjectList',params).then(res=>{
          let contentDisposition = res.headers['content-disposition'];               
          let fileName = decodeURI(contentDisposition.substring(contentDisposition.indexOf('=') + 1));  
          let url = window.URL.createObjectURL(new Blob([res.data],{type: 'application/vnd.ms-excel;charset=UTF-8'}));            
          let a = document.createElement('a');            
          a.style.display = 'none';            
          a.href = url;            
          a.setAttribute('download', fileName);
          document.body.appendChild(a);            
          a.click();            
          document.body.removeChild(a);            
          window.URL.revokeObjectURL(url);
          this.$message.success('下载成功')
          }).catch(err=>{
          })
        }
        
      },
      openExporProject(){
        this.projectValue = this.exportProjectRowData.name
        this.getExportProjectList()
        this.getProjectNameAreaList()
      },
      getExportProjectList(){
        console.log(this.selectProject)
        const params = {
          areaId: this.areaValue,
          currentPage:this.pageNo,
          pageSize:this.pageSize,
          currentProjectName: this.selectProject,
          endTime: this.deepEndTime==null? this.projectReportEndTime:this.deepEndTime,
          projectName: null,
          startTime: this.deepStartTime==null? this.projectReportStartTime:this.deepStartTime,
        }
        this.projectReportLoading = true
        this.$store.dispatch('project/getExportProjectList',params).then(res=>{
          console.log(res)
           if(res.totalList.length==0){
            this.exportProjectReportTableData = []
            this.projectReportLoading = false
            this.total = 0;
            this.totalTime = 0
            this.percent = 0
            this.currentTime = 0
           }else{
            this.exportProjectReportTableData = res.totalList[0].reqReportFromByUserVOS
            this.totalTime = res.totalList[0].totalTime
            this.total = res.total
            this.projectReportLoading = false
            this.percent = res.totalList[0].percent
            this.currentTime = res.totalList[0].currentTotalTime
           }
        }).catch(err=>{
           this.projectReportLoading = false
        })
      },
      getProjectNameAreaList(){
        const params = {
          projectName:this.selectProject
        }
        this.$store.dispatch('area/getProjectNameAreaList',params).then(res=>{
           this.areaOptions = res
        })
      }
    }
}
</script>

<style lang="scss">
// .el-scrollbar__wrap{
//   overflow-x: hidden;
// }
.expportProjectSearch {
  display: flex;
  width: 100%;
  height: auto;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}
.export-project .el-input__icon {
    align-items: center;
    display: flex;
}
.export-project .el-dialog__wrapper {
  overflow: hidden;;
}
.export-project .el-dialog__body {
  overflow: auto;
  max-height: calc(123vh - 281px);

}
.export-project .header-title{
  display: flex;
  justify-content: space-between;
}
.export-project .el-dialog__headerbtn{
  display: none;
}
.export-project .el-dialog{
  height: 100% !important;
  margin-top: 0 !important;
}
.project-title{
  font-weight: bold;
  font-size: 18px;
}
@media only screen
and (max-width : 1366px){
  .export-project .el-dialog {
    width: 90% !important; 
  }
}
@media only screen
and (min-device-width : 320px)
and (max-device-width : 500px){
  .export-project .el-dialog {
    width: 100% !important; 
  }
  .expportProjectSearch {
  display: block !important;
  }
  .export-project .el-select {
    width: 100%;
  }
  .export-project .el-date-editor {
    width: 100% !important;
  }
  .export-project .pReportSearchBtn{
    width: 100%;
  }
  .export-project .projectreportsearch{
    margin-top: 5px;
  }
  .export-project .projectrexport{
    margin-right: 0 !important;
    text-align: right;
  }
}
</style>