const api = require('../../utils/api.js')
const { showLoading, hideLoading, showToast } = require('../../utils/util.js')

const DEFAULT_IMAGE = 'https://dummyimage.com/480x480/0f172a/67e8f9.png&text=Item'

Page({
  data: {
    items: [],
    total: 0
  },

  onShow() {
    if (!this.ensureLogin()) {
      return
    }
    this.loadItems()
  },

  async loadItems() {
    showLoading()
    try {
      const res = await api.item.myItems({
        page: 1,
        size: 20
      })
      this.setData({
        items: (res.records || []).map(this.formatItem),
        total: res.total || 0
      })
    } catch (error) {
      console.error('加载我的发布失败:', error)
      showToast('加载失败')
    } finally {
      hideLoading()
    }
  },

  async handleToggleStatus(e) {
    const { id, status } = e.currentTarget.dataset
    try {
      await api.item.updateStatus(id, status)
      showToast('状态已更新', 'success')
      this.loadItems()
    } catch (error) {
      console.error('更新物品状态失败:', error)
    }
  },

  handleItemTap(e) {
    wx.navigateTo({
      url: '/pages/item/detail?id=' + e.currentTarget.dataset.id
    })
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

  formatItem(item) {
    const nextAction = item.status === 1
      ? { label: '下架', status: 4 }
      : item.status === 4
        ? { label: '重新上架', status: 1 }
        : null

    return {
      id: item.id,
      title: item.title,
      image: item.coverImage || DEFAULT_IMAGE,
      campus: item.campus || '校区待补充',
      typeText: item.typeText,
      typeClass: item.type === 1 ? 'lease' : 'sale',
      price: Number(item.price || 0).toFixed(2),
      statusText: item.statusText,
      reviewHint: item.reviewHint,
      nextAction
    }
  }
})
