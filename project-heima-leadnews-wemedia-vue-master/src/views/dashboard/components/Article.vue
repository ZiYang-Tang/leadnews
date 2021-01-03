<template>
  <section class="dashboard-article">
    <header class="header">
      最新图文
      <img @click="noAction" src="@/assets/more.png" class="pointer" />
    </header>
    <!-- TODO: 样式调整 -->
    <ul>
      <li>
        <img class="article-img" src="@/assets/itheima_new.png">
        <dl class="article-content">
          <dt><a @click="noAction" href="#" class="">个税法二审起征点仍为5000元，但有一个新变化</a></dt>
          <dd class="time"><span>已发表</span>2018-08-16 12:20:10</dd>
          <dd>推荐 6   阅读 29   评论 0   转发 0   收藏 6</dd>
        </dl>
      </li>
      <li>
        <img class="article-img" src="@/assets/itheima_new.png">
        <dl class="article-content">
          <dt><a @click="noAction" href="#" class="">个税法二审起征点仍为5000元，但有一个新变化</a></dt>
          <dd class="time"><span>已发表</span>2018-08-16 12:20:10</dd>
          <dd>推荐 6   阅读 29   评论 0   转发 0   收藏 6</dd>
        </dl>
      </li>
    </ul>
  </section>
</template>

<script>

export default {
  props: {

  },
  data () {
    return {
    }
  },
  mounted () {
  },
  methods: {
    noAction: function () {
      alert('该功能暂未实现！')
    }
  }
}
</script>
<style rel="stylesheet/scss" lang="scss" scoped>
  .dashboard-article {
    background-color: #ffffff;
    border: 1px solid #e7e7e9;
    margin-top: 19px;
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      color: #323745;
      font-size: 20px;
      height: 63px;
      padding: 0 15px;
      background-color: #fbfbfb;
      border-bottom: 1px solid #e8e8e8;
    }
    li {
      overflow: hidden;
      padding: 15px 5px;
      margin: 0 10px;
      border-top: 1px solid #f2f3f5;
    }
    li:first-child {
      border: none;
    }
    .article-img {
      float: left;
      width: 210px;
      height: 139px;
    }
    .article-content {
      margin-left: 222px;
      text-align: left;
      dt {
        color: #333333;
        font-size: 14px;
        margin: 7px 0;
      }
      .time {
        margin-bottom: 10px;
        span {
          color:#3296fa;
          margin-right: 8px;
        }
      }
      dd {
        font-size: 12px;
        margin: 5px 0;
        color: #999999;
      }
    }
  }
</style>
