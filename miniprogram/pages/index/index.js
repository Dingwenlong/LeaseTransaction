const api = require('../../utils/api.js')
const { showLoading, hideLoading, showToast } = require('../../utils/util.js')

const DEFAULT_IMAGE = 'https://dummyimage.com/640x640/0f172a/67e8f9.png&text=Campus+Lease'

Page({
  data: {
    searchKeyword: '',
    activeTab: 0,
    tabs: ['全部', '租赁', '出售'],
    activeCategory: '全部',
    categories: ['全部'],
    banners: [],
    announcements: [],
    items: [],
    nearbyItems: []
  },

  onLoad() {
    this.loadHome()
  },

  onPullDownRefresh() {
    this.loadHome().finally(() => {
      wx.stopPullDownRefresh()
    })
  },

  onSearchInput(e) {
    this.setData({ searchKeyword: e.detail.value })
  },

  onSearch() {
    this.loadItems()
  },

  onTabTap(e) {
    this.setData({ activeTab: e.currentTarget.dataset.index }, () => {
      this.loadItems()
    })
  },

  onCategoryTap(e) {
    this.setData({ activeCategory: e.currentTarget.dataset.category }, () => {
      this.loadItems()
    })
  },

  onItemTap(e) {
    wx.navigateTo({
      url: '/pages/item/detail?id=' + e.currentTarget.dataset.id
    })
  },

  onPublishTap() {
    if (!wx.getStorageSync('token')) {
      wx.navigateTo({
        url: '/pages/login/login'
      })
      return
    }
    wx.navigateTo({
      url: '/pages/item/publish'
    })
  },

  onNearbyTap(e) {
    wx.navigateTo({
      url: '/pages/item/detail?id=' + e.currentTarget.dataset.id
    })
  },

  async loadHome() {
    showLoading()
    try {
      const userInfo = wx.getStorageSync('userInfo') || {}
      const [config, itemRes, nearbyRes] = await Promise.all([
        api.config.system(),
        this.fetchItemList(),
        api.item.nearby({
          campus: userInfo.campus || undefined,
          limit: 4
        })
      ])

      const categories = ['全部'].concat(config.categories || [])
      this.setData({
        categories,
        banners: (config.banners || []).filter((item) => item.active !== false),
        announcements: config.announcements || [],
        items: (itemRes.records || []).map(this.formatItem),
        nearbyItems: (nearbyRes || []).map(this.formatItem)
      })
    } catch (error) {
      console.error('加载首页数据失败:', error)
      showToast('首页数据加载失败')
    } finally {
      hideLoading()
    }
  },

  async loadItems() {
    showLoading()
    try {
      const res = await this.fetchItemList()
      this.setData({
        items: (res.records || []).map(this.formatItem)
      })
    } catch (error) {
      console.error('加载物品失败:', error)
      showToast('加载物品失败')
    } finally {
      hideLoading()
    }
  },

  fetchItemList() {
    const type = this.data.activeTab === 1 ? 1 : this.data.activeTab === 2 ? 2 : undefined
    const category = this.data.activeCategory !== '全部' ? this.data.activeCategory : undefined
    return api.item.list({
      page: 1,
      size: 8,
      status: 1,
      keyword: this.data.searchKeyword || undefined,
      type,
      category
    })
  },

  formatItem(item) {
    return {
      id: item.id,
      title: item.title,
      price: Number(item.price || 0).toFixed(2),
      type: item.typeText || (item.type === 1 ? '租赁' : '出售'),
      typeClass: item.type === 1 ? 'lease' : 'sale',
      image: item.coverImage || (item.images && item.images[0]) || DEFAULT_IMAGE,
      location: item.campus || '校区待补充',
      ownerName: item.ownerName || '校园用户',
      reviewHint: item.reviewHint || '',
      statusText: item.statusText || ''
    }
  }
})
