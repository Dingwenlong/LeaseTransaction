const api = require('../../utils/api.js')
const { showLoading, hideLoading, showToast } = require('../../utils/util.js')

Page({
  data: {
    campuses: ['东校区', '西校区', '南校区', '北校区'],
    form: {
      studentId: '',
      department: '',
      campus: ''
    }
  },

  onLoad() {
    this.loadConfig()
    this.prefillUserInfo()
  },

  async loadConfig() {
    try {
      const config = await api.config.system()
      this.setData({
        campuses: config.campuses && config.campuses.length ? config.campuses : this.data.campuses
      })
    } catch (error) {
      console.error('加载配置失败:', error)
    }
  },

  prefillUserInfo() {
    const userInfo = wx.getStorageSync('userInfo') || {}
    this.setData({
      'form.studentId': userInfo.studentId || '',
      'form.department': userInfo.department || '',
      'form.campus': userInfo.campus || ''
    })
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({
      [`form.${field}`]: e.detail.value
    })
  },

  onCampusChange(e) {
    this.setData({
      'form.campus': this.data.campuses[e.detail.value]
    })
  },

  async handleSubmit() {
    const { studentId, department, campus } = this.data.form
    if (!studentId || !department || !campus) {
      showToast('请完整填写认证信息')
      return
    }

    showLoading('认证中...')
    try {
      const userInfo = await api.user.verify({ studentId, department, campus })
      hideLoading()
      wx.setStorageSync('userInfo', userInfo)
      getApp().globalData.userInfo = userInfo
      showToast('认证成功', 'success')
      setTimeout(() => {
        wx.navigateBack({
          delta: 1,
          fail: () => {
            wx.switchTab({ url: '/pages/profile/index' })
          }
        })
      }, 1200)
    } catch (error) {
      hideLoading()
      console.error('认证失败:', error)
    }
  },

  handleSkip() {
    wx.navigateBack({
      delta: 1,
      fail: () => {
        wx.switchTab({ url: '/pages/profile/index' })
      }
    })
  }
})
