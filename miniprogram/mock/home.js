const { isMockEnabled } = require('./env.js')

const buildImage = (background, foreground, text) => {
  return `https://dummyimage.com/640x640/${background}/${foreground}.png&text=${text}`
}

const HOME_CONFIG = {
  categories: ['数码设备', '学习办公', '运动户外', '生活电器', '服饰箱包'],
  banners: [
    {
      title: '春季租赁市集',
      subtitle: '首页当前展示的是演示数据，方便直接预览搜索、分类和推荐模块。'
    },
    {
      title: '同校当面交易更安心',
      subtitle: '支持按校区筛选，优先展示附近物品，适合课堂、社团和短期活动场景。'
    }
  ],
  announcements: [
    {
      title: '演示数据说明',
      content: '当前首页展示的是前端虚拟数据，不依赖后端接口，便于在微信开发者工具中直接查看效果。'
    },
    {
      title: '交易提醒',
      content: '建议优先选择同校区当面验货，租赁类物品先确认押金、归还时间和配件清单。'
    },
    {
      title: '校园认证',
      content: '发布者昵称、校区、成色说明和信用信息建议完整填写，能明显提升成交效率。'
    }
  ]
}

const MOCK_ITEMS = [
  {
    id: 'mock-camera-01',
    isMock: true,
    title: 'Sony A6000 微单相机',
    description: '适合社团活动拍摄和短视频入门。\n包含机身、16-50 镜头、电池两块和充电器。\n支持校内面交，当天可取。',
    category: '数码设备',
    price: 68,
    deposit: 500,
    type: 1,
    typeText: '租赁',
    campus: '紫金港校区',
    ownerName: '摄影社 陈同学',
    ownerId: 'mock-owner-01',
    ownerVerified: 1,
    viewCount: 126,
    favoriteCount: 18,
    reviewHint: '电池和存储卡可现场验收，支持当天归还。',
    status: 1,
    statusText: '今日可取',
    coverImage: buildImage('10233a', '67e8f9', 'Camera'),
    images: [
      buildImage('10233a', '67e8f9', 'Camera'),
      buildImage('1d4ed8', 'eff6ff', 'Lens')
    ]
  },
  {
    id: 'mock-projector-02',
    isMock: true,
    title: '便携投影仪 含 HDMI 线',
    description: '适合宿舍观影、课程汇报和社团路演。\n亮度足够小教室使用，附遥控器和三脚架。\n默认按天出租，可提前预约。',
    category: '学习办公',
    price: 36,
    deposit: 200,
    type: 1,
    typeText: '租赁',
    campus: '玉泉校区',
    ownerName: '电子院 李同学',
    ownerId: 'mock-owner-02',
    ownerVerified: 1,
    viewCount: 89,
    favoriteCount: 11,
    reviewHint: '领取时请测试投屏和焦距，归还前清点配件。',
    status: 1,
    statusText: '可预约周末',
    coverImage: buildImage('312e81', 'e9d5ff', 'Projector'),
    images: [
      buildImage('312e81', 'e9d5ff', 'Projector'),
      buildImage('6d28d9', 'faf5ff', 'HDMI')
    ]
  },
  {
    id: 'mock-bike-03',
    isMock: true,
    title: '九成新校园代步自行车',
    description: '前后刹车正常，夜间带车灯。\n适合短距离通勤和校内搬运行李。\n支持现场试看，售出后不退。',
    category: '运动户外',
    price: 299,
    deposit: 0,
    type: 2,
    typeText: '出售',
    campus: '下沙校区',
    ownerName: '机械系 王同学',
    ownerId: 'mock-owner-03',
    ownerVerified: 1,
    viewCount: 173,
    favoriteCount: 25,
    reviewHint: '支持现场试骑，建议同学结伴验车。',
    status: 1,
    statusText: '面交优先',
    coverImage: buildImage('14532d', 'bbf7d0', 'Bike'),
    images: [
      buildImage('14532d', 'bbf7d0', 'Bike'),
      buildImage('166534', 'f0fdf4', 'Ride')
    ]
  },
  {
    id: 'mock-calc-04',
    isMock: true,
    title: '卡西欧科学计算器 FX-991CN',
    description: '考试复习闲置转出，按键和屏幕完好。\n附保护套，适合高数、物理和工科课程使用。',
    category: '学习办公',
    price: 58,
    deposit: 0,
    type: 2,
    typeText: '出售',
    campus: '紫金港校区',
    ownerName: '数院 许同学',
    ownerId: 'mock-owner-04',
    ownerVerified: 1,
    viewCount: 64,
    favoriteCount: 7,
    reviewHint: '可现场试按，确认显示和电量后交易。',
    status: 1,
    statusText: '可立即下单',
    coverImage: buildImage('4c1d95', 'f5f3ff', 'Calculator'),
    images: [
      buildImage('4c1d95', 'f5f3ff', 'Calculator'),
      buildImage('7c3aed', 'f5f3ff', 'Study')
    ]
  },
  {
    id: 'mock-speaker-05',
    isMock: true,
    title: 'JBL 蓝牙音箱 Charge',
    description: '音量充足，适合露营、团建和宿舍聚会。\n充满电可用一整晚，外观有轻微使用痕迹。',
    category: '数码设备',
    price: 25,
    deposit: 120,
    type: 1,
    typeText: '租赁',
    campus: '华家池校区',
    ownerName: '音乐社 赵同学',
    ownerId: 'mock-owner-05',
    ownerVerified: 0,
    viewCount: 95,
    favoriteCount: 14,
    reviewHint: '建议现场测试蓝牙连接和续航状态。',
    status: 1,
    statusText: '今晚可借',
    coverImage: buildImage('7f1d1d', 'fecaca', 'Speaker'),
    images: [
      buildImage('7f1d1d', 'fecaca', 'Speaker'),
      buildImage('b91c1c', 'fef2f2', 'Music')
    ]
  },
  {
    id: 'mock-racket-06',
    isMock: true,
    title: '羽毛球拍双拍套装',
    description: '含两支球拍、三个球和便携拍套。\n适合体育课、周末活动和情侣搭子打球。',
    category: '运动户外',
    price: 18,
    deposit: 80,
    type: 1,
    typeText: '租赁',
    campus: '玉泉校区',
    ownerName: '羽协 林同学',
    ownerId: 'mock-owner-06',
    ownerVerified: 1,
    viewCount: 71,
    favoriteCount: 9,
    reviewHint: '球拍磕碰较少，归还时请保持手胶完整。',
    status: 1,
    statusText: '体育馆自提',
    coverImage: buildImage('0f766e', 'ccfbf1', 'Racket'),
    images: [
      buildImage('0f766e', 'ccfbf1', 'Racket'),
      buildImage('115e59', 'f0fdfa', 'Sport')
    ]
  },
  {
    id: 'mock-fan-07',
    isMock: true,
    title: '桌面静音小风扇',
    description: 'USB 供电，三档风速，适合寝室和图书馆自习位。\n体积小，方便毕业季低价转手。',
    category: '生活电器',
    price: 45,
    deposit: 0,
    type: 2,
    typeText: '出售',
    campus: '之江校区',
    ownerName: '法学院 周同学',
    ownerId: 'mock-owner-07',
    ownerVerified: 1,
    viewCount: 52,
    favoriteCount: 5,
    reviewHint: '支持现场通电测试，确认噪音和供电接口。',
    status: 1,
    statusText: '宿舍直取',
    coverImage: buildImage('1e3a8a', 'dbeafe', 'Fan'),
    images: [
      buildImage('1e3a8a', 'dbeafe', 'Fan'),
      buildImage('2563eb', 'eff6ff', 'Cool')
    ]
  },
  {
    id: 'mock-suitcase-08',
    isMock: true,
    title: '24 寸行李箱 可短租可出售',
    description: '万向轮顺滑，箱体完整，适合短途实习和返乡。\n默认按出售展示，如需短租可线下协商。',
    category: '服饰箱包',
    price: 99,
    deposit: 0,
    type: 2,
    typeText: '出售',
    campus: '紫金港校区',
    ownerName: '管理学院 胡同学',
    ownerId: 'mock-owner-08',
    ownerVerified: 1,
    viewCount: 118,
    favoriteCount: 16,
    reviewHint: '箱体有轻微划痕，不影响使用，可现场拉杆试推。',
    status: 1,
    statusText: '毕业季特价',
    coverImage: buildImage('78350f', 'fed7aa', 'Suitcase'),
    images: [
      buildImage('78350f', 'fed7aa', 'Suitcase'),
      buildImage('c2410c', 'fff7ed', 'Trip')
    ]
  }
]

const cloneItem = (item) => {
  return {
    ...item,
    images: (item.images || []).slice()
  }
}

const cloneList = (list) => list.map((item) => ({ ...item }))

const getHomeConfig = () => {
  return {
    categories: HOME_CONFIG.categories.slice(),
    banners: cloneList(HOME_CONFIG.banners),
    announcements: cloneList(HOME_CONFIG.announcements)
  }
}

const listMockItems = (params = {}) => {
  const keyword = String(params.keyword || '').trim().toLowerCase()
  const page = Number(params.page || 1)
  const size = Number(params.size || MOCK_ITEMS.length)
  const { type, category, status } = params

  const records = MOCK_ITEMS.filter((item) => {
    if (typeof status !== 'undefined' && item.status !== status) {
      return false
    }
    if (typeof type !== 'undefined' && item.type !== type) {
      return false
    }
    if (category && item.category !== category) {
      return false
    }
    if (!keyword) {
      return true
    }

    const text = [
      item.title,
      item.category,
      item.campus,
      item.ownerName,
      item.description
    ].join(' ').toLowerCase()

    return text.indexOf(keyword) !== -1
  })

  const total = records.length
  const start = (page - 1) * size

  return {
    records: records.slice(start, start + size).map(cloneItem),
    total,
    current: page,
    size
  }
}

const listMockNearbyItems = (params = {}) => {
  const campus = String(params.campus || '').trim()
  const limit = Number(params.limit || 4)
  const sameCampus = campus ? MOCK_ITEMS.filter((item) => item.campus === campus) : []
  const otherCampus = MOCK_ITEMS.filter((item) => item.campus !== campus)

  return sameCampus.concat(otherCampus).slice(0, limit).map(cloneItem)
}

const getMockItemDetail = (id) => {
  const record = MOCK_ITEMS.find((item) => String(item.id) === String(id))
  return record ? cloneItem(record) : null
}

module.exports = {
  isEnabled: isMockEnabled,
  getHomeConfig,
  listMockItems,
  listMockNearbyItems,
  getMockItemDetail
}
