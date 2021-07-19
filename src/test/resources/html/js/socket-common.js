function setSign(c) {
    $('#sign').html(JSON.stringify(c, null, 2));
}

function setContent(c) {
    $('#content').html(c);
}

function appendContent(c) {
    let $content = $('#content');
    const content = $content.html();
    $content.html(content + JSON.stringify(c, null, 2) + '<br><br>');
}

function addEvent() {
    socket.on('connect', function () {
        setSign('connect');
    });

    socket.on('disconnect', function () {
        setSign('disconnect');
    });

    socket.on('test', function (data, fn) {
        appendContent(data);
        fn('{"id":1}');
    });
}

function test() {
    appendContent('test');
}

function sendMsg() {
    let msg = {
        "id": 6
    };
    socket.emit('test', msg, function (data) {
        appendContent(data);
    });
}

function req() {
    $.ajax({
        type: 'get',
        timeout: 10000,
        headers: {
            'VVVV': 'vv'
        },
        url: 'http://jz.union-wifi.com:8003/room/6588/danmu?source=2',
        success: function (data) {
        },
        error: function (err) {
        },
        complete: function (XMLHttpRequest, status) { //请求完成后最终执行参数　
        }
    });
}
