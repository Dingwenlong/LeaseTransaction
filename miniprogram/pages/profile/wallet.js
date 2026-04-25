const api = require('../../utils/api.js')
const auth = require('../../utils/auth.js')
const { showLoading, hideLoading, showToast } = require('../../utils/util.js')

Page({
  data: {
    summary: {
      paidAmount: '0.00',
      depositFrozen: '0.00',
      refundingAmount: '0.00',
      refundedAmount: '0.00'
    },
    records: []
  },

  onShow() {
    if (!this.ensureLogin()) {
      return
    }
    this.loadWallet()
  },

  async loadWallet() {
    showLoading()
    try {
      const [summary, recordPage] = await Promise.all([
        api.payment.summary(),
        api.payment.records({ page: 1, size: 20 })
      ])
      this.setData({
        summary: {
          paidAmount: Number(summary.paidAmount || 0).toFixed(2),
          depositFrozen: Number(summary.depositFrozen || 0).toFixed(2),
          refundingAmount: Number(summary.refundingAmount || 0).toFixed(2),
          refundedAmount: Number(summary.refundedAmount || 0).toFixed(2)
        },
        records: (recordPage.records || []).map((item) => ({
          ...item,
          amount: Number(item.amount || 0).toFixed(2),
          createdAt: item.createdAt ? item.createdAt.replace('T', ' ').slice(0, 16) : '-'
        }))
      })
    } catch (error) {
      console.error('加载钱包失败:', error)
      showToast('钱包加载失败')
    } finally {
      hideLoading()
    }
  },

  ensureLogin() {
    return auth.ensureLogin()
  }
})
