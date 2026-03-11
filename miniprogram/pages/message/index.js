const api = require('../../utils/api.js')
const { showLoading, hideLoading, showToast } = require('../../utils/util.js')

Page({
  data: {
    activeTab: 0,
    tabs: [
      { name: '全部', key: 'all', count: 0 },
      { name: '未读', key: 'unread', count: 0 },
      { name: '系统', key: 'system', count: 0 }
    ],
    messages: [],
    displayMessages: []
  },

  onShow() {
    if (!this.ensureLogin()) {
      return
    }
    this.loadMessages()
  },

  onPullDownRefresh() {
    this.loadMessages().finally(() => {
      wx.stopPullDownRefresh()
    })
  },

  onTabTap(e) {
    this.setData({ activeTab: e.currentTarget.dataset.index }, () => {
      this.applyFilter()
    })
  },

  async onMessageTap(e) {
    const message = this.data.displayMessages.find((item) => item.id === e.currentTarget.dataset.id)
    if (!message) {
      return
    }

    const userInfo = wx.getStorageSync('userInfo') || {}
    if (!message.read && message.receiverId === userInfo.id) {
      try {
        await api.message.read(message.id)
        const messages = this.data.messages.map((item) => item.id === message.id ? { ...item, read: true } : item)
        this.setData({ messages })
        this.syncTabs(messages)
        this.applyFilter()
      } catch (error) {
        console.error('标记已读失败:', error)
      }
    }

    wx.showModal({
      title: message.title,
      content: message.content || message.desc,
      showCancel: false
    })
  },

  async loadMessages() {
    showLoading()
    try {
      const res = await api.message.list({
        page: 1,
        size: 50
      })
      const messages = (res.records || []).map((item) => ({
        ...item,
        time: this.formatDate(item.time)
      }))
      this.setData({ messages })
      this.syncTabs(messages)
      this.applyFilter()
    } catch (error) {
      console.error('加载消息失败:', error)
      showToast('消息加载失败')
    } finally {
      hideLoading()
    }
  },

  applyFilter() {
    const key = this.data.tabs[this.data.activeTab].key
    const displayMessages = this.data.messages.filter((item) => {
      if (key === 'all') {
        return true
      }
      if (key === 'unread') {
        return !item.read
      }
      if (key === 'system') {
        return item.type === 3
      }
      return true
    })
    this.setData({ displayMessages })
  },

  syncTabs(messages) {
    const tabs = this.data.tabs.map((tab) => ({ ...tab }))
    tabs[0].count = messages.length
    tabs[1].count = messages.filter((item) => !item.read).length
    tabs[2].count = messages.filter((item) => item.type === 3).length
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

  formatDate(value) {
    if (!value) return ''
    return value.replace('T', ' ').slice(5, 16)
  }
})
