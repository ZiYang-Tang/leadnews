<template>
  <section class="filter">
    <div class="filter-container">
      <!-- TODO: 是否需要添加检索条件 -->
      <el-form ref="form" :inline="true">
        <el-form-item label="搜索内容：">
          <el-input
            v-model="title"
            placeholder="请输入内容标题"
            class="filter-item"
            clearable
            @change="queryData"
          />
        </el-form-item>
      </el-form>
    </div>
  </section>
</template>

<script>
export default {
  props: ['changeParam'],
  data () {
    return {
      title: ''
    }
  },
  methods: {
    queryData () {
      this.changeParam(this.title)
    }
  }
}
</script>
