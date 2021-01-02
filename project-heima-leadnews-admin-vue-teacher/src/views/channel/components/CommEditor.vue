<template>
  <el-dialog ref="dialog" :title="title" :visible.sync="dialogFormVisible" width="442px">
    <el-form :model="form" :rules="rules" ref="commForm" :inline="true">
      <!-- TODO: 输入框宽度 -->
      <!-- <el-form-item label="频道编号：">
        <el-input v-model="form.name" autocomplete="off" placeholder="请输入频道编号"></el-input>
      </el-form-item>-->
      <el-form-item label="频道名称：">
        <el-input v-model="form.name" autocomplete="off" placeholder="请输入频道名称"></el-input>
      </el-form-item>
      <el-form-item label="频道描述：">
        <el-input v-model="form.description" autocomplete="off" placeholder="请输入频道描述"></el-input>
      </el-form-item>
      <el-form-item label="是否启动：">
        <el-radio-group v-model="form.status">
          <el-radio
            v-for="(item, index) in stateList"
            :key="index"
            :label="item.label"
          >{{ item.value }}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="排序方式：">
        <el-input v-model.number="form.ord" autocomplete="off" placeholder="请输入排序方式" type="number"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="warning" @click="dialogFormVisible = false">取消</el-button>
      <el-button type="success" @click="submit">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { saveData, updateData } from '@/api/channel'

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
      rules: {},
      stateList: [
        { label: true, value: '启动' },
        { label: false, value: '禁用' }
      ]
    }
  },
  methods: {
    add: function () {
      this.dialogFormVisible = true
      this.form = {
        name: '',
        status: false
      }
      this.model = 'add'
    },
    edit: function (item) {
      this.dialogFormVisible = true
      this.form = item
      this.model = 'edit'
    },
    submit: function () {
      this.$refs.commForm.validate(async (valid) => {
        if (valid) {
          if (this.model === 'add') {
            this.saveChannel()
          } else {
            this.updateChannel()
          }
        } else {
          return false
        }
      })
    },
    async saveChannel () {
      const param = {
        name: this.form.name,
        status: this.form.status,
        description: this.form.description,
        ord: this.form.ord
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
    async updateChannel () {
      const param = {
        id: this.form.id,
        name: this.form.name,
        status: this.form.status,
        description: this.form.description,
        ord: this.form.ord
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
