var sock = null;
//服务器的地址
var wsuri = "ws://10.7.7.198:8081/websocket";
window.onload = function() {
    console.log("onload");
    sock = new WebSocket(wsuri);
    sock.onopen = function() {
        //成功连接到服务器
        console.log("connected to " + wsuri);
    }
    sock.onclose = function(e) {
        console.log("connection closed (" + e.code + ")");
    }
    sock.onmessage = function(e) {
        //服务器发送通知
        //开始处理
        console.log("message received: " + e.data);
    }
};

function send() {
    var msg = document.getElementById('message').value;
    sock.send(msg);
};