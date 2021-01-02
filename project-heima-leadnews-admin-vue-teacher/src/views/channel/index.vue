<template>
  <div>
    <Editor ref="editor" :title="title" :table="this.params.name" :submitSuccess="submitSuccess"/>
    <search-tool :changeParam="changeParam" :addData="addData" />
    <search-result
      ref='mySearchResult'
      :list="list"
      :total="total"
      :table="this.params.name"
      :editData="editData"
      :changePage="changePage"
      :submitSuccess="submitSuccess"
      :pageSize="params.size"/>
  </div>
</template>

<script>
import SearchTool from './components/SearchTool'
import SearchResult from './components/SearchResult'
import Editor from './components/CommEditor'
import { loadList } from '@/api/channel'
export default {
  name: 'ChannelManager',
  data () {
    return {
      params: {
        name: '',
        page: 1,
        size: 10
      },
      total: 0,
      host: '',
      list: [],
      title: ''
    }
  },
  mounted () {
    this.loadData()
  },
  components: { SearchTool, SearchResult, Editor },
  methods: {
    // 编辑数据
    editData: function (item) {
      this.title = '编辑频道'
      this.$refs.editor.edit(JSON.parse(JSON.stringify(item)))
    },
    // 新增数据
    addData: function (item) {
      this.title = '新增频道'
      this.$refs.editor.add()
    },
    // 新增或者修改后的操作方法
    submitSuccess: function () {
      this.loadData()
    },
    changeParam: function (e) {
      this.params.page = 1
      this.params.name = e.name
      this.loadData()
    },
    changePage: function (e) {
      this.params.page = e.page
      this.loadData()
    },
    async loadData () {
      const res = await loadList({ ...this.params })
      if (res.code === 200) {
        this.list = res.data
        this.host = res.host
        this.total = res.total
      } else {
        this.$message({ type: 'error', message: res.error_message })
      }
    }
  }
}
</script>
