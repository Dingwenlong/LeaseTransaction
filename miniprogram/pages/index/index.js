const api = require('../../utils/api.js')
const { showLoading, hideLoading, showToast } = require('../../utils/util.js')

Page({
  data: {
    searchKeyword: '',
    activeTab: 0,
    tabs: ['全部', '租赁', '出售'],
    items: [
      {
        id: 1,
        title: 'iPhone 15 Pro Max',
        price: 150,
        type: '租赁',
        typeClass: 'lease',
        image: 'https://via.placeholder.com/200x200/1e293b/22d3ee?text=iPhone',
        location: '东校区'
      },
      {
        id: 2,
        title: '高等数学教材',
        price: 25,
        type: '出售',
        typeClass: 'sale',
        image: 'https://via.placeholder.com/200x200/1e293b/ec4899?text=Book',
        location: '西校区'
      },
      {
        id: 3,
        title: '羽毛球拍',
        price: 30,
        type: '租赁',
        typeClass: 'lease',
        image: 'https://via.placeholder.com/200x200/1e293b/22d3ee?text=Racket',
        location: '南校区'
      },
      {
        id: 4,
        title: '机械键盘',
        price: 350,
        type: '出售',
        typeClass: 'sale',
        image: 'https://via.placeholder.com/200x200/1e293b/ec4899?text=Keyboard',
        location: '东校区'
      }
    ]
  },

  onLoad() {
    this.loadItems()
  },

  onSearchInput(e) {
    this.setData({ searchKeyword: e.detail.value })
  },

  onSearch() {
    this.loadItems()
  },

  onTabTap(e) {
    const index = e.currentTarget.dataset.index
    this.setData({ activeTab: index })
    this.loadItems()
  },

  onItemTap(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: '/pages/item/detail?id=' + id
    })
  },

  onPublishTap() {
    const token = wx.getStorageSync('token')
    if (!token) {
      wx.navigateTo({
        url: '/pages/login/login'
      })
      return
    }
    wx.navigateTo({
      url: '/pages/item/publish'
    })
  },

  loadItems() {
    showLoading()
    setTimeout(() => {
      hideLoading()
    }, 500)
  },

  onPullDownRefresh() {
    this.loadItems()
    setTimeout(() => {
      wx.stopPullDownRefresh()
    }, 1000)
  }
})
