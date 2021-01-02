<template>
  <el-dialog ref="dialog" :title="title" :visible.sync="dialogFormVisible" width="442px">
    <el-form
      ref="commForm"
      :model="form"
      :rules="rules"
      :inline="true"
      label-width="70px"
      label-position="left"
    >
      <!-- TODO: 输入框宽度 -->
      <el-form-item label="敏感词：">
        <el-input v-model="form.sensitives" autocomplete="off" placeholder="请输入敏感词"></el-input>
      </el-form-item>
      <!-- <el-form-item label="权重：">
        <el-input v-model="form.sensitives" autocomplete="off" placeholder="请输入权重值，0-10"></el-input>
      </el-form-item>-->
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="warning" @click="dialogFormVisible = false">取消</el-button>
      <el-button type="success" @click="submit">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { saveData, updateData } from '@/api/sensitive'

export default {
  name: 'commn-editor',
  props: ['title', 'submitSuccess'],
  data () {
    return {
      disable: false,
      model: 'add',
      dialogFormVisible: false,
      formLabelWidth: '80px',
      form: {},
      rules: {}
    }
  },
  methods: {
    add: function () {
      this.dialogFormVisible = true
      this.form = {}
      this.model = 'add'
    },
    edit: function (item) {
      this.dialogFormVisible = true
      this.form = item
      this.model = 'edit'
    },
    submit: function () {
      this.$refs.commForm.validate((valid) => {
        if (valid) {
          if (this.model === 'add') {
            this.saveSensitive()
          } else {
            this.updateSensitive()
          }
        } else {
          return false
        }
      })
    },
    async saveSensitive () {
      const param = {
        sensitives: this.form.sensitives
      }
      const res = await saveData(param)

      if (res.code === 0) {
        this.dialogFormVisible = false
        this.submitSuccess()
        this.$message({ type: 'success', message: '操作成功！' })
      } else {
        this.$message({ type: 'error', message: res.error_message })
      }
    },
    async updateSensitive () {
      const param = {
        id: this.form.id,
        sensitives: this.form.sensitives
      }
      const res = await updateData(param)

      if (res.code === 0) {
        this.dialogFormVisible = false
        this.submitSuccess()
        this.$message({ type: 'success', message: '操作成功！' })
      } else {
        this.$message({ type: 'error', message: res.error_message })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/scss/element-variables.scss';
// TODO: 这里再想想
/deep/ .el-dialog__body {
  padding: 30px;
}

.el-form-item {
  margin-bottom: 20px;
}

/deep/ .el-form-item__label {
  color: $--color-text-primary;
}
</style>
