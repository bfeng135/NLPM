<template>
  <el-form :rules="editpasswordrules" ref="editPwdRuleForm" :model="user">
    <el-form-item label="旧密码" prop="oldPassword">
      <el-input v-model.trim="user.oldPassword" show-password placeholder="请输入旧密码"/>
    </el-form-item>
    <el-form-item label="新密码" prop="newPassword">
      <el-input v-model.trim="user.newPassword" show-password placeholder="请输入新密码"/>
    </el-form-item>
    <el-form-item label="再次输入新密码" prop="reNewPassword">
      <el-input v-model.trim="user.reNewPassword" show-password placeholder="请再次输入新密码"/>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="submit" :loading="updatepasswordLoading">更新</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
export default {
  data(){
    return{
      user:{
        oldPassword:'',
        newPassword:'',
        reNewPassword:''
      },
      updatepasswordLoading:false,
      editpasswordrules:{
        oldPassword:[
          { required: true, message: '请输入旧密码', trigger: 'blur' }
        ],
        newPassword:[
          { required: true, message: '请输入新密码', trigger: 'blur' }
        ],
        reNewPassword:[
          { required: true, message: '请再次输入新密码', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    submit() {
      this.$refs.editPwdRuleForm.validate(vaild=>{
        if(vaild){
        this.updatepasswordLoading = true
        const id = localStorage.getItem('id')
        const params = {
          newPassword: this.user.newPassword,
          oldPassword: this.user.oldPassword,
          reNewPassword: this.user.reNewPassword,
          userId: Number(id)
        }
        this.$store.dispatch('person/updatePassword',params).then(res=>{
        this.$message.success('更改成功')
        this.updatepasswordLoading = false
        setTimeout(()=>{
           this.$store.dispatch('user/resetToken').then(()=>{
          // location.reload();
          this.$router.push(`/login`)
          })
        },1000)
        
        
      }).catch(err=>{
        this.$message.error(err)
      })
        }else{
          return false
        }
      })
      
      
    }
  }
}
</script>
