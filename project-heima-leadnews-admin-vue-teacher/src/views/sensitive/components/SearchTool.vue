<template>
  <section class="filter">
    <div class="filter-container">
      <el-form ref="form" :inline="true">
        <el-form-item label="敏感词：">
          <el-input
            v-model="name"
            placeholder="请输入敏感词"
            class="filter-item"
            clearable
            @change="queryData"
          />
        </el-form-item>
      </el-form>
    </div>
    <el-button type="success" icon="el-icon-circle-plus-outline" @click="addData">新建</el-button>
  </section>
</template>

<script>
export default {
  props: ['changeParam', 'addData'],
  data () {
    return {
      name: ''
    }
  },
  methods: {
    queryData () {
      this.changeParam(this.name)
    }
  }
}
</script>
