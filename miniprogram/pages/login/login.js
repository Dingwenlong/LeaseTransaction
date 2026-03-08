const api = require('../../utils/api.js')
const { showLoading, hideLoading, showToast } = require('../../utils/util.js')

Page({
  data: {
    username: '',
    password: ''
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value })
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value })
  },

  handleLogin() {
    const { username, password } = this.data
    
    if (!username) {
      showToast('请输入学号/手机号')
      return
    }
    
    if (!password) {
      showToast('请输入密码')
      return
    }

    showLoading('登录中...')

    api.user.login({ username, password })
      .then(res => {
        hideLoading()
        
        const app = getApp()
        app.globalData.token = res.token
        app.globalData.userInfo = res.userInfo
        
        wx.setStorageSync('token', res.token)
        wx.setStorageSync('userInfo', res.userInfo)
        
        showToast('登录成功', 'success')
        
        setTimeout(() => {
          wx.switchTab({
            url: '/pages/index/index'
          })
        }, 1500)
      })
      .catch(err => {
        hideLoading()
        console.error('登录失败:', err)
      })
  }
})
