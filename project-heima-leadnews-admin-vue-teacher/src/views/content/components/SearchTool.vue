<template>
  <section class="filter">
    <div class="filter-container">
      <el-form ref="form" :inline="true">
        <el-form-item label="文章名称：">
          <el-input v-model="title" placeholder="请输入文章名称" class="filter-item" clearable @change="queryData" />
        </el-form-item>
        <!-- TODO: 频道名称模糊检索不对 -->
        <el-form-item label="频道名称：">
          <el-input v-model="channel_name" placeholder="请输入频道名称" class="filter-item" clearable @change="queryData" />
        </el-form-item>
        <el-form-item label="状态：">
          <el-select v-model="selectState" placeholder="请选择状态" clearable>
            <el-option
              v-for="item in stateList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <!-- TODO: 等待后台接口 -->
        <el-form-item label="发布日期：">
          <el-date-picker
            class="el-date-picker"
            style="width: 100%;"
            v-model="publishDate"
            type="daterange"
            align="right"
            unlink-panels
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            @change="handlePublishDateChange"
          ></el-date-picker>
        </el-form-item>
        <el-form-item style="float:right;">
          <el-button type="success" icon="el-icon-search" @click="queryData">查询</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- TODO: 重置按钮 -->
  </section>
</template>

<script>
export default {
  props: ['changeParam', 'addData'],
  data () {
    return {
      title: '',
      channel_name: '',
      selectState: '',
      // TODO: 状态逻辑需要后台提供
      stateList: [
        { label: '全部', value: '' },
        { label: '正常', value: 1 },
        { label: '已下架', value: 2 }
      ],
      publishDate: []
    }
  },
  methods: {
    queryData () {
      const params = []
      if (this.title) {
        params.push({
          filed: 'title',
          type: 'like',
          value: this.title
        })
      }
      if (this.channel_name) {
        params.push({
          filed: 'channel_name',
          type: 'like',
          value: this.channel_name
        })
      }
      this.changeParam(params)
    },
    handlePublishDateChange (orderDate) {}
  }
}
</script>
