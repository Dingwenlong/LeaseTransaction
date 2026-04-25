const api = require('../../utils/api.js')
const { showLoading, hideLoading, showToast } = require('../../utils/util.js')

Page({
  data: {
    profile: null,
    reviewStats: {
      averageRating: '0.0',
      reviewCount: 0
    },
    reviews: []
  },

  onLoad(options) {
    if (!options.id) {
      showToast('用户不存在')
      return
    }
    this.userId = options.id
  },

  onShow() {
    if (!this.userId) {
      return
    }
    this.loadProfile()
  },

  onPullDownRefresh() {
    this.loadProfile().finally(() => {
      wx.stopPullDownRefresh()
    })
  },

  async loadProfile() {
    showLoading()
    try {
      const [profile, reviews] = await Promise.all([
        api.user.profile(this.userId),
        api.review.user(this.userId).catch(() => [])
      ])

      this.setData({
        profile: {
          ...profile,
          creditLevel: profile.creditLevel || this.resolveCreditLevel(profile.creditScore)
        },
        reviewStats: this.buildReviewStats(reviews || []),
        reviews: reviews || []
      })
    } catch (error) {
      console.error('加载用户信誉主页失败:', error)
      showToast('加载用户主页失败')
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
  }
})
