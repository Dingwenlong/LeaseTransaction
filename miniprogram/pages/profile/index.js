const api = require('../../utils/api.js')
const { showToast, showLoading, hideLoading } = require('../../utils/util.js')

Page({
  data: {
    isLoggedIn: false,
    userInfo: {
      id: '未登录',
      nickname: '微信用户',
      avatar: '',
      creditScore: 100,
      isVerified: 0,
      campus: '',
      department: ''
    },
    stats: {
      renting: 0,
      publishing: 0,
      completed: 0
    },
    wallet: {
      paidAmount: '0.00',
      depositFrozen: '0.00',
      refundedAmount: '0.00'
    }
  },

  onShow() {
    if (!this.hasLogin()) {
      this.resetProfile()
      return
    }
    this.loadProfile()
  },

  hasLogin() {
    return Boolean(wx.getStorageSync('token'))
  },

  resetProfile() {
    this.setData({
      isLoggedIn: false,
      userInfo: {
        id: '未登录',
        nickname: '微信用户',
        avatar: '',
        creditScore: 100,
        isVerified: 0,
        campus: '',
        department: ''
      },
      stats: {
        renting: 0,
        publishing: 0,
        completed: 0
      },
      wallet: {
        paidAmount: '0.00',
        depositFrozen: '0.00',
        refundedAmount: '0.00'
      }
    })
  },

  async loadProfile() {
    showLoading()
    try {
      const [userInfo, orderRes, itemRes, wallet] = await Promise.all([
        api.user.getInfo(),
        api.order.list({ page: 1, size: 50 }),
        api.item.myItems({ page: 1, size: 20 }),
        api.payment.summary()
      ])

      const orders = orderRes.records || []
      const items = itemRes.records || []

      wx.setStorageSync('userInfo', userInfo)
      getApp().globalData.userInfo = userInfo

      this.setData({
        isLoggedIn: true,
        userInfo: {
          ...userInfo
        },
        stats: {
          renting: orders.filter((item) => [2, 3, 4].includes(item.status)).length,
          publishing: items.filter((item) => [0, 1, 2].includes(item.status)).length,
          completed: orders.filter((item) => item.status === 5).length
        },
        wallet: {
          paidAmount: Number(wallet.paidAmount || 0).toFixed(2),
          depositFrozen: Number(wallet.depositFrozen || 0).toFixed(2),
          refundedAmount: Number(wallet.refundedAmount || 0).toFixed(2)
        }
      })
    } catch (error) {
      console.error('加载个人中心失败:', error)
      showToast('个人信息加载失败')
    } finally {
      hideLoading()
    }
  },

  goToOrders() {
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
    wx.navigateTo({
      url: '/pages/profile/items'
    })
  },

  goToWallet() {
    if (!this.data.isLoggedIn) {
      this.goToLogin()
      return
    }
    wx.navigateTo({
      url: '/pages/profile/wallet'
    })
  },

  goToVerify() {
    if (!this.data.isLoggedIn) {
      this.goToLogin()
      return
    }
    wx.navigateTo({
      url: '/pages/verify/index'
    })
  },

  goToMessages() {
    if (!this.data.isLoggedIn) {
      this.goToLogin()
      return
    }
    wx.switchTab({
      url: '/pages/message/index'
    })
  },

  goToHelp() {
    wx.showModal({
      title: '帮助中心',
      content: '建议优先完成校园认证、选择校内当面交接，并在归还验收前保留聊天记录与现场照片。',
      showCancel: false
    })
  },

  goToService() {
    wx.showModal({
      title: '联系客服',
      content: '如需人工介入，请在订单详情页发起申诉，并在工作时间联系平台客服。',
      showCancel: false
    })
  },

  goToAbout() {
    wx.showModal({
      title: '关于平台',
      content: '校园个人物品租赁与交易系统，支持校园认证、租售双模式、订单履约、消息通知与信用评价。',
      showCancel: false
    })
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
        if (!res.confirm) {
          return
        }

        wx.removeStorageSync('token')
        wx.removeStorageSync('userInfo')
        getApp().globalData.token = null
        getApp().globalData.userInfo = null
        this.resetProfile()
        showToast('已退出登录')
      }
    })
  }
})
