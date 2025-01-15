<template>
    <div class="dayexchange">
      <header-title>
        <template slot="title"> 倒休管理 </template>
      </header-title>
      <div class="daySearch">
      <div style="margin-left:55px" class="dayexchangeSearchBox">
        <el-input v-model="search" style="width:300px" placeholder="请输入员工姓名" clearable @keyup.enter.native="searchDayExchanget" class="dayexchangeinput"></el-input>
        <el-select v-model="areaValue" placeholder="请选择区域" style="margin-left:5px" clearable filterable class="dayexchangeSearchBoxmargin dayexchangeinput">
          <el-option
            v-for="item in areaOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
        </el-select>
        <el-button style="margin-left:5px" @click.native.prevent="searchDayExchanget" size="small" class="dayexchangeSearchBoxmargin dayexchangeinput dayexsearchbtn" type="success">搜索</el-button>
      </div>
    </div>
    <el-table :data="dayexchangeData" style="width:100%" v-loading="dayloading">
      <el-table-column type="index">
         <template slot-scope="scope">
          <span>{{(pageNo - 1) * pageSize + scope.$index + 1}}</span>
        </template>
      </el-table-column>
      <el-table-column label="区域名称" prop="areaName"></el-table-column>
      <el-table-column label="员工姓名" prop="nickname">
        <template slot-scope="scope">
          <span style="color:blue;cursor: pointer;" @click="detail(scope.row)">{{scope.row.nickname}}</span>
        </template>
      </el-table-column>
      <el-table-column label="加班时长" prop="overHour"></el-table-column>
      <el-table-column label="请假时长" prop="leaveHour"></el-table-column>
      <el-table-column label="剩余调休时长" prop="exchangeHour"></el-table-column>
      <el-table-column label="总实际工作时长" prop="workHour"></el-table-column>
    </el-table>
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pageNo"
      :page-sizes="[10,20,30]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      style="float:right;margin-top:10px">
    </el-pagination>
    <detailchange :detailchange.sync="detailchange" :detailData="detailData"/>
    </div>
    
</template>

<script>
import headerTitle from '@/components/Title/index.vue'
import detailchange from './components/detailchange'
export default {
  components:{
    headerTitle,
    detailchange
  },
  data(){
    return{
      areaOptions:[],
      areaValue:null,
      personValue:null,
      personOptions:[],
      search:null,
      pageSize:10,
      pageNo:1,
      dayexchangeData:[],
      total:0,
      detailchange:false,
      detailData:[],
      dayloading:false
    }
  },
  created(){
    this.getdayExchangeList()
    this.getAreaList()
  },
  methods:{
    searchDayExchanget(){
      this.pageNo = 1
      this.getdayExchangeList()
    },
    detail(row){
      this.detailchange = true
      this.$store.dispatch('dailyreport/getDayExchangeDetail',row.userId).then(res=>{
        this.detailData = res
      })
    },
    getAreaList(){
      const params = {
        golbalName: '',
        pageNumber: 0,
        pageSize: 0
      }
      this.$store.dispatch('area/getAreaList',params).then(res=>{
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
            }
          })
        }else if(role=='SUPER_ADMIN'||role=='HR'||role=='FINANCE'||role=='MANAGEMENT'){
          this.areaOptions = res.totalList;
        }
      })
    },
    handleSizeChange(val){
      this.pageSize = val
      this.getdayExchangeList()
    },
    handleCurrentChange(val){
      this.pageNo = val
      this.getdayExchangeList()
    },
    getdayExchangeList(){
      const params = {
        areaId: this.areaValue,
        currentPage: this.pageNo,
        nickname: this.search,
        pageSize: this.pageSize,
        searchVal:null
      }
      this.dayloading = true
      this.$store.dispatch('dailyreport/getDayExchangeList',params).then(res=>{
        this.dayexchangeData = res.totalList
        this.total = res.total
        this.dayloading = false
      }).catch(err=>{})
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
  height: 50px;
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
@media only screen
and (min-device-width : 320px)
and (max-device-width : 500px){
  .daySearch{
    width: 100%;
    height: auto !important;
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