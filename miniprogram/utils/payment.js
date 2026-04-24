const request = require('./request.js')
const env = require('../mock/env.js')

const pay = {
  createPayment: async (orderId) => {
    try {
      const payParams = await request.post('/payment/create', { orderId })

      if (String(payParams.paySign || '').startsWith('LOCAL_PAY_SIGN_')) {
        return pay.confirmLocalPayment(payParams.paymentNo)
      }

      return new Promise((resolve, reject) => {
        wx.requestPayment({
          timeStamp: String(payParams.timeStamp || payParams.timestamp || ''),
          nonceStr: payParams.nonceStr,
          package: payParams.package,
          signType: payParams.signType,
          paySign: payParams.paySign,
          success: (res) => {
            console.log('支付成功:', res)
            resolve(res)
          },
          fail: (err) => {
            console.warn('支付失败:', err)
            reject(err)
          }
        })
      })
    } catch (error) {
      console.error('创建支付订单失败:', error)
      throw error
    }
  },

  confirmLocalPayment: (paymentNo) => {
    return new Promise((resolve, reject) => {
      wx.showModal({
        title: '本地支付模拟',
        content: `当前为 ${env.getCurrentEnvVersion()} 环境，将使用本地模拟网关确认支付。`,
        confirmText: '确认支付',
        success: async (res) => {
          if (!res.confirm) {
            reject(new Error('用户取消本地支付'))
            return
          }
          try {
            const result = await request.post('/payment/local/confirm/' + encodeURIComponent(paymentNo))
            resolve(result)
          } catch (error) {
            reject(error)
          }
        },
        fail: reject
      })
    })
  },

  queryPayment: async (orderId) => {
    return request.get('/payment/query/' + orderId)
  },

  requestRefund: async (orderId, reason) => {
    return request.post('/payment/refund', { orderId, reason })
  },

  summary: async () => {
    return request.get('/payment/summary')
  },

  records: async (data) => {
    return request.get('/payment/records', data)
  }
}

module.exports = pay
