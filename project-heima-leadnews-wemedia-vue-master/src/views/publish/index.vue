<template>
  <div class="tinymce-container">
    <el-form ref="form">
      <el-form-item prop="title">
        <el-input
          v-model="FormData.title"
          placeholder="请在这里输入标题"
          maxlength="30"
          show-word-limit
          class="filter-item title"
        />
      </el-form-item>
      <el-form-item>
        <Heima ref="heima" @addImg="selectHeiMaImg" />
      </el-form-item>
      <el-form-item>
        <el-col :span="4">
          <el-form-item label="标签：" prop="labels" label-width="55px">
            <el-input v-model="FormData.labels" placeholder="请输入标签" class="filter-item" />
          </el-form-item>
        </el-col>
        <el-col :span="4">
          <el-form-item label="频道：" prop="channelId" label-width="85px">
            <el-select v-model="FormData.channelId" placeholder="请选择频道">
              <el-option
                v-for="item in channel_list"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="7">
          <el-form-item label="定时：" prop="publishTime" label-width="85px">
            <el-date-picker
              v-model="FormData.publishTime"
              type="datetime"
              placeholder="请选择日期时间"
              default-time="12:00:00"
              style="width:100%;"
            ></el-date-picker>
          </el-form-item>
        </el-col>
      </el-form-item>
      <el-form-item label="封面：" label-width="55px">
        <el-radio-group v-model="FormData.type">
          <el-radio label="1">单图</el-radio>
          <el-radio label="3">三图</el-radio>
          <el-radio label="0">无图</el-radio>
          <el-radio label="-1">自动</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="FormData.type == '1' || FormData.type == '3'">
        <div v-if="FormData.type == '1'" class="single_pic" @click="selectSinglePic">
          <svg v-if="!singlePic" class="icon svg-icon" aria-hidden="true">
            <use xlink:href="#icon_btn_addpic" />
          </svg>
          <span v-if="!singlePic">选择图片</span>
          <img v-if="singlePic" :src="parseImage(singlePic)" />
        </div>
        <div v-if="FormData.type == '3'" class="three_pic">
          <div
            class="three_pic_item"
            v-for="(item,index) in threePicList"
            :key="index"
            @click="selectThreePic(index)"
          >
            <svg v-if="!item" class="icon svg-icon" aria-hidden="true">
              <use xlink:href="#icon_btn_addpic" />
            </svg>
            <span v-if="!item">选择图片</span>
            <img v-if="item" :src="parseImage(item)" />
          </div>
        </div>
      </el-form-item>
      <el-form-item class="btn">
        <el-button type="warning" class="filter-item" @click="publish(true)">
          <svg class="icon svg-icon" aria-hidden="true">
            <use xlink:href="#iconicon_btn_savesketch" />
          </svg>
          存入草稿
        </el-button>
        <el-button type="success" class="filter-item" @click="publish(false)">
          <svg class="icon svg-icon" aria-hidden="true">
            <use xlink:href="#iconicon_btn_tjsh" />
          </svg>
          提交审核
        </el-button>
      </el-form-item>
    </el-form>
    <el-dialog
      width="849px"
      :visible.sync="showPicDialog"
      :close-on-click-modal="false"
      :show-close="false"
      :center="true"
    >
      <el-switch
        v-model="activeName"
        :width="151"
        active-text="本地上传"
        inactive-text="素材库"
        active-color="#F3F4F7"
        inactive-color="#F3F4F7"
      ></el-switch>
      <!-- 素材库 -->
      <!-- TODO: 样式调整，全部和收藏数量 -->
      <div v-show="!activeName">
        <div style="display: flex;">
          <el-tabs tab-position="left" v-model="activeName2" @tab-click="getImgData">
            <el-tab-pane label="全部" name="all"></el-tab-pane>
            <el-tab-pane label="收藏" name="collect"></el-tab-pane>
          </el-tabs>
          <div class="img_list_con">
            <div
              class="img_list"
              v-for="item in imgData"
              :key="item.id"
              @click="selectPic(item.id,item.url)"
            >
              <img :src="item.url" />
              <img
                :src="item.id === selectedImg.id ? selected_img_url : unselected_img_url"
                class="selected"
              />
            </div>
          </div>
        </div>
        <!-- TODO: 调整pagination细节 -->
        <div class="pagination">
          <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :page-size="imgPage.pageSize"
            :total="imgPage.total"
            :page-count="imgPage.pageCount"
            :current-page.sync="imgPage.currentPage"
            @current-change="getImgData"
          ></el-pagination>
        </div>
      </div>
      <!-- 本地上传 -->
      <div v-show="activeName">
        <upload :imgChange="uploadSuccess" />
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="warning" @click="cancleImg">取 消</el-button>
        <el-button type="success" @click="btnOKImg">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import Heima from '@/components/editor/heima.vue'
import Upload from '@/components/Upload/upload.vue'
import { getArticleById } from '@/api/content'
import {
  getAllImgData,
  getChannels,
  publishArticles,
  modifyArticles
} from '@/api/publish'

export default {
  name: 'PublishArticle',
  components: { Upload, Heima },
  data () {
    return {
      FormData: {
        id: '',
        title: '', // 标题
        type: '3',
        labels: '',
        publishTime: '',
        channelId: null// 频道ID
      },
      host: '', // 图片host
      singlePic: null, // 单图模式
      threePicList: [null, null, null], // 三图模式
      pubForm: {},
      channel_list: [],
      showPicDialog: false, // 显示图片上传提示框
      activeName: false,
      activeName2: 'all',
      selected_img_url: require('@/assets/checkbox_sel.png'),
      unselected_img_url: require('@/assets/checkbox_pic_nor.png'),
      imgPage: {
        /** *用来存储页面的页码及行数信息*****/
        total: 0, // 总页数
        currentPage: 1, // 第几页
        pageSize: 5, // 每页多少条
        pageCount: 1 // 共多少页
      },
      imgData: [], // 存储图片的数据
      selectedImg: {}, // 已经选择的图片
      currentType: {
        key: 0, // 编辑序列
        type: '' // 存储弹层的操作类型  single three insert  之所以用对象是因为要存放三张图的索引
      }
    }
  },
  beforeMount () {
    const { articleId } = this.$route.query
    if (articleId) {
      // 如果id存在 则拉取新数据
      this.getArticle(articleId)
    }
    this.getChannels() // 拉取文章频道
  },
  methods: {
    parseImage: function (item) {
      if (item.indexOf('http') > -1) {
        return item
      } else {
        return this.host + item
      }
    },
    // 获取文章频道
    async getChannels () {
      const result = await getChannels()
      this.channel_list = result.data
    },
    // 获取文章
    async getArticle (id) {
      const result = await getArticleById(id)
      this.FormData = {
        id: result.data.id,
        title: result.data.title,
        channelId: result.data.channelId,
        labels: result.data.labels,
        type: '' + result.data.type,
        publishTime: result.data.publishTime
      }
      let conts = []
      if (result.data.content) {
        try {
          conts = eval('(' + result.data.content + ')')
        } catch (e) {
          console.error(e)
        }
      }
      this.$refs.heima.setContent(conts)
      this.host = result.host
      this.transImages(this.FormData.type, result.data.images) // 还原数据
    },
    // 选择一张图片
    selectPic (id, url) {
      this.selectedImg = { id, url }
    },
    // 上传成功后
    uploadSuccess (url) {
      this.selectedImg = { url } // 将上传的图片认为是新组件
    },
    selectHeiMaImg (key) {
      this.currentType.key = key
      this.currentType.type = 'insert'
      this.uploadPic()
    },
    // 点击图片上传图标
    uploadPic () {
      this.imgPage.currentPage = 1
      this.showPicDialog = true // 显示弹层
      this.getImgData() // 拉取图片数据
    },
    // 插入图片 或者替换封面图片
    btnOKImg () {
      if (this.selectedImg.url) {
        if (this.selectedImg.url.indexOf('http') > 0) {
          this.selectedImg.url = this.host + this.selectedImg.url
        }
        if (this.currentType.type === 'single') {
          this.singlePic = this.selectedImg.url
        } else if (this.currentType.type === 'three') {
          // 三张图时  需要找到数组中存储的对象
          this.threePicList[this.currentType.index] = this.selectedImg.url // 找到那条记录更新
          this.$forceUpdate() // 由于直接改变的对象 所以这里强制更新下
        } else if (this.currentType.type === 'insert') {
          this.$refs.heima.saveImage(this.currentType.key, this.selectedImg.url)
        }
      }
      this.currentType = {} // 清空类型缓存
      this.selectedImg = {} // 首先清空选择的缓存
      this.showPicDialog = false // 关闭弹层
    },
    // 取消插入
    cancleImg () {
      this.showPicDialog = false // 关闭弹层
    },
    // 点击三图中的图片
    selectThreePic (index) {
      this.currentType.type = 'three'
      this.currentType.index = index // 这里需要记录图片的索引 因为要按照顺序 不能乱
      this.uploadPic() // 打开弹层
    },
    // 选择单张图片
    selectSinglePic () {
      this.currentType.type = 'single'
      this.uploadPic() // 打开弹层
    },
    // 拉取所有的图片数据
    async getImgData (page) {
      let temp = page === undefined ? this.imgPage.currentPage : page
      try {
        temp = parseInt(temp)
      } catch (e) {
        temp = 1
      }
      const isCollect = this.activeName2 === 'collect' // 是否是收藏的列表
      const result = await getAllImgData({
        size: this.imgPage.pageSize,
        page: temp,
        is_collected: isCollect ? 1 : 0 // 是否是收藏
      })
      this.imgData = result.data
      this.imgPage.total = result.total
      this.imgPage.pageCount = Math.ceil(
        this.imgPage.total / this.imgPage.pageSize
      )
    },
    // 转换图片
    transImages (type, images) {
      images = images.split(',')
      if (type === '1') {
        this.singlePic = images[0]
      } else if (type === '3') {
        this.threePicList = [...images]
      }
    },
    // 获取图片列表
    getImages () {
      if (
        this.FormData.type === '1' ||
        this.FormData.type === '3'
      ) {
        if (this.FormData.type === '1') {
          return this.singlePic ? [this.singlePic] : []
        } else {
          return this.threePicList.map(item => item)
        }
      }
      return []
    },
    // 发布文章
    async publish (draft) {
      const { articleId } = this.$route.query
      const params = { draft } // 路径参数
      const images = this.getImages()
      const data = {
        ...this.FormData,
        images: images,
        status: draft ? 0 : 1,
        content: this.$refs.heima.getContent()
      } // 请求参数
      if (!draft) {
        // 非草稿需要校验
        if (!data.title || data.title.length < 5 || data.title.length > 32) {
          this.$message({
            type: 'warning',
            message: '文章标题不能小于5个字符或大于32个字符'
          })
          return
        }
        if (!data.labels || data.title.length > 20) {
          this.$message({ type: 'warning', message: '内容标签不能为或超过20字符' })
          return
        }
        if (!data.content) {
          this.$message({ type: 'warning', message: '文章内容不能为空' })
          return
        }
        if (!data.channelId) {
          this.$message({ type: 'warning', message: '文章频道不能为空' })
          return
        }
        if (
          (data.type === '1' && data.images.length !== 1) ||
          (data.type === '3' && data.images.length !== 3)
        ) {
          this.$message({ type: 'warning', message: '文章封面未设置' })
          return
        }
        for (let i = 0; i < data.images.length; i++) {
          if (data.images[i] == null || data.images[i] === 'null') {
            this.$message({ type: 'warning', message: '请选择文章封面' })
            return
          }
        }
      }
      // 编辑或者发布文章
      !articleId
        ? await publishArticles(params, data)
        : await modifyArticles(articleId, params, data)
      this.$message({
        type: 'success',
        message: articleId ? '编辑文章成功' : '新增文章成功'
      })
      this.$router.replace({ path: '/article/list' })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/scss/element-variables.scss';

.tinymce-container {
  padding: 30px;
  text-align: left;

  .el-form {
    .title {
      /deep/ .el-input__inner {
        padding: 0;
        padding-bottom: 19px;
        border: none;
        border-bottom: 2px solid #ebeef5;
        border-radius: 0;
        line-height: 26px;
        font-size: 20px;
        color: $--color-text-placeholder;
      }
    }

    // .el-form-item {
    //   margin: 20px 0;
    // }

    .btn {
      border-top: 2px solid #ebeef5;
      // margin: 0 15px;
      // padding: 30px 0;
      padding-top: 31px;
      margin-bottom: 0;
    }
  }
}

.editor-content {
  margin-top: 20px;
}

.img_list {
  width: 155px;
  height: 103px;
  float: left;
  margin: 0px auto;
  border: 1px solid #eee;
  overflow: hidden;
  border-radius: 4px;
  margin: 0px 18px 16px 0;
  position: relative;
}

.img_list_con {
  flex: 1;
  overflow: hidden;
  margin-left: 30px;
  max-height: 340px;
}

.selected {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 32px !important;
  height: 33px !important;
}

.img_list img {
  width: 155px;
  height: 103px;
  display: block;
  cursor: pointer;
}

.pagination {
  text-align: center;
}

.upload_pic_show {
  display: block;
  width: 240px;
  height: 180px;
  margin: 15px auto 10px;
}

.single_pic {
  width: 278px;
  height: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 2px dashed $--border-color-base;
  border-radius: 4px;
  cursor: pointer;
}

.three_pic {
  display: flex;
  flex-direction: row;
  width: 840px;
  height: 180px;
  cursor: pointer;

  .three_pic_item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    border: 2px dashed $--border-color-base;
    border-right: none;
  }

  .three_pic_item:last-child {
    border-right: 2px dashed $--border-color-base;
  }
}

.single_pic:hover, .three_pic_item:hover {
  color: $--color-primary;
}

.single_pic .icon, .three_pic .three_pic_item .icon {
  width: 40px;
  height: 40px;
}

.single_pic span, .three_pic .three_pic_item span {
  line-height: normal;
}

.single_pic img, .three_pic .three_pic_item img {
  width: 278px;
  height: 180px;
  object-fit: cover;
}

/deep/ .el-switch {
  height: auto;
  margin-bottom: 30px;
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
  margin-left: 16px;
  z-index: 9999;
}

/deep/ .el-switch__label--right {
  right: 0;
  margin-right: 10px;
}

/deep/ .el-switch__core {
  height: 40px;
  border-radius: 4px;
}

/deep/ .el-switch__core:after {
  width: 66px;
  height: 32px;
  top: 3px;
  left: 3px;
  box-shadow: 0px 1px 4px 0px rgba(0, 0, 0, 0.06);
  border-radius: 4px;
}

/deep/ .el-switch.is-checked .el-switch__core::after {
  margin-left: 76px;
  left: 3px;
}

/deep/ .el-dialog__footer {
  border-top: 2px solid #EBEEF5;
}
</style>
