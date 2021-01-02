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
        <!-- TODO: 缺少频道编码和文章数字段 -->
        <!-- <el-table-column label="频道编码">
          <template slot-scope="scope">
            <span></span>
          </template>
        </el-table-column> -->
        <el-table-column label="频道名称">
          <template slot-scope="scope">
            <span>{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <!-- TODO: 添加排序功能 -->
        <!-- <el-table-column label="文章数">
          <template slot-scope="scope">
            <span></span>
          </template>
        </el-table-column> -->
        <el-table-column
          label="描述">
          <template slot-scope="scope">
            <span>{{ scope.row.description }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="是否默认频道">
          <template slot-scope="scope">
            {{ scope.row.isDefault ? '是' : '否'}}
          </template>
        </el-table-column>
        <!-- TODO: 添加小圆圈 -->
        <el-table-column label="状态">
          <template slot-scope="scope">{{ scope.row.status == 1 ? '启动' : '禁用' }}</template>
        </el-table-column>
        <!-- <el-table-column
          label="排序">
          <template slot-scope="scope">
            <span>{{ scope.row.ord }}</span>
          </template>
        </el-table-column>-->
        <!-- <el-table-column
          label="创建时间">
          <template slot-scope="scope">
            <span>{{ dateFormat(scope.row.created_time) }}</span>
          </template>
        </el-table-column>-->
        <el-table-column label="操作" width="240">
          <template slot-scope="scope">
            <el-button
              type="text"
              class="el-button--success-text"
              @click="operateForEditor(scope.row)"
            >编辑</el-button>
            <el-button
              type="text"
              class="el-button--primary-text"
              :disabled="scope.row.status"
              @click="updateChannel(scope.row.id, scope.row.name, true)"
            >启用</el-button>
            <el-button
              type="text"
              class="el-button--danger-text"
              :disabled="!scope.row.status"
              @click="updateChannel(scope.row.id, scope.row.name, false)"
            >禁用</el-button>
            <el-button
              type="text"
              class="el-button--danger-text"
              @click="delChannel(scope.row.id)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>
    <!-- TODO: 当条数大于10条是有问题 -->
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
import { updateData,delData } from '@/api/channel'

export default {
  props: ['list', 'table', 'pageSize', 'total', 'changePage', 'submitSuccess', 'editData'],
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
      return DateUtil.format13(time)
    },
    operateForEditor (item) {
      this.editData(item)
    },
    // TODO: 禁用提示用户
    async updateChannel (id, name, status) {
      const param = {
        id: id,
        name: name,
        status: status
      }
      const res = await updateData(param)

      if (res.code === 0) {
        this.dialogFormVisible = false
        this.submitSuccess()
        this.$message({ type: 'success', message: '操作成功！' })
      } else {
        this.$message({ type: 'error', message: res.error_message })
      }
    },
    async delChannel (id) {
      const res = await delData(id)

      if (res.code === 0) {
        this.dialogFormVisible = false
        this.submitSuccess()
        this.$message({ type: 'success', message: '删除成功！' })
      } else {
        this.$message({ type: 'error', message: res.errorMessage })
      }
    }
  }
}
</script>
