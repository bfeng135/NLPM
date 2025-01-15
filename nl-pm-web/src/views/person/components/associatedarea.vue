<template>
  <el-dialog
  class="associated"
  title="解除区域项目关系"
  :visible.sync="associatedAreaDialogVisible"
  width="30%"
  @close="$emit('update:associatedAreaFalg', false)"
  @open="openAssociated">
  <span style="padding-bottom:10px" v-if="associateAreaData.length>0">
      <span class="el-icon-warning warningicon">
      </span>此操作将解除所选区域下的项目关系,是否继续？</span>
  <span v-else>
      <span class="el-icon-warning warningicon"></span>
      该人员没有关联项目</span>
  <el-form ref="ruleForm"  class="demo-ruleForm" style="margin-top:10px">
  <el-form-item label="区域名称" prop="name">
    <el-select v-model="value" placeholder="请选择区域列表" clearable :disabled="role=='AREA_MANAGER'? true:false">
    <el-option
      v-for="item in associateAreaData"
      :key="item.id"
      :label="item.name"
      :value="item.id">
    </el-option>
  </el-select>
  </el-form-item>
  </el-form>
  <span slot="footer" class="dialog-footer">
    <el-button @click="associatedAreaDialogVisible = false" size="mini">取 消</el-button>
    <el-button type="primary" @click="submit" size="mini" :disabled="associateAreaData.length>0? false:true">确 定</el-button>
  </span>
</el-dialog>
</template>

<script>
export default {
  props:{
    associatedAreaFalg:{
       type:Boolean,
       default:false
    },
    associateAreaData:{
       type:Array,
       default:()=>[]
    },
    associatedObject:{
      type:Object,
      default:()=>{}
    },
    otherFlags:{
      type:Boolean,
      default:false
    }
  },
  data(){
    return{
       associatedAreaDialogVisible:false,
       value:'',
       areaId:localStorage.getItem('area-id'),
       role:localStorage.getItem("role-code")
    }
  },
  watch:{
    associatedAreaFalg(val){
      this.associatedAreaDialogVisible = val
    },
  },
  methods:{
    openAssociated(){
      const rolecode = localStorage.getItem('role-code')
      if(rolecode=='AREA_MANAGER'){
        this.value = Number(this.areaId)
      }else{
        this.value = ''
      }
    },
    submit(){
      const params = {
        userId:this.associatedObject.id,
        areaId:this.value
      }
      this.$store.dispatch("person/removeAssociated", params).then(() => {
        this.$message.success("解除成功");
        this.associatedAreaDialogVisible = false
        this.$emit('refresh')
      }).catch((err) => {});
    }
  }
}
</script>

<style>
.associated .el-select {
    display: inline-block;
    position: relative;
    width: 75%;
}
.warningicon{
  color:#e99840;
  font-size:20px;
  margin-right:3px
}
</style>