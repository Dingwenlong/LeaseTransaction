const api = require('../../utils/api.js')
const auth = require('../../utils/auth.js')
const homeMock = require('../../mock/home.js')
const { showLoading, hideLoading, showToast } = require('../../utils/util.js')

const DEFAULT_IMAGE = 'https://dummyimage.com/640x640/0f172a/67e8f9.png&text=Campus+Lease'

Page({
  data: {
    item: null,
    isOwner: false
  },

  onLoad(options) {
    if (!options.id) {
      showToast('物品不存在')
      return
    }
    this.itemId = options.id
    this.loadItem()
  },

  onPullDownRefresh() {
    this.loadItem().finally(() => {
      wx.stopPullDownRefresh()
    })
  },

  async loadItem() {
    showLoading()
    try {
      if (homeMock.isEnabled()) {
        const mockItem = homeMock.getMockItemDetail(this.itemId)
        if (mockItem) {
          this.setItemData(mockItem)
          return
        }
      }

      const detail = await api.item.detail(this.itemId)
      this.setItemData(detail)
    } catch (error) {
      const mockItem = homeMock.getMockItemDetail(this.itemId)
      if (mockItem) {
        this.setItemData(mockItem)
        return
      }

      console.error('加载物品详情失败:', error)
      showToast('加载详情失败')
    } finally {
      hideLoading()
    }
  },

  async handleContact() {
    if (!this.data.item) {
      return
    }
    if (this.data.item.isMock) {
      showToast('演示数据仅供预览')
      return
    }
    if (!this.ensureLogin()) {
      return
    }
    if (this.data.isOwner) {
      wx.navigateTo({
        url: '/pages/profile/items'
      })
      return
    }

    showLoading('发送中...')
    try {
      await api.message.send({
        receiverId: this.data.item.ownerId,
        type: 1,
        content: `你好，我对你发布的“${this.data.item.title}”感兴趣，方便沟通一下吗？`
      })
      hideLoading()
      showToast('已发送联系消息', 'success')
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

  handleRent() {
    if (!this.data.item) {
      return
    }
    if (this.data.item.isMock) {
      showToast('演示数据暂不支持下单')
      return
    }
    if (!this.ensureLogin()) {
      return
    }
    if (this.data.isOwner) {
      wx.navigateTo({
        url: '/pages/profile/items'
      })
      return
    }
    wx.navigateTo({
      url: `/pages/order/create?itemId=${this.data.item.id}`
    })
  },

  goToOwnerProfile() {
    if (!this.data.item || !this.data.item.ownerId) {
      return
    }
    wx.navigateTo({
      url: `/pages/profile/user?id=${this.data.item.ownerId}`
    })
  },

  ensureLogin() {
    return auth.ensureLogin()
  },

  setItemData(detail) {
    const userInfo = wx.getStorageSync('userInfo') || {}
    this.setData({
      item: this.formatItem(detail),
      isOwner: Boolean(userInfo.id && detail.ownerId === userInfo.id)
    })
  },

  formatItem(item) {
    const images = item.images && item.images.length ? item.images : [item.coverImage || DEFAULT_IMAGE]
    return {
      id: item.id,
      isMock: Boolean(item.isMock),
      title: item.title,
      description: item.description || '暂无描述',
      images: images.map((image) => image || DEFAULT_IMAGE),
      coverImage: item.coverImage || images[0] || DEFAULT_IMAGE,
      price: Number(item.price || 0).toFixed(2),
      deposit: Number(item.deposit || 0).toFixed(2),
      depositText: item.type === 1 ? `¥${Number(item.deposit || 0).toFixed(2)}` : '无押金',
      type: item.type,
      typeText: item.typeText || (item.type === 1 ? '租赁' : '出售'),
      typeClass: item.type === 1 ? 'lease' : 'sale',
      campus: item.campus || '校区待补充',
      ownerName: item.ownerName || '校园用户',
      ownerId: item.ownerId,
      ownerVerified: item.ownerVerified === 1,
      ownerCreditScore: Number(item.ownerCreditScore || 100),
      ownerCreditLevel: item.ownerCreditLevel || '优秀',
      viewCount: item.viewCount || 0,
      favoriteCount: item.favoriteCount || 0,
      reviewHint: item.reviewHint || '建议优先校内当面验货。',
      statusText: item.statusText || ''
    }
  }
})
