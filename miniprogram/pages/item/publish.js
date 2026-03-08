const { showToast, showLoading, hideLoading } = require('../../utils/util.js')
const { uploadFile } = require('../../utils/request.js')

Page({
  data: {
    categories: ['电子产品', '书籍资料', '运动器材', '生活用品', '服装配饰', '其他'],
    campuses: ['东校区', '西校区', '南校区', '北校区'],
    form: {
      images: [],
      title: '',
      type: 'lease',
      category: '',
      price: '',
      deposit: '',
      campus: '',
      description: ''
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({
      [`form.${field}`]: e.detail.value
    })
  },

  selectType(e) {
    const type = e.currentTarget.dataset.type
    this.setData({
      'form.type': type
    })
  },

  onCategoryChange(e) {
    this.setData({
      'form.category': this.data.categories[e.detail.value]
    })
  },

  onCampusChange(e) {
    this.setData({
      'form.campus': this.data.campuses[e.detail.value]
    })
  },

  chooseImage() {
    const that = this
    wx.chooseImage({
      count: 9 - this.data.form.images.length,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success(res) {
        const tempFilePaths = res.tempFilePaths
        that.uploadImages(tempFilePaths)
      }
    })
  },

  uploadImages(filePaths) {
    showLoading('上传中...')
    const that = this
    let uploadedCount = 0
    const totalCount = filePaths.length
    const newImages = [...this.data.form.images]

    filePaths.forEach((filePath) => {
      uploadFile(filePath)
        .then((res) => {
          newImages.push(res.url)
          uploadedCount++
          if (uploadedCount === totalCount) {
            hideLoading()
            that.setData({
              'form.images': newImages
            })
            showToast('上传成功', 'success')
          }
        })
        .catch((err) => {
          uploadedCount++
          if (uploadedCount === totalCount) {
            hideLoading()
          }
          console.error('上传失败:', err)
        })
    })
  },

  deleteImage(e) {
    const index = e.currentTarget.dataset.index
    const images = this.data.form.images.filter((_, i) => i !== index)
    this.setData({
      'form.images': images
    })
  },

  handleSubmit() {
    const { images, title, type, category, price, campus, description } = this.data.form
    
    if (images.length === 0) {
      showToast('请至少上传一张图片')
      return
    }
    if (!title) {
      showToast('请输入物品名称')
      return
    }
    if (!category) {
      showToast('请选择物品分类')
      return
    }
    if (!price) {
      showToast('请输入价格')
      return
    }
    if (!campus) {
      showToast('请选择校区')
      return
    }
    if (!description) {
      showToast('请输入物品描述')
      return
    }

    showLoading('发布中...')
    setTimeout(() => {
      hideLoading()
      showToast('发布成功', 'success')
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    }, 1000)
  }
})
