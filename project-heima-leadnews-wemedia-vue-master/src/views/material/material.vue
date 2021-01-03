<template>
  <div>
    <div class="filter">
      <div class="filter-container">
        <el-switch
          v-model="activeSelect"
          :width="138"
          active-value="1"
          inactive-value="0"
          active-text="收藏"
          inactive-text="全部"
          active-color="#F3F4F7"
          inactive-color="#F3F4F7"
          @change="loadData"
        ></el-switch>
      </div>
      <div>
        <span class="total">已上传{{ imgPage.total }}张图片</span>
        <el-button type="success" @click="showPicDialog = true">
          <svg class="icon svg-icon" aria-hidden="true">
            <use xlink:href="#iconicon_btn_tjsh" />
          </svg>
          上传图片
        </el-button>
      </div>
    </div>
    <div class="content-card">
      <el-card v-for="img in imgData" :key="img.id" :body-style="{ padding: '0px' }" shadow="hover">
        <img class="image" :class="{'collection': activeSelect == '1'}" :src="img.url" />
        <div v-if="activeSelect == '0'" class="operate">
          <div class="item" @click="collectOrCancel(img)">
            <svg class="icon svg-icon" aria-hidden="true">
              <use :xlink:href="img.isCollection ? '#iconbtn_collect_sel' : '#iconbtn_collect'" />
            </svg>
            {{ img.isCollection ? '已收藏' : '收藏' }}
          </div>
          <div class="item" @click="delImg(img)">
            <svg class="icon svg-icon" aria-hidden="true">
              <use xlink:href="#iconbtn_del" />
            </svg>删除
          </div>
        </div>
      </el-card>
    </div>
    <el-pagination
      layout="total, sizes, prev, pager, next, jumper"
      :total="imgPage.total"
      :page-count="imgPage.pageCount"
      :page-size="imgPage.pageSize"
      :current-page="imgPage.currentPage"
      @current-change="pageChange"
      @size-change="sizeChange"
    ></el-pagination>
    <el-dialog
      width="849px"
      center
      :visible.sync="showPicDialog"
      :show-close="false"
      :before-close="closeModal"
    >
      <upload v-if="showPicDialog" :imgChange="imgChangeCall" />
      <div slot="footer" class="dialog-footer">
        <el-button type="warning" @click="closeModal">取消</el-button>
        <el-button type="success" @click="closeModal">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { getAllImgData, delImg, collectOrCancel } from '@/api/publish'
import Upload from '@/components/Upload/upload.vue'
export default {
  name: 'material',
  data () {
    return {
      imgPage: {
        total: 0,
        currentPage: 1,
        pageCount: 0,
        pageSize: 20
      },
      imgChange: false, // 是否上传过图片导致图片数据变化 此状态用来控制是否在关闭后要进行重新加载
      showPicDialog: false,
      activeSelect: '0',
      imgData: []// 存储图片的数据 同时作为收藏数据和全部数据的引用
    }
  },
  components: {
    Upload
  },
  mounted () {
    this.loadData()
  },
  methods: {
    loadData: function () {
      // 初始化时加载数据
      this.getImgData({
        page: this.imgPage.currentPage,
        size: this.imgPage.pageSize,
        isCollection: this.activeSelect
      })
    },
    // 页面发生变化
    pageChange (newPage) {
      this.imgPage.currentPage = newPage
      this.loadData()
    },
    sizeChange(val){
      this.imgPage.pageSize=val
      this.loadData()
    },
    // 获取图片素材
    async getImgData (params) {
      const result = await getAllImgData(params)
      this.imgData = result.data
      this.imgPage.total = result.total
      this.imgPage.pageCount = Math.ceil(this.imgPage.total / this.imgPage.pageSize)
    },
    // 取消或者收藏图片
    async collectOrCancel (img) {
      let isCollected = img.isCollection
      if (isCollected === 1) { isCollected = 0 } else { isCollected = 1 }
      // 取相反状态
      const result = await collectOrCancel(img.id, { isCollected: isCollected })
      if (result.code === 200) {
        img.isCollection = isCollected // 取相反状态
        this.$forceUpdate() // 强制更新
        this.$message({ type: 'success', message: '操作成功' })
      } else {
        this.$message({ type: 'error', message: result.errorMessage })
      }
    },
    // 删除图片
    async delImg (img) {
      let result = await this.$confirm('确认删除该素材?')
      if (result) {
        result = await delImg(img.id)// 删除数据
        if (result.code === 200) {
          // 写多了if  else 写个三元表达式 换换口味
          this.$message({ type: 'success', message: '删除成功' })
          this.loadData()
        } else {
          this.$message({ type: 'error', message: result.error_message })
        }
      }
    },
    imgChangeCall () {
      // 图片变化了 记录改变的状态 用于关闭弹层时 重新加载数据
      this.imgChange = true
    },
    // 关闭弹层时触发
    // 注意 这里 为什么不在click用表达式赋值的方式去关掉弹层呢
    // 因为发现在click="dialog = false" 模式下 不能触发关闭的回调 应该是实现机制的顺序问题
    closeModal () {
      if (this.imgChange) {
        this.loadData()
        this.imgChange = false
      }
      this.showPicDialog = false
    }
  }
}
</script>
<style lang="scss" scoped>
@import '@/scss/element-variables.scss';

.filter {
  height: 96px;
}

/deep/ .el-switch {
  height: auto;
}

/deep/ .el-switch__label {
  position: absolute;
  color: $--color-text-secondary;
}

/deep/ .el-switch__label.is-active {
  color: $--color-text-primary;
  font-weight: 600;
}

/deep/ .el-switch__label--left {
  left: 0;
  margin-left: 22px;
  z-index: 9999;
}

/deep/ .el-switch__label--right {
  right: 0;
  margin-right: 23px;
}

/deep/ .el-switch__core {
  height: 40px;
  border-radius: 4px;
}

/deep/ .el-switch__core:after {
  width: 63px;
  height: 32px;
  top: 3px;
  left: 3px;
  box-shadow: 0px 1px 4px 0px rgba(0, 0, 0, 0.06);
  border-radius: 4px;
}

/deep/ .el-switch.is-checked .el-switch__core::after {
  margin-left: 65px;
  left: 3px;
}

.total {
  margin-right: 16px;
  color: $--color-text-secondary;
}

.content-card {
  display: flex;
  padding-left: 6px;
  padding-bottom: 30px;
  flex-wrap: wrap;

  .el-card {
    position: relative;
    width: 186px;
    height: 183px;
    margin-top: 30px;
    margin-left: 24px;
    border-radius: 8px;
    border: 1px solid $--background-color-base;

    .image {
      display: block;
      width: 100%;
      height: 124px;
      object-fit: cover;
    }

    .collection {
      height: 183px;
    }

    .operate {
      display: flex;
      height: 57px;
      justify-content: space-around;
      align-items: center;

      .item {
        display: flex;
        align-items: center;
        font-size: 12px;
        cursor: pointer;

        .icon {
          width: 30px;
          height: 30px;
        }
      }
    }
  }

  .el-card.is-always-shadow,
  .el-card.is-hover-shadow:focus,
  .el-card.is-hover-shadow:hover {
    -webkit-box-shadow: 0px 2px 8px 4px rgba(51, 53, 58, 0.07);
    box-shadow: 0px 2px 8px 4px rgba(51, 53, 58, 0.07);
  }
}

.el-pagination {
  border-top: 2px solid #EBEEF5;
}

/deep/ .el-dialog__footer {
  border-top: 2px solid #EBEEF5;
}
</style>
