<template>
  <div class="system">
    <header-title>
      <template slot="title"> 系统名称管理 </template>
    </header-title>
    <el-card class="box-card">
      <div class="box-main">
        <el-form
          :model="ruleForm"
          :rules="rules"
          ref="systemRuleForm"
          label-width="100px"
          class="demo-ruleForm">
          <el-form-item label="系统名称" prop="name" class="systemName">
            <el-input v-model="ruleForm.name" placeholder="请输入系统名称"></el-input>
          </el-form-item>
          <el-form-item label="系统描述" class="desc">
            <el-input
              type="textarea"
              :rows="2"
              placeholder="请输入内容"
              v-model="textarea"
            >
            </el-input>
          </el-form-item>
          <el-form-item style="float:right" class="systemupdate"> 
            <el-button type="primary" @click="handleAddEmail" :loading="updateEmail" class="systemupdatebtn">更新</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script>
import headerTitle from "@/components/Title/index.vue";
export default {
  components: {
    headerTitle,
  },
  data() {
    return {
      ruleForm: {
        name: "",
      },
      emailId:0,
      updateEmail:false,
      textarea:'',
      rules: {
        name: [
          { required: true, message: "请输入活动名称", trigger: "blur" },
        ],
      },
    };
  },
  created(){
    this.getEmail()
  },
  methods:{
    getEmail(){
      this.$store.dispatch('system/getEmail').then(res=>{
        this.ruleForm.name = res.name
        this.textarea = res.desc
        this.emailId = res.id
      })
    },
    handleAddEmail(){
      this.updateEmail = true
      this.$refs.systemRuleForm.validate(valid=>{
        if(valid){
          const params = {
            desc:this.textarea,
            name:this.ruleForm.name,
            id:this.emailId,
          }
          this.$store.dispatch('system/editEmail',params).then(res=>{
            this.$message.success('更新成功')
            this.updateEmail = false
            this.$store.dispatch('system/updateSystemName',this.ruleForm.name)
          }).catch(err=>{
            this.updateEmail = false
            this.$message.error(err)
          })
        }
      })
    }
  }
};
</script>

<style lang="scss">
.system {
  padding: 20px;
  .box-card {
    width: 100%;
    height: 750px;
    margin-top: 20px;
    .box-main {
      width: 50%;
      height: 700px;
      margin: 0 auto;
    }
  }
}
.system .el-input__inner {
  width: 87%;
}
.system .el-button{
  margin-right: 92px;
}
.system .el-textarea__inner {
  width: 87%;
  height: 200px;
}
.system .el-input__icon {
  margin-right: 100px;
}
@media only screen
and (min-device-width : 320px)
and (max-device-width : 500px){
  .box-main {
      width: 100% !important;
      height: 700px;
      margin: 0 auto;
  }
  .desc .el-form-item__label {
    width: 70px !important;
  }
  .systemName .el-form-item__label {
    width: 78px !important;
  }
  .system .el-input__inner {
    width: 100% !important;
  }
  .system .el-form-item__content {
    margin-left: 0 !important;
  }
  .system .el-textarea__inner {
    width: 100% !important;
  }
  .systemupdate{
    float: none !important;
  }
  .systemupdatebtn{
    width: 100%;
  }
}
</style>