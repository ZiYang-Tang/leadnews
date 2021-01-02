<template>
  <section class="filter">
    <div class="filter-container">
      <el-form ref="form" :inline="true">
        <!-- TODO: clearable按钮还原 -->
        <!-- TODO: 输入框宽度 -->
        <el-form-item label="账号/手机号：">
          <el-input
            v-model="name"
            placeholder="请输入账号或手机号"
            class="filter-item"
            clearable
            @change="queryData"
          />
        </el-form-item>
        <el-form-item label="审核状态：">
          <!-- TODO: clearable按钮还原，下拉列表宽度 -->
          <el-select v-model="status" placeholder="请选择状态" clearable @change="changeStatus">
            <el-option
              v-for="item in statusList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
  </section>
</template>

<script>
export default {
  props: ['changeParam', 'addData'],
  data () {
    return {
      statusList: [
        { label: '全部', value: '' },
        { label: '正常', value: 0 },
        { label: '冻结', value: 1 }
      ],
      name: '',
      status: ''
    }
  },
  methods: {
    queryData () {
      const params = []
      if (this.name) {
        params.push({
          filed: 'name',
          type: 'like',
          value: this.name
        })
      }
      if (this.status !== '') {
        params.push({
          filed: 'status',
          type: 'eq',
          value: this.status
        })
      }
      this.changeParam(params)
    },
    // 切换状态
    changeStatus () {
      this.queryData() // 查询数据
    }
  }
}
</script>
