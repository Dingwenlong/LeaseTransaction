const api = require('../../utils/api.js')
const { showLoading, hideLoading, showToast } = require('../../utils/util.js')

Page({
  data: {
    mode: 'review',
    order: null,
    existingReview: null,
    ratings: [1, 2, 3, 4, 5],
    form: {
      rating: 5,
      content: '',
      anonymous: false
    }
  },

  onLoad(options) {
    this.orderId = options.id
    this.setData({
      mode: options.mode || 'review'
    })
  },

  onShow() {
    if (!this.orderId) {
      showToast('缺少订单信息')
      return
    }
    this.loadData()
  },

  async loadData() {
    showLoading()
    try {
      const [order, reviews] = await Promise.all([
        api.order.detail(this.orderId),
        api.review.order(this.orderId).catch(() => [])
      ])
      const currentUser = wx.getStorageSync('userInfo') || {}
      const existingReview = (reviews || []).find((item) => item.reviewerId === currentUser.id) || null
      this.setData({
        order: {
          id: order.id,
          orderNo: order.orderNo,
          itemTitle: order.itemTitle,
          statusText: order.statusText,
          remark: order.remark || ''
        },
        existingReview,
        form: {
          ...this.data.form,
          content: this.data.mode === 'dispute' && order.remark ? order.remark : this.data.form.content
        }
      })
    } catch (error) {
      console.error('加载评价页失败:', error)
      showToast('页面加载失败')
    } finally {
      hideLoading()
    }
  },

  onRatingTap(e) {
    this.setData({
      'form.rating': e.currentTarget.dataset.rating
    })
  },

  onInput(e) {
    this.setData({
      'form.content': e.detail.value
    })
  },

  onAnonymousChange(e) {
    this.setData({
      'form.anonymous': e.detail.value.length > 0
    })
  },

  async handleSubmit() {
    if (!this.data.order) {
      return
    }
    if (!this.data.form.content) {
      showToast(this.data.mode === 'review' ? '请填写评价内容' : '请填写申诉原因')
      return
    }

    showLoading('提交中...')
    try {
      if (this.data.mode === 'review') {
        await api.review.submit({
          orderId: this.data.order.id,
          rating: this.data.form.rating,
          content: this.data.form.content,
          isAnonymous: this.data.form.anonymous ? 1 : 0,
          images: ''
        })
      } else {
        await api.order.updateStatus(this.data.order.id, 7, this.data.form.content)
      }
      hideLoading()
      showToast(this.data.mode === 'review' ? '评价提交成功' : '申诉已提交', 'success')
      setTimeout(() => {
        wx.redirectTo({
          url: `/pages/order/detail?id=${this.data.order.id}`
        })
      }, 800)
    } catch (error) {
      hideLoading()
      console.error('提交失败:', error)
    }
  }
})
