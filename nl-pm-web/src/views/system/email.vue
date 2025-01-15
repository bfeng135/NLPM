<template>
  <div class="email">
    <header-title>
      <template slot="title"> 邮箱管理 </template>
    </header-title>
    <div class="emailSearch">
      <div style="margin-right: 15px">
        <el-button type="text" @click="addEmail">
          <span
            class="el-icon-circle-plus"
            style="color: #13ce66; font-size: 16px"
          ></span>
          <span style="color: #606266">新建系统邮箱</span>
        </el-button>
      </div>
    </div>
    <el-table :data="emailData" style="width: 100%">
      <el-table-column type="index"></el-table-column>
      <el-table-column label="邮箱地址" prop="username"></el-table-column>
      <el-table-column label="密码" prop="password"></el-table-column>
      <el-table-column label="主机名" prop="host"></el-table-column>
      <el-table-column label="发送数量" prop="sendNum"></el-table-column>
      <el-table-column label="操作"  width="118">
          <template slot-scope="scope">
              <el-button size="mini" icon="el-icon-edit-outline" title="编辑" @click="handleEdit(scope.row)" type="warning"></el-button>
              <el-button type="danger" icon="el-icon-delete" title="删除" size="mini" @click="handleDelete(scope.row)"></el-button>
          </template>
      </el-table-column>
    </el-table>
    <addemail :addemailvisable.sync="addemailvisable" :isEdit="isEdit" :editRowData="editRowData" @refresh="getAllEmail"/>
  </div>
</template>

<script>
import headerTitle from "@/components/Title/index.vue";
import addemail from './component/addemail.vue'
export default {
  components: {
    headerTitle,
    addemail
  },
  data() {
    return {
      emailData: [],
      addemailvisable:false,
      isEdit:false,
      editRowData:{}
    };
  },
  created() {
    this.getAllEmail();
  },
  methods: {
    getAllEmail() {
      this.$store
        .dispatch("system/getAllEmail")
        .then((res) => {
          this.emailData = res;
        })
        .catch((err) => {});
    },
    addEmail(){
      this.addemailvisable = true
      this.isEdit = false
    },
    handleEdit(row){
      this.isEdit = true
      this.addemailvisable = true
      this.editRowData = row
    },
    handleDelete(row){
      this.$confirm(
        `此操作将删除该邮箱,是否继续`,
        "删除", {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: "error",
          showClose: true,
          beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.showClose = false;
              instance.showCancelButton = false;
              instance.closeOnClickModal = false;
              this.$store
                .dispatch("system/DeleteEmail", row.id)
                .then(() => {
                  done();
                  instance.showClose = true;
                  instance.showCancelButton = true;
                  instance.closeOnClickModal = true;
                  instance.confirmButtonLoading = false;
                  this.$message.success("删除成功");
                  this.getAllEmail()
                })
                .catch((err) => {
                  done();
                  instance.confirmButtonLoading = false;
                  this.$message.error(err)
                });
            } else {
              done();
            }
          },
        },
      ).catch(() => {});
    }
  },
};
</script>

<style>
.email {
  padding: 20px;
}
.emailSearch {
  width: 100%;
  height: 50px;
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}
</style>