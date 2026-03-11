const { showToast } = require('../../utils/util.js')

Page({
  data: {
    order: {
      id: 1,
      orderNo: 'ORD202412010001',
      itemTitle: 'iPhone 15 Pro Max',
      itemImage: 'https://via.placeholder.com/200x200/1e293b/22d3ee?text=iPhone',
      type: '租赁',
      typeClass: 'lease',
      amount: 150.00,
      deposit: 5000.00,
      totalAmount: 5150.00,
      status: '进行中',
      statusDesc: '租赁中，请按时归还物品',
      createTime: '2024-12-01 10:30:00'
    },
    statusTagClass: 'mp-tag-cyan',
    priceLabel: '租金',
    showActionBar: true,
    canPay: false,
    canContact: true
  },

  onLoad(options) {
    if (options.id) {
      console.log('订单ID:', options.id)
    }
    this.syncViewState()
  },

  syncViewState() {
    const status = this.data.order.status
    this.setData({
      statusTagClass: status === '待付款' ? 'mp-tag-yellow' : status === '进行中' ? 'mp-tag-cyan' : 'mp-tag-green',
      priceLabel: this.data.order.type === '租赁' ? '租金' : '商品价格',
      showActionBar: status === '待付款' || status === '进行中',
      canPay: status === '待付款',
      canContact: status === '进行中'
    })
  },

  handleCancel() {
    showToast('取消订单功能开发中')
  },

  handlePay() {
    showToast('支付功能开发中')
  },

  handleContact() {
    showToast('联系功能开发中')
  }
})
