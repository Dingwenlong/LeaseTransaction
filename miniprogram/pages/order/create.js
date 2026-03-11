const api = require('../../utils/api.js')
const { formatDate, showLoading, hideLoading, showToast } = require('../../utils/util.js')

Page({
  data: {
    item: null,
    rentalAmount: '0.00',
    totalAmount: '0.00',
    form: {
      startDate: formatDate(new Date(Date.now() + 24 * 60 * 60 * 1000)),
      rentalDays: '1',
      deliveryMethod: '校内面交',
      remark: ''
    }
  },

  onLoad(options) {
    if (!options.itemId) {
      showToast('缺少物品信息')
      return
    }
    this.itemId = options.itemId
    this.loadItem()
  },

  async loadItem() {
    showLoading()
    try {
      const item = await api.item.detail(this.itemId)
      this.setData({ item })
      this.calculateAmount()
    } catch (error) {
      console.error('加载物品失败:', error)
    } finally {
      hideLoading()
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({
      [`form.${field}`]: e.detail.value
    }, () => {
      this.calculateAmount()
    })
  },

  onDateChange(e) {
    this.setData({
      'form.startDate': e.detail.value
    })
  },

  calculateAmount() {
    const item = this.data.item
    if (!item) {
      return
    }
    const rentalDays = Math.max(1, Number(this.data.form.rentalDays || 1))
    const rentalAmount = item.type === 1
      ? (Number(item.price || 0) * rentalDays)
      : Number(item.price || 0)
    const totalAmount = rentalAmount + Number(item.deposit || 0)
    this.setData({
      rentalAmount: rentalAmount.toFixed(2),
      totalAmount: totalAmount.toFixed(2)
    })
  },

  async handleSubmit() {
    const item = this.data.item
    if (!item) {
      showToast('物品信息不存在')
      return
    }

    const rentalDays = Math.max(1, Number(this.data.form.rentalDays || 1))
    const payload = {
      itemId: item.id,
      type: item.type,
      startDate: item.type === 1 ? `${this.data.form.startDate}T10:00:00` : null,
      rentalDays: item.type === 1 ? rentalDays : null,
      deliveryMethod: this.data.form.deliveryMethod,
      remark: this.data.form.remark
    }

    showLoading('提交中...')
    try {
      const order = await api.order.create(payload)
      hideLoading()
      showToast('订单创建成功', 'success')
      setTimeout(() => {
        wx.redirectTo({
          url: `/pages/order/detail?id=${order.id}`
        })
      }, 800)
    } catch (error) {
      hideLoading()
      console.error('创建订单失败:', error)
    }
  },

  handleBack() {
    wx.navigateBack({
      delta: 1
    })
  }
})
