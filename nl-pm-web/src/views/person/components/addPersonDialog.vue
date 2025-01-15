<template>
  <div class="add-person-dialog">
    <el-dialog
      :title="isEdit ? '编辑人员' : '新建人员'"
      :visible.sync="personDialogVisible"
      @close="$emit('update:addPersonVisable', false)"
      width="40%"
      @opened="openAddPerson"
      @closed="closePerson"
      :show-close="!addpersonloading"
    >
      <span>
        <el-form
          :model="ruleForm"
          :rules="rules"
          ref="addUserRuleForm"
          label-width="100px"
          class="demo-ruleForm">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="ruleForm.username" :disabled="isEdit" placeholder="请输入用户名" maxlength="30"></el-input>
          </el-form-item>
          <el-form-item label="姓名" prop="nickname">
            <el-input v-model="ruleForm.nickname" placeholder="请输入姓名" maxlength="30"></el-input>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="ruleForm.phone" placeholder="请输入手机号"></el-input>
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="ruleForm.email" placeholder="请输入邮箱"></el-input>
          </el-form-item>
          <el-form-item label="区域" prop="areaId">
            <el-select v-model="ruleForm.areaId" placeholder="请选择区域" class="select" :disabled="role=='AREA_MANAGER'? true:false" filterable>
              <el-option
                v-for="item in areaOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="角色" prop="roleId">
            <el-select v-model="ruleForm.roleId" placeholder="请选择角色" class="select" filterable>
              <el-option
                v-for="item in roleOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="personDialogVisible = false" :disabled="addpersonloading">取 消</el-button>
        <el-button type="primary" @click="submitAddUser" :loading="addpersonloading">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  props: {
    addPersonVisable: {
      type: Boolean,
      default: false,
    },
    isEdit: {
      type: Boolean,
      default: false,
    },
    editRowData:{
      type:Object,
      default:()=>{}
    }
  },
  data() {
    return {
      personDialogVisible: false,
      addpersonloading:false,
      ruleForm: {
        username: "",
        nickname: "",
        phone: "",
        email: "",
        areaId: "",
        roleId: "",
      },
      deepArea:'',
      areaOptions: [],
      roleOptions: [],
      role:localStorage.getItem('role-code'),
      rules: {
        username: [
          { required: true, message: "请输入用户名", trigger: "blur" },
        ],
        nickname: [{ required: true, message: "请输入姓名", trigger: "blur" }],
        // phone: [{ required: true, message: "请填写手机号", trigger: "blur" }],
        email: [
          { required: true, message: "请输入邮箱地址", trigger: "blur" },
          { type: "email", message: "请输入正确的邮箱地址", trigger: "blur" },
        ],
        areaId: [
          { required: true, message: "请至少选择一个区域", trigger: "blur" },
        ],
        roleId: [
          { required: true, message: "请选择角色", trigger: "blur" },
        ],
      },
    };
  },
  watch: {
    addPersonVisable(val) {
      this.personDialogVisible = val;
    },
  },
  created() {
    this.getRoleList();
    this.getAreaList();
  },
  methods: {
    closePerson(){
      this.$refs.addUserRuleForm.clearValidate()
    },
    openAddPerson(){
      if(this.isEdit){
        this.ruleForm.username = this.editRowData.username,
        this.ruleForm.nickname = this.editRowData.nickname,
        this.ruleForm.phone = this.editRowData.phone
        this.ruleForm.email = this.editRowData.email
        this.ruleForm.areaId = this.editRowData.areaId
        this.ruleForm.roleId = this.editRowData.roleId
      }else if (this.isEdit === false) {
        this.ruleForm.username = '',
        this.ruleForm.nickname = '',
        this.ruleForm.phone = ''
        this.ruleForm.email = ''
        this.ruleForm.roleId = ''
        if(this.role=='AREA_MANAGER'){
          this.ruleForm.areaId = this.deepArea
        }else{
          this.ruleForm.areaId= ''
        }
        this.$refs.addUserRuleForm.clearValidate();
      } 
    },
    getRoleList() {
      this.$store.dispatch("person/findRole").then((res) => {
        this.roleOptions = res;
      });
    },
    getAreaList() {
      const params = {
        golbalName: "",
        pageNumber: 0,
        pageSize: 0,
      };
      this.$store.dispatch("area/getAreaList", params).then((res) => {
        // this.areaOptions = res.totalList;
        const role = localStorage.getItem('role-code')
        const areaid = localStorage.getItem('area-id')
        if(role=='AREA_MANAGER'){
          res.totalList.forEach(item=>{
            if(item.id == areaid){
              this.areaOptions.push(item)
              this.deepArea = item.id
            }
          })
        }else{
          this.areaOptions = res.totalList
        }
      });
    },
    submitAddUser() {
      this.$refs.addUserRuleForm.validate((valid) => {
        if (valid) {
          this.addpersonloading = true
          const params = {
              areaId: this.ruleForm.areaId,
              email: this.ruleForm.email,
              nickname: this.ruleForm.nickname,
              phone: this.ruleForm.phone,
              roleId: this.ruleForm.roleId,
              userId: this.editRowData.id
          }
          this.$store
            .dispatch(this.isEdit? 'person/editUser':'person/addUser', this.isEdit? params:this.ruleForm)
            .then((res) => {
              this.addpersonloading = false
              if(this.isEdit){
                this.$message.success('编辑成功')
              }else{
                this.$message.success("添加成功");
              }
              this.personDialogVisible = false;
              this.$emit("refresh");
            })
            .catch((err) => {
              this.addpersonloading = false
              this.$message.error(err);
            });
        }
      });
    },
  },
};
</script>

<style>
.add-person-dialog .el-select {
  width: 88%;
}
.add-person-dialog .el-input__inner{
  width: 88%;
}
.select .el-input__inner{
  width: 100%;
}
@media only screen
and (min-device-width : 320px)
and (max-device-width : 500px){
  .add-person-dialog .el-dialog {
    width: 100% !important; 
  }
}
</style>