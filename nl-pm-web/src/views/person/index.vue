<template>
  <div class="person">
    <header-title>
      <template slot="title"> 人员管理 </template>
    </header-title>
    <div class="personSearch">
      <div style="margin-left: 55px" class="personSearchBox">
        <el-input
          v-model="search"
          style="width: 220px"
          placeholder="请输入姓名"
          clearable
          class="searchnickname"
          :disabled="otherFlags==true"
          @keyup.enter.native="searchPerson"
        />
        <el-select
          v-model="areaValue"
          placeholder="请选择区域"
          style="margin-left: 5px"
          class="personmargin personselect"
          clearable
          filterable
          :disabled="role == 'AREA_MANAGER' ? true : false"
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
          filterable
          class="personmargin personselect"
          :disabled="loading||otherFlags==true"
        >
          <el-option
            v-for="item in projectOptions"
            :key="item.id"
            :label="`${item.name}-${item.areaName}`"
            :value="item.id"
          />
        </el-select>
        <el-select
          v-model="roleValue"
          placeholder="请选择角色"
          style="margin-left: 5px"
          clearable
          filterable
          class="personmargin personselect"
          :disabled="loading||otherFlags==true"
        >
          <el-option
            v-for="item in roleOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
        <el-button
          style="margin-left: 5px"
          size="small"
          :disabled="loading"
          type="success"
          class="personmargin searchpersonBtn"
          @click="searchPerson"
        >搜索</el-button>
        <el-button
          style="margin-left: 5px"
          size="small"
          :disabled="loading"
          type="primary"
          class="personmargin searchpersonBtn"
          @click="searchOtherPerson"
        >查询所选区域关联外部人员</el-button>
      </div>
      <div
        v-if="role == 'SUPER_ADMIN' || role == 'HR' || role == 'AREA_MANAGER'"
        style="margin-right: 15px"
        class="searchpersonbtn"
      >
        <el-button v-if="readonly=='false'" type="text" :disabled="loading" @click="handleAddPerson">
          <span class="el-icon-circle-plus" style="color: #13ce66" />
          <span style="color: #606266">新建人员</span>
        </el-button>
      </div>
    </div>
    <el-table v-if="otherFlags==false" v-loading="loading" :data="personTableData" style="width: 100%">
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" class="demo-table-expand">
            <el-form-item label="邮箱">
              <span>{{ props.row.email }}</span>
            </el-form-item>
            <el-form-item label="手机号">
              <span v-if="props.row.phone !== null">{{ props.row.phone }}</span>
              <span v-else>--</span>
            </el-form-item>
            <el-form-item label="创建时间">
              <span v-if="props.row.createTime">{{
                props.row.createTime | formatDate
              }}</span>
              <span v-else>--</span>
            </el-form-item>
            <el-form-item label="更新时间">
              <span v-if="props.row.updateTime">{{
                props.row.updateTime | formatDate
              }}</span>
              <span v-else>--</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column type="index">
        <template slot-scope="scope">
          <span>{{ (pageNo - 1) * pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="区域名" prop="areaName" />
      <el-table-column label="用户名" prop="username" />
      <el-table-column label="姓名" prop="nickname" />
      <el-table-column label="职称" prop="roleName" />
      <el-table-column label="在职状态" prop="status">
        <template slot-scope="scope">
          <span v-if="scope.row.status === true" class="">
            <el-tag type="success">在职</el-tag>
          </span>
          <span v-else>
            <el-tag type="danger">离开</el-tag>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="发送邮件状态" prop="emailNotice">
        <template slot-scope="scope">
          <span v-if="scope.row.emailNotice === true" class="">
            <el-tag type="success">发送邮件</el-tag>
          </span>
          <span v-else>
            <el-tag type="danger">不发送邮件</el-tag>
          </span>
        </template>
      </el-table-column>
      <el-table-column
        v-if="role == 'SUPER_ADMIN' || role == 'HR' || (role == 'AREA_MANAGER' && readonly=='false')"
        label="操作"
        :width="role == 'HR' ? '338' : '390'"
      >
        <template slot-scope="scope">
          <el-button
            size="mini"
            title="编辑"
            type="warning"
            icon="el-icon-edit-outline"
            :disabled="
              scope.row.id === 1 ||
                (role == 'AREA_MANAGER' && scope.row.id == id)
                ? true
                : false
            "
            @click="handleEdit(scope.$index, scope.row)"
          />
          <el-button
            v-if="
              role == 'SUPER_ADMIN' ||
                role == 'MANAGEMENT' ||
                role == 'AREA_MANAGER'
            "
            size="mini"
            title="解除关系"
            icon="el-icon-close"
            type="info"
            :disabled="
              scope.row.id === 1 ||
                (role == 'AREA_MANAGER' && scope.row.id == id)
                ? true
                : false
            "
            @click="handleRemoveAssociated(scope.row)"
          />
          <el-button
            size="mini"
            title="详情"
            icon="el-icon-document"
            type="success"
            @click="handleDetail(scope.row)"
          />
          <el-button
            size="mini"
            title="删除"
            icon="el-icon-delete"
            type="danger"
            :disabled="
              scope.row.id === 1 ||
                (role == 'AREA_MANAGER' && scope.row.id == id)
                ? true
                : false
            "
            @click="handleDelete(scope.row)"
          />
          <el-button
            v-if="scope.row.status === true"
            size="mini"
            title="离开"
            icon="el-icon-user"
            style="background: #dc143c; color: #fff"
            :disabled="
              scope.row.id === 1 ||
                (role == 'AREA_MANAGER' && scope.row.id == id)
                ? true
                : false
            "
            @click="handleStatus(scope.row)"
          />
          <el-button
            v-else
            size="mini"
            title="在职"
            icon="el-icon-user-solid"
            type="success"
            :disabled="scope.row.id === 1 ? true : false"
            @click="handleStatus(scope.row)"
          />
          <el-button
            size="mini"
            title="初始化密码"
            icon="el-icon-key"
            style="background-color: beige;"
            @click="handleInitPassword(scope.row)"
          />
          <el-button
            v-if="scope.row.emailNotice === true"
            size="mini"
            title="不发送邮件"
            icon="el-icon-message-solid"
            type="primary"
            @click="handleEmailStatus(scope.row)"
          />
          <el-button
            v-else
            size="mini"
            title="发送邮件"
            icon="el-icon-message"
            type="success"
            @click="handleEmailStatus(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column v-else-if="role == 'MANAGEMENT'" label="操作" width="80">
        <template slot-scope="scope">
          <el-button
            size="mini"
            title="解除关系"
            icon="el-icon-close"
            type="info"
            :disabled="
              scope.row.id === 1 ||
                (role == 'AREA_MANAGER' && scope.row.id == id)
                ? true
                : false
            "
            @click="handleRemoveAssociated(scope.row)"
          />
        </template>
      </el-table-column>
    </el-table>
    <el-table v-if="otherFlags==true" v-loading="loading" :data="otherPersonData" style="width: 100%">
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" class="demo-table-expand">
            <el-form-item label="邮箱">
              <span>{{ props.row.email }}</span>
            </el-form-item>
            <el-form-item label="手机号">
              <span v-if="props.row.phone !== null">{{ props.row.phone }}</span>
              <span v-else>--</span>
            </el-form-item>
            <el-form-item label="创建时间">
              <span v-if="props.row.createTime">{{
                props.row.createTime | formatDate
              }}</span>
              <span v-else>--</span>
            </el-form-item>
            <el-form-item label="更新时间">
              <span v-if="props.row.updateTime">{{
                props.row.updateTime | formatDate
              }}</span>
              <span v-else>--</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column type="index">
        <template slot-scope="scope">
          <span>{{ (pageNo - 1) * pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="区域名" prop="areaName" />
      <el-table-column label="用户名" prop="username" />
      <el-table-column label="姓名" prop="nickname" />
      <el-table-column label="职称" prop="roleName" />
      <el-table-column label="在职状态" prop="status">
        <template slot-scope="scope">
          <span v-if="scope.row.status === true" class="">
            <el-tag type="success">在职</el-tag>
          </span>
          <span v-else>
            <el-tag type="danger">离开</el-tag>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="发送邮件状态" prop="emailNotice">
        <template slot-scope="scope">
          <span v-if="scope.row.emailNotice === true" class="">
            <el-tag type="success">发送邮件</el-tag>
          </span>
          <span v-else>
            <el-tag type="danger">不发送邮件</el-tag>
          </span>
        </template>
      </el-table-column>
      <el-table-column v-if="readonly=='false'" label="操作" width="80">
        <template slot-scope="scope">
          <el-button
            size="mini"
            title="解除关系"
            icon="el-icon-close"
            type="info"
            :disabled="
              scope.row.id === 1 ||
                (role == 'AREA_MANAGER' && scope.row.id == id)
                ? true
                : false
            "
            @click="handleRemoveAssociated(scope.row)"
          />
        </template>
      </el-table-column>
    </el-table>
    <div class="footer">
      <el-pagination
        :current-page="pageNo"
        :page-sizes="[10, 20, 30]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    <add-person
      :add-person-visable.sync="addPersonVisable"
      :is-edit="isEdit"
      :edit-row-data="editRowData"
      @refresh="getPersonList"
    />
    <detail :detail-visable.sync="detailVisable" :user-info="userInfoObject" />
    <associatedarea
      :associated-area-falg.sync="associatedAreaFalg"
      :associate-area-data="associateAreaData"
      :associated-object="associatedObject"
      :other-flags="otherFlags"
      @refresh="refreshList"
    />
  </div>
</template>

<script>
import headerTitle from '@/components/Title/index.vue'
import AddPerson from './components/addPersonDialog.vue'
import detail from './components/detailDialog.vue'
import associatedarea from './components/associatedarea.vue'
export default {
  components: {
    headerTitle,
    AddPerson,
    detail,
    associatedarea
  },
  data() {
    return {
      readonly: localStorage.getItem('read-only'),
      personTableData: [],
      loading: false,
      search: '',
      total: 0,
      pageSize: 10,
      pageNo: 1,
      addPersonVisable: false,
      isEdit: false,
      detailVisable: false,
      userInfoObject: {},
      roleOptions: [],
      roleValue: '',
      areaOptions: [],
      role: localStorage.getItem('role-code'),
      areaValue: null,
      projectOptions: [],
      projectValue: '',
      editRowData: {},
      id: localStorage.getItem('id'),
      associatedAreaFalg: false,
      associateAreaData: [],
      associatedObject: {},
      otherFlags: false,
      otherPersonData: []
    }
  },
  mounted() {
    this.getPersonList()
    this.getRoleList()
    this.getAreaList()
    this.getProjectList()
  },
  methods: {
    refreshList() {
      if (this.otherFlags == true) {
        this.getOtherAreaUser()
      } else {
        this.getPersonList()
      }
    },
    searchPerson() {
      this.pageNo = 1
      this.getPersonList()
      this.otherFlags = false
    },
    searchOtherPerson() {
      if (this.areaValue == null || this.areaValue == '') {
        this.$message.error('请先选择区域')
      } else {
        this.pageNo = 1
        this.getOtherAreaUser()
        this.otherFlags = true
      }
    },
    handleAddPerson() {
      this.addPersonVisable = true
      this.isEdit = false
    },
    handleDetail(row) {
      this.detailVisable = true
      this.$store.dispatch('person/findUserInfo', row.id).then((res) => {
        this.userInfoObject = res
      })
    },
    handleDelete(row) {
      this.$confirm(`此操作将删除该人员,是否继续`, '删除', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'error',
        showClose: true,
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true
            instance.showClose = false
            instance.showCancelButton = false
            instance.closeOnClickModal = false
            this.$store
              .dispatch('person/deleteUser', row.id)
              .then(() => {
                done()
                instance.showClose = true
                instance.showCancelButton = true
                instance.closeOnClickModal = true
                instance.confirmButtonLoading = false
                this.$message.success('删除成功')
                this.getPersonList()
              })
              .catch((err) => {
                done()
                instance.confirmButtonLoading = false
              })
          } else {
            done()
          }
        }
      }).catch(() => {})
    },
    getOtherAreaUser() {
      const params = {
        areaId: this.areaValue,
        currentPage: this.pageNo,
        nickname: null,
        pageSize: this.pageSize,
        projectId: 0,
        roleId: 0,
        searchVal: null
      }
      this.$store.dispatch('person/getOtherAreaUser', params).then((res) => {
        this.otherPersonData = res.totalList
      }).catch(err => {})
    },
    getPersonList() {
      const params = {
        areaId: this.areaValue,
        pageSize: this.pageSize,
        currentPage: this.pageNo,
        searchVal: null,
        nickname: this.search,
        projectId: this.projectValue,
        roleId: this.roleValue
      }
      const that = this
      that.loading = true
      that.$store
        .dispatch('person/getpseronList', params)
        .then((res) => {
          that.personTableData = res.totalList
          that.total = res.total
          that.loading = false
        })
        .catch((err) => {
          that.loading = false
        })
    },
    getRoleList() {
      this.$store.dispatch('person/findRole').then((res) => {
        this.roleOptions = res
      })
    },
    getAreaList() {
      const params = {
        golbalName: '',
        pageNumber: 0,
        pageSize: 0
      }
      this.$store.dispatch('area/getAreaList', params).then((res) => {
        const role = localStorage.getItem('role-code')
        const areaid = localStorage.getItem('area-id')
        if (role == 'AREA_MANAGER') {
          res.totalList.forEach((item) => {
            if (item.id == areaid) {
              this.areaOptions.push(item)
              this.areaValue = item.id
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
        pageSize: 100000,
        searchVal: '',
        userId: null
      }
      this.$store.dispatch('project/getProjectList', params).then((res) => {
        this.projectOptions = res.totalList
      })
    },
    handleEdit(index, row) {
      this.addPersonVisable = true
      this.isEdit = true
      this.editRowData = row
    },
    handleRemoveAssociated(row) {
      this.associatedObject = row
      this.associatedAreaFalg = true
      this.$store
        .dispatch('person/getAssociatedArea', row.id)
        .then((res) => {
          this.associateAreaData = res
        })
        .catch((err) => {})
    },
    handleStatus(row) {
      this.$confirm(`此操作将修改该人员状态,是否继续`, '修改', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'error',
        showClose: true,
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true
            instance.showClose = false
            instance.showCancelButton = false
            instance.closeOnClickModal = false
            this.$store
              .dispatch('person/editStatus', row.id)
              .then(() => {
                done()
                instance.showClose = true
                instance.showCancelButton = true
                instance.closeOnClickModal = true
                instance.confirmButtonLoading = false
                this.$message.success('修改成功')
                this.getPersonList()
              })
              .catch((err) => {
                done()
                instance.confirmButtonLoading = false
              })
          } else {
            done()
          }
        }
      }).catch(() => {})
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getPersonList()
    },
    handleCurrentChange(val) {
      this.pageNo = val
      this.getPersonList()
    },
    handleInitPassword(row) {
      this.$confirm('此操作将给该账号初始化密码, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.$store
            .dispatch('person/initPasswprd', { userId: row.id })
            .then((res) => {
              this.$message.success('初始化密码成功')
            })
            .catch((err) => {
              this.$message.error(error)
            })
        })
        .catch(() => {})
    },
    handleEmailStatus(row) {
      this.$store
        .dispatch('person/editEmail', row.id)
        .then((res) => {
          this.$message.success('修改成功')
          this.getPersonList()
        })
        .catch((err) => {
          this.$message.error(err)
        })
    }
  }
}
</script>
<style lang="scss">
.personSearch {
  width: 100%;
  height: 50px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}
.person {
  padding: 20px;
}
.footer {
  float: right;
  margin-top: 20px;
}
.person .el-input__inner {
  height: 32px;
}
.person .el-input .el-input__clear {
  margin-top: -4px;
}
.person .el-tag {
  border-radius: 20px;
}
.person .el-select__caret {
  display: flex;
  justify-content: center;
  align-items: center;
}
@media only screen and (max-width:1500px) {
  .personSearch {
    width: 100%;
    height: auto !important;
    // display: block !important;
  }
  .personSearchBox {
    margin-left: 0 !important;
  }
  // .personmargin {
  //   margin-left: 0px !important;
  //   width: 100%;
  //   margin-top: 5px;
  // }
  // .searchnickname {
  //   width: 100% !important;
  // }
  // .searchpersonbtn {
  //   text-align: right;
  //   margin-right: 0px !important;
  // }
  // .searchpersonBtn {
  //   background-color: #409eff;
  //   color: #fff;
  // }
}
@media only screen and (max-width:1370px) {
  .personSearch {
    width: 100%;
    height: auto !important;
  }
  .personSearchBox {
    margin-left: 0 !important;
  }
  .searchnickname {
    width: 170px !important;
  }
  .personselect{
    width: 186px !important;
  }
}
@media only screen and (min-device-width: 320px) and (max-device-width: 500px) {
  .personSearch {
    width: 100%;
    height: auto !important;
    display: block !important;
  }
  .personSearchBox {
    margin-left: 0 !important;
  }
  .personmargin {
    margin-left: 0px !important;
    width: 100%;
    margin-top: 5px;
  }
  .searchnickname {
    width: 100% !important;
  }
  .searchpersonbtn {
    text-align: right;
    margin-right: 0px !important;
  }
  .searchpersonBtn {
    background-color: #409eff;
    color: #fff;
  }
}
</style>
