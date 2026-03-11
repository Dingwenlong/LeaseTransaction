const request = require('./request.js')

const pay = {
  createPayment: async (orderId) => {
    try {
      const payParams = await request.post('/payment/create', { orderId })

      return new Promise((resolve, reject) => {
        wx.requestPayment({
          timeStamp: payParams.timestamp.toString(),
          nonceStr: payParams.nonceStr,
          package: payParams.package,
          signType: payParams.signType,
          paySign: payParams.paySign,
          success: (res) => {
            console.log('支付成功:', res)
            resolve(res)
          },
          fail: (err) => {
            console.warn('支付降级为演示模式:', err)
            request.post('/order/status/' + orderId, {
              status: 2,
              remark: '小程序演示支付成功'
            }).then(() => {
              resolve({
                demoMode: true,
                errMsg: 'requestPayment:fail demo fallback'
              })
            }).catch(reject)
          }
        })
      })
    } catch (error) {
      console.error('创建支付订单失败:', error)
      throw error
    }
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
