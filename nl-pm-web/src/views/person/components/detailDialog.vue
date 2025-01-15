<template>
  <div class="detail">
    <el-dialog
      title="人员详情"
      :visible.sync="detailDialogVisible"
      width="40%"
      @close="$emit('update:detailVisable', false)"
    >
      <span>
        <el-card class="box-card" v-loading="persondetailLoading">
          <div class="text item">
            <span class="textRight">区域名:</span>
            <span>{{deepUserInfo.areaName}}</span>
          </div>
          <div class="text item">
            <span class="textRight">用户名:</span>
            <span>{{deepUserInfo.username}}</span>
          </div>
          <div class="text item">
            <span class="textRight">姓名:</span>
            <span>{{deepUserInfo.nickname}}</span>
          </div>
          <div class="text item">
            <span class="textRight">角色:</span>
            <span>{{deepUserInfo.roleName}}</span>
          </div>
          <div class="text item">
            <span class="textRight">在职状态:</span>
            <span v-if="deepUserInfo.status==true"><el-tag type="success">在职</el-tag></span>
            <span v-else><el-tag type="danger">离职</el-tag></span>
          </div>
          <div class="text item">
            <span class="textRight">发送邮件状态:</span>
            <span v-if="deepUserInfo.emailNotice==true"><el-tag type="success">发送邮件</el-tag></span>
            <span v-else><el-tag type="danger">不发送邮件</el-tag></span>
          </div>
          <div class="text item">
            <span class="textRight">邮箱:</span>
            <span>{{deepUserInfo.email}}</span>
          </div>
          <div class="text item">
            <span class="textRight">手机号:</span>
            <span v-if="deepUserInfo.phone!==null">{{deepUserInfo.phone}}</span>
            <span v-else>--</span>
          </div>
          <div class="text item">
            <span class="textRight">创建时间:</span>
            <span>{{deepUserInfo.createTime|formatDate}}</span>
          </div>
          <div class="text item">
            <span class="textRight">更新时间:</span>
            <span>{{deepUserInfo.updateTime|formatDate}}</span>
          </div>
        </el-card>
      </span>
    </el-dialog >
  </div>
</template>

<script>
export default {
  props: {
    detailVisable: {
      type: Boolean,
      default: false,
    },
    userInfo:{
      type:Object,
      default:()=>{}
    }
  },
  data() {
    return {
      detailDialogVisible: false,
      deepUserInfo:{},
      persondetailLoading:false
    };
  },
  watch: {
    detailVisable(val) {
      this.persondetailLoading = true
      this.detailDialogVisible = val;
    },
    userInfo(val){
      if(val){
       this.persondetailLoading = false
       this.deepUserInfo = val
      }
      
    }
  },
};
</script>

<style>
.el-dialog__header {
  border-bottom: 1px solid #e5e9eb;
}
.detail .text {
  font-size: 14px;
  }
.detail .item {
  margin-bottom: 25px;
}
.textRight{
  margin-right: 10px;
}
@media only screen
and (min-device-width : 320px)
and (max-device-width : 500px){
  .detail .el-dialog {
    width: 100% !important; 
  }
}
</style>