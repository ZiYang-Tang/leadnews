import Request from '@/utils/request'
import { API_NEWS_AUTH_LIST, API_NEWS_AUTH_ONE, API_NEWS_AUTH_PASS, API_NEWS_AUTH_FAIL } from '@/constants/api'

export function authList (data) {
  return new Request({
    url: API_NEWS_AUTH_LIST,
    method: 'post',
    data
  })
}

export function authOne (id) {
  return new Request({
    url: API_NEWS_AUTH_ONE + id,
    method: 'get'
  })
}

export function authPass (data) {
  return new Request({
    url: API_NEWS_AUTH_PASS,
    method: 'post',
    data
  })
}

export function authFail (data) {
  return new Request({
    url: API_NEWS_AUTH_FAIL,
    method: 'post',
    data
  })
}
