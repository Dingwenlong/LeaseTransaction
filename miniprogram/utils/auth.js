const isLoggedIn = () => Boolean(wx.getStorageSync('token'))

const clearSession = () => {
  wx.removeStorageSync('token')
  wx.removeStorageSync('userInfo')
  const app = getApp()
  app.globalData.token = null
  app.globalData.userInfo = null
}

const ensureLogin = () => {
  if (isLoggedIn()) {
    return true
  }
  wx.navigateTo({
    url: '/pages/login/login'
  })
  return false
}

module.exports = {
  isLoggedIn,
  clearSession,
  ensureLogin
}
