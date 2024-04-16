//index.js
//获取应用实例
const app = getApp()

Page({
  data: {
    url:'',
    motto: 'Hello World',
    userInfo: {},
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo')
  },
  deal_with_msg: function (e) {
    console.log("ok");
    var data = e.detail.data
    this.setData({
      name: data.name,
      age: data.age
    })
  },
  
  //事件处理函数
  bindViewTap: function() {
    wx.navigateTo({
      url: '../logs/logs'
    })
  },
  onLoad: function (option) {
    // wx.redirectTo({//失败的话，直接重定向到页面，并且不带任何参数
    //   url: '../soundrecord/soundrecord'
    // })
    var ercode = option.ercode;
    var uid = option.uid;
	  const wechatapp = this;
    wechatapp.setData({
      url: "http://localhost/web"
    })
  },
  onShareAppMessage() {
    const promise = new Promise(resolve => {
      setTimeout(() => {
        resolve({
          title: '自定义转发标题'
        })
      }, 2000)
    })
    return {
      title: '自定义转发标题',
      path: '/page/user?id=123',
      promise 
    }
},

  getUserInfo: function(e) {
    console.log(e)
    app.globalData.userInfo = e.detail.userInfo
    this.setData({
      userInfo: e.detail.userInfo,
      hasUserInfo: true
    })
  }
})
