const api = require('../../utils/api.js')
const auth = require('../../utils/auth.js')
const messageMock = require('../../mock/message.js')
const { showLoading, hideLoading } = require('../../utils/util.js')

Page({
  data: {
    activeTab: 0,
    tabs: [
      { name: '全部', key: 'all', count: 0 },
      { name: '未读', key: 'unread', count: 0 },
      { name: '系统', key: 'system', count: 0 }
    ],
    messages: [],
    displayMessages: [],
    usingMockData: false
  },

  onShow() {
    if (!this.canLoadMessages()) {
      return
    }
    this.loadMessages()
  },

  onPullDownRefresh() {
    if (!this.canLoadMessages()) {
      wx.stopPullDownRefresh()
      return
    }
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

    if (!message.read && message.receiverId === this.getCurrentUserId()) {
      if (message.isMock) {
        this.markMessageRead(message.id)
      } else {
        try {
          await api.message.read(message.id)
          this.markMessageRead(message.id)
        } catch (error) {
          console.error('标记已读失败:', error)
        }
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
      const useMockData = messageMock.isEnabled()
      const res = useMockData
        ? messageMock.listMockMessages({
            page: 1,
            size: 50,
            userId: this.getCurrentUserId(),
            studentId: this.getCurrentStudentId()
          })
        : await api.message.list({
            page: 1,
            size: 50
          })

      const messages = (res.records || []).map((item) => this.formatMessage(item))
      this.setData({
        messages,
        usingMockData: useMockData
      })
      this.syncTabs(messages)
      this.applyFilter()
    } catch (error) {
      console.error('加载消息失败，切换为演示数据:', error)
      const res = messageMock.listMockMessages({
        page: 1,
        size: 50,
        userId: this.getCurrentUserId(),
        studentId: this.getCurrentStudentId()
      })
      const messages = (res.records || []).map((item) => this.formatMessage(item))
      this.setData({
        messages,
        usingMockData: true
      })
      this.syncTabs(messages)
      this.applyFilter()
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

  canLoadMessages() {
    if (messageMock.isEnabled()) {
      return true
    }
    return this.ensureLogin()
  },

  ensureLogin() {
    return auth.ensureLogin()
  },

  getCurrentUserId() {
    const userInfo = wx.getStorageSync('userInfo') || {}
    return userInfo.id || messageMock.MOCK_USER_ID
  },

  getCurrentStudentId() {
    const userInfo = wx.getStorageSync('userInfo') || {}
    if (userInfo.studentId) {
      return userInfo.studentId
    }
    if (typeof userInfo.id === 'string' && /^\d{10}$/.test(userInfo.id)) {
      return userInfo.id
    }
    return messageMock.TARGET_STUDENT_ID
  },

  markMessageRead(messageId) {
    const messages = this.data.messages.map((item) => {
      if (item.id !== messageId) {
        return item
      }
      return {
        ...item,
        read: true
      }
    })

    this.setData({ messages })
    this.syncTabs(messages)
    this.applyFilter()
  },

  formatMessage(item) {
    return {
      ...item,
      desc: item.desc || item.content || '',
      icon: item.icon || this.getMessageIcon(item.type),
      time: this.formatDate(item.time || item.createdAt)
    }
  },

  getMessageIcon(type) {
    if (type === 3) return '🔔'
    if (type === 2) return '📦'
    return '💬'
  },

  formatDate(value) {
    if (!value) return ''
    return value.replace('T', ' ').slice(5, 16)
  }
})
