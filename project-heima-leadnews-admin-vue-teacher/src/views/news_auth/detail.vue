<template>
  <div class="container">
    <div class="header">
      <el-page-header @back="goBack" content="文章详情"></el-page-header>
      <div v-if="detail.status === 3">
        <el-button type="primary" @click="operateForPass()">
          <svg class="icon svg-icon" aria-hidden="true">
            <use xlink:href="#iconicon_btn_pass" />
          </svg>
          通过
        </el-button>
        <el-button type="danger" @click="operateForFail()">
          <svg class="icon svg-icon" aria-hidden="true">
            <use xlink:href="#iconicon_btn_reject" />
          </svg>
          驳回
        </el-button>
      </div>
      <div v-else-if="detail.status === 4" class="operate">
        <span class="status">状态：</span>
        <span class="pass">审核通过</span>
      </div>
      <div v-else-if="detail.status === 2" class="operate">
        <span class="status">状态：</span>
        <span class="fail">已驳回</span>
        <br />
        <span class="reason">理由：{{ detail.reason }}</span>
      </div>
    </div>
    <div class="main">
      <el-row :gutter="30">
        <el-col :span="5">
          <span class="name">文章标题</span>
        </el-col>
        <el-col :span="5">
          <span class="name">作者</span>
        </el-col>
        <el-col :span="5">
          <span class="name">更新时间</span>
        </el-col>
      </el-row>
      <el-row :gutter="30" style="margin-top:20px;">
        <el-col :span="5">
          <span class="value">{{ detail.title }}</span>
        </el-col>
        <el-col :span="5">
          <span class="value">{{ detail.userId }}</span>
        </el-col>
        <el-col :span="5">
          <span class="value">{{ dateFormat(detail.submitedTime) }}</span>
        </el-col>
      </el-row>
      <el-row :gutter="30" style="margin-top:50px;">
        <el-col :span="5">
          <span class="name">正文信息</span>
        </el-col>
      </el-row>
      <p v-for="(item, index) in JSON.parse(detail.content)" :key="index">
        <span v-if="item.type === 'text'" style="color: #17233e;">{{ item.value }}</span>
        <img v-else :src="item.value" />
      </p>
    </div>
  </div>
</template>

<script>
import DateUtil from '@/utils/date'
import { authOne, authPass, authFail } from '@/api/news_auth'

export default {
  name: 'ContentDetail',
  data () {
    return {
      detail: {
        content: '[]'
      }
    }
  },
  created () {
    this.loadData(this.$route.query.id)
  },
  methods: {
    async loadData (id) {
      const res = await authOne(id)
      this.detail = res.data
    },
    goBack () {
      this.$router.go(-1)
    },
    dateFormat (time) {
      return DateUtil.format13HH(time)
    },
    async operateForPass () {
      const params = {
        id: this.detail.id,
        status: 4
      }
      const res = await authPass(params)
      if (res.code === 200) {
        this.submitSuccess()
        this.$message({ type: 'success', message: '操作成功！' })
      } else {
        this.$message({ type: 'error', message: res.errorMessage })
      }
    },
    operateForFail () {
      const params = {
        id: this.detail.id,
        status: 2
      }
      this.open('请输入驳回理由', params)
    },
    open (msg, params) {
      this.$prompt(undefined, '驳回理由', {
        confirmButtonText: '提交',
        confirmButtonClass: 'el-button--success',
        cancelButtonText: '取消',
        cancelButtonClass: 'el-button--warning',
        inputPlaceholder: msg,
        inputType: 'textarea'
      }).then(async ({ value }) => {
        // TODO: 驳回理由为空的情况
        params.title = value

        const res = await authFail(params)
        if (res.code === 200) {
          this.submitSuccess()
          this.$message({ type: 'success', message: '操作成功！' })
        } else {
          this.$message({ type: 'error', message: res.errorMessage })
        }
      }).catch(() => {
        this.$message({ type: 'info', message: '已取消驳回' })
      })
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
  }

  .main {
    min-height: calc(100vh - 80px - 30px - 89px);
    padding-top: 30px;
    text-align: left;

    .name {
      color: $--color-text-secondary;
    }
  }
}
</style>
