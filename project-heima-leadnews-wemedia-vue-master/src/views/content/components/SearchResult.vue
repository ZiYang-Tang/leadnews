<template>
  <section class="result">
    <div class="content-card">
      <!-- TODO: hover时，按钮渐变动画 -->
      <div v-for="(item, index) in articleList" :key="index" class="item-card">
        <img class="image" :src="getImage(item)" />
        <!-- TODO: 抽出组件，样式细化 -->
        <div class="top">
          <div v-show="item.status == '9'&&item.enable=='0'" class="item" @click="operateBtn(item.id, 'up')">
            <el-tooltip effect="dark" content="上架" placement="top" visible-arrow="false">
              <svg class="icon svg-icon" aria-hidden="true">
                <use xlink:href="#iconbtn_down" />
              </svg>
            </el-tooltip>
          </div>
          <div v-if="item.status == '9'&&item.enable=='1'" class="item" @click="operateBtn(item.id, 'down')">
            <el-tooltip effect="dark" content="下架" placement="top" visible-arrow="false">
              <svg class="icon svg-icon" aria-hidden="true">
                <use xlink:href="#iconbtn_up" />
              </svg>
            </el-tooltip>
          </div>
          <div v-if="item.status != '9' || item.enable == '0' " class="item" @click="operateBtn(item.id, 'modify')">
            <el-tooltip effect="dark" content="编辑" placement="top" visible-arrow="false">
              <svg class="icon svg-icon" aria-hidden="true">
                <use xlink:href="#iconbtn_edit" />
              </svg>
            </el-tooltip>
          </div>
          <div v-if="item.status != '9' || item.enable == '0' " class="item" @click="operateBtn(item.id, 'del')">
            <el-tooltip effect="dark" content="删除" placement="top" visible-arrow="false">
              <svg class="icon svg-icon" aria-hidden="true">
                <use xlink:href="#iconbtn_del" />
              </svg>
            </el-tooltip>
          </div>
        </div>
        <div class="content">
          <div class="center">{{item.title}}</div>
          <div class="bottom">
            <time class="time">{{dateFormat(item.created_time)}}</time>
            <!-- TODO: 不同状态显示不同颜色 -->
            <span class="draft" v-if="item.status == '0'">草稿</span>
            <span class="audit" v-if="item.status == '1'">待审核</span>
            <span class="audit" v-if="item.status == '3'">待人工审核</span>
            <span class="audit" v-if="item.status == '4'">待发布</span>
            <span class="publish" v-if="item.status == '8'">待发布</span>
            <span class="publish" v-if="item.status == '9'">已发表</span>
            <span class="unaudit" v-if="item.status == '2'">未通过审核</span>
            <span class="delete" v-if="item.status == '100'">已删除</span>
            <template v-if="item.status == '9'">
              <span class="draft" v-if="item.enable == '0'">已下架</span>
              <span class="audit" v-if="item.enable == '1'">已上架</span>
            </template>
          </div>
        </div>
      </div>
    </div>
    <el-pagination
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="pageChange"
      @size-change="sizeChange"
      :current-page.sync="listPage.currentPage"
      :page-size="pageSize"
      :total="total"
    ></el-pagination>
  </section>
</template>

<script>
import DateUtil from '@/utils/date'
const avatar = require('@/assets/avatar.jpg')
export default {
  props: ['host', 'articleList', 'pageSize', 'total', 'changePage', 'deleteArticlesById', 'upOrDown'],
  data () {
    return {
      listPage: {
        currentPage: 1
      }
    }
  },
  methods: {
    noAction: function () {
      alert('该功能暂未实现')
    },
    getImage: function (item) {
      if (item.images) {
        const temp = item.images.split(',')
        if (temp.length > 0) {
          return this.host + temp[0]
        }
      }
      return avatar
    },
    // 页码变化 调用上层组件的方法
    pageChange (newPage) {
      this.changePage && this.changePage({ page: newPage })
    },
    sizeChange(val){
      this.changePage && this.changePage({ size: val })
    },
    resetPage () {

    },
    // 重新设置页码
    dateFormat (time) {
      return DateUtil.format13(time)
    },
    // 操作
    operateBtn (Id, type) {
      switch (type) {
        case 'modify':
          this.$router.push({ path: '/article/publish', query: { articleId: Id } })
          break
        case 'down':
          this.upOrDown(Id, 0)
          break
        case 'up':
          this.upOrDown(Id, 1)
          break
        case 'del':
          this.$confirm('此操作将永久删除该文章, 是否继续?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.deleteArticlesById && this.deleteArticlesById(Id) // 删除文件
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '已取消删除'
            })
          })
          break
        default:
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/scss/element-variables.scss';

.content-card {
  display: flex;
  padding-left: 6px;
  padding-bottom: 30px;
  flex-wrap: wrap;

  .item-card {
    position: relative;
    width: 233px;
    height: 315px;
    margin-top: 30px;
    margin-left: 30px;
    border-radius: 8px;
    border: 1px solid $--background-color-base;

    .image {
      display: block;
      width: 100%;
      height: 155px;
      object-fit: cover;
      border-top-left-radius: 8px;
      border-top-right-radius: 8px;
    }

    .top {
      position: absolute;
      top: 21px;
      right: 17px;
      display: none;
      -webkit-transition: .3s;
      transition: .3s;

      .item {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 40px;
        height: 40px;
        margin-left: 16px;
        background: $--color-white;
        border-radius: 50%;
        cursor: pointer;

        .icon {
          width: 30px;
          height: 30px;
        }
      }
    }

    .content {
      padding: 22px 16px 0 17px;

      .center {
        height: 71px;
        color: #20232A;
        font-weight: 600;
      }

      .bottom {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 13px;

        .time {
          font-size: 14px;
          color: $--color-text-secondary;
          line-height: 19px;
        }

        .draft {
          padding: 3px 7px;
          background: #F0F3F9;
          border-radius: 4px;
          color: $--color-text-secondary;
        }

        .audit {
          padding: 3px 7px;
          background: #EEF4FF;
          border-radius: 4px;
          color: $--color-primary;
        }

        .publish {
          padding: 3px 7px;
          background: #EBF7F2;
          border-radius: 4px;
          color: $--color-success;
        }

        .unaudit {
          padding: 3px 7px;
          background: #FFEFEF;
          border-radius: 4px;
          color: $--color-danger;
        }
      }
    }
  }

  .item-card:hover .top {
    display: flex;
  }
}

.el-pagination {
  border-top: 2px solid #ebeef5;
}
</style>
