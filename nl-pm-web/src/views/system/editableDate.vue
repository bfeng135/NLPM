<template>
  <div class="system">
    <header-title>
      <template slot="title"> 限制编辑日期 </template>
    </header-title>
    <el-card class="box-card">
      <div class="box-main">
        <el-form
          ref="systemRuleForm"
          :model="ruleForm"
          :rules="rules"
          label-width="120px"
          class="demo-ruleForm"
        >
          <el-form-item label="最早可编辑日期" prop="deadline">
            <el-date-picker
              v-model="ruleForm.deadline"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="请选择最早可编辑日期"
            />
          </el-form-item>
          <el-form-item style="float:right" class="systemupdate">
            <el-button type="primary" :loading="updateLoading" class="systemupdatebtn" @click="update">更新</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script>
import headerTitle from '@/components/Title/index.vue'
export default {
  components: {
    headerTitle
  },
  data() {
    return {
      ruleForm: {
        deadline: ''
      },
      updateLoading: false,
      rules: {
        name: [{ required: false, message: '请选择可编辑日期', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getEditableDate()
  },
  methods: {
    getEditableDate() {
      this.$store.dispatch('system/getEditableDate').then(res => {
        this.ruleForm.deadline = res.deadline
      })
    },
    update() {
      this.updateLoading = true
      this.$refs.systemRuleForm.validate((valid) => {
        if (valid) {
          const params = {
            deadline: this.ruleForm.deadline
          }
          this.$store
            .dispatch('system/setEditableDate', params)
            .then((res) => {
              this.$message.success('更新成功')
              this.updateLoading = false
              this.getEditableDate()
            })
            .catch((err) => {
              this.updateLoading = false
              this.$message.error(err)
            })
        }
      })
    }
  }
}
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
.system .el-button {
  margin-right: 92px;
}
.system .el-textarea__inner {
  width: 87%;
  height: 200px;
}
.system .el-input__icon {
  margin-right: 100px;
}
.system .el-input__prefix {
  width: 0;
}
.el-date-editor.el-input, .el-date-editor.el-input__inner {
  width: 100%;
}
@media only screen and (min-device-width: 320px) and (max-device-width: 500px) {
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
  .systemupdate {
    float: none !important;
  }
  .systemupdatebtn {
    width: 100%;
  }
}
</style>
