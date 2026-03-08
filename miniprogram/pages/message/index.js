Page({
  data: {
    activeTab: 0,
    tabs: [
      { name: '全部', count: 0 },
      { name: '订单', count: 2 },
      { name: '系统', count: 1 }
    ],
    messages: [
      {
        id: 1,
        icon: '📦',
        title: '订单已创建',
        desc: '您的iPhone 15 Pro Max租赁订单已创建，请及时付款',
        time: '10:30',
        read: false
      },
      {
        id: 2,
        icon: '💬',
        title: '新消息提醒',
        desc: '张三给您发送了一条消息',
        time: '09:15',
        read: false
      },
      {
        id: 3,
        icon: '🔔',
        title: '系统通知',
        desc: '欢迎使用校园租赁交易平台',
        time: '昨天',
        read: true
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

  onMessageTap(e) {
    console.log('消息ID:', e.currentTarget.dataset.id)
  }
})
