<template>
  <div class="add-area-dialog">
    <el-dialog
      :title="isEdit === true ? '编辑区域' : '新建区域'"
      :visible.sync="addAreaDialogVisible"
      width="35%"
      @close="$emit('update:addAreaVisable', false)"
      @opened="openAddAreaDialog"
      :show-close="!addArea"
    >
      <span>
        <el-form
          :model="ruleForm"
          :rules="rules"
          ref="addArea"
          label-width="100px"
          class="demo-ruleForm"
        >
          <el-form-item label="区域名称" prop="name">
            <el-input
              v-model="ruleForm.name"
              placeholder="请输入区域名称"
              maxlength="30"
            ></el-input>
          </el-form-item>
          <el-form-item label="区域负责人">
            <el-select
              v-model="ruleForm.region"
              placeholder="请选择区域负责人"
              style="width: 85%"
              class="areaName"
              clearable
            >
            <el-option v-for="item in areaPerson" :key="item.id"
      :label="item.nickname"
      :value="item.id"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="区域描述">
            <el-input
              type="textarea"
              :rows="4"
              placeholder="请输入描述信息"
              v-model="ruleForm.desc"
              maxlength="400"
            >
            </el-input>
          </el-form-item>
        </el-form>
      </span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addAreaDialogVisible = false" :disabled="addArea">取 消</el-button>
        <el-button type="primary" @click="submieArea" :loading="addArea"
          >确 定</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  props: {
    addAreaVisable: {
      type: Boolean,
      default: false,
    },
    isEdit: {
      type: Boolean,
      default: false,
    },
    AreaRowData:{
      type:Object,
      default:()=>{}
    }
  },
  data() {
    return {
      addAreaDialogVisible: false,
      addArea:false,
      ruleForm: {
        name: "",
        desc: "",
        region: null
      },
      rules: {
        name: [{ required: true, message: "请输入活动名称", trigger: "blur" }],
        region: [{ required: true, message: "请选择区域负责人", trigger: "blur" }],
      },
      areaPerson:[]
    };
  },
  created(){
    this.getRoleUsers()
  },
  methods:{
    openAddAreaDialog(){
      if(this.isEdit){
        this.ruleForm.name = this.AreaRowData.name,
        this.ruleForm.desc = this.AreaRowData.desc,
        this.ruleForm.region = this.AreaRowData.managerId
      }else if (this.isEdit === false) {
        this.ruleForm.name = '',
        this.ruleForm.desc = '',
        this.ruleForm.region = null
        this.$refs.addArea.clearValidate();
      }  
    },
    getRoleUsers(){
      const params = {
        areaId: 0,
        listRoleCode: [
          "AREA_MANAGER"
        ]
      }
      this.$store.dispatch('person/getRoleUser',params).then(res=>{
        this.areaPerson = res
      })
    },
    submieArea(){
      const parmas = {
        desc:this.ruleForm.desc,
        managerId:this.ruleForm.region,
        name:this.ruleForm.name
      }
      const editParams = {
        desc:this.ruleForm.desc,
        id: this.AreaRowData.id,
        managerId:this.ruleForm.region==''?null:this.ruleForm.region,
        name:this.ruleForm.name
      }
      
      this.$refs.addArea.validate(vaild=>{
        if(vaild){
          this.addArea = true
          this.$store.dispatch(this.isEdit?'area/updateArea':'area/addArea',this.isEdit?editParams:parmas).then(()=>{
            this.ruleForm.name = '',
            this.ruleForm.desc = '',
            this.ruleForm.region = ''
            this.addAreaDialogVisible = false
            this.addArea = false
            if(this.isEdit){
              this.$message.success('编辑成功')
            }else{
              this.$message.success('添加成功')
            }
            this.$emit('refresh')
          }).catch(err=>{
            this.addArea = false
          })
        }
      })
      
    }
  },
  watch: {
    addAreaVisable(val) {
      this.addAreaDialogVisible = val;
    },
  },
};
</script>

<style>
.add-area-dialog .el-textarea__inner {
  width: 85%;
}
.add-area-dialog .el-input__inner {
  width: 85%;
}
.areaName .el-input__inner {
  width: 100%;
}
@media only screen
and (min-device-width : 320px)
and (max-device-width : 500px){
  .add-area-dialog .el-dialog {
    width: 100% !important; 
  }
}
</style>