import Vue from 'vue'
import App from './App.vue'
import './permission' // permission control

import router from './router'

import './style/theme/index.css'
import Element from 'element-ui'

import './styles/index.scss' // global css
import Meta from 'vue-meta'
import '@/assets/iconfont/iconfont.js'
import '@/assets/iconfont/icon.css'
import 'font-awesome/css/font-awesome.min.css'

Vue.use(Meta)
Vue.config.productionTip = false
Vue.use(Element)

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
