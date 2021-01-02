<template>
  <div>
    <section class="result">
      <el-table
        :data="authList"
        :header-cell-style="{textAlign: 'center', fontWeight: '600'}"
        :cell-style="{textAlign: 'center'}"
        highlight-current-row
      >
        <el-table-column label="序号" type="index" width="50"></el-table-column>
        <!-- <el-table-column
          label="账号">
          <template slot-scope="scope">
            <span></span>
          </template>
        </el-table-column>-->
        <el-table-column label="姓名">
          <template slot-scope="scope">
            <span>{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column label="身份证号">
          <template slot-scope="scope">
            <span>{{ scope.row.idno }}</span>
          </template>
        </el-table-column>
        <!-- <el-table-column
          label="手机">
          <template slot-scope="scope">
            <span></span>
          </template>
        </el-table-column>
        <el-table-column
          label="认证时间">
          <template slot-scope="scope">
            <span v-html="dateFormat(scope.row.submited_time).split(' ').join('<br/>')"></span>
          </template>
        </el-table-column>-->
        <el-table-column label="认证类型">实名认证</el-table-column>
        <el-table-column label="状态">
          <template slot-scope="scope">
            <span>{{ statusList.find(item => { return scope.row.status === item.label }).value }}</span>
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
              :disabled="scope.row.status !== 1 ? true : false"
              @click="operateForPass(scope.row.id)"
            >通过</el-button>
            <el-button
              type="text"
              class="el-button--danger-text"
              :disabled="scope.row.status !== 1 ? true : false"
              @click="operateForFail(scope.row.id)"
            >驳回</el-button>
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
export default {
  props: ['statusList', 'host', 'authList', 'pageSize', 'total', 'changePage', 'authPassRealName', 'authFailRealName'],
  data () {
    return {
      listPage: {
        currentPage: 1
      },
      params: {
        id: '',
        msg: ''
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
    operateForView (row) {
      this.$router.push({
        path: '/auth/realname',
        query: {
          id: row.id,
          name: row.name,
          idno: row.idno,
          fontImage: row.fontImage,
          backImage: row.backImage,
          holdImage: row.holdImage,
          liveImage: row.liveImage,
          status: row.status,
          reason: row.reason,
          host: this.host
        }
      })
    },
    operateForPass (id) {
      this.params.id = id
      this.authPassRealName(this.params)
    },
    operateForFail (id) {
      this.params.id = id
      this.open('请输入驳回理由')
    },
    open (msg) {
      this.$prompt(undefined, '驳回理由', {
        confirmButtonText: '提交',
        confirmButtonClass: 'el-button--success',
        cancelButtonText: '取消',
        cancelButtonClass: 'el-button--warning',
        inputPlaceholder: msg,
        inputType: 'textarea'
      }).then(({ value }) => {
        // TODO: 驳回理由为空的情况
        this.params.msg = value
        this.authFailRealName(this.params)
      }).catch(() => {
        this.$message({ type: 'info', message: '已取消驳回' })
      })
    }
  }
}
</script>
