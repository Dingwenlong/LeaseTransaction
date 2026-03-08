const request = require('./request.js')
const payment = require('./payment.js')
const websocket = require('./websocket.js')

module.exports = {
  user: {
    login: (data) => request.post('/user/login', data),
    getInfo: () => request.get('/user/info')
  },
  item: {
    list: (data) => request.get('/item/list', data),
    detail: (id) => request.get('/item/detail/' + id),
    publish: (data) => request.post('/item/publish', data),
    myItems: () => request.get('/item/my'),
    uploadImage: (filePath) => request.uploadFile(filePath)
  },
  order: {
    list: (data) => request.get('/order/list', data),
    detail: (id) => request.get('/order/detail/' + id),
    create: (data) => request.post('/order/create', data),
    updateStatus: (id, status) => request.post('/order/status/' + id, { status })
  },
  payment,
  websocket
}
