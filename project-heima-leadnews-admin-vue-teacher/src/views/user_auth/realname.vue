<template>
  <div class="container">
    <div class="header">
      <el-page-header @back="goBack" content="实名认证"></el-page-header>
      <div v-if="realname.status.toString() === '1'">
        <el-button type="primary" @click="operateForPass()">
          <svg class="icon svg-icon" aria-hidden="true">
            <use xlink:href="#iconicon_btn_pass" />
          </svg>
          通过
        </el-button>
        <el-button type="danger" @click="operateForFail('请输入驳回理由')">
          <svg class="icon svg-icon" aria-hidden="true">
            <use xlink:href="#iconicon_btn_reject" />
          </svg>
          驳回
        </el-button>
      </div>
      <div v-else-if="realname.status.toString() === '9'" class="operate">
        <span class="status">状态：</span>
        <span class="pass">审核通过</span>
      </div>
      <div v-else-if="realname.status.toString() === '2'" class="operate">
        <span class="status">状态：</span>
        <span class="fail">已驳回</span>
        <br />
        <span class="reason">理由：{{ realname.reason }}</span>
      </div>
    </div>
    <div class="main">
      <el-row :gutter="30">
        <el-col :span="4">
          <span class="name">姓名</span>
          <span class="value">{{ realname.name }}</span>
        </el-col>
        <el-col :span="20">
          <span class="name">身份证号</span>
          <span class="value">{{ realname.idno }}</span>
        </el-col>
      </el-row>
      <el-row :gutter="30">
        <el-col :span="9">
          <img :src="realname.host + realname.fontImage" />
        </el-col>
        <el-col :span="9">
          <img :src="realname.host + realname.backImage" />
        </el-col>
      </el-row>
      <el-row :gutter="30">
        <el-col :span="9">
          <i class="triangle">&#9650;</i>身份证正面
        </el-col>
        <el-col :span="9">
          <i class="triangle">&#9650;</i>身份证背面
        </el-col>
      </el-row>
      <el-row :gutter="30">
        <el-col :span="9">
          <img :src="realname.host + realname.holdImage" />
        </el-col>
        <el-col :span="9">
          <img :src="realname.host + realname.liveImage" />
        </el-col>
      </el-row>
      <el-row :gutter="30">
        <el-col :span="9">
          <i class="triangle">&#9650;</i>手持身份证
        </el-col>
        <el-col :span="9">
          <i class="triangle">&#9650;</i>人脸识别信息
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { authPass, authFail } from '@/api/user_auth'

export default {
  name: 'RealnameAuth',
  data () {
    return {
      realname: {},
      params: {
        id: '',
        msg: ''
      }
    }
  },
  created () {
    this.realname = this.$route.query
    this.params.id = this.realname.id
  },
  methods: {
    goBack () {
      this.$router.go(-1)
    },
    operateForFail (msg) {
      this.$prompt(undefined, '驳回理由', {
        confirmButtonText: '提交',
        confirmButtonClass: 'el-button--success',
        cancelButtonText: '取消',
        cancelButtonClass: 'el-button--warning',
        inputPlaceholder: msg,
        inputType: 'textarea'
      }).then(async ({ value }) => {
        // TODO: 驳回理由为空的情况
        this.params.msg = value
        await authFail(this.params)
      }).catch(() => {
        this.$message({ type: 'info', message: '已取消驳回' })
      })
    },
    async operateForPass () {
      this.params.msg = ''
      const res = await authPass(this.params)

      if (res.code === 200) {
        this.$message({ type: 'success', message: '操作成功' })
      } else {
        this.$message({ type: 'error', message: res.error_message })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/scss/element-variables.scss';

.container {
  height: 100%;
  padding: 30px;

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 22px;
    border-bottom: 1px solid #ebeef5;

    .operate {
      text-align: right;

      .status {
        height: 20px;
        color: $--color-text-secondary;
        line-height: 20px;
      }

      .pass {
        height: 26px;
        padding: 3px 7px;
        border-radius: 4px;
        background: #ebf7f2;
        color: $--color-success;
        line-height: 20px;
      }

      .fail {
        height: 26px;
        padding: 3px 8px;
        border-radius: 4px;
        background: #ffefef;
        color: $--color-danger;
        line-height: 20px;
      }

      .reason {
        display: inline-block;
        height: 17px;
        margin-top: 6px;
        color: $--color-danger;
        line-height: 17px;
      }
    }
  }

  .main {
    min-height: calc(100vh - 80px - 30px - 89px);
    padding-top: 30px;
    text-align: left;

    .name {
      margin-right: 20px;
      color: $--color-text-secondary;
    }

    img {
      width: 100%;
      // min-width: 410px;
      margin-top: 30px;
      margin-bottom: 20px;
    }

    .triangle {
      color: $--color-primary;
    }
  }
}
</style>
