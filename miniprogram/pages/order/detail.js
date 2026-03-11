const api = require('../../utils/api.js')
const { showLoading, hideLoading, showToast } = require('../../utils/util.js')

const DEFAULT_IMAGE = 'https://dummyimage.com/480x480/0f172a/67e8f9.png&text=Order'

Page({
  data: {
    order: null,
    statusTagClass: 'mp-tag-cyan',
    priceLabel: '租金',
    actions: [],
    reviews: [],
    hasCurrentReview: false
  },

  onLoad(options) {
    if (!options.id) {
      showToast('订单不存在')
      return
    }
    this.orderId = options.id
  },

  onShow() {
    if (!this.ensureLogin()) {
      return
    }
    this.loadOrder()
  },

  onPullDownRefresh() {
    this.loadOrder().finally(() => {
      wx.stopPullDownRefresh()
    })
  },

  async loadOrder() {
    showLoading()
    try {
      const order = await api.order.detail(this.orderId)
      let reviews = []
      if (order.status === 5) {
        reviews = await api.review.order(this.orderId)
      }
      const currentUser = wx.getStorageSync('userInfo') || {}
      const formattedOrder = this.formatOrder(order)
      const hasCurrentReview = (reviews || []).some((item) => item.reviewerId === currentUser.id)

      this.setData({
        order: formattedOrder,
        reviews: reviews || [],
        hasCurrentReview
      })
      this.syncViewState(formattedOrder, hasCurrentReview)
    } catch (error) {
      console.error('加载订单详情失败:', error)
      showToast('加载订单失败')
    } finally {
      hideLoading()
    }
  },

  syncViewState(order, hasCurrentReview) {
    const actions = []
    if (order.status === 1) {
      actions.push({ key: 'cancel', label: '取消订单', primary: false })
      actions.push({ key: 'pay', label: '立即付款', primary: true })
    } else if (order.status === 2) {
      actions.push({ key: 'contact', label: '联系对方', primary: true })
    } else if (order.status === 3) {
      actions.push({ key: 'contact', label: '联系对方', primary: false })
      actions.push({
        key: order.type === 1 ? 'return' : 'confirm',
        label: order.type === 1 ? '发起归还' : '确认收货',
        primary: true
      })
    } else if (order.status === 4) {
      actions.push({ key: 'contact', label: '联系对方', primary: false })
      actions.push({ key: 'dispute', label: '申请申诉', primary: true })
    } else if (order.status === 5) {
      actions.push({
        key: 'review',
        label: hasCurrentReview ? '查看评价' : '提交评价',
        primary: true
      })
    } else if (order.status === 7 || order.status === 8) {
      actions.push({ key: 'review', label: '查看申诉', primary: true })
    }

    this.setData({
      statusTagClass: this.getStatusTagClass(order.status),
      priceLabel: order.type === 1 ? '租金' : '商品价格',
      actions
    })
  },

  async handleAction(e) {
    const action = e.currentTarget.dataset.action
    const order = this.data.order
    if (!order) {
      return
    }

    if (action === 'cancel') {
      await this.updateStatus(6, '用户取消订单')
      return
    }
    if (action === 'pay') {
      await this.handlePay()
      return
    }
    if (action === 'contact') {
      await this.handleContact()
      return
    }
    if (action === 'return') {
      await this.updateStatus(4, '承租人发起归还验收')
      return
    }
    if (action === 'confirm') {
      await this.updateStatus(5, '买家确认收货，订单完成')
      return
    }
    if (action === 'review') {
      wx.navigateTo({
        url: `/pages/order/review?id=${order.id}&mode=${order.status === 7 || order.status === 8 ? 'dispute' : 'review'}`
      })
      return
    }
    if (action === 'dispute') {
      wx.navigateTo({
        url: `/pages/order/review?id=${order.id}&mode=dispute`
      })
    }
  },

  async handlePay() {
    showLoading('支付中...')
    try {
      await api.payment.createPayment(this.data.order.id)
      hideLoading()
      showToast('支付已提交', 'success')
      this.loadOrder()
    } catch (error) {
      hideLoading()
      console.error('支付失败:', error)
    }
  },

  async handleContact() {
    const order = this.data.order
    const userInfo = wx.getStorageSync('userInfo') || {}
    if (!order) {
      return
    }
    const receiverId = userInfo.id === order.buyerId ? order.sellerId : order.buyerId
    showLoading('发送中...')
    try {
      await api.message.send({
        receiverId,
        type: 1,
        content: `你好，我想沟通订单 ${order.orderNo} 的交接安排。`
      })
      hideLoading()
      showToast('消息已发送', 'success')
      setTimeout(() => {
        wx.switchTab({
          url: '/pages/message/index'
        })
      }, 800)
    } catch (error) {
      hideLoading()
      console.error('发送消息失败:', error)
    }
  },

  async updateStatus(status, remark) {
    showLoading('处理中...')
    try {
      await api.order.updateStatus(this.data.order.id, status, remark)
      hideLoading()
      showToast('订单已更新', 'success')
      this.loadOrder()
    } catch (error) {
      hideLoading()
      console.error('更新订单状态失败:', error)
    }
  },

  ensureLogin() {
    const token = wx.getStorageSync('token')
    if (token) {
      return true
    }
    wx.navigateTo({
      url: '/pages/login/login'
    })
    return false
  },

  formatOrder(order) {
    return {
      id: order.id,
      orderNo: order.orderNo,
      itemTitle: order.itemTitle,
      itemImage: order.itemImage || DEFAULT_IMAGE,
      type: order.type,
      typeText: order.typeText,
      typeClass: order.type === 1 ? 'lease' : 'sale',
      amount: Number(order.amount || 0).toFixed(2),
      deposit: Number(order.deposit || 0).toFixed(2),
      totalAmount: Number(order.totalAmount || 0).toFixed(2),
      rentalDays: order.rentalDays || 0,
      startDate: this.formatDate(order.startDate),
      endDate: this.formatDate(order.endDate),
      deliveryMethod: order.deliveryMethod || '校内面交',
      remark: order.remark || '暂无备注',
      status: order.status,
      statusText: order.statusText,
      statusDesc: this.getStatusDesc(order),
      createTime: this.formatDate(order.createdAt),
      buyerId: order.buyerId,
      sellerId: order.sellerId
    }
  },

  getStatusTagClass(status) {
    if (status === 1) return 'mp-tag-yellow'
    if (status === 5) return 'mp-tag-green'
    if (status === 7 || status === 8) return 'mp-tag-magenta'
    if (status === 6) return 'mp-tag-slate'
    return 'mp-tag-cyan'
  },

  getStatusDesc(order) {
    if (order.status === 1) return '订单已创建，请尽快完成支付。'
    if (order.status === 2) return '支付完成，等待线下交付或确认交接。'
    if (order.status === 3) return order.type === 1 ? '租赁履约中，请按时归还物品。' : '交易履约中，确认收货后即可完结。'
    if (order.status === 4) return '已进入归还验收阶段，可补充申诉说明。'
    if (order.status === 5) return '订单已完成，可以提交评价记录。'
    if (order.status === 6) return '订单已取消，相关流程已终止。'
    if (order.status === 7) return '平台已收到申诉，正在进入仲裁。'
    if (order.status === 8) return '退款流程处理中，请留意后续通知。'
    return '订单状态同步中。'
  },

  formatDate(value) {
    if (!value) return '-'
    return value.replace('T', ' ').slice(0, 16)
  }
})
