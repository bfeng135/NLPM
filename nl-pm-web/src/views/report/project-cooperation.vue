<template>
  <div class="project-cooperation">
    <div class="project-search">
      <el-date-picker
        v-model="personChartsStartValue"
        type="date"
        placeholder="选择开始日期(必填)"
        class="startTime"
        @change="changeStartTime"
      />
      <el-date-picker
        v-model="personChartsEndValue"
        type="date"
        placeholder="选择结束日期(必填)"
        class="startTime"
        @change="changeEndTime"
      />
      <el-select v-model="areaValue" placeholder="请选择区域(必填)" class="startTime" clearable :disabled="role=='AREA_MANAGER'? true:false" @clear="clearSelect">
        <el-option
          v-for="item in areaOptions"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-button type="text" class="startTime" :disabled="personChartsStartValue!==null&&personChartsEndValue!==null&& areaValue!==null ? false:true" @click="exportReport">
        <span
          class="el-icon-download"
          style="color: #13ce66; font-size: 16px"
        />
        <span style="color: #606266">导出报表</span>
      </el-button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      personChartsStartValue: null,
      personChartsEndValue: null,
      areaOptions: [],
      areaValue: null,
      role: localStorage.getItem('role-code')
    }
  },
  created() {
    this.getAreaList()
  },
  methods: {
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
              this.areaValue = item.id
            }
          })
        } else {
          this.areaOptions = res.totalList
        }
      }).catch(err => {})
    },
    clearSelect() {
      this.areaValue = null
    },
    changeStartTime(val) {
      this.personChartsStartValue = Date.parse(val)
      console.log(this.personChartsStartValue)
    },
    changeEndTime(val) {
      this.personChartsEndValue = (Date.parse(val) - 1) + 24 * 60 * 60 * 1000
      console.log(this.personChartsEndValue)
    },
    exportReport() {
      const params = {
        areaId: this.areaValue,
        endTime: this.personChartsEndValue,
        startTime: this.personChartsStartValue,
        costTypeEnum: null
      }
      this.$store.dispatch('report/downProjectCoo', params).then(res => {
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
      })
    }
  }
}
</script>

<style>
.project-cooperation {
  padding: 20px;
}
.startTime{
  margin-left: 10px;
}
</style>
