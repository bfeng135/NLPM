<template>
  <div class="detail-project-dialog">
    <el-dialog
      title="项目信息详情"
      :visible.sync="detailDialogVisible"
      width="40%"
      @close="$emit('update:projectDetailVisable', false)"
    >
      <span>
        <el-card class="box-card" v-loading="detailprojectLoading">
          <div class="text item">
            <span class="textRight">项目名称:</span>
            <span>{{deepProjectInfo.name}}</span>
          </div>
          <div class="text item">
            <span class="textRight">项目负责人:</span>
            <span>{{deepProjectInfo.managerName}}</span>
          </div>
          <div class="text item">
            <span class="textRight">区域名称:</span>
            <span>{{deepProjectInfo.areaName}}</span>
          </div>
          <div class="text item">
            <span class="textRight">项目状态:</span>
            <span v-if="deepProjectInfo.enable===true">
              <el-tag type="success">启用</el-tag>
            </span>
            <span v-else>
              <el-tag type="danger">禁用</el-tag>
            </span>
          </div>
          <div class="text item">
            <span class="textRight">项目成员:</span>
              <div v-for="(item,index) in deepProjectInfo.projectUserList" :key="index" style="display:inline-block">
                <span style="margin-right:5px">{{item.nickname}}</span>
              </div>
            </div>
          <div class="text item">
            <span class="textRight">项目描述:</span>
            <span v-if="deepProjectInfo.desc!==null">{{deepProjectInfo.desc}}</span>
            <span v-else>--</span>
          </div>
          <div class="text item">
            <span class="textRight">创建时间:</span>
            <span>{{deepProjectInfo.createTime|formatDate}}</span>
          </div>
          <div class="text item">
            <span class="textRight">更新时间:</span>
            <span>{{deepProjectInfo.updateTime|formatDate}}</span>
          </div>
        </el-card>
      </span>
    </el-dialog >
  </div>
</template>

<script>
export default {
  props: {
    projectDetailVisable: {
      type: Boolean,
      default: false,
    },
    projectInfo:{
      type:Object,
      default:()=>{}
    }
  },
  data() {
    return {
      detailDialogVisible: false,
      deepProjectInfo:{},
      detailprojectLoading:false
    };
  },
  watch: {
    projectDetailVisable(val) {
      this.detailprojectLoading = true
      this.detailDialogVisible = val;
    },
    projectInfo(val){
      if(val){
        this.detailprojectLoading = false
        this.deepProjectInfo = val
      }
      
    }
  },
};
</script>

<style>
.el-dialog__header {
  border-bottom: 1px solid #e5e9eb;
}
.detail-project-dialog .text {
  font-size: 14px;
  }
.detail-project-dialog .item {
  margin-bottom: 25px;
}
.textRight{
  margin-right: 10px;
}
@media only screen
and (min-device-width : 320px)
and (max-device-width : 500px){
  .detail-project-dialog .el-dialog {
    width: 100% !important; 
  }
}
</style>