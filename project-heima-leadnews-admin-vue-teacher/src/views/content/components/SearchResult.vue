<template>
  <div>
    <section class="result">
      <el-table
        :data="list"
        :header-cell-style="{textAlign: 'center', fontWeight: '600'}"
        :cell-style="{textAlign: 'center'}"
        highlight-current-row
      >
        <el-table-column label="序号" type="index" width="50"></el-table-column>
        <el-table-column label="文章名称">
          <template slot-scope="scope">
            <span>{{ scope.row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column label="作者">
          <template slot-scope="scope">
            <span>{{ scope.row.author_name }}</span>
          </template>
        </el-table-column>
        <el-table-column label="所属频道">
          <template slot-scope="scope">
            <span>{{ scope.row.channel_name }}</span>
          </template>
        </el-table-column>
        <el-table-column label="评论数">
          <template slot-scope="scope">
            <span>{{ scope.row.collection }}</span>
          </template>
        </el-table-column>
        <el-table-column label="更新时间">
          <template slot-scope="scope">
            <span v-html="dateFormat(scope.row.publish_time).split(' ').join('<br/>')"></span>
          </template>
        </el-table-column>
        <!-- TODO: 后台接口没有返回status字段 -->
        <el-table-column label="状态">
          <template slot-scope="scope">
            <span>{{ scope.row.status === 0 ? '正常' : '已下架' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button
              type="text"
              class="el-button--success-text"
              @click="operateForView(scope.row)"
            >查看</el-button>
            <!-- TODO: 等待上架和下架补接口 -->
            <el-button
              type="text"
              class="el-button--primary-text"
              :disabled="scope.row.status === 0"
              @click="operateForDisable(scope.row.id, 0, scope.$index )"
            >上架</el-button>
            <el-button
              type="text"
              class="el-button--danger-text"
              :disabled="scope.row.status === 1"
              @click="operateForDisable(scope.row.id, 1, scope.$index )"
            >下架</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>
    <el-pagination
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="pageChange"
      :current-page.sync="listPage.currentPage"
      :page-size="pageSize"
      :total="total"
    ></el-pagination>
  </div>
</template>

<script>
import DateUtil from '@/utils/date'
import { down, up } from '@/api/content'
export default {
  props: ['host', 'list', 'fileds', 'table', 'pageSize', 'total', 'changePage', 'changeStatus', 'editData', 'viewData'],
  data () {
    return {
      listPage: {
        currentPage: 1
      },
      id: {
        filed: 'id',
        type: 'eq',
        value: ''
      },
      setForStatus: {
        status: ''
      }
    }
  },
  methods: {
    pageChange (newPage) {
      this.changePage && this.changePage({ page: newPage })
    },
    dateFormat (time) {
      return DateUtil.format13HH(time)
    },
    // TODO: 下架提示用户
    async operateForDisable (id, status, index) {
      this.id.value = id
      const params = { id: id }
      let res
      if (status === 1) res = await down(params)
      else res = await up(params)
      if (res.code === 200) {
        this.changeStatus(index, status)
        this.$message({ type: 'success', message: '操作成功！' })
      } else {
        this.$message({ type: 'error', message: res.error_message })
      }
    },
    operateForView (row) {
      // 由于这次改版不添加接口，所以用这种写法
      // TODO: 跳转页面，左边侧栏状态丢失
      this.$router.push({
        path: '/content/detail',
        query: {
          id: row.id,
          title: row.title,
          author_name: row.author_name,
          publish_time: row.publish_time
        }
      })
    }
  }
}
</script>
