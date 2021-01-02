<template>
  <div>
    <Editor ref="editor" :title="title" :submitSuccess="submitSuccess"/>
    <search-tool :changeParam="changeParam" :addData="addData" />
    <search-result
      ref='mySearchResult'
      :list="list"
      :host="host"
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
import { loadList } from '@/api/sensitive'
import DateUtil from '@/utils/date'
export default {
  name: 'SensitiveIndex',
  data () {
    return {
      params: {
        page: 1,
        size: 10,
        name: ''
      },
      total: 0,
      host: '',
      list: [],
      fileds: [
        {
          label: '敏感词',
          name: 'sensitives',
          type: 'input',
          placeholder: '请输入敏感词',
          rule: [
            { required: true, message: '请输入敏感词', trigger: 'blur' },
            { min: 2, max: 8, message: '敏感词在2~8个字符', trigger: 'blur' }
          ]
        },
        { label: '创建时间', name: 'created_time', type: 'hidden', value: DateUtil.format13HH(new Date().getTime()) }
      ],
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
      this.title = '编辑敏感词'
      this.$refs.editor.edit(JSON.parse(JSON.stringify(item)))
    },
    // 新增数据
    addData: function (item) {
      this.title = '新增敏感词'
      this.$refs.editor.add()
    },
    // 新增或者修改后的操作方法
    submitSuccess: function () {
      this.loadData()
    },
    changeParam: function (name) {
      this.params.page = 1
      this.params.name = name
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
