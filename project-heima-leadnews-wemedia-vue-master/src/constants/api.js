export const API_GETPHONECODE = 'sms/codes/' // 获取短信验证码
export const API_USERAUTH = '/wemedia/login/in' // 用户认证
export const API_CAPTCHAS = 'captchas/' // 人机验证码
export const API_USERIMAGES_LIST = '/wemedia/api/v1/material/list' // 用户图片素材列表
export const API_USERIMAGES_ADD = '/wemedia/api/v1/material/upload_picture' // 用户图片素材列表
export const API_CHANNELS = '/admin/api/v1/channel/channels' // 获取文章频道
export const API_ARTICLES = '/wemedia/api/v1/news/submit' // post文章(新建)  get拉取文章列表
export const API_SEARCHARTICELS = '/wemedia/api/v1/news/list' // 检索文章
export const API_ARTICLES_UPDOWN = '/wemedia/api/v1/news/down_or_up' // 上下架
export const API_ARTICLES_DELETE = '/wemedia/api/v1/news/del_news' // 删除文章API_MODIFYIMAGE_DELETE
export const API_ARTICLES_INFO = '/wemedia/api/v1/news/one' // 获取文章
export const API_COMMENTS = 'comments' // 获取评论或者评论回复
export const API_COMMENT_LIST = '/api/v1/comment/list' // 获取评论或者评论回复

export const API_CLOSECOMMENTS = 'comments/status' // 关闭或者打开评论
export const API_ADMIRECOMMENT = 'comment/likings' // 对评论或回复点赞
export const API_CANCELADMIRECOMMENT = 'comment/likings/' // 对评论或回复取消点赞
export const API_MODIFYIMAGE_COL = '/wemedia/api/v1/material/collect' // 收藏用户素材 或 修改收藏状态接口
export const API_MODIFYIMAGE_COL_CANCEL = '/wemedia/api/v1/material/cancel_collect' // 取消用户素材 或 修改收藏状态接口
export const API_MODIFYIMAGE_DELETE = '/wemedia/api/v1/material/del_picture' // 删除图片
export const API_USERPROFILE = 'user/profile' // 获取用户个人资料
export const API_USERUPDATEPROFILE = 'user/updateprofile' // 修改用户个人资料
export const API_FANS = '/api/v1/user_fans/list' // 粉丝列表
export const API_FOLLOWER_PORTRAIT = '/api/v1/user_fans/fans_portrait' // 获取粉丝性别同级数据
export const API_HEAD = 'user/photo' // 编辑用户信息
export const API_FANS_AVATAR = '/api/v1/user_fans/avatar' // 获取粉丝头像
export const API_CHANGE_FOLLOW_STATE = '/api/v1/user_fans/change_follow_state' // 改变粉丝关注状态
export const API_GET_FANS_STATISTIC = '/api/v1/statistics/fans' // 粉丝统计数据

export const API_STATISTICS_NEWS = '/api/v1/statistics/news' // 图文统计
export const API_STATISTICS_PORTRAIT = '/api/v1/statistics/portrait' // 画像统计
