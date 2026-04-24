const getCurrentEnvVersion = () => {
  try {
    const accountInfo = wx.getAccountInfoSync()
    return accountInfo.miniProgram.envVersion || 'develop'
  } catch (error) {
    return 'develop'
  }
}

const isMockEnabled = () => getCurrentEnvVersion() !== 'release'

const getExtConfig = () => {
  try {
    return wx.getExtConfigSync ? wx.getExtConfigSync() : {}
  } catch (error) {
    return {}
  }
}

const getApiBaseUrl = () => {
  const app = typeof getApp === 'function' ? getApp() : null
  const extConfig = getExtConfig()
  return extConfig.apiBaseUrl
    || (app && app.globalData && app.globalData.apiBaseUrl)
    || 'http://127.0.0.1:8081/api'
}

const getWsUrl = () => {
  const app = typeof getApp === 'function' ? getApp() : null
  const extConfig = getExtConfig()
  return extConfig.wsUrl
    || (app && app.globalData && app.globalData.wsUrl)
    || 'ws://127.0.0.1:8081/ws/websocket'
}

module.exports = {
  getCurrentEnvVersion,
  isMockEnabled,
  getApiBaseUrl,
  getWsUrl
}
