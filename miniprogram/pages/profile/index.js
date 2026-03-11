const { showToast } = require('../../utils/util.js')

Page({
  data: {
    isLoggedIn: false,
    userInfo: {
      id: '未登录',
      nickname: '微信用户',
      avatar: '',
      creditScore: 100
    },
    stats: {
      renting: 3,
      selling: 5,
      completed: 12
    }
  },

  getDefaultUserInfo() {
    return {
      id: '未登录',
      nickname: '微信用户',
      avatar: '',
      creditScore: 100
    }
  },

  onLoad() {
    this.checkLoginStatus()
  },

  onShow() {
    this.checkLoginStatus()
  },

  checkLoginStatus() {
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')
    
    if (token && userInfo) {
      this.setData({
        isLoggedIn: true,
        userInfo: {
          ...this.getDefaultUserInfo(),
          ...userInfo
        }
      })
    } else {
      this.setData({
        isLoggedIn: false,
        userInfo: this.getDefaultUserInfo()
      })
    }
  },

  goToOrders(e) {
    if (!this.data.isLoggedIn) {
      this.goToLogin()
      return
    }
    wx.switchTab({
      url: '/pages/order/list'
    })
  },

  goToMyItems() {
    if (!this.data.isLoggedIn) {
      this.goToLogin()
      return
    }
    showToast('我的发布功能开发中')
  },

  goToFavorites() {
    if (!this.data.isLoggedIn) {
      this.goToLogin()
      return
    }
    showToast('我的收藏功能开发中')
  },

  goToWallet() {
    if (!this.data.isLoggedIn) {
      this.goToLogin()
      return
    }
    showToast('我的钱包功能开发中')
  },

  goToSettings() {
    showToast('设置功能开发中')
  },

  goToHelp() {
    showToast('帮助中心功能开发中')
  },

  goToAbout() {
    showToast('关于我们功能开发中')
  },

  goToLogin() {
    wx.navigateTo({
      url: '/pages/login/login'
    })
  },

  handleLogout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          
          const app = getApp()
          app.globalData.token = null
          app.globalData.userInfo = null
          
          this.setData({
            isLoggedIn: false,
            userInfo: this.getDefaultUserInfo()
          })
          
          showToast('已退出登录')
        }
      }
    })
  }
})
