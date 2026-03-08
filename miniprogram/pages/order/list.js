Page({
  data: {
    activeTab: 0,
    tabs: ['全部', '待付款', '进行中', '已完成'],
    orders: [
      {
        id: 1,
        orderNo: 'ORD202412010001',
        itemTitle: 'iPhone 15 Pro Max',
        itemImage: 'https://via.placeholder.com/200x200/1e293b/22d3ee?text=iPhone',
        type: '租赁',
        typeClass: 'lease',
        amount: 150.00,
        status: '进行中',
        statusClass: 'progress',
        createTime: '2024-12-01 10:30'
      },
      {
        id: 2,
        orderNo: 'ORD202412010002',
        itemTitle: '高等数学教材',
        itemImage: 'https://via.placeholder.com/200x200/1e293b/ec4899?text=Book',
        type: '出售',
        typeClass: 'sale',
        amount: 25.00,
        status: '已完成',
        statusClass: 'completed',
        createTime: '2024-12-01 11:20'
      }
    ]
  },

  onLoad() {
    const token = wx.getStorageSync('token')
    if (!token) {
      wx.navigateTo({
        url: '/pages/login/login'
      })
    }
  },

  onTabTap(e) {
    this.setData({ activeTab: e.currentTarget.dataset.index })
  },

  onOrderTap(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: '/pages/order/detail?id=' + id
    })
  }
})
