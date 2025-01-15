<template>
  <div class="project">
    <header-title>
      <template slot="title"> 项目列表管理 </template>
    </header-title>
    <div class="projectSearch" v-if="role=='AREA_MANAGER'||role=='MANAGEMENT'||role=='SUPER_ADMIN'||role=='FINANCE'">
      <div style="margin-left:55px" class="projectSearchBox">
        <!-- <el-input v-model="search" style="width:300px" placeholder="请输入项目名称" clearable @keyup.enter.native="searchProject"></el-input> -->
        <el-select v-model="search" placeholder="请选择项目" style="margin-left:5px" clearable  filterable :disabled="projectLoading" class="projectselect">
          <el-option
            v-for="item in projectOptions"
            :key="item.id"
            :label="item.name"
            :value="item.name">
          </el-option>
        </el-select>
        <el-select v-model="areaValue" placeholder="请选择区域" style="margin-left:5px" clearable @change="areaChange" :disabled="role=='AREA_MANAGER'||projectLoading? true:false" filterable class="projectselect">
          <el-option
            v-for="item in areaOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
        </el-select>
        <el-select v-model="roleValue" placeholder="请选择员工" style="margin-left:5px" clearable filterable :disabled="projectLoading" class="projectselect">
          <el-option
            v-for="item in roleOptions"
            :key="item.id"
            :label="item.nickname"
            :value="item.id">
          </el-option>
        </el-select>
        <el-button style="margin-left:5px" @click="searchProject" size="small" :disabled="projectLoading" class="projectserach searchprojectbtn" type="success">搜索</el-button>
      </div>
      <div style="margin-right:15px" class="addproject">
        <el-button type="text" :disabled="projectLoading" @click="handleAddproject" v-if="role!=='FINANCE'&&readonly=='false'">
          <span class="el-icon-circle-plus" style="color:#13ce66;"></span>
          <span style="color:#606266;">新建项目</span>
        </el-button>
        <el-button type="text" @click="exportReport">
          <span
            class="el-icon-download"
            style="color: #13ce66; font-size: 16px"
          ></span>
          <span style="color: #606266">导出报表</span>
        </el-button>
      </div>
    </div>
    <el-table
      :data="projectTableData"
      style="width: 100%;margin-top:10px"
      v-loading="projectLoading">
      <el-table-column type="expand">
      <template slot-scope="props">
        <el-form label-position="left"  class="demo-table-expand">
          <el-form-item label="项目描述">
            <span v-if="props.row.desc!==null">{{ props.row.desc }}</span>
            <span v-else>--</span>
          </el-form-item>
          <el-form-item label="创建时间">
            <span v-if="props.row.createTime">{{ props.row.createTime|formatDate }}</span>
            <span v-else>--</span>
          </el-form-item>
          <el-form-item label="更新时间">
            <span v-if="props.row.updateTime">{{ props.row.updateTime|formatDate }}</span>
            <span v-else>--</span>
          </el-form-item>
        </el-form>
      </template>
    </el-table-column>
      <el-table-column
        type="index">
         <template slot-scope="scope">
          <span>{{(pageNo - 1) * pageSize + scope.$index + 1}}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="name"
        label="项目名称">
      </el-table-column>
      <el-table-column
        prop="managerName"
        label="项目负责人">
         <template slot-scope="scope">
           <span v-if="scope.row.managerName!==null">{{scope.row.managerName}}</span>
           <span v-else>--</span>
         </template>
      </el-table-column>
      <el-table-column
        prop="areaName"
        label="区域名称">
        <template slot-scope="scope">
           <span v-if="scope.row.areaName!==null">{{scope.row.areaName}}</span>
           <span v-else>--</span>
         </template>
      </el-table-column>
      <el-table-column
        prop="enable"
        label="项目状态">
        <template slot-scope="scope">
          <span v-if="scope.row.enable===true">
            <el-tag type="success">启用</el-tag>
          </span>
          <span v-else>
            <el-tag type="danger">结束</el-tag>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="强制日报描述" prop="crmStageId">
         <template slot-scope="scope">
           <!-- <span v-if="scope.row.crmStageId!==null">{{scope.row.crmStageId}}</span>
           <span v-else>--</span> -->
          <el-switch
            v-model="scope.row.forceDescFlag" @change="changeDesc(scope.row.systemProjectId)" :disabled="scope.row.leaderFlag==true && readonly=='false'? false:true" inactive-color="#ff4949"  active-text="开"
                inactive-text="关">
          </el-switch>
         </template>
       </el-table-column>
       <!-- <el-table-column label="工时消耗进度">
         <template slot-scope="scope">
           <span v-if="scope.row.leaderFlag==false">--</span>
           <el-progress :percentage="percentage" :color="customColors" v-else></el-progress>
         </template>
       </el-table-column> -->
        <el-table-column label="操作" :width="readonly=='true'? 120:340" v-if="role=='AREA_MANAGER'||role=='MANAGEMENT'||role=='SUPER_ADMIN'">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" icon="el-icon-alarm-clock" title="项目单报表详细" @click="handleExportProject(scope.row)" :disabled="scope.row.leaderFlag==true? false:true"></el-button>
          <el-button size="mini" icon="el-icon-s-custom" title="配置人员" @click="handleConfigPerson(scope.row)" type="info" v-if="readonly=='false'"></el-button>
          <el-button type="warning" size="mini" icon="el-icon-edit-outline" title="编辑" v-if="readonly=='false'" @click="handleEdit(scope.row)" :disabled="scope.row.name ==='公司销售支持'||scope.row.name ==='学习提升'|| scope.row.name=='公司会议'||scope.row.name=='公司展会支持'||scope.row.name=='公司培训'||scope.row.name=='公司其他活动'||scope.row.name=='公司集团内项目'? true:false"></el-button>
          <el-button size="mini" icon="el-icon-document" title="详情" @click="handleDetail(scope.row)" type="success"></el-button>
          <el-button
          size="mini"
          icon="el-icon-delete"
          title="删除"
          v-if="readonly=='false'"
          @click="handleDelete(scope.row)"
          type="danger"
          :disabled="scope.row.name ==='公司销售支持'||scope.row.name=='公司会议'||scope.row.name=='公司展会支持'||scope.row.name=='公司培训'||scope.row.name=='公司其他活动'||scope.row.name=='学习提升'||scope.row.name=='公司集团内项目'? true:false"></el-button>
          <el-button size="mini" icon="el-icon-turn-off" title="结束" @click="handleStatus(scope.row)" type="danger" v-if="scope.row.enable==true&&readonly=='false'"
          :disabled="scope.row.name ==='公司销售支持'||scope.row.name=='公司会议'||scope.row.name=='公司展会支持'||scope.row.name=='公司培训'||scope.row.name=='公司其他活动'||scope.row.name=='学习提升'||scope.row.name=='公司集团内项目'? true:false"></el-button>
          <el-button v-if="scope.row.enable==false&&readonly=='false'"
          size="mini"
          icon="el-icon-open"
          title="启用"
          @click="handleStatus(scope.row)"
          type="success"></el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" v-else-if="role=='GROUP_MANAGER'||role=='FINANCE'">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" icon="el-icon-alarm-clock" title="项目单报表详细" @click="handleExportProject(scope.row)" :disabled="scope.row.leaderFlag==true? false:true"></el-button>
          <el-button size="mini" @click="handleDetail(scope.row)" type="success" icon="el-icon-document" title="详情"></el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="80" v-else>
        <template slot-scope="scope">
          <el-button size="mini" @click="handleDetail(scope.row)" type="success" icon="el-icon-document" title="详情"></el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      style="float:right;margin-top:10px"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pageNo"
      :page-sizes="[10, 20, 30]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>
    <add-project :addProject.sync="addProject" :isEdit="isEdit" :rowDatas="rowDatas" @refresh="refreshList" :areasOptions="areaOptions"/>
    <detail-project :projectDetailVisable.sync="projectDetailVisable" :projectInfo ="projectInfo"/>
    <config-person :configVisable.sync="configVisable" :configRowDatas="configRowDatas"/>
    <export-project :exportproject.sync="exportproject" :exportProjectRowData="exportProjectRowData" :singleProjectList="singleProjectList"/>
  </div>
</template>

<script>
import headerTitle from '@/components/Title/index.vue'
import addProject from './components/addProjectDialog.vue'
import detailProject from './components/detailProjectDialog.vue'
import configPerson from './components/editProjectPerson.vue'
import exportProject from './components/exportProject.vue'
export default {
  components:{
    headerTitle,
    addProject,
    detailProject,
    configPerson,
    exportProject
  },
  data(){
    return{
      readonly:localStorage.getItem('read-only'),
      projectOptions:[],
      projectSearch:'',
      projectTableData:[],
      addProject:false,
      roleValue:null,
      roleOptions:[],
      areaOptions:[],
      areaValue:'',
      projectDetailVisable:false,
      projectInfo:{},
      isProjectPerson:true,
      projectPersonOptions:[],
      projectPersonValue:'',
      search:'',
      isEdit:false,
      rowDatas:{},
      configVisable:false,
      total:0,
      pageNo:1,
      pageSize:10,
      configRowDatas:{},
      role:localStorage.getItem('role-code'),
      projectLoading:false,
      exportproject:false,
      exportProjectRowData:{},
      singleProjectList:[]
    }
  },
  created(){
    const role = localStorage.getItem('role-code')
    if(role=='GROUP_MANAGER'||role=='EMPLOYEE'){
      this.getAssignUser()
    }else{
      this.getProjectList()
      this.getPersonList()
      this.getselectproject()
    }
    if(role!=='GROUP_MANAGER'&&role!=='EMPLOYEE'){
      this.getAreaList()
    }
    // this.getRoleUsers()
  },
  methods:{
    refreshList(){
      this.getPersonList()
      this.getProjectList()
      this.getselectproject()
    },    
    changeDesc(id){
      const role = localStorage.getItem('role-code')
      this.$store.dispatch('system/changeDesc',id).then(res=>{
        this.$message.success('修改状态成功')
        if(role=='GROUP_MANAGER'||role=='EMPLOYEE'){
          this.getAssignUser()
        }else{
          this.getProjectList()
        }
      }).catch(err=>{
        if(role=='GROUP_MANAGER'||role=='EMPLOYEE'){
          this.getAssignUser()
        }else{
          this.getProjectList()
        }
      })
    },
    handleExportProject(row){
      const roleCode = localStorage.getItem('role-code')
      let areaId = null
      if(roleCode=="SUPER_ADMIN"||roleCode=='MANAGEMENT'||roleCode=='FINANCE'){
        areaId = null
      }else{
        areaId = roleCode
      }
      this.exportproject = true
      this.exportProjectRowData = row
      const params = {
        mainAreaId: null,
      };
      this.$store
        .dispatch("project/getProjectCharts", params)
        .then((res) => {
          this.singleProjectList = []
          if(roleCode=="SUPER_ADMIN"||roleCode=='MANAGEMENT'||roleCode=='FINANCE'){
            this.singleProjectList = res.data
          }
          if(roleCode=='AREA_MANAGER'){
            res.data.forEach(item=>{
              if(item.enable==true||item.enable==false){
                this.singleProjectList.push(item)
              }
            })
          }
          if(roleCode=='GROUP_MANAGER'){
            res.data.forEach(item=>{
              if(item.enable==true){
                this.singleProjectList.push(item)
              }
            })
          }
        })
        .catch((err) => {});
    },
    searchProject(){
      this.pageNo = 1
      // this.getProjectList()
      const role = localStorage.getItem('role-code')
      if(role=='GROUP_MANAGER'||role=='EMPLOYEE'){
      this.getAssignUser()
      }else{
      this.getProjectList()
      this.getPersonList()
     }
    },
    areaChange(data){
      if(data){
        this.getAreaProjectPerson()
      }
      if(!data){
        this.getPersonList()
      }
    },
    getAreaProjectPerson(){
      const params = {
        areaId:this.areaValue
      }
      this.$store.dispatch('project/getAreaProjectPerson',params).then(res=>{
        this.roleOptions = res
      })
    },
    getPersonList() {
      const params = {
        areaId:0,
        pageSize: 1000000,
        currentPage: 1,
        searchVal: null,
        nickname:'',
        projectId:null,
        roleId:null
      };
      const that = this;
      that.$store
        .dispatch("person/getpseronList", params)
        .then((res) => {
          that.roleOptions = res.totalList;
        })
        .catch((err) => {
          that.loading = false;
        });
    },
    getAssignUser(){
      const params = {
        currentPage: this.pageNo,
        pageSize: this.pageSize,
        searchVal: null
      }
      this.projectLoading = true
      this.$store.dispatch("project/getAssignUser",params).then(res=>{
        this.projectTableData = res.totalList;
        this.total = res.total
        this.projectLoading = false
      }).catch(err=>{
        this.projectLoading = false
      })
    },
    unique(arr) {
      const res = new Map();
      this.projectOptions = arr.filter((arr) => !res.has(arr.name) && res.set(arr.name, 1))
    },
    getselectproject(){
      const params = {
        areaId: this.areaValue,
        currentPage: 1,
        desc: "",
        managerId: null,
        name: this.search,
        pageSize: 100000,
        searchVal: "",
        userId: this.roleValue
      }
      this.$store.dispatch('project/getProjectList',params).then(res=>{
        this.projectOptions = res.totalList
        this.unique(this.projectOptions)
      }).catch(err=>{})
    },
    getProjectList(){
      // const areaId = localStorage.getItem('area-id')
      const params = {
        areaId: this.areaValue,
        currentPage: this.pageNo,
        desc: "",
        managerId: null,
        name: this.search,
        pageSize: this.pageSize,
        searchVal: "",
        userId: this.roleValue
      }
      this.projectLoading = true
      this.$store.dispatch('project/getProjectList',params).then(res=>{
        this.projectTableData = res.totalList;
        this.total = res.total
        this.projectLoading = false
      }).catch(err=>{})
    },
    handleCurrentChange(val){
      this.pageNo = val
      if(this.role=='GROUP_MANAGER'||this.role=='EMPLOYEE'){
        this.getAssignUser()
      }else{
        this.getProjectList()
      }

    },
    handleSizeChange(val){
      this.pageSize = val
      if(this.role=='GROUP_MANAGER'||this.role=='EMPLOYEE'){
        this.getAssignUser()
      }else{
        this.getProjectList()
      }
    },
    getAreaList(){
      const params = {
        golbalName: '',
        pageNumber: 0,
        pageSize: 0
      }
      this.$store.dispatch('area/getAreaList',params).then(res=>{
        // this.areaOptions = res.totalList;
        const role = localStorage.getItem('role-code')
        const areaid = localStorage.getItem('area-id')
        if(role=='GROUP_MANAGER'){
          res.totalList.forEach(item=>{
            if(item.id == areaid){
              this.areaOptions.push(item)
            }
          })
        }else if(role=='EMPLOYEE'){
          res.totalList.forEach(item=>{
            if(item.id == areaid){
              this.areaOptions.push(item)
            }
          })
        }else if(role=='AREA_MANAGER'){
          res.totalList.forEach(item=>{
            if(item.id == areaid){
              this.areaOptions.push(item)
              this.areaValue = item.id
            }
          })
        }else if(role=='SUPER_ADMIN'||'HR'||'FINANCE'||'MANAGEMENT'){
          this.areaOptions = res.totalList;
        }

      })
    },
    handleAddproject(){
      this.addProject = true
      this.isEdit = false
    },
    handleStatus(row){
      this.$confirm(
        `此操作将修改该项目状态,是否继续`,
        "修改", {
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
                .dispatch("project/projectStatus", row.id)
                .then(() => {
                  done();
                  instance.showClose = true;
                  instance.showCancelButton = true;
                  instance.closeOnClickModal = true;
                  instance.confirmButtonLoading = false;
                  this.$message.success("更改成功");
                  this.getProjectList()
                })
                .catch((err) => {
                  done();
                  instance.confirmButtonLoading = false;
                  // this.$message.error(err)
                });
            } else {
              done();
            }
          },
        },
      ).catch(() => {});
      //  this.$store.dispatch('project/projectStatus',row.id).then(res=>{
      //    this.$message.success('更改成功');
      //    this.getProjectList()
      //  })
    },
    handleConfigPerson(row){
      this.configVisable = true
      this.configRowDatas = row
    },
    handleDetail(row){
      this.projectDetailVisable = true;
      this.$store.dispatch('project/getProjectPersonDetail',row.id).then(res=>{
        this.projectInfo = res
      })
    },
    handleDelete(row){
      this.$confirm(
        `此操作将删除该项目,是否继续`,
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
                .dispatch("project/deleteProject", row.id)
                .then(() => {
                  done();
                  instance.showClose = true;
                  instance.showCancelButton = true;
                  instance.closeOnClickModal = true;
                  instance.confirmButtonLoading = false;
                  this.$message.success("删除成功");
                  this.getProjectList()
                })
                .catch((err) => {
                  done();
                  instance.confirmButtonLoading = false;
                  // this.$message.error(err)
                });
            } else {
              done();
            }
          },
        },
      ).catch(() => {});
    },
    handleEdit(row){
      this.isEdit = true
      this.addProject = true
      this.rowDatas = row
    },
    exportReport(){
      const params = {
        areaId: this.areaValue,
        currentPage: 1,
        desc: "",
        managerId: null,
        name: this.search,
        pageSize: 10000000,
        searchVal: "",
        userId: this.roleValue
      }
      this.$store.dispatch('project/downloadProject',params).then(res=>{
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
.project{
  padding: 20px;
}
.el-switch.is-disabled {
  opacity: 0.2;
}
.el-switch__label--right {
    z-index: 1;
}
.projectSearch{
  display: flex;
  width: 100%;
  height: 50px;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}
.project .el-input__inner{
  height: 32px;
}
.project .el-input .el-input__clear{
  margin-top: -4px;
}
.project .el-select__caret{
  display: flex;
  justify-content: center;
  align-items: center;
}
@media only screen
and (min-device-width : 320px)
and (max-device-width : 500px){
  .searchBox{
    margin-left:0 !important
  }
  .projectSearch{
  width: 100%;
  height: auto !important;
  display: block !important;
  }
  .projectSearchBox{
    margin-left: 0 !important;
  }
  .areamargin{
    margin-left: 0px !important;
  }
  .areainput{
    width: 200px !important;
  }
  .projectselect{
    width: 100% !important;
    margin-left: 0 !important;
    margin-top: 5px !important;
  }
  .projectserach{
    margin-left: 0 !important;
    margin-top: 5px;
    width: 100%;
  }
  .addproject{
    text-align: right !important;
    margin-right: 0px !important;
  }
  .searchprojectbtn{
    background-color: #409eff;
    color: #fff;
  }
}
</style>