<template>
  <section class="filter">
    <div class="filter-container">
      <el-form ref="form" :inline="true">
        <el-col>
          <el-form-item label="文章状态：">
            <el-radio-group v-model="status" @change="changeStatus">
              <el-radio v-for="(item, index) in statusList" :key="index" :label="item.label">{{ item.value }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col>
          <el-form-item label="关键字：">
            <el-input v-model="keyword" placeholder="请输入关键字" style="width: 179px;" clearable @change="queryData" />
          </el-form-item>
          <el-form-item label="频道列表：">
            <el-select v-model="channelId" style="width: 179px;" @change="queryData">
              <el-option
                v-for="item in channel_list"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="发布日期：">
            <el-date-picker
              type="datetimerange"
              v-model="date"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="yyyy-MM-dd"
              value-format="yyyy-MM-dd"
              placeholder="选择日期"
              style="width: 239px;"
              @change="queryData"
            />
          </el-form-item>
        </el-col>
      </el-form>
    </div>
  </section>
</template>
<script>

export default {
  props: ['changePage', 'channel_list'],
  data () {
    return {
      keyword: '',
      statusList: [
        { label: 5, value: '全部' },
        { label: 0, value: '草稿' },
        { label: 1, value: '待审核' },
        { label: 2, value: '审核通过' },
        { label: 3, value: '审核失败' }
      ],
      status: 5,
      channelId: null, // 频道id
      date: null
    }
  },
  methods: {
    // 查询数据 值得注意的是 一旦条件形成 那么页码应该重新设置为1
    // 因为查询条件的变化  页码应该从第一页开始
    queryData () {
      const params = {
        resetPage: true, // 用于判断是否需要重新设置分页器的标记
        keyword: this.keyword.trim() ? this.keyword.trim() : null,
        channelId: this.channelId,
        status: this.status === 5 ? null : this.status,
        page: 1,
        beginPubDate: (this.date && this.date.length) ? this.date[0] : null,
        endPubDate: (this.date && this.date.length > 1) ? this.date[1] : null
      }
      this.changePage && this.changePage(params) // 调用上层组件的查询方法
    },
    // 切换文章状态
    changeStatus () {
      this.queryData() // 查询数据
    }
  }
}
</script>
<style lang="scss" scoped>
.filter {
  height: 158px;
}
</style>
