<template>
  <el-card style="margin-bottom:20px;">
    <div slot="header" class="clearfix">
      <span>关于我</span>
    </div>

    <div class="user-profile">
      <div class="box-center">
        <pan-thumb :image='require("@/assets/images_icon.png")' :height="'100px'" :width="'100px'" :hoverable="false">
          <div class="pan-text">{{ name }}</div>
          <!-- <span style="text-align:center">{{ name }}</span> -->
        </pan-thumb>
      </div>
      <div class="box-center">
        <div class="user-name text-center">{{ name }}</div>
        <!-- <div class="user-role text-center text-muted">{{ user.role | uppercaseFirst }}</div> -->
      </div>
    </div>

    <div class="user-bio">
      <div class="user-education user-bio-section">
        <div class="user-bio-section-header"><svg-icon icon-class="education" /><span>个人信息</span></div>
        <div class="user-bio-section-body">
          <div class="text-muted">
             <span>用户名：</span>
             <span>{{userInfo.username}}</span>
          </div>
          <div class="text-muted">
             <span>姓名：</span>
             <span>{{userInfo.nickname}}</span>
          </div>
          <div class="text-muted">
             <span>角色：</span>
             <span>{{userInfo.roleName}}</span>
          </div>
          <div class="text-muted">
             <span>联系电话：</span>
             <span v-if='userInfo.phone!== ""'>{{userInfo.phone}}</span>
             <span v-else>暂无</span>
          </div>
          <div class="text-muted">
             <span>人员状态：</span>
             <span v-if="userInfo.status===true">
               <el-tag type="success" style="border-radius:20px;text-align:center"><span style="margin-left:2px">在职</span></el-tag>
             </span>
             <span v-else>
               <el-tag type="danger" style="border-radius:20px;width:50px;text-align:center"><span style="margin-left:2px">离职</span></el-tag>
             </span>
          </div>
          <div class="text-muted">
             <span>邮件地址：</span>
             <span>{{userInfo.email}}</span>
          </div>
          <div class="text-muted">
             <span>创建时间：</span>
             <span>{{userInfo.createTime|formatDate}}</span>
          </div>
          <div class="text-muted">
             <span>更新时间：</span>
             <span>{{userInfo.updateTime|formatDate}}</span>
          </div>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script>
import PanThumb from '@/components/PanThumb'
import { mapGetters } from 'vuex'
export default {
  components: { PanThumb },
  props:{
    userInfo:{
      type:Object,
      default:()=>{}
    }
  },
  computed: {
    ...mapGetters([
      'name'
    ])
  },
  data(){
    return{
    }
  },
  created(){
  },
  watch:{
  },
  methods:{
  }
}
</script>

<style lang="scss" scoped>
.box-center {
  margin: 0 auto;
  display: table;
}

.text-muted {
  color: #777;
  margin-bottom: 20px;
}

.user-profile {
  .user-name {
    font-weight: bold;
  }

  .box-center {
    padding-top: 10px;
  }

  .user-role {
    padding-top: 10px;
    font-weight: 400;
    font-size: 14px;
  }

  .box-social {
    padding-top: 30px;

    .el-table {
      border-top: 1px solid #dfe6ec;
    }
  }

  .user-follow {
    padding-top: 20px;
  }
}
.pan-text{
  display: flex;
  justify-content: center;
  align-items: center;
  line-height: 100%;
  margin-top: 22px;
}
.user-bio {
  margin-top: 20px;
  color: #606266;

  span {
    padding-left: 4px;
  }

  .user-bio-section {
    font-size: 14px;
    padding: 15px 0;

    .user-bio-section-header {
      border-bottom: 1px solid #dfe6ec;
      padding-bottom: 10px;
      margin-bottom: 10px;
      font-weight: bold;
    }
  }
}
</style>
