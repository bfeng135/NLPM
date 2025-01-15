<template>
  <div class="add-email">
    <el-dialog
    :title="isEdit? '编辑邮箱':'新建邮箱'"
    :visible.sync="addEmailDialogVisible"
    width="40%"
    @close="$emit('update:addemailvisable', false)"
    @open="openAddEmail"
    :show-close="!addEmailLoading">
    <span>
      <el-form :model="ruleForm" :rules="rules" ref="addEmailRuleForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="邮箱地址" prop="username">
            <el-input v-model="ruleForm.username" style="width:88%" placeholder="请输入邮箱地址"></el-input>
        </el-form-item>
        <el-form-item label="主机名称" prop="host">
            <el-input v-model="ruleForm.host" style="width:88%" placeholder="请输入主机名称"></el-input>
        </el-form-item>
        <el-form-item label="邮箱密码" prop="password" >
            <el-input v-model="ruleForm.password" style="width:88%" placeholder="请输入邮箱密码" show-password></el-input>
        </el-form-item>
      </el-form>
    </span>
    <span slot="footer" class="dialog-footer">
        <el-button @click="addEmailDialogVisible = false" :disabled="addEmailLoading">取 消</el-button>
        <el-button type="primary" @click="submit" :loading="addEmailLoading">确 定</el-button>
    </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  props:{
    addemailvisable:{
      type:Boolean,
      default:false
    },
    isEdit:{
      type:Boolean,
      default:false
    },
    editRowData:{
      type:Object,
      default:()=>{}
    }
  },
  data(){
    return{
      addEmailDialogVisible:false,
      addEmailLoading:false,
      ruleForm: {
        username: '',
        host:'',
        password:'',
      },
      rules: {
        username: [
            {required: true, message: '请输入邮箱地址', trigger: 'blur' },
        ],
        host: [
            {required: true, message: '请输入主机名称', trigger: 'blur' },
        ],
        password: [
            {required: true, message: '请输入密码', trigger: 'blur' },
        ],
      }
    }
  },
  watch:{
    addemailvisable(val){
      this.addEmailDialogVisible = val
    }
  },
  methods:{
    openAddEmail(){
      if(this.isEdit){
        this.ruleForm.username = this.editRowData.username,
        this.ruleForm.password = this.editRowData.password,
        this.ruleForm.host = this.editRowData.host
      }else{
        this.ruleForm.username = '',
        this.ruleForm.password = '',
        this.ruleForm.host = '',
        this.$refs.addEmailRuleForm.clearValidate()
      }
    },
    submit(){
      const params = [
        {
            host: this.ruleForm.host,
            password: this.ruleForm.password,
            username: this.ruleForm.username
        }
      ]
      const editparams = {
        host: this.ruleForm.host,
        id:this.editRowData.id,
        password: this.ruleForm.password,
        username: this.ruleForm.username
      }
      this.$refs.addEmailRuleForm.validate(valid=>{
        if(valid){
          this.addEmailLoading = true
         this.$store.dispatch(this.isEdit? 'system/UpdateEmails':'system/AddEmails',this.isEdit? editparams:params).then(res=>{
        if(this.isEdit){
          this.$message.success('编辑成功')
        }else{
          this.$message.success('添加成功')
        }
        this.addEmailLoading = false
        this.$emit('refresh')
        this.addEmailDialogVisible = false
     }).catch(err=>{
         this.addEmailLoading = false
         this.addEmailDialogVisible = false
     })
        }
      })
     
    }
  }
}
</script>

<style>
.add-email .el-dialog__header {
  border-bottom: 1px solid #e5e9eb;
}
@media only screen
and (min-device-width : 320px)
and (max-device-width : 500px){
  .add-email .el-dialog {
    width: 100% !important; 
  }
}
</style>