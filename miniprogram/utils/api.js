const request = require('./request.js')
const payment = require('./payment.js')
const websocket = require('./websocket.js')

module.exports = {
  user: {
    login: (data) => request.post('/user/login', data),
    getInfo: () => request.get('/user/info'),
    profile: (id) => request.get('/user/profile/' + id),
    updateProfile: (data) => request.put('/user/profile', data),
    verify: (data) => request.post('/user/verify', data)
  },
  item: {
    list: (data) => request.get('/item/list', data),
    detail: (id) => request.get('/item/detail/' + id),
    publish: (data) => request.post('/item/publish', data),
    myItems: (data) => request.get('/item/my', data),
    nearby: (data) => request.get('/item/nearby', data),
    updateStatus: (id, status) => request.post('/item/status/' + id, { status }),
    uploadImage: (filePath) => request.uploadFile(filePath)
  },
  order: {
    list: (data) => request.get('/order/list', data),
    detail: (id) => request.get('/order/detail/' + id),
    create: (data) => request.post('/order/create', data),
    updateStatus: (id, status, remark) => request.post('/order/status/' + id, { status, remark })
  },
  message: {
    list: (data) => request.get('/message/list', data),
    send: (data) => request.post('/message/send', data),
    read: (id) => request.post('/message/read/' + id),
    unreadCount: () => request.get('/message/unread-count')
  },
  config: {
    system: () => request.get('/config/system')
  },
  stats: {
    dashboard: () => request.get('/stats/dashboard')
  },
  review: {
    submit: (data) => request.post('/review/submit', data),
    order: (orderId) => request.get('/review/order/' + orderId),
    user: (userId) => request.get('/review/user/' + userId)
  },
  payment,
  websocket
}
