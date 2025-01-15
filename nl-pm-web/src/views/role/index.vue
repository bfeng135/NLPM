<template>
  <div class="role">
    <header-title>
      <template slot="title"> 角色管理 </template>
    </header-title>
    <el-table
      :data="roleTableData"
      style="width: 100%">
      <el-table-column
        type="index">
      </el-table-column>
      <el-table-column
        prop="name"
        label="职称">
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import headerTitle from '@/components/Title/index.vue'
export default {
  components:{
    headerTitle
  },
  data(){
    return{
      roleTableData:[]
    }
  },
  created(){
    this.getRoleLists()
  },
  methods:{
    getRoleLists(){
      this.$store.dispatch('role/getRoleList').then(res=>{
        this.roleTableData = res
      })
    }
  }
}
</script>

<style>
.role{
  padding: 20px;
}
</style>