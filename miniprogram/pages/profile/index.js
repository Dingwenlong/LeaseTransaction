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
      creditLevel: '优秀',
      isVerified: 0,
      campus: '',
      department: ''
    },
    reviewStats: {
      averageRating: '0.0',
      reviewCount: 0
    },
    reviews: [],
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
        creditLevel: '优秀',
        isVerified: 0,
        campus: '',
        department: ''
      },
      reviewStats: {
        averageRating: '0.0',
        reviewCount: 0
      },
      reviews: [],
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
      const userInfo = await api.user.getInfo()
      const [orderRes, itemRes, wallet, reviews] = await Promise.all([
        api.order.list({ page: 1, size: 50 }),
        api.item.myItems({ page: 1, size: 20 }),
        api.payment.summary(),
        api.review.user(userInfo.id).catch(() => [])
      ])

      const orders = orderRes.records || []
      const items = itemRes.records || []
      const reviewStats = this.buildReviewStats(reviews || [])

      wx.setStorageSync('userInfo', userInfo)
      getApp().globalData.userInfo = userInfo

      this.setData({
        isLoggedIn: true,
        userInfo: {
          ...userInfo,
          creditLevel: this.resolveCreditLevel(userInfo.creditScore)
        },
        reviewStats,
        reviews: (reviews || []).slice(0, 5),
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

  buildReviewStats(reviews) {
    if (!reviews || reviews.length === 0) {
      return {
        averageRating: '0.0',
        reviewCount: 0
      }
    }
    const averageRating = reviews.reduce((sum, item) => sum + Number(item.rating || 0), 0) / reviews.length
    return {
      averageRating: averageRating.toFixed(1),
      reviewCount: reviews.length
    }
  },

  resolveCreditLevel(score) {
    const actualScore = Number(score || 0)
    if (actualScore >= 90) return '优秀'
    if (actualScore >= 70) return '良好'
    if (actualScore >= 50) return '一般'
    return '较差'
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

  goToMyCreditProfile() {
    if (!this.data.isLoggedIn || !this.data.userInfo.id) {
      this.goToLogin()
      return
    }
    wx.navigateTo({
      url: `/pages/profile/user?id=${this.data.userInfo.id}`
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
