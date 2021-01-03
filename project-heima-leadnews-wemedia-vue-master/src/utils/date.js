function FormatDate () {}
FormatDate.prototype = {
  formatDate: function (date, fmt) {
    if (/(y+)/.test(fmt)) {
      fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length))
    }
    const o = {
      'M+': date.getMonth() + 1,
      'd+': date.getDate(),
      'h+': date.getHours(),
      'm+': date.getMinutes(),
      's+': date.getSeconds()
    }
    for (const k in o) {
      if (new RegExp(`(${k})`).test(fmt)) {
        const str = o[k] + ''
        fmt = fmt.replace(RegExp.$1, RegExp.$1.length === 1 ? str : this.padLeftZero(str))
      }
    }
    return fmt
  },
  padLeftZero: function (str) {
    return ('00' + str).substr(str.length)
  },
  format10: function (time) {
    return this.format13(time * 1000)
  },
  format13: function (time) {
    if (time == undefined) {
      return ''
    }
    const date = new Date(time)
    return this.formatDate(date, 'yyyy-MM-dd')
  },
  format13HH: function (time) {
    if (time == undefined) {
      return ''
    }
    const date = new Date(time)
    return this.formatDate(date, 'yyyy-MM-dd hh:mm:ss')
  },
  // 最近几天时间
  getNearTime: function (AddDayCount) {
    var dd = new Date()
    return dd.getTime() - AddDayCount * 24 * 3600000
  },
  // 最近本周开始时间
  getWeekSTime: function () {
    var dd = new Date()
    dd.setDate(dd.getDate() - dd.getDay())
    return dd.getTime()
  },
  // 最近本周结束时间
  getWeekETime: function () {
    var dd = new Date()
    dd.setDate(dd.getDate() + (7 - dd.getDay()))
    return dd.getTime()
  },
  diffTime: function (time) {
    if (time.length == 10) {
      time = parseInt(time) * 1000
    }
    var nowDate = new Date().getTime()
    var oldDate = new Date(time).getTime()
    var diffTime = parseInt((nowDate - oldDate) / 1000, 10)
    var oneMinute = 60
    var oneHour = 60 * oneMinute
    var oneDay = 24 * oneHour
    var oneMonth = 30 * oneDay
    var oneYear = 12 * oneMonth
    var compareArr = [oneYear, oneMonth, oneDay, oneHour, oneMinute]
    var postfix = ['年前', '个月前', '天前', '个小时前', '分钟前', '1分钟内']
    var diffYear; var diffMonth; var diffDay; var diffHour; var diffMinute; var len = 5
    for (var i = 0; i < len; i++) {
      var diff = Math.floor(diffTime / compareArr[i])
      if (diff > 0) {
        return diff + postfix[i]
      } else if (i === len - 1 && diff === 0) {
        return postfix[len]
      }
    }
  }
}
export default new FormatDate()
