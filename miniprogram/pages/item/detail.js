const { showToast } = require('../../utils/util.js')

Page({
  data: {
    item: {
      id: 1,
      title: 'iPhone 15 Pro Max',
      price: 150,
      type: '租赁',
      typeClass: 'lease',
      image: 'https://via.placeholder.com/400x400/1e293b/22d3ee?text=iPhone',
      location: '东校区',
      ownerName: '张三同学',
      description: '99新iPhone 15 Pro Max，256GB，黑色。租期一周起租，押金5000元，支持当面验机。'
    }
  },

  onLoad(options) {
    if (options.id) {
      console.log('物品ID:', options.id)
    }
  },

  handleContact() {
    const token = wx.getStorageSync('token')
    if (!token) {
      wx.navigateTo({
        url: '/pages/login/login'
      })
      return
    }
    showToast('联系功能开发中')
  },

  handleRent() {
    const token = wx.getStorageSync('token')
    if (!token) {
      wx.navigateTo({
        url: '/pages/login/login'
      })
      return
    }
    showToast('下单功能开发中')
  }
})
