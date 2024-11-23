$(function () {
    var hhh = $(window).height();
    var www = $(window).width();
    //普通气泡消息
    $('body').append(
        '       <a id="_msg_btn" data-role="button" class="ui-content2"' +
        '           style="width:200px;position: absolute;padding:20px;color:white;' +
        '            margin-left:' + (www - 240) / 2 + 'px;margin-top:' + (hhh / 2 - 100) + 'px;' +
        '          background-color: #5a5a5a;text-align: center;z-index: 9999;display: none">' +
        '        </a>'
    );
    $('body').append('<div id="mask" style="width:' + www + 'px;height:' + hhh + 'px;position: absolute;z-index: 99999;background:url(../img/ajax-loader.gif) no-repeat center 50%;background-color: #ccc;opacity: 0.6;display:none"></div>');
    //确认弹窗，方式一
    //使用方式：参考 tripReq.html
    $('body').find('div').eq(0).append(
        '<div data-role="popup" id="myPopupDialog" style="width:300px;background-color:white;border:0px;">  ' +
        '    <div data-role="main" class="ui-content">' +
        '    <h3 style="text-align:center">请确认</h3>' +
        '    <p id="popup-msg" style="margin:30px;"></p>' +
        '    <p style="text-align:right;">  ' +
        '<a data-role="button" data-mini="true" data-inline="true" data-rel="back" style="border:1px solid #ccc;"' +
        '   class="ui-link ui-btn ui-btn-inline ui-shadow ui-corner-all ui-mini" role="button">取消</a>  ' +
        '<button id="popup-yes" data-mini="true" data-inline="true" ' +
        '   class="ui-btn-active ui-btn ui-btn-inline ui-shadow ui-corner-all ui-mini">确定</button>' +
        '    </p></div>' +
        '</div>'
    ).trigger('create');
    $('#popup-yes').click(function () {
        confirmCallBack($(this).data('btn-id'));
    });
    $('.popup-btn').bind('tap', function () {
        console.log($(this).attr('show-txt'));
        $('#popup-msg').text($(this).attr('show-txt'));
        $('#popup-yes').data('btn-id', $(this).attr('id'));
    });
})

//确认弹窗，方式二
//使用方式：参考 driver/tripDetail.html
function confirmInfo(id, info) {
    console.log(id + "," + info);
    $('#popup-msg').text(info);
    $('#popup-yes').data('btn-id', id);
}

//气泡提示信息
function showMsg(txt) {
    $('#_msg_btn').text(txt).fadeIn(500);
    setTimeout(
        function () {
            $('#_msg_btn').fadeOut(500);
        },
        1500
    );
    _msg_btn.scrollIntoView(); //确保弹窗可见，防止页面太长跑到顶部
}

//等待time毫秒后，跳到url，无缓存
function toUrl(url, time) {
    setTimeout(
        function () {
            var t = (url.indexOf('?') == -1) ? '?' : '&';

            window.location.href = url + t + 't=' + Math.random();
        },
        time
    );
}

//切换角色，乘客还是司机（页面）
function changeRole() {
    var href = window.location.href;
    if (href.indexOf('/driver/') != -1) {
        //切换成乘客
        href = href.replace('/driver/', '/user/');
        localStorage.setItem('current_role', 0);
    } else {
        //切换成司机
        if (current_user.role == 0 || current_user.status == 0) {
            showMsg('您需要先实名认证，并申请为车主！');
            return;
        }
        href = href.replace('/user/', '/driver/');
        localStorage.setItem('current_role', 1);
    }
    toUrl(href);
}


//重写jquery的ajax方法，自动附带token信息，并处理登录问题
//首先备份下jquery的ajax方法  
var _ajax = $.ajax;

//重写jquery的ajax方法，携带登录信息
$.ajax = function (opt) {
    //备份opt中error和success方法 
    var fn = {
        error: function (XMLHttpRequest, textStatus, errorThrown) { },
        success: function (data, textStatus) { }
    }
    if (opt.error) {
        fn.error = opt.error;
    }
    if (opt.success) {
        fn.success = opt.success;
    }

    //扩展增强处理 
    var _opt = $.extend(opt, {
        error: function (XMLHttpRequest, textStatus, errorThrown) {

            if (XMLHttpRequest.status == 401) {
                showMsg('登录信息验证失败');
                toUrl('/web/login.html', 1000);
            } else if (XMLHttpRequest.status > 500) {
                showMsg('服务器请求失败');
            }
            //错误方法增强处理 
            fn.error(XMLHttpRequest, textStatus, errorThrown);
        },
        success: function (data, textStatus) {
            //成功回调方法增强处理  
            fn.success(data, textStatus);
        },
        beforeSend: function (xhr) {
            xhr.setRequestHeader('SESSION_TOKEN_KEY', localStorage.getItem('SESSION_TOKEN_KEY'));
            // xhr.setRequestHeader('X-Client',"PC");
            xhr.setRequestHeader('Content-Type', "application/json");
        }
    });
    return _ajax(_opt);
};


//当前登录用户 - 缓存
var current_user;
//开启ws，用于即时消息通信                    
var websocket;

//校验token是否有效，这个ajax会被上面的ajax增强方法拦截，如果401上面直接跳去了登录页
function getUser() {
    if (location.href.indexOf('login.html') == -1) {
        var token = localStorage.getItem('SESSION_TOKEN_KEY');
        if (current_user) return current_user;
        $.ajax({
            type: "POST",
            url: "/account/api/verifyToken",
            cache: false,
            async: false,
            // timeout: 3000, //异步有效
            headers: {
                SESSION_TOKEN_KEY: token
            },
            success: function (data) {
                //验证成功，返回的是当前用户信息
                if (data.code == 200) {
                    current_user = data.data[0];
                    console.log(current_user);

                    //验证用户通过后，开启ws，用于即时消息通信
                    //判断当前浏览器是否支持WebSocket
                    if ('WebSocket' in window) {
                        //截取浏览器地址栏host和端口
                        var l=l = window.location.href;
                        var r=new RegExp('http://(.+)?/web/.*');
                        var h=l.match(r)[1];
                        //拼接ws的地址，走nginx代理转发
                        websocket = new WebSocket('ws://'+h+'/notice/ws/socket?SESSION_TOKEN_KEY='+token);
                    } else {
                        alert('Not support websocket')
                    }
                    websocket.onmessage = function (event) {
                        showMsg('您有新消息，请注意查看！');
                        var d = JSON.parse(event.data);
                        console.log(d);
                        var msgStr = localStorage.getItem('_msg');
                        var msg = [];
                        if (msgStr) {
                            msg = JSON.parse(msgStr) ;
                        }
                        // 压缩消息，去掉没用的属性
                        delete(d['read']);
                        delete(d['receiverUseralias']);
                        delete(d['receiverId']);
                        delete(d['retripIdad']);
                        delete(d['vO']);
                        delete(d['tripId']);
                        delete(d['createdTime']);
                        msg.push(d);
                        localStorage.setItem('_msg', JSON.stringify(msg));
                    }

                } else {
                    showMsg(data.message);
                    toUrl('/web/login.html', 1000);
                }

            },
            error: function (r) {
                console.log(r.status);
            }
        });
        return current_user;
    }
}

//页面根据token获取当前登录用户信息
$(function () {
    getUser();
    // console.log(getUser());
})

//将id对应的form转成json，直接作为ajax的data属性，可用
function formToJson(id) {

    var fields = $(id).serializeArray();
    var obj = {};
    $.each(fields, function (index, field) {
        obj[field.name] = field.value;
    })
    console.log(obj);
    return JSON.stringify(obj);
}

//获取url参数
function getParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]); return null;
}

//扩展jquery，直接把表单ajax post发往后台
//参考：user/user.html
$.postForm = function (url, id, callback) {
    $.ajax({
        type: "POST",
        url: url,
        cache: false,
        data: formToJson(id),
        headers: {
            // SESSION_TOKEN_KEY: token
        },
        success: callback,
        error: function (r) {
            console.log(r.status);
        }
    });
};

//格式化日期的显示
function transDate(date) {
    return date.replace('T', ' ').replace('/-/g', '.').substring(0, 16);
}

//date参数为字符串：2021-07-07T03:38:57.000+0000
function diffDate(date1, date2) {
    console.log(date1 + "," + date2);

    var d1 = new Date(date1);
    var d2 = new Date(date2);

    var difftime = Math.abs((d1 - d2) / 1000); //计算时间差,并把毫秒转换成秒

    var days = parseInt(difftime / 86400); // 天  24*60*60*1000 
    var hours = parseInt(difftime / 3600) - 24 * days;    // 小时 60*60 总小时数-过去的小时数=现在的小时数 
    var minutes = parseInt(difftime % 3600 / 60); // 分钟 -(day*24) 以60秒为一整份 取余 剩下秒数 秒数/60 就是分钟数
    var seconds = parseInt(difftime % 60);  // 以60秒为一整份 取余 剩下秒数

    return days + '天' + hours + '小时' + minutes + '分';

}

//格式化字符串，替代内部的字符位 '{0}is{1}'.format('cat','dog');
//用于ajax动态生成页面，参考：user/tripReq.html
String.prototype.format = function (args) {
    if (arguments.length > 0) {
        var result = this;
        if (arguments.length == 1 && typeof (args) == "object") {
            for (var key in args) {
                var reg = new RegExp("({" + key + "})", "g");
                result = result.replace(reg, args[key]);
            }
        } else {
            for (var i = 0; i < arguments.length; i++) {
                if (arguments[i] == undefined) {
                    return "";
                } else {
                    var reg = new RegExp("({[" + i + "]})", "g");
                    result = result.replace(reg, arguments[i]);
                }
            }
        }
        return result;
    } else {
        return this;
    }
}
//将template模板下的内容用data渲染后放到element里去
//参考：driver/order.html
function renderByTemplate(element, template, data) {
    // console.log($(template).html());
    $(element).append($(template).html().format(data));
}

//遮罩
function showMask(loadingText) {
    $("#mask").show();
}

//隐藏遮罩
function hideMask() {
    $("#mask").hide();
}



function random(minNum, maxNum) {
    switch (arguments.length) {
        case 1:
            return parseInt(Math.random() * minNum + 1, 10);
            break;
        case 2:
            return parseInt(Math.random() * (maxNum - minNum + 1) + minNum, 10);
            break;
        default:
            return 0;
            break;
    }
}

var tripStatus = { 0: '邀请中', 1: '已确认同行', 2: '已上车', 3: '已下车', 4: '已取消' };
var driverStatus = { 0: '邀请中', 1: '已发车', 3: '已送达' };
var orderStatus = { 0: '临时订单', 1: '未支付', 2: '已支付' };