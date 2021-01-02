<template>
  <section class="result">
    <el-table
      :data="list"
      :header-cell-style="{textAlign: 'center', fontWeight: '600'}"
      :cell-style="{textAlign: 'center'}"
      highlight-current-row
    >
      <el-table-column label="序号" type="index" width="50"></el-table-column>
      <el-table-column label="标题">
        <template slot-scope="scope">
          <span>{{ scope.row.title }}</span>
        </template>
      </el-table-column>
      <el-table-column label="作者">
        <template slot-scope="scope">
          <span>{{ scope.row.userId }}</span>
        </template>
      </el-table-column>
      <el-table-column label="类型">
        <template slot-scope="scope">
          <span>{{ typeList.find((item) => { return item.value === scope.row.type }).label }}</span>
        </template>
      </el-table-column>
      <el-table-column label="标签">
        <template slot-scope="scope">
          <span>{{ scope.row.labels }}</span>
        </template>
      </el-table-column>
      <el-table-column label="定时时间">
        <template slot-scope="scope">
          <span v-html="dateFormat(scope.row.publishTime).split(' ').join('<br/>')"></span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间">
        <template slot-scope="scope">
          <span v-html="dateFormat(scope.row.createdTime).split(' ').join('<br/>')"></span>
        </template>
      </el-table-column>
      <el-table-column label="提交时间">
        <template slot-scope="scope">
          <span v-html="dateFormat(scope.row.submitedTime).split(' ').join('<br/>')"></span>
        </template>
      </el-table-column>
      <el-table-column label="状态">
        <template slot-scope="scope">
          <span>{{ statusList.find((item) => { return item.value === scope.row.status }).label }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button
            type="text"
            class="el-button--success-text"
            @click="operateForView(scope.row)"
          >查看</el-button>
          <el-button
            type="text"
            class="el-button--primary-text"
            :disabled="scope.row.status !== 3 ? true : false"
            @click="operateForPass(scope.row.id)"
          >通过</el-button>
          <el-button
            type="text"
            class="el-button--danger-text"
            :disabled="scope.row.status !== 3 ? true : false"
            @click="operateForFail(scope.row.id)"
          >驳回</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="pageChange"
        :current-page.sync="listPage.currentPage"
        :page-size="pageSize"
        :total="total"
      ></el-pagination>
    </div>
  </section>
</template>

<script>
import DateUtil from '@/utils/date'
import { authPass, authFail } from '@/api/news_auth'
export default {
  props: ['list', 'pageSize', 'total', 'changePage', 'submitSuccess'],
  data () {
    return {
      listPage: {
        currentPage: 1
      },
      typeList: [
        {
          value: 0,
          label: '无图文章'
        },
        {
          value: 1,
          label: '单图文章'
        },
        {
          value: 3,
          label: '多图文章'
        }
      ],
      statusList: [
        {
          value: 0,
          label: '草稿'
        },
        {
          value: 1,
          label: '提交（待审核）'
        },
        {
          value: 2,
          label: '审核失败'
        },
        {
          value: 3,
          label: '人工审核'
        },
        {
          value: 4,
          label: '人工审核通过'
        },
        {
          value: 8,
          label: '审核通过（待发布）'
        },
        {
          value: 9,
          label: '已发布'
        }
      ]
    }
  },
  methods: {
    pageChange (newPage) {
      this.changePage && this.changePage({ page: newPage })
    },
    dateFormat (time) {
      return DateUtil.format13HH(time)
    },
    operateForView (row) {
      // TODO: 跳转页面，左边侧栏状态丢失
      this.$router.push({
        path: '/content/detail',
        query: {
          id: row.id
        }
      })
    },
    async operateForPass (id) {
      const params = {
        id: id,
        status: 4
      }
      const res = await authPass(params)
      if (res.code === 0) {
        this.submitSuccess()
        this.$message({ type: 'success', message: '操作成功！' })
      } else {
        this.$message({ type: 'error', message: res.errorMessage })
      }
    },
    operateForFail (id) {
      const params = {
        id: id,
        status: 2
      }
      this.open('请输入驳回理由', params)
    },
    open (msg, params) {
      this.$prompt(undefined, '驳回理由', {
        confirmButtonText: '提交',
        confirmButtonClass: 'el-button--success',
        cancelButtonText: '取消',
        cancelButtonClass: 'el-button--warning',
        inputPlaceholder: msg,
        inputType: 'textarea'
      }).then(async ({ value }) => {
        // TODO: 驳回理由为空的情况
        params.msg = value

        const res = await authFail(params)
        if (res.code === 0) {
          this.submitSuccess()
          this.$message({ type: 'success', message: '操作成功！' })
        } else {
          this.$message({ type: 'error', message: res.errorMessage })
        }
      }).catch(() => {
        this.$message({ type: 'info', message: '已取消驳回' })
      })
    }
  }
}
</script>
