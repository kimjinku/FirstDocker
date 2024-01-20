var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#savemessages").html("");

}

function connect() {
    var socket = new SockJS('/aaa');
    stompClient = Stomp.over(socket);
    console.log(stompClient);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messaging', function (messaging) {
            showMessaging(JSON.parse(messaging.body).content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}


function sendContent() {
    stompClient.send("/app/hello", {}, JSON.stringify({'content': $("#content").val()}));
}

function showMessaging(message) {
    $("#savemessages").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendContent(); });
});