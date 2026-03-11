const WS_URL = 'ws://127.0.0.1:8081/ws/websocket'

class WebSocketClient {
  constructor() {
    this.socket = null
    this.reconnectAttempts = 0
    this.maxReconnectAttempts = 5
    this.reconnectDelay = 3000
    this.messageHandlers = []
    this.isConnected = false
  }

  connect() {
    if (this.socket && this.isConnected) {
      console.log('WebSocket已连接')
      return
    }

    console.log('正在连接WebSocket...')
    this.socket = wx.connectSocket({
      url: WS_URL,
      protocols: []
    })

    this.socket.onOpen(() => {
      console.log('WebSocket连接成功')
      this.isConnected = true
      this.reconnectAttempts = 0
      this.emit('connected')
    })

    this.socket.onMessage((res) => {
      try {
        const message = JSON.parse(res.data)
        console.log('收到消息:', message)
        this.messageHandlers.forEach(handler => handler(message))
      } catch (e) {
        console.log('消息解析失败:', e)
      }
    })

    this.socket.onClose(() => {
      console.log('WebSocket连接关闭')
      this.isConnected = false
      this.emit('disconnected')
      this.attemptReconnect()
    })

    this.socket.onError((err) => {
      console.error('WebSocket错误:', err)
      this.isConnected = false
      this.emit('error', err)
    })
  }

  disconnect() {
    if (this.socket) {
      this.socket.close()
      this.socket = null
      this.isConnected = false
    }
  }

  send(message) {
    if (!this.isConnected) {
      console.log('WebSocket未连接，无法发送消息')
      return
    }
    this.socket.send({
      data: JSON.stringify(message)
    })
  }

  onMessage(handler) {
    this.messageHandlers.push(handler)
  }

  offMessage(handler) {
    const index = this.messageHandlers.indexOf(handler)
    if (index > -1) {
      this.messageHandlers.splice(index, 1)
    }
  }

  attemptReconnect() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.log('重连次数达到上限，停止重连')
      return
    }

    this.reconnectAttempts++
    console.log(`正在尝试第 ${this.reconnectAttempts} 次重连...`)

    setTimeout(() => {
      this.connect()
    }, this.reconnectDelay)
  }

  emit(event, data) {
  }
}

module.exports = new WebSocketClient()
