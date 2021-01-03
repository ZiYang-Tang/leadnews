<template>
  <section class="dashboard-article">
    <header class="header">
      动态
      <img @click="noAction" src="@/assets/more.png" class="pointer" />
    </header>
    <ul>
      <li>
        <img class="article-img" src="@/assets/avatar1.png">
        <dl class="article-content">
          <dt>林东东 在 <a @click="noAction" href="#" class="">Ant Design Pro</a> 发布了 评论</dt>
          <dd>3天前</dd>
        </dl>
      </li>
      <li>
        <img class="article-img" src="@/assets/avatar2.png">
        <dl class="article-content">
          <dt>周星星 收藏了  <a @click="noAction" href="#" class="">项目管理感悟（五）</a> </dt>
          <dd>3天前</dd>
        </dl>
      </li>
      <li>
        <img class="article-img" src="@/assets/avatar3.png">
        <dl class="article-content">
          <dt>林东东 在 <a @click="noAction" href="#" class="">Ant Design Pro</a> 发布了 评论</dt>
          <dd>3天前</dd>
        </dl>
      </li>
      <li>
        <img class="article-img" src="@/assets/avatar4.png">
        <dl class="article-content">
          <dt>周星星 收藏了  <a @click="noAction" href="#" class="">项目管理感悟（五）</a> </dt>
          <dd>3天前</dd>
        </dl>
      </li>
      <li>
        <img class="article-img" src="@/assets/avatar5.png">
        <dl class="article-content">
          <dt>周星星 收藏了  <a @click="noAction" href="#" class="">项目管理感悟（五）</a> </dt>
          <dd>3天前</dd>
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
      width: 60px;
      height: 60px;
      border-radius: 50%;
    }
    .article-content {
      margin-left: 75px;
      text-align: left;
      dt {
        color: #333333;
        font-size: 14px;
        margin: 2px 0;
        a {
          color: #3296fa;
        }
      }
      dd {
        font-size: 14px;
        margin: 5px 0;
        color: #999999;
      }
    }
  }
</style>
