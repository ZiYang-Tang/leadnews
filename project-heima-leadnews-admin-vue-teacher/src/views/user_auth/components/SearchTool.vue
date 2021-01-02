<template>
  <section class="filter">
    <div class="filter-container">
      <el-form ref="form" :inline="true">
        <el-form-item label="选择类型：">
          <el-radio-group v-model="status" @change="changeStatus">
            <el-radio v-for="(item, index) in statusList" :key="index" :label="item.label">{{ item.value }}</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </div>
  </section>
</template>

<script>
export default {
  props: ['statusList', 'changeParam'],
  data () {
    return {
      date: null,
      status: '',
      userName: ''
    }
  },

  methods: {
    queryData () {
      // TODO: 查询全部接口传值
      const params = {}
      if (this.status !== '') {
        params.status = this.status
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
