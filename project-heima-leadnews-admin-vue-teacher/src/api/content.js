import Request from '@/utils/request'
import { API_ARTICLE_UP, API_ARTICLE_DOWN } from '@/constants/api'

// 下架
export function down (data) {
  return Request({
    url: API_ARTICLE_DOWN,
    method: 'post',
    data,
    params: {}
  })
}

// 上架
export function up (data) {
  return Request({
    url: API_ARTICLE_UP,
    method: 'post',
    data,
    params: {}
  })
}
