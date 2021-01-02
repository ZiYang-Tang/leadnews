<template>
  <div>
    <search-tool :changeParam="changeParam" />
    <search-result
      ref="mySearchResult"
      :list="list"
      :total="total"
      :changePage="changePage"
      :submitSuccess="submitSuccess"
      :pageSize="params.size"
    />
  </div>
</template>

<script>
import SearchTool from './components/SearchTool.vue'
import SearchResult from './components/SearchResult.vue'
import { authList } from '@/api/news_auth'
export default {
  name: 'ChannelManager',
  data () {
    return {
      params: {
        page: 1,
        size: 10,
        title: ''
      },
      total: 0,
      list: []
    }
  },
  mounted () {
    this.loadData()
  },
  components: { SearchTool, SearchResult },
  methods: {
    submitSuccess: function () {
      this.loadData()
    },
    changeParam: function (title) {
      this.params.page = 1
      this.params.title = title
      this.loadData()
    },
    changePage: function (e) {
      this.params.page = e.page
      this.loadData()
    },
    async loadData () {
      const res = await authList({ ...this.params })
      if (res.code === 200) {
        this.list = res.data
        this.total = res.total
      } else {
        this.$message({ type: 'error', message: res.error_message })
      }
    }
  }
}
</script>
