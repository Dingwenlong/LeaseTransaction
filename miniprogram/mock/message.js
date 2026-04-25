const { isMockEnabled } = require('./env.js')

const MOCK_USER_ID = 'mock-current-user'
const TARGET_STUDENT_ID = '2202222200'

const COMMON_MESSAGES = [
  {
    id: 'mock-message-01',
    isMock: true,
    type: 1,
    icon: '💬',
    title: '摄影社 陈同学',
    desc: 'Sony A6000 微单相机可以今晚 7 点在紫金港东二门面交。',
    content: '你好，相机今天下午刚充好电，镜头和备用电池都在。今晚 7 点可以在紫金港东二门面交，你到了微信小程序里再点我一下就行。',
    read: false,
    receiverId: MOCK_USER_ID,
    time: '2026-03-18T15:20:00'
  },
  {
    id: 'mock-message-02',
    isMock: true,
    type: 2,
    icon: '📦',
    title: '订单状态更新',
    desc: '你租赁的便携投影仪已被卖家确认，记得按时取货。',
    content: '订单 LT202603180018 已确认。取货时间为 3 月 19 日 18:30，地点玉泉校区教学楼门口。建议提前检查遥控器、HDMI 线和电源适配器。',
    read: false,
    receiverId: MOCK_USER_ID,
    time: '2026-03-18T14:06:00'
  },
  {
    id: 'mock-message-03',
    isMock: true,
    type: 3,
    icon: '🔔',
    title: '系统通知',
    desc: '首页和消息中心当前使用演示数据，方便预览页面效果。',
    content: '你当前看到的是开发环境演示数据，不依赖后端接口。消息点击后会本地切换为已读，适合直接验收列表、未读角标和弹窗交互。',
    read: false,
    receiverId: MOCK_USER_ID,
    time: '2026-03-18T13:30:00'
  },
  {
    id: 'mock-message-04',
    isMock: true,
    type: 1,
    icon: '🧾',
    title: '管理学院 胡同学',
    desc: '24 寸行李箱箱体有轻微划痕，今晚宿舍楼下可看实物。',
    content: '你好，行李箱轮子和拉杆都正常，只有正面有一点轻微划痕。如果你今晚 8 点后方便，我可以在宿舍楼下把实物拿给你看。',
    read: true,
    receiverId: MOCK_USER_ID,
    time: '2026-03-18T11:42:00'
  },
  {
    id: 'mock-message-05',
    isMock: true,
    type: 3,
    icon: '🛡️',
    title: '安全提醒',
    desc: '平台建议优先选择同校区当面验货，并确认配件清单。',
    content: '无论是租赁还是出售，建议优先选择同校区当面交易。请确认序列号、配件数量、押金金额和归还时间，并保留聊天记录以便后续核验。',
    read: true,
    receiverId: MOCK_USER_ID,
    time: '2026-03-17T20:18:00'
  },
  {
    id: 'mock-message-06',
    isMock: true,
    type: 2,
    icon: '✅',
    title: '订单已完成',
    desc: '羽毛球拍双拍套装已归还，押金将在 24 小时内退回。',
    content: '订单 LT202603160031 已完成。物品归还状态正常，押金退款将原路返回。若 24 小时后仍未到账，请联系平台客服并提供订单号。',
    read: true,
    receiverId: MOCK_USER_ID,
    time: '2026-03-17T18:05:00'
  }
]

const TARGET_STUDENT_MESSAGES = [
  {
    id: 'mock-message-student-01',
    isMock: true,
    type: 3,
    icon: '🎓',
    title: '校园认证进度',
    desc: `学号 ${TARGET_STUDENT_ID} 的校园认证资料已通过初审，请补充宿舍楼栋信息。`,
    content: `学号 ${TARGET_STUDENT_ID} 的校园认证资料已通过初审。为方便后续校内面交，请在个人中心补充宿舍楼栋或常用取货点信息，提升发布可信度。`,
    read: false,
    receiverId: MOCK_USER_ID,
    time: '2026-03-18T16:08:00'
  },
  {
    id: 'mock-message-student-02',
    isMock: true,
    type: 2,
    icon: '📚',
    title: '教材租赁提醒',
    desc: `${TARGET_STUDENT_ID} 同学预约的《大学物理实验》教材将于明晚到期，请及时续租或归还。`,
    content: `${TARGET_STUDENT_ID} 同学你好，你预约的《大学物理实验》教材租期将在 3 月 19 日 21:00 到期。若还需要继续使用，可在订单页发起续租；若准备归还，请提前与发布者约定时间。`,
    read: false,
    receiverId: MOCK_USER_ID,
    time: '2026-03-18T15:48:00'
  },
  {
    id: 'mock-message-student-03',
    isMock: true,
    type: 1,
    icon: '🏸',
    title: '羽协 林同学',
    desc: `看到学号 ${TARGET_STUDENT_ID} 的咨询了，羽毛球拍今晚 6 点后可以在体育馆门口交接。`,
    content: `看到你留言了。双拍套装今晚 6 点后可以在体育馆门口交接，我会把球拍、拍套和备用羽毛球一起带上。你到的时候在消息中心再联系我就行。`,
    read: true,
    receiverId: MOCK_USER_ID,
    time: '2026-03-18T12:26:00'
  }
]

const cloneMessage = (message, receiverId, studentId) => ({
  ...message,
  receiverId: receiverId || message.receiverId,
  receiverStudentId: studentId || message.receiverStudentId || TARGET_STUDENT_ID
})

const listMockMessages = (params = {}) => {
  const page = Number(params.page || 1)
  const size = Number(params.size || COMMON_MESSAGES.length + TARGET_STUDENT_MESSAGES.length)
  const receiverId = params.userId || MOCK_USER_ID
  const studentId = params.studentId || TARGET_STUDENT_ID
  const start = (page - 1) * size

  const rawMessages = studentId === TARGET_STUDENT_ID
    ? TARGET_STUDENT_MESSAGES.concat(COMMON_MESSAGES)
    : COMMON_MESSAGES

  const records = rawMessages
    .sort((left, right) => new Date(right.time).getTime() - new Date(left.time).getTime())
    .map((item) => cloneMessage(item, receiverId, studentId))

  return {
    records: records.slice(start, start + size),
    total: records.length,
    current: page,
    size
  }
}

module.exports = {
  MOCK_USER_ID,
  TARGET_STUDENT_ID,
  isEnabled: isMockEnabled,
  listMockMessages
}
