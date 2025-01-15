<template>
  <div class="complex-report">
    <header-title>
      <template slot="title"> 复杂列表查询 </template>
    </header-title>
    <div class="complexSearch">
      <div style="margin-left: 55px" class="complexSearchBox">
        <el-select
          v-model="areaValue"
          placeholder="请选择区域"
          style="margin-left: 5px"
          clearable
          multiple
          collapse-tags
          :disabled="role=='AREA_MANAGER'? true:false"
          filterable
          class="complexTime complexselect"
        >
          <el-option
            v-for="item in areaOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
        <el-select
          v-model="projectValue"
          placeholder="请选择项目"
          style="margin-left: 5px"
          clearable
          collapse-tags
          multiple
          filterable
          class="complexTime complexselect"
        >
          <el-option
            v-for="item in projectOptions"
            :key="item.id"
            :label="item.name"
            :value="item.name"
          />
        </el-select>

        <el-date-picker
          v-model="complexStartTime"
          type="date"
          placeholder="选择查询开始日期(必填)"
          style="margin-left: 5px;width:211px"
          class="complexTime"
          :picker-options="pickerOptions"
        />
        <el-date-picker
          v-model="complexEndTime"
          type="date"
          placeholder="选择查询结束日期(必填)"
          style="margin-left: 5px;width:211px"
          class="complexTime"
          :picker-options="pickerOptions"
        />
        <el-button
          style="margin-left: 5px"
          size="small"
          :disabled="complexStartTime!==null&&complexEndTime!==null?false:true"
          class="complexTime complexSearchBtn"
          type="success"
          @click="getComplexReportList"
        >搜索</el-button>
        <span style="margin-left:10px">包含离开人员:</span>
        <a-switch checked-children="是" un-checked-children="否" :default-checked="false" style="margin-left:10px" @change="onChange" />
      </div>
      <div style="margin-right:15px" class="complexerport">
        <el-button type="text" :disabled="complexStartTime!==null&&complexEndTime!==null?false:true" @click="exportReport">
          <span class="el-icon-download" style="color:#13ce66;font-size:16px" />
          <span style="color:#606266;">导出报表</span>
        </el-button>
      </div>
    </div>
    <el-table v-loading="complexLoading" :data="complexTableData" style="width: 100%">
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-table :data="props.row.resReportFormComplexUserVO">
            <el-table-column label="姓名" prop="nickname" />
            <el-table-column label="数量" prop="totalTime" />
          </el-table>
        </template>
      </el-table-column>
      <el-table-column label="项目名称" prop="projectName" />
    </el-table>
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
      complexTableData: [],
      complexStartTime: null,
      complexEndTime: null,
      projectValue: [],
      projectOptions: [],
      areaValue: [],
      areaOptions: [],
      isdate: false,
      complexLoading: false,
      role: localStorage.getItem('role-code'),
      isOtherPerson: false
    }
  },
  created() {
    // this.getComplexReportList();
    this.getAreaList()
    this.getProjectList()
  },
  methods: {
    onChange(change) {
      this.isOtherPerson = change
    },
    getTime(time) {
      return Date.parse(time)
    },
    unique(arr) {
      const res = new Map()
      this.projectOptions = arr.filter((arr) => !res.has(arr.name) && res.set(arr.name, 1))
    },
    getAreaList() {
      const params = {
        golbalName: '',
        pageNumber: 0,
        pageSize: 0
      }
      this.$store.dispatch('area/getAreaList', params).then((res) => {
        // this.areaOptions = res.totalList;
        const role = localStorage.getItem('role-code')
        const areaid = localStorage.getItem('area-id')
        if (role == 'AREA_MANAGER') {
          res.totalList.forEach(item => {
            if (item.id == areaid) {
              this.areaOptions.push(item)
              this.areaValue.push(item.id)
            }
          })
        } else {
          this.areaOptions = res.totalList
        }
      })
    },
    getProjectList() {
      const params = {
        areaId: null,
        currentPage: 1,
        desc: '',
        managerId: null,
        name: '',
        pageSize: 10000,
        searchVal: '',
        userId: null
      }
      this.$store.dispatch('project/getProjectList', params).then((res) => {
        this.projectOptions = res.totalList
        this.unique(this.projectOptions)
      }).catch(err => {})
    },
    getComplexReportList() {
      const params = {
        areaId: this.areaValue,
        endTime: this.getTime(this.complexEndTime),
        projectId: null,
        projectName: this.projectValue,
        startTime: this.getTime(this.complexStartTime),
        containAwayUserFlag: this.isOtherPerson
      }
      if (this.getTime(this.complexStartTime) > this.getTime(this.complexEndTime)) {
        this.$message.error('结束日期必须大于起始日期')
      } else {
        this.complexLoading = true
        this.$store.dispatch('report/getComplexList', params).then((res) => {
          this.complexTableData = res
          this.complexLoading = false
        }).catch(err => {
          this.complexLoading = false
          this.complexTableData = []
          this.$message.error(err)
        })
      }
    },
    exportReport() {
      if (this.getTime(this.complexStartTime) > this.getTime(this.complexEndTime)) {
        this.$message.error('结束日期必须大于起始日期')
      } else {
        const params = {
          areaId: this.areaValue,
          endTime: this.getTime(this.complexEndTime),
          projectId: null,
          projectName: this.projectValue,
          startTime: this.getTime(this.complexStartTime),
          containAwayUserFlag: this.isOtherPerson
        }
        this.$store.dispatch('report/downComplexReportForm', params).then(res => {
          const contentDisposition = res.headers['content-disposition']
          const fileName = decodeURI(contentDisposition.substring(contentDisposition.indexOf('=') + 1))
          const url = window.URL.createObjectURL(new Blob([res.data], { type: 'application/vnd.ms-excel;charset=UTF-8' }))
          const a = document.createElement('a')
          a.style.display = 'none'
          a.href = url
          a.setAttribute('download', fileName)
          document.body.appendChild(a)
          a.click()
          document.body.removeChild(a)
          window.URL.revokeObjectURL(url)
          this.$message.success('下载成功')
        }).catch(err => {
          this.$message.error(err)
        })
      }
    }
  }
}
</script>

<style lang="scss">
.complex-report {
  padding: 20px;
}
.complexSearch {
  display: flex;
  width: 100%;
  height: 50px;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}
.complex-report .el-input__inner {
  height: 32px;
}
.complex-report .el-input .el-input__clear {
  margin-top: -4px;
}
.complex-report .el-select__caret {
  display: flex;
  justify-content: center;
  align-items: center;
}
.complex-report .el-input__icon {
  display: flex;
  align-items: center;
}
@media only screen
and (max-width : 1500px){
  .complexSearchBox{
    margin-left: 0 !important;
  }
  .complexselect{
    width: 165px !important;
  }
}
@media only screen
and (max-width : 1366px){
  .complexSearchBox{
    margin-left: 0 !important;
  }
  .complexSearch{
    height: auto;
  }
}
@media only screen
and (min-device-width : 320px)
and (max-device-width : 500px){
  .searchBox{
    margin-left:0 !important
  }
  .complexSearch{
  width: 100%;
  height: auto !important;
  display: block !important;
  }
  .complexSearchBox{
    margin-left: 0 !important;
  }
  .areamargin{
    margin-left: 0px !important;
  }
  .areainput{
    width: 200px !important;
  }
  .complexTime{
    width: 100% !important;
    margin-top: 5px;
    margin-left: 0px !important;
  }
  .complexerport{
    text-align: right;
    margin-right: 0 !important;
  }
  .complexSearchBtn{
    background-color: #409eff;
    color: #fff;
  }
}
</style>
