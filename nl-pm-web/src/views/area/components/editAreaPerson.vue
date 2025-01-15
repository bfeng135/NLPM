<template>
   <div class="config-project">
       <el-dialog
        title="关联其他区人员"
        :visible.sync="editAreaDialogVisible"
        width="50%"
        @close="$emit('update:AreaConfigVisable', false)"
        @closed="closeConfig"
        :show-close="!editAreaLoading">
        <span class="configMain">
          <el-transfer v-model="value" :data="configData" :titles="['项目成员', '以选择项目成员']" v-loading="editLoading"></el-transfer>
        </span>
        <span slot="footer" class="dialog-footer">
        <el-button @click="editAreaDialogVisible = false" :disabled='editAreaLoading'>取 消</el-button>
        <el-button type="primary" @click="submitConfig" :loading="editAreaLoading">确 定</el-button>
        </span>
        </el-dialog>
   </div>
</template>

<script>
export default {
  props:{
    AreaConfigVisable:{
      type:Boolean,
      default:false
    },
    configAreaRowDatas:{
      type:Object,
      default:()=>{}
    }
  },
  data(){
    return {
       editAreaDialogVisible:false,
       value: [],
       configData:[],
       editLoading:false,
       editAreaLoading:false
    }
  },
  watch:{
    AreaConfigVisable(val){
      this.editAreaDialogVisible = val
    },
    configAreaRowDatas(val){
      if(val){
        this.getRoleUsers()
        this.value = []
        this.configPerson(val.id)
      }
    }
  },
  methods:{
    closeConfig(){
      this.value = []
      this.configData = []
      this.getRoleUsers()
      this.configPerson(this.configAreaRowDatas.id)
    },
    configPerson(id){
      this.$store.dispatch('area/getAreaPerson',id).then(res=>{
        res.map(item=>{
          this.value.push(
            item.userId
          )
        })
      })
    },
     getRoleUsers(){
      // const params = {
      //   areaId: 0,
      //   listRoleCode: [
      //     "AREA_MANAGER",
      //     "GROUP_MANAGER",
      //     "EMPLOYEE"
      //   ]
      // }
      this.editLoading = true
      this.configData = []
      this.$store.dispatch('area/getAreaPersonList',this.configAreaRowDatas.id).then(res=>{
        this.editLoading = false
        res.forEach(element => {
          this.configData.push({
            key:element.id,
            label:element.nickname
          })
        });
      })
    },
    submitConfig(){
      const params = {
        areaId:this.configAreaRowDatas.id,
        userIdList:this.value
      }
      this.editAreaLoading = true
      this.$store.dispatch('area/AssignAreaPerson',params).then(res=>{
        this.$message.success('配置成功')
        this.editAreaLoading = false
        this.editAreaDialogVisible = false
      }).catch(err=>{
        this.editAreaLoading = false
        this.$message.error(err)
      })
    }
  }
}
</script>

<style>
.configMain{
  display: flex;
  justify-content: center;
}
</style>