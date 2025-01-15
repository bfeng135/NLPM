<template>
  <div class="user-activity">
    <el-form :model="user" ref='userActive'>
    <el-form-item label="姓名" prop="name" :rules="{ required: true, message: '请输入姓名', trigger: 'blur' }">
      <el-input v-model.trim="user.name" placeholder="请输入姓名"/>
    </el-form-item>
    <el-form-item label="邮箱" prop="email" :rules="[
      { required: true, message: '请输入邮箱地址', trigger: 'blur' },
      { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur'] }
    ]">
      <el-input v-model.trim="user.email" placeholder="请输入邮箱"/>
    </el-form-item>
    <el-form-item label="手机号">
      <el-input v-model.trim="user.phone" placeholder="请输入手机号"/>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="submit" :loading="updateUserLoading">更新</el-button>
    </el-form-item>
  </el-form>
  </div>
</template>

<script>

export default {
  props:{
    userInfo:{
      type:Object,
      default:()=>{}
    }
  },
  data() {
    return {
      user:{
        name:'',
        email:'',
        phone:''
      },
      flag:'',
      updateUserLoading:false
    }
  },
  watch:{
    userInfo(val){
      if(val){
        this.user.name=this.userInfo.nickname
        this.user.email=this.userInfo.email
        this.user.phone=this.userInfo.phone
      }
    }
  },
  methods:{
    submit(){
      this.$refs.userActive.validate(valid=>{
        if(valid){
          this.updateUserLoading = true
         const id = localStorage.getItem('id')
        const params = {
        areaId: 0,
        email: this.user.email,
        emailNotice: false,
        nickname: this.user.name,
        phone: this.user.phone,
        roleId: 0,
        status: false,
        userId: Number(id)
      }
      this.$store.dispatch('person/editUser',params).then(res=>{
        this.$emit('refresh')
        this.updateUserLoading = false
        this.$store.dispatch("user/updateName", this.user.name)
        this.$message.success('更新成功')
      })
        }
      })
      
    }
  }
}
</script>

<style lang="scss" scoped>
.user-activity {
  .user-block {

    .username,
    .description {
      display: block;
      margin-left: 50px;
      padding: 2px 0;
    }

    .username{
      font-size: 16px;
      color: #000;
    }

    :after {
      clear: both;
    }

    .img-circle {
      border-radius: 50%;
      width: 40px;
      height: 40px;
      float: left;
    }

    span {
      font-weight: 500;
      font-size: 12px;
    }
  }

  .post {
    font-size: 14px;
    border-bottom: 1px solid #d2d6de;
    margin-bottom: 15px;
    padding-bottom: 15px;
    color: #666;

    .image {
      width: 100%;
      height: 100%;

    }

    .user-images {
      padding-top: 20px;
    }
  }

  .list-inline {
    padding-left: 0;
    margin-left: -5px;
    list-style: none;

    li {
      display: inline-block;
      padding-right: 5px;
      padding-left: 5px;
      font-size: 13px;
    }

    .link-black {

      &:hover,
      &:focus {
        color: #999;
      }
    }
  }

}

.box-center {
  margin: 0 auto;
  display: table;
}

.text-muted {
  color: #777;
}
</style>
