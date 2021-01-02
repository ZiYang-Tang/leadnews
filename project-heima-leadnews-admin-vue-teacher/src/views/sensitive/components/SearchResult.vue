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
        <el-table-column label="敏感词">
          <template slot-scope="scope">
            <span>{{ scope.row.sensitives }}</span>
          </template>
        </el-table-column>
        <!-- TODO: 后台缺少字段 -->
        <!-- <el-table-column label="权重">
          <template slot-scope="scope">
            <span></span>
          </template>
        </el-table-column>
        <el-table-column label="拦截次数">
          <template slot-scope="scope">
            <span></span>
          </template>
        </el-table-column>-->
        <el-table-column label="创建时间">
          <template slot-scope="scope">
            <span>{{ dateFormat(scope.row.createdTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button
              type="text"
              class="el-button--success-text"
              @click="operateForEditor(scope.row)"
            >编辑</el-button>
            <el-button
              type="text"
              class="el-button--danger-text"
              @click="operateForDelete(scope.row.id)"
            >删除</el-button>
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
import { deleteData } from '@/api/sensitive'
export default {
  props: ['host', 'list', 'table', 'pageSize', 'total', 'changePage', 'editData', 'submitSuccess'],
  data () {
    return {
      listPage: {
        currentPage: 1
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
    operateForEditor (item) {
      this.editData(item)
    },
    async operateForDelete (id) {
      try {
        await this.showDeleteConfirm()
        this.doDelete(id)
      } catch (err) {
        this.$message({ type: 'info', message: '已取消删除' })
      }
    },
    // 删除确认
    // TODO: 按钮hover颜色
    async showDeleteConfirm () {
      return await this.$confirm('删除后敏感词将不可找回，确认将敏感词删除吗？', '删除', {
        confirmButtonText: '删除',
        confirmButtonClass: 'el-button--danger',
        cancelButtonText: '取消',
        cancelButtonClass: 'el-button--warning'
      })
    },
    async doDelete (id) {
      const res = await deleteData(id)
      if (res.code === 0) {
        this.submitSuccess()
        this.$message({ type: 'success', message: '操作成功！' })
      } else {
        this.$message({ type: 'error', message: res.errorMessage })
      }
    }
  }
}
</script>
