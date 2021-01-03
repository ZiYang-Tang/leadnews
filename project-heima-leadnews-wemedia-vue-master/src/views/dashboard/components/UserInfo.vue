<template>
  <section class="dashboard-user">
    <el-row type="flex" align="middle" class="user-info">
      <el-col :span="2">
        <img class="user-avatar" src="@/assets/avatar_new.png" />
      </el-col>
      <el-col :span="13">
        <el-row>
          <el-col :span="24" class="account">黑马头条官方账号</el-col>
          <el-col :span="24" class="tips">视频发布</el-col>
        </el-row>
      </el-col>
      <el-col :span="3">
        <el-row>
          <el-col :span="24" class="num">566</el-col>
          <el-col :span="24" class="text">粉丝数</el-col>
        </el-row>
      </el-col>
      <el-col :span="3">
        <el-row>
          <el-col :span="24" class="num">3241</el-col>
          <el-col :span="24" class="text">累计阅读量</el-col>
        </el-row>
      </el-col>
      <el-col :span="3">
        <el-row>
          <el-col :span="24" class="num">2651</el-col>
          <el-col :span="24" class="text">累计评论数</el-col>
        </el-row>
      </el-col>
    </el-row>
  </section>
</template>

<script>
export default {
  props: {},
  data () {
    return {}
  },
  mounted () {},
  methods: {}
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.dashboard-user {
  background: #FFFFFF;
  border: 1px solid #E7E7E9;
  box-shadow: 0px 2px 6px 0px rgba(242, 242, 242, 0.35);

  .user-info {
    height: 126px;

    .user-avatar {
      width: 85px;
      height: 85px;
      border-radius: 50%;
    }

    .account {
      font-size: 22px;
      color: #323745;
      text-align: left;
    }

    .tips {
      margin-top: 12px;
      font-size: 18px;
      color: #999999;
      text-align: left;
    }

    .num {
      font-size: 35px;
      color: #333333;
    }

    .text {
      margin-top: 3px;
      font-size: 18px;
      color: #999999;
    }

    .el-col-3 {
      border-right: 1px solid #EEEEEE;
    }
  }
}
</style>
