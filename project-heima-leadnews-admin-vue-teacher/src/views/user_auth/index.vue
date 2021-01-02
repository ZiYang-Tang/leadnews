<template>
  <div>
    <search-tool
      :statusList="statusList"
      :changeParam="searchAuthList"
    />
    <search-result
      ref='mySearchResult'
      :statusList="statusList"
      :authList="authList"
      :host="host"
      :total="total"
      :changePage="searchAuthList"
      :pageSize="params.size"
      :authPassRealName="authPassRealName"
      :authFailRealName="authFailRealName"
    />
  </div>
</template>

<script>
import SearchTool from './components/SearchTool.vue'
import SearchResult from './components/SearchResult.vue'
import { findAuthList, authPass, authFail } from '@/api/user_auth'
export default {
  name: 'AuthManage',
  data () {
    return {
      params: {
        page: 1,
        size: 10
      },
      total: 0,
      host: '',
      statusList: [
        { label: '', value: '全部' },
        { label: 0, value: '创建中' },
        { label: 1, value: '待审核' },
        { label: 2, value: '审核失败' },
        { label: 9, value: '审核通过' }
      ],
      authList: []
    }
  },
  created () {
    this.searchAuthList()
  },
  components: {
    SearchTool,
    SearchResult
  },
  methods: {
    async searchAuthList (newParams) {
      const res = await findAuthList({ ...this.params, ...newParams })
      if (res.code === 200) {
        this.authList = res.data
        this.host = res.host
        this.total = res.total
      } else {
        this.$message({ type: 'error', message: res.error_message })
      }
    },
    async authPassRealName (params) {
      const res = await authPass(params)
      if (res.code === 200) { this.$message({ type: 'success', message: '操作成功' }) } else { this.$message({ type: 'success', message: res.error_message }) }
      this.searchAuthList()
    },
    async authFailRealName (params) {
      await authFail(params)
      this.searchAuthList()
    }
  }
}
</script>
