import request from '@/utils/request'
import { setUser } from '@/utils/store'
import { API_USERAUTH } from '@/constants/api'

export function loginByUsername (name, password) {
  const data = {
    name, password
  }
  return request({
    url: API_USERAUTH,
    method: 'post',
    data
  }).then(result => {
    if (result.code === 200) {
      const temp = result.data
      setUser({ name: temp.user.name, photo: null, token: temp.token }) // 设置用户的个人数据
    }
    return result
  })
}
