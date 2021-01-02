import Vue from 'vue'
import Router from 'vue-router'
import Layout from '@/views/layout/Layout.vue'
Vue.use(Router)
export const asyncRouterMap = [
  {
    path: '/',
    component: Layout,
    redirect: '/login', // 默认子路由
    name: 'mainIndex',
    children: [
      {
        path: '/users/index', // 用户列表
        component: () => import('@/views/users/index.vue')
      },
      {
        path: '/auth/index', // 用户审核
        component: () => import('./views/user_auth/index.vue')
      },
      {
        path: '/auth/realname', // 实名认证
        component: () => import('@/views/user_auth/realname.vue')
      },
      {
        path: '/channel/index', // 频道管理
        component: () => import('@/views/channel/index.vue')
      },
      {
        path: '/content/news_auth', // 媒体审核
        component: () => import('@/views/news_auth/index.vue')
      },
      {
        path: '/content/detail', // 媒体查看
        component: () => import('@/views/news_auth/detail.vue')
      },
      {
        path: '/content/index', // 内容管理
        component: () => import('@/views/content/index.vue')
      },
      {
        path: '/sensitive/index', // 敏感词
        component: () => import('@/views/sensitive/index.vue')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login/index.vue')
  },
  {
    path: '*',
    component: () => import('@/views/404.vue')
  }
]
var myRouter = new Router({
  routes: asyncRouterMap
})
export default myRouter
