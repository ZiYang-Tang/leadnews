<template>
  <div>
    <el-upload
      class="uploader"
      action
      :show-file-list="true"
      :auto-upload="false"
      :on-change="handleOnChange">
      <svg v-if="!upload_img_url" class="icon svg-icon" aria-hidden="true">
        <use xlink:href="#icon_btn_addpic" />
      </svg>
      <span v-if="!upload_img_url">选择图片</span>
      <img v-if="upload_img_url" :src="upload_img_url" class="avatar">
    </el-upload>
    <el-button type="success" @click="fnUpload" style="margin-top: 30px;">
      <svg class="icon svg-icon" aria-hidden="true">
        <use xlink:href="#iconicon_btn_tjsh" />
      </svg>
      开始上传
    </el-button>
  </div>
</template>

<script>
import { uploadImg } from '@/api/publish'

export default {
  name: 'upload',
  props: ['imgChange'],
  data () {
    return {
      upload_img_url: '',
      file: [],
      dialogVisible: false
    }
  },
  methods: {
    handleOnChange (file, fileList) {
      this.file = file
    },
    // 上传图片
    async fnUpload () {
      const file = this.file.raw
      const fd = new FormData()
      fd.append('multipartFile', file, file.name)

      const result = await uploadImg(fd)
      if (result.code === 200) {
        this.$message({ message: '上传成功', type: 'success' }) && (this.upload_img_url = result.data.url)
        this.imgChange && this.imgChange(result.data.url) // 调用上层的方法 通知数据变化
      } else {
        this.$message({ message: result.error_message, type: 'error' })
      }
    }
  }
}
</script>

<style lang="scss">
@import '@/scss/element-variables.scss';

.el-dialog--center .el-dialog__body {
  padding: 40px 49px 19px 49px;
}

.uploader {
  text-align: left;

  .el-upload {
    position: relative;
    width: 225px;
    height: 150px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    border: 2px dashed $--border-color-base;
    color: $--color-text-primary;

    .icon {
      width: 40px;
      height: 40px;
    }

    .avatar {
      width: 225px;
      height: 150px;
      object-fit: cover;
    }
  }

  .el-upload:hover {
    color: $--color-primary;
  }

  .el-upload-list__item {
    height: 40px;
    line-height: 40px;
  }

  .el-upload-list__item:hover {
    background-color: #F0F0F0;
  }

  .el-upload-list__item-name {
    margin-left: 14px;
    color: $--color-text-secondary;
  }

  .el-upload-list__item:first-child {
    margin-top: 20px;
  }

  .el-upload-list__item .el-icon-close {
    top: 13px;
    right: 13px;
  }
}
</style>
