<template>
<div class="project-name">
    <header-title>
      <template slot="title"> 项目字典管理 </template>
    </header-title>
    <div class="projectnameSearch">
      <div style="margin-left:55px" class="projectNameSearchBox">
        <el-input v-model="search" style="width:200px;height:32px" placeholder="请输入项目名称" clearable  class="projectnameinput crminput" @keyup.enter.native="searchProjectName"></el-input>
        <el-input v-model="searchCrmId" style="width:200px;margin-left:5px" placeholder="请输入CRM项目ID" clearable  class="projectnameinput crminput" @keyup.enter.native="searchProjectName"></el-input>
        <el-select v-model="crmStageValue" placeholder="请选择CRM项目阶段" style="margin-left:5px" clearable filterable class="dayexchangeSearchBoxmargin projectnameinput">
          <el-option
            v-for="item in crmStageOptions"
            :key="item.crmStageId"
            :label="item.crmStageName"
            :value="item.crmStageId">
          </el-option>
        </el-select>
        <el-select v-model="isCreateValue" placeholder="请选择是否可新建" style="margin-left:5px" clearable filterable class="dayexchangeSearchBoxmargin projectnameinput">
          <el-option
            v-for="item in isCreate"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
        <el-select v-model="areaValue" placeholder="请选择区域" style="margin-left:5px" clearable filterable class="dayexchangeSearchBoxmargin projectnameinput">
          <el-option
            v-for="item in areaOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
        </el-select>
        <el-button style="margin-left:5px" @click.native.prevent="searchProjectName" size="small" class="dayexchangeSearchBoxmargin projectnameinput dayexsearchbtn" type="success">搜索</el-button>
         &nbsp; 
        <el-button style="margin-left:5px" @click.native.prevent="syncProjectName" size="small" class="dayexchangeSearchBoxmargin projectnameinput dayexsearchbtn" type="success">项目同步</el-button>
      </div>
      <div style="margin-right:15px" class="exportProjectName">
        <el-button type="text" @click="handleAddprojectName">
          <span class="el-icon-circle-plus" style="color:#13ce66;"></span>
          <span style="color:#606266;">新建项目名称</span>
        </el-button>
        
        <el-button type="text" @click="handleExportName">
          <span class="el-icon-download" style="color:#13ce66;"></span>
          <span style="color:#606266;">导出项目字典</span>
        </el-button>
      </div>
    </div>
    <el-table :data="projectNameData" style="width:100%" v-loading="projectNameLoading">
       <el-table-column type="index">
         <template slot-scope="scope">
          <span>{{(pageNo - 1) * pageSize + scope.$index + 1}}</span>
        </template>
       </el-table-column>
       <el-table-column label="项目名称" prop="name"></el-table-column>
       <el-table-column label="CRM项目ID" prop="crmProjectId">
         <template slot-scope="scope">
           <span v-if="scope.row.crmStageName!==null">{{scope.row.crmProjectId}}</span>
           <span v-else>--</span>
         </template>
       </el-table-column>
       <el-table-column label="CRM项目阶段" prop="crmStageName">
         <template slot-scope="scope">
           <span v-if="scope.row.crmStageName!==null">{{scope.row.crmStageName}}</span>
           <span v-else>--</span>
         </template>
       </el-table-column>
       <el-table-column label="CRM项目阶段ID" prop="crmStageId">
         <template slot-scope="scope">
           <span v-if="scope.row.crmStageId!==null">{{scope.row.crmStageId}}</span>
           <span v-else>--</span>
         </template>
       </el-table-column>
       <el-table-column label="各大区是否可新建" prop="enable">
         <template slot-scope="scope">
           <span v-if="scope.row.enable==false">
            <el-tag type="danger">否</el-tag>
           </span>
           <span v-else>
            <el-tag type="success">是</el-tag>
           </span>
         </template>
       </el-table-column>
       <el-table-column label="强制日报描述" prop="crmStageId" >
         <template slot-scope="scope">
           <!-- <span v-if="scope.row.crmStageId!==null">{{scope.row.crmStageId}}</span>
           <span v-else>--</span> -->
          <el-switch
            v-model="scope.row.forceDescFlag" @change="changeDesc(scope.row.id)" inactive-color="#ff4949" active-text="开" inactive-text="关">
          </el-switch>
         </template>
       </el-table-column>
       <el-table-column label="主要责任区域" width="280">
         <template slot-scope="scope">
          <el-select v-model="scope.row.areaId" placeholder="请选择"  @change="selectArea(scope.row)" clearable>
            <el-option
            v-for="item in options"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
          </el-select>
          <el-button type="success" icon="el-icon-check" circle size="mini" style="margin-left:5px" @click="add(scope.row)" :disabled="isBtnDisabled(scope.row)" title="保存责任区域"></el-button>
         </template>
       </el-table-column>
       <el-table-column label="操作" width="118">
           <template slot-scope="scope">
               <el-button size="mini" icon="el-icon-edit-outline" title="编辑" @click="handleEdit(scope.row)" type="warning"></el-button>
               <el-button type="danger" icon="el-icon-delete" title="删除" size="mini" @click="handleDelete(scope.row)"></el-button>
           </template>
       </el-table-column>
    </el-table>
    <el-pagination
      style="float:right;margin-top:20px"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pageNo"
      :page-sizes="[10, 20, 30]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>
    <projectname
    :projectNameVisable.sync="projectNameVisable"
    :isEdit="isEdit"
    @refresh="getProjectNameList"
    :projectNameRowData="projectNameRowData"></projectname>
</div>

</template>

<script>
import headerTitle from "@/components/Title/index.vue";
import projectname from './component/addprojectname.vue'
export default {
  components: {
    headerTitle,
    projectname
  },
  data(){
    return{
      projectNameData:[],
      projectNameVisable:false,
      isEdit:false,
      projectNameLoading:false,
      projectNameRowData:{},
      pageSize:10,
      pageNo:1,
      total:0,
      options: [],
      value: '',
      value1:null,
      isChange: 1,
      selectData:null,
      deepData:null,
      search:null,
      searchCrmId:null,
      crmStageValue:null,
      crmStageOptions:[],
      areaOptions:[],
      areaValue:null,
      isCreateValue:null,
      isCreate:[
        {
          label:'是',
          value:true
        },
        {
          label:'否',
          value:false
        }
      ]
    }
  },
  created(){
    this.getProjectNameList()
    this.getAreaList()
    this.getAllStageList()
  },
  methods:{
    changeDesc(id){
      this.$store.dispatch('system/changeDesc',id).then(res=>{
        this.$message.success('修改状态成功')
        this.getProjectNameList()
      }).catch(err=>{
        this.getProjectNameList()
      })
    },
    getAllStageList(){
      this.$store.dispatch('system/getAllStage').then(res=>{
        this.crmStageOptions = res
      }).catch(err=>{})
    },
    searchProjectName(){
      this.pageNo = 1;
      this.getProjectNameList()
    },
    syncProjectName(){
      this.pageNo = 1;
      this.syncProjectNameList()
    },
    isBtnDisabled(row){
      // const isDis = this.deepData.find(item => row.id===item)
      // if(isDis!==undefined){
      //   return false
      // }else{
      //   return true
      // }
      if(this.deepData == row.id){
        return false
      }else{
        return true
      }
    },
    add(row){
      const params = {
        areaId:row.areaId,
        id:row.id
      }
      this.$store.dispatch('system/disArea',params).then(res=>{
        this.$message.success('分配成功')
        this.getProjectNameList()
        this.deepData = null
      })
    },
    selectArea(data){
      if(data){
        // this.deepData.push(data.id)
        this.deepData = data.id
      }
      // if(data.areaId==""){
      //   this.deepData = null
      // }
    },
    getAreaList() {
      const params = {
        golbalName: "",
        pageNumber: 0,
        pageSize: 0,
      };
      this.$store.dispatch("area/getAreaList", params).then((res) => {
        this.options = res.totalList;
        this.areaOptions = res.totalList
      }).catch(err=>{});
    },
    handleAddprojectName(){
      this.projectNameVisable = true
      this.isEdit = false
    },
    handleSizeChange(val){
      this.pageSize = val
      this.getProjectNameList()
    },
    handleCurrentChange(val){
      this.pageNo = val
      this.getProjectNameList()
    },
    handleEdit(row){
      this.projectNameVisable = true
      this.isEdit = true
      this.projectNameRowData = row
    },
    handleDelete(row){
        this.$confirm(
        `此操作将删除该项目名称,是否继续`,
        "删除", {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: "error",
          showClose: true,
          beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.showClose = false;
              instance.showCancelButton = false;
              instance.closeOnClickModal = false;
              this.$store
                .dispatch("system/deleteProjectName", row)
                .then(() => {
                  done();
                  instance.showClose = true;
                  instance.showCancelButton = true;
                  instance.closeOnClickModal = true;
                  instance.confirmButtonLoading = false;
                  this.$message.success("删除成功");
                  this.getProjectNameList()
                })
                .catch((err) => {
                  done();
                  instance.confirmButtonLoading = false;
                  this.$message.error(err)
                });
            } else {
              done();
            }
          },
        },
      ).catch(() => {});
    },
    getProjectNameList(){
      this.projectNameLoading = true
      const params = {
        areaId:this.areaValue,
        crmProjectId:this.searchCrmId,
        crmStageId:this.crmStageValue,
        enable:this.isCreateValue,
        currentPage: this.pageNo,
        pageSize: this.pageSize,
        searchVal: null,
        systemProjectName: this.search
      }
      this.$store.dispatch('system/getProjectNameList',params).then(res=>{
        this.projectNameLoading = false
        this.projectNameData = res.totalList
        this.total = res.total
      }).catch(err=>{})
    },
    syncProjectNameList(){
      this.projectNameLoading = true
      const params = {
        areaId:this.areaValue,
        crmProjectId:this.searchCrmId,
        crmStageId:this.crmStageValue,
        enable:this.isCreateValue,
        currentPage: this.pageNo,
        pageSize: this.pageSize,
        searchVal: null,
        systemProjectName: this.search
      }
      this.$store.dispatch('system/syncProjectNameList',{}).then(res=>{
         const mess = res.message
        if (res.result == true) {
          this.$message.success(mess)
        } else {
          this.$message.error(mess)
        }
        this.$store.dispatch('system/getProjectNameList',params).then(res=>{
          this.projectNameLoading = false
          this.projectNameData = res.totalList
          this.total = res.total
        }).catch(err=>{})
        
      }).catch(err=> {
         this.projectNameLoading = false
         this.$message.error("CRM项目名称列表同步失败")
      })
    },
    handleExportName(){
      const params = {
        areaId:this.areaValue,
        crmProjectId:this.searchCrmId,
        crmStageId:this.crmStageValue,
        enable:this.isCreateValue,
        currentPage: 1,
        pageSize: 1000000,
        searchVal: null,
        systemProjectName: this.search
      }
      this.$store.dispatch('report/downProjectName',params).then(res=>{
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
      }).catch(err=>{})
    }
  }
}
</script>

<style>
.el-switch__label {
    position: absolute;
    display: none;
    color: #fff !important;
    font-size: 12px !important;
}
.el-switch__label * {
    line-height: 1;
    font-size: 12px;
    display: inline-block;
}
/*打开时文字位置设置*/
.el-switch__label--right {
    z-index: 1;
}
/* 调整打开时文字的显示位子 */
 .el-switch__label--right span{
  margin-left: 13px;
  font-weight: bold;
}
/*关闭时文字位置设置*/
.el-switch__label--left {
    z-index: 1;
}
/* 调整关闭时文字的显示位子 */
.el-switch__label--left span{
    margin-left: 24px;
}
/*显示文字*/
.el-switch__label.is-active {
    display: block;
}
/* 调整按钮的宽度 */
.el-switch .el-switch__core,
.el-switch .el-switch__label {
  width: 50px !important;
  margin: 0;
}
.project-name{
  padding: 20px;
}
.projectnameSearch{
  /* float:right !important;
  margin-top: 20px; */
  width: 100%;
  height: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}
.project-name .el-input__inner{
  height: 32px;
}
.project-name .el-input .el-input__clear{
  margin-top: -4px;
}
.project-name .el-select__caret{
  display: flex;
  justify-content: center;
  align-items: center;
}
@media only screen
and (max-width : 1366px){
  .crminput{
    width: 175px !important;
  }
  .project-name .el-input__inner{
    width: 177px !important;
  }
}
@media only screen
and (max-width : 1500px){
  .project-name .el-input__inner{
    width:180px;
  }
  .crminput{
    width: 180px !important;
  }
  .projectNameSearchBox{
    margin-left: 0 !important;
  }
}
@media only screen
and (max-width : 1650px){
  .exportProjectName{
    display: flex;
    flex-direction: column;
    margin-right: 0px !important;
  }
  .exportProjectName .el-button+.el-button {
    margin-left: 0px !important;
  }
}
@media only screen
and (min-device-width : 320px)
and (max-device-width : 500px){
  .searchBox{
    margin-left:0 !important
  }
  .projectnameSearch{
    width: 100%;
    height: auto !important;
    display: block !important;
  }
  .dayexchangeSearchBox{
    margin-left: 0 !important;
  }
  .projectnameinput{
    width: 100% !important;
    margin-left: 0 !important;
    margin-top: 5px !important;
  }
  .project-name .el-input__inner{
    width: 100% !important;
  }
  .exportProjectName{
    text-align: right !important;
    margin-right: 0 !important;
  }
}
</style>