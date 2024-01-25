var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
//    if (connected) {
//        $("#conversation").show();
//    }
//    else {
//        $("#conversation").hide();
//    }
//    $("#savemessages").html("");
}

function connect() {
    var socket = new SockJS('/ws-stomp');
    stompClient = Stomp.over(socket);
    console.log(stompClient);

    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/sub/messaging', function (messaging) {
            showMessaging(JSON.parse(messaging.body));
        });
    });
}

function sendConnectionStatus(status) {
        stompClient.send("/pub/connect-status", {}, status.toString());
    }



function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendContent() {//이게  sendmessage!!!!!!!!!!
    // content 입력 필드에서 값 가져오기
    var contentValue = $("#content").val();
    // 템플릿의 id=receiver 에서 값 가져오기
    var receiverName = $("#receiver").val();
    var createDate = $("#createDate").val();

    console.log(typeof createDate);


    // contentValue와 receiverName 포함한 메시지 객체 생성
        var message = {
            'content': contentValue,
            'receiver': receiverName,
            'createDate' : createDate
        };

//    stompClient.send("/app/hello", {}, JSON.stringify(message));

    // stompClient가 정의되어 있고 연결이 성공적인 경우에만 send 호출
    if (stompClient && stompClient.connected) {
        stompClient.send("/pub/hello", {}, JSON.stringify(message));
        stompClient.send("/pub/connect-status", {}, JSON.stringify(message));
    } else {
        console.error('WebSocket connection is not established.');
    }

}

function sendConnectionStatus(status) {
        var status = 'sender.connected';
        stompClient.send("/pub/connect-status", {}, status.toString());
    }

function showMessaging(message, myprofileName) { //이게  savemessage인듯

    let reg = /[`~!@#$%^&*()_|+\-=?;:'"<>\{\}\[\]\\\/ ]/gim;
    let myprofileNameReg = myprofileName.replace(reg, "");


    console.log(message.author);
    console.log(myprofileNameReg);
    console.log(typeof message.author);
    console.log(typeof myprofileName);

    // author가 myprofileNameReg와 일치하는지 확인하여 조건에 따라 id가 Me 또는 You인 balloon을 동적으로 생성
         var balloonHTML;
         if (message.author === myprofileNameReg) {
             // 보낸메시지
             balloonHTML =  '<div class="msg right-msg" id="scrollToAnchor">' +
                            '<div class="msg-bubble">' +

                             '<div class="msg-text" th:text="${message.content}">' +
                                 message.content +
                             '</div>' +
                           '</div>'
                           '</div>';

         } else {
             // 받은메시지
            balloonHTML =  '<div class="msg left-msg" id="scrollToAnchor">' +
                                    '<div class="msg-bubble">' +

                                     '<div class="msg-text" th:text="${message.content}">' +
                                         message.content +
                                     '</div>' +
                                   '</div>'
                                   '</div>';
         }

         // 생성된 HTML을 #savemessages에 추가
         $("#savemessages").append(balloonHTML);

         // 메시지 추가 후에 스크롤하도록
         var anchorElement = document.getElementById('scrollToAnchor');
         if (anchorElement) {

             anchorElement.scrollIntoView({ behavior: 'auto' });
         }

}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
//    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#btn-chat" ).click(function() { sendContent(); });
});



// 클라이언트 1
const ws1 = new WebSocket('ws://서버주소');
ws1.onopen = function() {
    // 서버에 연결 상태 전송
    sendConnectionStatus(true);
};
ws1.onclose = function() {
    // 서버에 연결 상태 전송
    sendConnectionStatus(false);
};

function sendConnectionStatus(status) {
    // 서버에 연결 상태 전송
    ws1.send(JSON.stringify({ type: 'connection_status', isConnected: status }));
}

// 클라이언트 2
const ws2 = new WebSocket('ws://서버주소');
ws2.onmessage = function(event) {
    const data = JSON.parse(event.data);
    if (data.type === 'connection_status') {
        // 다른 클라이언트의 연결 상태를 확인
        console.log('다른 클라이언트의 연결 상태:', data.isConnected);
    }
};
