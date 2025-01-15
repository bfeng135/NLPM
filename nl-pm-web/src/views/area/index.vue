<template>
  <div class="area">
    <header-title>
      <template slot="title"> 区域管理 </template>
    </header-title>
    <div class="areaSearch">
      <div style="margin-left:55px" class="areaSearchBox">
        <el-input v-model="areaSearch" style="width:300px" class="areainput" clearable placeholder="请输入关键字" @keyup.enter.native="searchAreaList"></el-input>
        <el-button style="margin-left:5px" @click="searchAreaList" size="small" :disabled="loading" class="areamargin areainput areasearchbtn" type="success">搜索</el-button>
      </div>
      <div style="margin-right:15px" class="addarea" v-if="role=='MANAGEMENT'||role=='SUPER_ADMIN'">
        <el-button type="text" @click="handleAddArea" :disabled="loading">
          <span class="el-icon-circle-plus" style="color:#13ce66;"></span>
          <span style="color:#606266;">新建区域</span>
        </el-button>
      </div>
    </div>
    <el-table :data="areaTableData" style="width: 100%" v-loading="loading">
      <el-table-column type="expand">
      <template slot-scope="props">
        <el-form label-position="left" inline class="demo-table-expand">
          <el-form-item label="描述">
            <span>{{ props.row.desc }}</span>
          </el-form-item>
        </el-form>
      </template>
    </el-table-column>
      <el-table-column type="index">
         <template slot-scope="scope">
          <span>{{(pageNum - 1) * pageSize + scope.$index + 1}}</span>
        </template>
      </el-table-column>
      <el-table-column label="名称" prop="name"> </el-table-column>
      <el-table-column label="区域负责人" prop="nickname"> </el-table-column>
      <el-table-column label="创建时间"  prop="createTime">
        <template slot-scope="scope">
          <span v-if="scope.row.email !== ''">{{
            scope.row.createTime | formatDate
          }}</span>
          <span v-else>--</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间"  prop="updateTime">
        <template slot-scope="scope">
          <span v-if="scope.row.updateTime !== ''">{{
            scope.row.updateTime | formatDate
          }}</span>
          <span v-else>--</span>
        </template>
      </el-table-column>
      <el-table-column label="是否启用" prop="status">
         <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status" @change="changeStatus(scope.row)" inactive-color="#ff4949"  active-text="开"
            :disabled="role=='FINANCE'||role=='HR'? true:false"
            inactive-text="关">
          </el-switch>
         </template>
       </el-table-column>
      <el-table-column label="操作"  width="226" v-if="role=='MANAGEMENT'||role=='SUPER_ADMIN'">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleAreaPerson(scope.row)" icon="el-icon-link" title="关联其他区人员" type="primary"></el-button>
          <el-button size="mini" @click="handleAreaEdit(scope.row)" type="warning" class="el-icon-edit-outline" title="编辑"></el-button>
          <el-button size="mini" @click="handleDetail(scope.row)" type="success" icon="el-icon-document" title="详情"></el-button>
          <el-button size="mini" type="danger" @click="handleDelete(scope.row)" icon="el-icon-delete" title="删除"></el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="footer">
      <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pageNum"
      :page-sizes="[10,20,30]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>
    </div>
    <detail-area :detail-area-visible.sync="detailAreaVisible" :area-detail="areaDetail"/>
    <add-area :add-area-visable.sync="addAreaVisable" :isEdit="isEdit" @refresh="getAreaList" :area-row-data="AreaRowData"></add-area>
    <area-config-person :AreaConfigVisable.sync="AreaConfigVisable" :configAreaRowDatas="configAreaRowDatas"/>
  </div>
</template>

<script>
import headerTitle from '@/components/Title/index.vue'
import detailArea from './components/detailArea.vue'
import addArea from './components/addAreaDialog.vue'
import AreaConfigPerson from './components/editAreaPerson.vue'
export default {
  components:{
    headerTitle,
    detailArea,
    addArea,
    AreaConfigPerson
  },
  data(){
    return{
      areaTableData:[],
      total:0,
      pageSize:10,
      pageNum:1,
      loading:false,
      areaSearch:'',
      detailAreaVisible:false,
      areaDetail:{},
      addAreaVisable:false,
      isEdit:false,
      AreaRowData:{},
      AreaConfigVisable:false,
      configAreaRowDatas:{},
      role:localStorage.getItem('role-code')
    }
  },
  created(){
    this.getAreaList()
  },
  methods:{
    changeStatus(row){
      const params = {
        id:row.id,
        status:row.status
      }
      this.$store.dispatch('area/updateAreaStatus',params).then(res=>{
        this.getAreaList()
        this.$message.success('更改成功') 
      }).catch(err=>{
        this.getAreaList()
      })
    },
    searchAreaList(){
      this.pageNum = 1
      this.getAreaList()
    },
    getAreaList(){
      const params = {
        golbalName: this.areaSearch,
        pageNumber: this.pageNum,
        pageSize: this.pageSize
      }
      this.loading = true
      this.$store.dispatch('area/getAreaList',params).then(res=>{
        this.areaTableData = res.totalList;
        this.total = res.total;
        this.loading = false
      })
    },
    handleSizeChange(val){
      this.pageSize = val
      this.getAreaList()
    },
    handleCurrentChange(val){
      this.pageNum = val;
      this.getAreaList()
    },
    handleAddArea(){
      this.addAreaVisable = true
      this.isEdit = false
    },
    handleAreaPerson(row){
      this.AreaConfigVisable = true
      this.configAreaRowDatas = row
    },
    handleDetail(row){
      this.detailAreaVisible = true
      const params = {
        areaId:row.id
      }
      this.$store.dispatch('area/getAreaDetail',params).then(res=>{
        this.areaDetail = res
      }).catch(err=>{
        this.$message.error(err)
      })
    },
    handleAreaEdit(row){
      this.addAreaVisable = true
      this.isEdit = true
      this.AreaRowData = row
    },
    handleDelete(row){
      const params = {
        areaId: row.id
      }
      this.$confirm(
        `此操作将删除该区域,是否继续`,
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
                .dispatch("area/detleteArea", params)
                .then(() => {
                  done();
                  instance.showClose = true;
                  instance.showCancelButton = true;
                  instance.closeOnClickModal = true;
                  instance.confirmButtonLoading = false;
                  this.$message.success("删除成功");
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
    }
  }
}
</script>

<style lang="scss">
.area{
  padding: 20px;
}
.areaSearch{
  display: flex;
  width: 100%;
  height: 50px;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}
.area .footer{
  float:right;
  margin-top: 20px;
}
.area .el-input__inner{
  height: 32px;
}
.area .el-input .el-input__clear{
  margin-top: -4px;
}
@media only screen
and (min-device-width : 320px)
and (max-device-width : 500px){
  .searchBox{
    margin-left:0 !important
  }
  .areaSearch{
  width: 100%;
  height: auto !important;
  display: block !important;
  }
  .areaSearchBox{
    margin-left: 0 !important;
  }
  .areamargin{
    margin-left: 0px !important;
  }
  .areainput{
    width: 100% !important;
    margin-bottom: 5px;
  }
  .addarea{
    text-align: right;
    margin-right: 0 !important;
  }
  .areasearchbtn{
    background-color: #409eff;
    color: #fff;
  }
}
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
</style>