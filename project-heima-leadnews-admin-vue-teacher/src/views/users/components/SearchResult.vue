<template>
  <div>
    <section class="result">
      <el-table
        :data="list"
        :header-cell-style="{textAlign: 'center', fontWeight: '600'}"
        :cell-style="{textAlign: 'center'}"
        highlight-current-row>
        <el-table-column
          label="序号"
          type="index"
          width="50">
        </el-table-column>
        <!-- TODO: 修改table字段 -->
        <!-- TODO: 添加小圆圈 -->
        <el-table-column
          label="账号">
          <template slot-scope="scope">
            <span>{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="姓名">
          <template slot-scope="scope">
            <span></span>
          </template>
        </el-table-column>
        <el-table-column
          label="邮箱">
          <template slot-scope="scope">
            <span></span>
          </template>
        </el-table-column>
        <el-table-column
          label="手机">
          <template slot-scope="scope">
            <span>{{ scope.row.phone }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="最后活动时间">
          <template slot-scope="scope">
            <span></span>
          </template>
        </el-table-column>
        <el-table-column
          label="实名认证">
          <template slot-scope="scope">
            <span>{{ scope.row.is_certification ? '是' : '否'}}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="身份认证">
          <template slot-scope="scope">
            <span>{{ scope.row.is_identity_authentication ? '是' : '否'}}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="账号状态">
          <template slot-scope="scope">
            <span>{{ scope.row.status ? '冻结' : '正常'}}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
              type="text"
              v-if="scope.row.status == 1"
              class="el-button--primary-text"
              @click="operateForConfirm(scope.row.id,0,scope.$index )">启用</el-button>
            <el-button
              type="text"
              v-else
              class="el-button--danger-text"
              @click="operateForConfirm(scope.row.id,1,scope.$index )">冻结</el-button>
            <el-button
              type="text"
              class="el-button--success-text"
              @click="operateForView(scope.row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>
    <!-- TODO: 样式还原 -->
    <el-pagination
      layout="total, sizes, prev, pager, next, jumper"
      @current-change='pageChange'
      :current-page.sync='listPage.currentPage'
      :page-size="pageSize"
      :total="total">
    </el-pagination>
  </div>
</template>

<script>
import DateUtil from '@/utils/date'
import { updateData } from '@/api/common'
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
    // 冻结确认
    // TODO: 按钮hover颜色
    async showFreezeConfirm () {
      return await this.$confirm('冻结用户无法登陆进行操作，是否确认冻结该用户？', '冻结确认', {
        confirmButtonText: '冻结',
        confirmButtonClass: 'el-button--danger',
        cancelButtonText: '取消',
        cancelButtonClass: 'el-button--warning'
      })
    },
    async operateForConfirm (id, status, index) {
      if (status === 1) {
        try {
          await this.showFreezeConfirm()
          await this.operateForDisable(id, status, index)
        } catch (err) {
          this.$message({ type: 'info', message: '已取消冻结' })
        }
      } else {
        await this.operateForDisable(id, status, index)
      }
    },
    async operateForDisable (id, status, index) {
      this.id.value = id

      const params = {
        name: this.table,
        where: [this.id],
        sets: [{ filed: 'status', value: status }]
      }
      const res = await updateData(params)
      if (res.code === 200) {
        this.changeStatus(index, status)
        this.$message({ type: 'success', message: '操作成功！' })
      } else {
        this.$message({ type: 'error', message: res.errorMessage })
      }
    },
    operateForView (item) {
      this.viewData(item)
    },
    open (msg) {
      this.$prompt(msg, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        this.params.msg = value
        this.updateData(this.params)
      }).catch(() => {

      })
    }
  }
}
</script>
