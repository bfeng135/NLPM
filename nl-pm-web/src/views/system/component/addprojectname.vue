<template>
  <div class="addprojectname">
    <el-dialog
      :title="isEdit ? '编辑项目名称' : '新建项目名称'"
      :visible.sync="projectNamedialogVisible"
      width="35%"
      @close="$emit('update:projectNameVisable', false)"
      @open="openAddProjectName"
      :show-close="!addProjectName">
      <span>
        <el-form
          :model="ruleForm"
          :rules="rules"
          ref="ruleForm"
          label-width="100px"
          class="demo-ruleForm"
        >
          <el-form-item label="项目名称" prop="name">
            <el-input
              v-model="ruleForm.name"
              placeholder="请输入项目名称"
               maxlength="50"
               style="width:83%"
            ></el-input>
          </el-form-item>
        </el-form>
      </span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="projectNamedialogVisible = false" :disabled="addProjectName">取 消</el-button>
        <el-button type="primary" @click="submitProjectName" :loading="addProjectName">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  props: {
    projectNameVisable: {
      type: Boolean,
      default: false,
    },
    isEdit: {
      type: Boolean,
      default: false,
    },
    projectNameRowData: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      projectNamedialogVisible: false,
      addProjectName:false,
      ruleForm: {
        name: "",
        areaValue:""
      },
      rules: {
        name: [{ required: true, message: "请输入项目名称", trigger: "blur" }],
      },
      areaOptions:[]
    };
  },
  watch: {
    projectNameVisable(val) {
      this.projectNamedialogVisible = val;
    },
  },
  created(){
    this.getAreaList()
  },
  methods: {
      getAreaList() {
      const params = {
        golbalName: "",
        pageNumber: 0,
        pageSize: 0,
      };
      
      this.$store.dispatch("area/getAreaList", params).then((res) => {
        this.areaOptions = res.totalList;
      }).catch(err=>{});
    },
    openAddProjectName() {
      if (this.isEdit === true) {
        this.ruleForm.name = this.projectNameRowData.name;
        this.ruleForm.areaValue = this.projectNameRowData.areaId
      } else if (this.isEdit === false) {
        this.ruleForm.name = "";
        this.ruleForm.areaValue = "";
        this.$refs.ruleForm.clearValidate();
      }
    },
    submitProjectName() {
      const params = {
        name: this.ruleForm.name,
        id:this.projectNameRowData.id,
        areaId:null
      };
      this.$refs.ruleForm.validate((vaild) => {
        if (vaild) {
          this.addProjectName = true
          this.$store
            .dispatch(this.isEdit==true?'system/putProjectName':'system/addProjectName', params)
            .then(() => {
              this.addProjectName = false
              if(this.isEdit==true){
                this.$message.success('编辑成功')
              }else{
                this.$message.success("添加成功");
              }
              this.projectNamedialogVisible = false;
              this.$emit("refresh");
            })
            .catch((err) => {
              this.addProjectName = false
            });
        }
      });
    },
  },
};
</script>

<style>
.addprojectname .el-dialog__header {
  border-bottom: 1px solid #e5e9eb;
}
.addprojectname .el-input__inner {
  width: 100%;
}
@media only screen
and (min-device-width : 320px)
and (max-device-width : 500px){
  .addprojectname .el-dialog {
    width: 100% !important; 
  }
}
</style>