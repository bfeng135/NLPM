<template>
   <div class="config-project">
       <el-dialog
        title="配置人员"
        :visible.sync="editProjectDialogVisible"
        width="50%"
        @close="$emit('update:configVisable', false)"
        @closed="closeConfig"
        :show-close="!editProjectloading">
        <span class="configMain">
          <el-transfer v-model="value" :data="configData" :titles="['项目成员', '已选择项目成员']"></el-transfer>
        </span>
        <span slot="footer" class="dialog-footer">
        <el-button @click="editProjectDialogVisible = false" :disabled="editProjectloading">取 消</el-button>
        <el-button type="primary" @click="submitConfig" :loading="editProjectloading">确 定</el-button>
        </span>
        </el-dialog>
   </div>
</template>

<script>
export default {
  props:{
    configVisable:{
      type:Boolean,
      default:false
    },
    configRowDatas:{
      type:Object,
      default:()=>{}
    }
  },
  data(){
    return {
       editProjectDialogVisible:false,
       value: [],
       configData:[],
       editProjectloading:false,
       leftData:[],
       rightData:[]
    }
  },
  watch:{
    configVisable(val){
      this.editProjectDialogVisible = val
    },
    configRowDatas(val){
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
      this.configPerson(this.configRowDatas.id)
    },
    configPerson(id){
      this.$store.dispatch('person/getProjectAssignList',id).then(res=>{
        this.rightData = res
        res.map(item=>{
          this.value.push(
            item.id
          )
        })
      })
    },
     getRoleUsers(){
      // const params = {
      //   areaId: this.configRowDatas.areaId
      // }
      this.configData = []
      this.$store.dispatch('project/getProjectAssignPerson',this.configRowDatas.areaId).then(res=>{
        this.leftData = res
        this.rightData.forEach(item=>{
          const findId = res.find(element=> element.id==item.id)
          if(findId==undefined){
            this.leftData.push(item)
          }
        })
        this.leftData.forEach(element => {
          this.configData.push({
            key:element.id,
            label:element.nickname
          })
        });
      })
    },
    submitConfig(){
      this.editProjectloading = true
      const params = {
        projectId:this.configRowDatas.id,
        userIdList:this.value
      }
      this.$store.dispatch('project/configPerson',params).then(res=>{
        this.$message.success('配置成功')
        this.editProjectloading = false
        this.editProjectDialogVisible = false
      }).catch(err=>{
        this.editProjectloading = false
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