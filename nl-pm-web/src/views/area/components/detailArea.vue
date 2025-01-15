<template>
  <div class="detail-area">
    <el-dialog
      title="区域详情"
      :visible.sync="detailAreaDialogVisible"
      width="40%"
      @close="$emit('update:detailAreaVisible', false)"
    >
      <span>
        <el-card class="box-card">
          <div class="text item">
            <span class="textRight">区域名称:</span>
            <span>{{deepAreaInfo.name}}</span>
          </div>
          <div class="text item">
            <span class="textRight">描述:</span>
            <span>{{deepAreaInfo.desc}}</span>
          </div>
          <div class="text item">
            <span class="textRight">创建时间:</span>
            <span>{{deepAreaInfo.createTime|formatDate}}</span>
          </div>
          <div class="text item">
            <span class="textRight">更新时间:</span>
            <span>{{deepAreaInfo.updateTime|formatDate}}</span>
          </div>
        </el-card>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  props: {
    detailAreaVisible: {
      type: Boolean,
      default: false,
    },
    areaDetail:{
      type:Object,
      default:()=>{}
    }
  },
  data() {
    return {
      detailAreaDialogVisible: false,
      deepAreaInfo:{}
    };
  },
  watch: {
    detailAreaVisible(val) {
      this.detailAreaDialogVisible = val;
    },
    areaDetail(val){
      this.deepAreaInfo = val
    }
  },
};
</script>

<style>
.el-dialog__header {
  border-bottom: 1px solid #e5e9eb;
}
.detail-area .text {
  font-size: 14px;
  }
.detail-area .item {
  margin-bottom: 25px;
}
.textRight{
  margin-right: 10px;
}
@media only screen
and (min-device-width : 320px)
and (max-device-width : 500px){
  .detail-area .el-dialog {
    width: 100% !important; 
  }
}
</style>