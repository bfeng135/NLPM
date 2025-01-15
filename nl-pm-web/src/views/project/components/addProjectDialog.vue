<template>
  <div class="add-project-dialog">
    <el-dialog
      :title="isEdit? '编辑项目':'新建项目'"
      :visible.sync="addProjectDialogVisible"
      width="40%"
      :show-close="!addprojectloading"
      @close="closeAddProject"
      @open="openAddPersonDialog"
    >
      <span>
        <el-form ref="editProjectruleForm" :model="ruleForm" :rules="rules" label-width="100px" class="demo-ruleForm">
          <!-- <el-form-item label="项目名称" prop="name">
            <el-input v-model="ruleForm.name" placeholder="请选择项目名称"></el-input>
          </el-form-item> -->
          <el-form-item label="项目名称" prop="name">
            <el-select v-model="ruleForm.name" placeholder="请选择项目" class="projectselect" filterable :disabled="isEdit">
              <el-option
                v-for="item in projectNameOpation"
                :key="item.id"
                :label="item.name"
                :value="item.name"
              />
            </el-select>
          </el-form-item>
          <el-form-item v-loading="arealoading" label="区域" prop="area">
            <el-select v-model="ruleForm.area" placeholder="请选择活动区域" class="projectselect" :disabled="role=='AREA_MANAGER'|| isEdit? true:false" filterable @change="selectArea">
              <el-option
                v-for="item in areaOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="负责人" prop="manager">
            <el-select v-model="ruleForm.manager" placeholder="请选择活动区域" class="projectselect" filterable>
              <el-option
                v-for="item in managerOptions"
                :key="item.id"
                :label="item.nickname"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="项目描述">
            <el-input v-model="ruleForm.desc" type="textarea" :rows="4" placeholder="请填写项目描述" maxlength="400" />
          </el-form-item>
        </el-form>
      </span>
      <span slot="footer" class="dialog-footer">
        <el-button :disabled="addprojectloading" @click="closeAddProject">取 消</el-button>
        <el-button type="primary" :loading="addprojectloading" @click="submit">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  props: {
    addProject: {
      type: Boolean,
      default: false
    },
    isEdit: {
      type: Boolean,
      default: false
    },
    rowDatas: {
      type: Object,
      default: () => {}
    },
    areasOptions: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      addProjectDialogVisible: false,
      addprojectloading: false,
      arealoading: false,
      ruleForm: {
        name: '',
        area: null,
        desc: '',
        manager: ''
      },
      isManager: true,
      areaOptions: [],
      managerOptions: [],
      projectNameOpation: [],
      createFlag: false,
      deepareaId: '',
      role: localStorage.getItem('role-code'),
      rules: {
        name: [
          { required: true, message: '请输入项目名称', trigger: 'blur' }
        ],
        area: [
          { required: true, message: '请选择一个区域', trigger: 'blur' }
        ],
        manager: [
          { required: true, message: '请选择负责人', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    addProject(val) {
      this.addProjectDialogVisible = val
    }
    // areasOptions(val){
    //   const role = localStorage.getItem('role')
    //   if(role=='AREA_MANAGER'){
    //     this.areaOptions = val
    //   }
    // }
    // rowDatas(val){
    //   console.log(val)
    // },
  },
  created() {
    const role = localStorage.getItem('role-code')
    if (role !== 'EMPLOYEE' && role !== 'GROUP_MANAGER') {
      this.getProjectName()
      this.getAreaList()
    }
  },
  methods: {
    closeAddProject() {
      this.addProjectDialogVisible = false
      this.managerOptions = [],
      this.$emit('update:addProject', false)
    },
    openAddPersonDialog() {
      if (this.isEdit) {
        this.ruleForm.name = this.rowDatas.name,
        this.ruleForm.area = this.rowDatas.areaId,
        this.ruleForm.desc = this.rowDatas.desc,
        this.ruleForm.manager = this.rowDatas.managerId
        const params = {
          areaId: this.ruleForm.area,
          listRoleCode: [
            'AREA_MANAGER',
            'GROUP_MANAGER'
          ]
        }
        this.$store.dispatch('person/getRoleUser', params).then(res => {
          this.managerOptions = res
        // this.isManager = false
        })
      } else if (this.isEdit === false) {
        this.ruleForm.name = '',
        this.ruleForm.desc = '',
        this.ruleForm.manager = ''
        // this.getAreaList()
        if (this.role == 'AREA_MANAGER') {
          this.ruleForm.area = this.deepareaId
          if (this.ruleForm.area) {
            this.getRoleUsers()
          }
        } else {
          this.ruleForm.area = null
        }
        this.$refs.editProjectruleForm.clearValidate()
      }
    },
    getProjectName() {
      this.$store.dispatch('system/getProjectName').then(res => {
        this.projectNameOpation = res
      }).catch(err => {})
    },
    getAreaList() {
      const params = {
        golbalName: '',
        pageNumber: 0,
        pageSize: 0
      }
      this.$store.dispatch('area/getAreaList', params).then(res => {
        // this.areaOptions = res.totalList;
        const role = localStorage.getItem('role-code')
        const areaid = localStorage.getItem('area-id')
        if (role == 'GROUP_MANAGER') {
          res.totalList.forEach(item => {
            if (item.id == areaid) {
              this.areaOptions.push(item)
              // this.ruleForm.area = item.areaId
            }
          })
        } else if (role == 'EMPLOYEE') {
          res.totalList.forEach(item => {
            if (item.id == areaid) {
              this.areaOptions.push(item)
            }
          })
        } else if (role == 'AREA_MANAGER') {
          res.totalList.forEach(item => {
            if (item.id == areaid) {
              this.areaOptions.push(item)
              this.deepareaId = item.id
            }
          })
        } else if (role == 'SUPER_ADMIN' || 'HR' || 'FINANCE' || 'MANAGEMENT') {
          this.areaOptions = res.totalList
        }
      })
    },
    selectArea(data) {
      if (data) {
        this.getRoleUsers()
      }
    },
    submit() {
      if (this.isEdit) {
        this.editProject()
      } else {
        this.submitProject()
      }
    },
    submitProject() {
      const params = {
        areaId: this.ruleForm.area,
        desc: this.ruleForm.desc,
        forceFlag: this.createFlag,
        managerId: this.ruleForm.manager,
        name: this.ruleForm.name
      }
      this.addprojectloading = true
      this.$store.dispatch('project/createProject', params).then(res => {
        if (res.askForceUpdate === false) {
          this.$message.success('添加成功')
          this.addProjectDialogVisible = false
          this.ruleForm.name = '',
          this.ruleForm.area = '',
          this.ruleForm.desc = '',
          this.ruleForm.manager = ''
          this.$emit('refresh')
          this.addprojectloading = false
        } else if (res.askForceUpdate === true) {
          this.addprojectloading = false
          this.$confirm(`您将和其他区合作此项目【${this.ruleForm.name}】, 是否继续?`, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.addprojectloading = true
            const params = {
              areaId: this.ruleForm.area,
              desc: this.ruleForm.desc,
              forceFlag: true,
              managerId: this.ruleForm.manager,
              name: this.ruleForm.name
            }
            this.$store.dispatch('project/createProject', params).then(() => {
              this.$message({
                type: 'success',
                message: '添加成功!'
              })
              this.addprojectloading = false
              this.ruleForm.name = '',
              this.ruleForm.area = '',
              this.ruleForm.desc = '',
              this.ruleForm.manager = ''
              this.$emit('refresh')
              this.addProjectDialogVisible = false
            }).catch(err => {
              this.addprojectloading = false
              this.$message.error(err)
            })
          }).catch(() => {
            this.addprojectloading = false
            this.$message({
              type: 'info',
              message: '已取消添加'
            })
          })
        }
      }).catch(err => {
        this.addprojectloading = false
      })
    },
    editProject() {
      this.addprojectloading = true
      const params = {
        id: this.rowDatas.id,
        desc: this.ruleForm.desc,
        forceFlag: this.createFlag,
        managerId: this.ruleForm.manager,
        name: this.ruleForm.name
      }
      this.addProjectDialogVisible = true
      this.$store.dispatch('project/updateProject', params).then(res => {
        if (res.askForceUpdate === false) {
          this.addprojectloading = false
          this.addProjectDialogVisible = false
          this.$message.success('编辑成功')
          this.ruleForm.name = '',
          this.ruleForm.area = '',
          this.ruleForm.desc = '',
          this.ruleForm.manager = ''
          this.$emit('refresh')
        } else if (res.askForceUpdate === true) {
          this.$confirm(`您将和其他区合作此项目【${this.ruleForm.name}】, 是否继续?`, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            const params = {
              desc: this.ruleForm.desc,
              forceFlag: true,
              id: this.rowDatas.id,
              managerId: this.ruleForm.manager,
              name: this.ruleForm.name
            }
            this.$store.dispatch('project/updateProject', params).then(() => {
              this.$message({
                type: 'success',
                message: '编辑成功!'
              })
              this.ruleForm.name = '',
              this.ruleForm.area = '',
              this.ruleForm.desc = '',
              this.ruleForm.manager = ''
              this.$emit('refresh')
              this.addprojectloading = false
              this.addProjectDialogVisible = false
            }).catch(err => {
              this.$message.error(err)
            })
          }).catch(() => {
            this.addprojectloading = false
            this.$message({
              type: 'info',
              message: '已取消更新'
            })
          })
        }
      }).catch(err => {
        this.addprojectloading = false
        this.addProjectDialogVisible = false
      })
    },
    getRoleUsers() {
      const params = {
        areaId: this.ruleForm.area,
        listRoleCode: [
          'AREA_MANAGER',
          'GROUP_MANAGER'
        ]
      }
      this.$store.dispatch('person/getRoleUser', params).then(res => {
        this.managerOptions = res
        this.isManager = false
      })
    }
  }
}
</script>

<style lang="scss">
.add-project-dialog .el-input__inner {
  width: 87%;
}
.add-project-dialog .el-textarea__inner {
  width: 87%;
}
.projectselect .el-input__inner {
  width: 100%;
}
.add-project-dialog .el-select {
  width: 87%;
}
@media only screen
and (min-device-width : 320px)
and (max-device-width : 500px){
  .add-project-dialog .el-dialog {
    width: 100% !important;
  }
}
</style>
