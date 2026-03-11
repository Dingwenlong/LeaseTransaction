const api = require('../../utils/api.js')
const { showLoading, hideLoading, showToast } = require('../../utils/util.js')

const DEFAULT_IMAGE = 'https://dummyimage.com/480x480/0f172a/67e8f9.png&text=Order'

Page({
  data: {
    activeTab: 0,
    tabs: [
      { name: '全部', key: 'all', count: 0 },
      { name: '待付款', key: 'pending', count: 0 },
      { name: '进行中', key: 'active', count: 0 },
      { name: '已完成', key: 'completed', count: 0 },
      { name: '纠纷', key: 'dispute', count: 0 }
    ],
    orders: [],
    displayOrders: []
  },

  onShow() {
    if (!this.ensureLogin()) {
      return
    }
    this.loadOrders()
  },

  onPullDownRefresh() {
    this.loadOrders().finally(() => {
      wx.stopPullDownRefresh()
    })
  },

  onTabTap(e) {
    this.setData({ activeTab: e.currentTarget.dataset.index }, () => {
      this.applyFilter()
    })
  },

  onOrderTap(e) {
    wx.navigateTo({
      url: '/pages/order/detail?id=' + e.currentTarget.dataset.id
    })
  },

  async loadOrders() {
    showLoading()
    try {
      const res = await api.order.list({
        page: 1,
        size: 50
      })
      const orders = (res.records || []).map(this.formatOrder)
      this.setData({ orders })
      this.syncTabs(orders)
      this.applyFilter()
    } catch (error) {
      console.error('加载订单失败:', error)
      showToast('订单加载失败')
    } finally {
      hideLoading()
    }
  },

  applyFilter() {
    const key = this.data.tabs[this.data.activeTab].key
    const displayOrders = this.data.orders.filter((order) => {
      if (key === 'all') {
        return true
      }
      if (key === 'pending') {
        return order.status === 1
      }
      if (key === 'active') {
        return [2, 3, 4].includes(order.status)
      }
      if (key === 'completed') {
        return order.status === 5
      }
      if (key === 'dispute') {
        return [7, 8].includes(order.status)
      }
      return true
    })
    this.setData({ displayOrders })
  },

  syncTabs(orders) {
    const tabs = this.data.tabs.map((tab) => ({ ...tab }))
    tabs[0].count = orders.length
    tabs[1].count = orders.filter((order) => order.status === 1).length
    tabs[2].count = orders.filter((order) => [2, 3, 4].includes(order.status)).length
    tabs[3].count = orders.filter((order) => order.status === 5).length
    tabs[4].count = orders.filter((order) => [7, 8].includes(order.status)).length
    this.setData({ tabs })
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
      type: order.typeText || (order.type === 1 ? '租赁' : '出售'),
      typeClass: order.type === 1 ? 'lease' : 'sale',
      amount: Number(order.totalAmount || 0).toFixed(2),
      status: order.status,
      statusText: order.statusText,
      statusClass: this.getStatusClass(order.status),
      createTime: this.formatDate(order.createdAt)
    }
  },

  getStatusClass(status) {
    if (status === 1) return 'pending'
    if ([2, 3, 4].includes(status)) return 'progress'
    if (status === 5) return 'completed'
    if ([7, 8].includes(status)) return 'dispute'
    return 'cancelled'
  },

  formatDate(value) {
    if (!value) return '-'
    return value.replace('T', ' ').slice(0, 16)
  }
})
