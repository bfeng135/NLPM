<template>
  <el-dialog
  title="休假详情"
  :visible.sync="daychengedialogVisible"
  width="40%"
  @close="$emit('update:detailchange', false)">
  <span>
      <el-table :data="detailData">
        <el-table-column property="date" label="请假时间">
          <template slot-scope="scope">
              {{scope.row.date|formatymd}}
          </template>
        </el-table-column>
        <el-table-column property="leaveHour" label="请假时长"></el-table-column>
        <el-table-column property="desc" label="请假原因" ></el-table-column>
      </el-table>
  </span>
</el-dialog>
</template>

<script>
export default {
  props:{
    detailchange:{
      type:Boolean,
      default:false
    },
    detailData:{
      type:Array,
      default:()=>[]
    }
  },
  data(){
    return{
        daychengedialogVisible:false,
        gridData:[]
    }
  },
  watch:{
    detailchange(val){
      this.daychengedialogVisible = val
    }
  }
}
</script>

<style>
.el-dialog__header {
  border-bottom: 1px solid #e5e9eb;
}
</style>