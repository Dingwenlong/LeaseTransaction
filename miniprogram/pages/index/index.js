const api = require('../../utils/api.js')
const homeMock = require('../../mock/home.js')
const { showLoading, hideLoading, showToast } = require('../../utils/util.js')

const DEFAULT_IMAGE = 'https://dummyimage.com/640x640/0f172a/67e8f9.png&text=Campus+Lease'
const SEARCH_HISTORY_KEY = 'home-search-history'
const SEARCH_HISTORY_LIMIT = 10

Page({
  data: {
    searchKeyword: '',
    hasSearchKeyword: false,
    activeTab: 0,
    tabs: ['全部', '租赁', '出售'],
    activeCategory: '全部',
    categories: ['全部'],
    banners: [],
    announcements: [],
    items: [],
    nearbyItems: [],
    usingMockData: false,
    showHomeHighlights: true,
    searchHistory: []
  },

  onLoad() {
    this.loadSearchHistory()
    this.loadHome()
  },

  onPullDownRefresh() {
    this.loadHome().finally(() => {
      wx.stopPullDownRefresh()
    })
  },

  onSearchInput(e) {
    const searchKeyword = e.detail.value || ''
    this.setData({
      searchKeyword,
      hasSearchKeyword: Boolean(searchKeyword.trim())
    })
  },

  onSearch() {
    this.executeSearch(this.data.searchKeyword)
  },

  onHistoryTap(e) {
    this.executeSearch(e.currentTarget.dataset.keyword || '')
  },

  onClearHistory() {
    if (!this.data.searchHistory.length) {
      return
    }

    wx.showModal({
      title: '清空搜索记录',
      content: '确定清空最近搜索记录吗？',
      success: (res) => {
        if (!res.confirm) {
          return
        }

        wx.removeStorageSync(SEARCH_HISTORY_KEY)
        this.setData({ searchHistory: [] })
        showToast('已清空')
      }
    })
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

  executeSearch(keyword) {
    const normalizedKeyword = String(keyword || '').trim()
    const hasKeyword = Boolean(normalizedKeyword)

    this.setData({
      searchKeyword: normalizedKeyword,
      hasSearchKeyword: hasKeyword,
      showHomeHighlights: !hasKeyword
    }, () => {
      if (hasKeyword) {
        this.saveSearchHistory(normalizedKeyword)
      }
      this.loadItems()
    })
  },

  loadSearchHistory() {
    const history = wx.getStorageSync(SEARCH_HISTORY_KEY)
    const searchHistory = Array.isArray(history)
      ? history.filter((item) => typeof item === 'string' && item.trim()).slice(0, SEARCH_HISTORY_LIMIT)
      : []

    this.setData({ searchHistory })
  },

  saveSearchHistory(keyword) {
    const normalizedKeyword = String(keyword || '').trim()
    if (!normalizedKeyword) {
      return
    }

    const loweredKeyword = normalizedKeyword.toLowerCase()
    const searchHistory = [normalizedKeyword]
      .concat(this.data.searchHistory.filter((item) => item.toLowerCase() !== loweredKeyword))
      .slice(0, SEARCH_HISTORY_LIMIT)

    wx.setStorageSync(SEARCH_HISTORY_KEY, searchHistory)
    this.setData({ searchHistory })
  },

  async loadHome() {
    showLoading()
    try {
      if (homeMock.isEnabled()) {
        this.applyMockHomeData()
        return
      }

      const userInfo = wx.getStorageSync('userInfo') || {}
      const [config, itemRes, nearbyRes] = await Promise.all([
        api.config.system(),
        this.fetchItemList(),
        api.item.nearby({
          campus: userInfo.campus || undefined,
          limit: 4
        })
      ])

      this.setHomeData({
        categories: ['全部'].concat(config.categories || []),
        banners: (config.banners || []).filter((item) => item.active !== false),
        announcements: config.announcements || [],
        items: (itemRes.records || []).map(this.formatItem),
        nearbyItems: (nearbyRes || []).map(this.formatItem),
        usingMockData: false
      })
    } catch (error) {
      console.error('加载首页数据失败，切换为演示数据:', error)
      this.applyMockHomeData()
    } finally {
      hideLoading()
    }
  },

  async loadItems() {
    showLoading()
    try {
      const useMockData = homeMock.isEnabled()
      const res = useMockData
        ? homeMock.listMockItems(this.buildItemListParams())
        : await this.fetchItemList()

      this.setData({
        items: (res.records || []).map(this.formatItem),
        usingMockData: useMockData
      })
    } catch (error) {
      console.error('加载物品失败，切换为演示数据:', error)
      const res = homeMock.listMockItems(this.buildItemListParams())
      this.setData({
        items: (res.records || []).map(this.formatItem),
        usingMockData: true
      })
    } finally {
      hideLoading()
    }
  },

  buildItemListParams() {
    const type = this.data.activeTab === 1 ? 1 : this.data.activeTab === 2 ? 2 : undefined
    const category = this.data.activeCategory !== '全部' ? this.data.activeCategory : undefined

    return {
      page: 1,
      size: 8,
      status: 1,
      keyword: this.data.searchKeyword.trim() || undefined,
      type,
      category
    }
  },

  fetchItemList() {
    return api.item.list(this.buildItemListParams())
  },

  applyMockHomeData() {
    const userInfo = wx.getStorageSync('userInfo') || {}
    const config = homeMock.getHomeConfig()
    const itemRes = homeMock.listMockItems(this.buildItemListParams())
    const nearbyRes = homeMock.listMockNearbyItems({
      campus: userInfo.campus || undefined,
      limit: 4
    })

    this.setHomeData({
      categories: ['全部'].concat(config.categories || []),
      banners: (config.banners || []).filter((item) => item.active !== false),
      announcements: config.announcements || [],
      items: (itemRes.records || []).map(this.formatItem),
      nearbyItems: nearbyRes.map(this.formatItem),
      usingMockData: true
    })
  },

  setHomeData(payload) {
    this.setData(payload)
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
      statusText: item.statusText || '',
      isMock: Boolean(item.isMock)
    }
  }
})
