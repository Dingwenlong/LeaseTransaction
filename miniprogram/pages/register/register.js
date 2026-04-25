const api = require('../../utils/api.js')
const { showLoading, hideLoading, showToast } = require('../../utils/util.js')

Page({
  data: {
    username: '',
    password: '',
    confirmPassword: '',
    nickname: ''
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value })
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value })
  },

  onConfirmPasswordInput(e) {
    this.setData({ confirmPassword: e.detail.value })
  },

  onNicknameInput(e) {
    this.setData({ nickname: e.detail.value })
  },

  handleRegister() {
    const { username, password, confirmPassword, nickname } = this.data

    if (!username) {
      showToast('请输入学号/手机号')
      return
    }

    if (!password) {
      showToast('请输入密码')
      return
    }

    if (password.length < 6) {
      showToast('密码长度不能少于6位')
      return
    }

    if (!confirmPassword) {
      showToast('请确认密码')
      return
    }

    if (password !== confirmPassword) {
      showToast('两次密码输入不一致')
      return
    }

    showLoading('注册中...')

    api.user.register({ username, password, confirmPassword, nickname })
      .then(res => {
        hideLoading()

        const app = getApp()
        app.globalData.token = res.token
        app.globalData.userInfo = res.userInfo

        wx.setStorageSync('token', res.token)
        wx.setStorageSync('userInfo', res.userInfo)

        showToast('注册成功', 'success')

        setTimeout(() => {
          wx.switchTab({
            url: '/pages/index/index'
          })
        }, 1500)
      })
      .catch(err => {
        hideLoading()
        console.error('注册失败:', err)
      })
  },

  goToLogin() {
    wx.navigateTo({
      url: '/pages/login/login'
    })
  }
})
