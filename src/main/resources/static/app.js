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
<<<<<<< HEAD
    $("#greetings").html("");
=======
    $("#savemessages").html("");
>>>>>>> 5f915483e7b5317df0fc7afa14f58ff4fc9f94cf
}

function connect() {
    var socket = new SockJS('/aaa');
    stompClient = Stomp.over(socket);
    console.log(stompClient);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
<<<<<<< HEAD
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
=======
        stompClient.subscribe('/topic/messaging', function (messaging) {
            showMessaging(JSON.parse(messaging.body).content);
>>>>>>> 5f915483e7b5317df0fc7afa14f58ff4fc9f94cf
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

<<<<<<< HEAD
function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
=======
function sendContent() {
    stompClient.send("/app/hello", {}, JSON.stringify({'content': $("#content").val()}));
}

function showMessaging(message) {
    $("#savemessages").append("<tr><td>" + message + "</td></tr>");
>>>>>>> 5f915483e7b5317df0fc7afa14f58ff4fc9f94cf
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
<<<<<<< HEAD
    $( "#send" ).click(function() { sendName(); });
=======
    $( "#send" ).click(function() { sendContent(); });
>>>>>>> 5f915483e7b5317df0fc7afa14f58ff4fc9f94cf
});