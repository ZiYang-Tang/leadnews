<template>
  <el-dialog ref="dialog" :title="title" :visible.sync="dialogFormVisible" width="442px">
    <el-form :model="form" :rules="rules" ref="commForm" label-position="left">
      <el-row :gutter="22">
        <el-col :span="12">
          <el-form-item label="用户名">{{ form.name }}</el-form-item>
          <el-form-item label="密码">{{ form.password }}</el-form-item>
          <el-form-item label="手机号">{{ form.phone }}</el-form-item>
          <el-form-item label="性别">{{ form.sex ? '女': '男' }}</el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="实名认证">{{ form.is_certification ? '已认证' : '未认证'}}</el-form-item>
          <el-form-item label="身份认证">{{ form.is_identity_authentication ? '已认证' : '未认证' }}</el-form-item>
          <el-form-item label="账号状态">{{ form.status ? '冻结' : '正常'}}</el-form-item>
          <el-form-item label="用户标识">{{ form.flag ? '自媒体人' : '普通用户' }}</el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </el-dialog>
</template>

<script>
export default {
  name: 'commn-editor',
  props: ['title', 'fileds', 'table', 'submitSuccess'],
  data () {
    return {
      dialogFormVisible: false,
      formLabelWidth: '80px',
      entry: {},
      form: {},
      rules: {}
    }
  },
  methods: {
    view: function (item) {
      this.dialogFormVisible = true
      this.entry = item
      this.refresh()
    },
    refresh: function () {
      // 初始化数据
      for (let i = 0; i < this.fileds.length; i++) {
        const tmp = this.fileds[i]
        if (tmp.rule) {
          this.rules[tmp.name] = tmp.rule
        }
        // 是否有修改值entry，否则使用默认值
        let val = tmp.value
        if (this.entry && this.entry[tmp.name] !== undefined) {
          val = this.entry[tmp.name]
        }
        this.$set(this.form, tmp.name, val)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
  /deep/ .el-dialog__body {
    padding: 30px;

    .el-form-item {
      margin: 0;
      border-top: 1px solid #EBEEF5;

      .el-form-item__content {
        text-align: right;
      }
    }

    .el-form-item:last-child {
      border-bottom: 1px solid #EBEEF5;
    }
  }
</style>
