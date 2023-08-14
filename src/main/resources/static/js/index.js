'use strict';

String.prototype.hashCode = function () {
    let value = this.md5(this, true);
    value = value.substring(0, value.length <= this.length ? value.length : this.length);
    let h = 0;

    if (value.length > 0) {
        for (let i = 0; i < value.length; i++) {
            h = 31 * h + value.charCodeAt(i);
        }
    }
    return h;
}

String.prototype.hash = function () {
    const h = this.hashCode();
    return h === 0 ? 0 : (h ^ (h >>> 16));
}

String.prototype.md5 = function (key, flag) {
    return md5(this, key, flag);
}

String.prototype.indexForArray = function(arrayLength) {
    return (arrayLength - 1) & this.hash();
}

function get(url, callback, params) {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', url);
    xhr.send();

    xhr.onreadystatechange = function() {
        if ( this.readyState === 4 && this.status === 200 ) {
            if (typeof(callback) === 'function') {
                callback(JSON.parse(this.response));
            }
        }
    }
}

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');

const CONTEXT_PATH = "/websocket";

let stompClient = null;
let username = null;

const colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

let len = colors.length;
for (let i = len - 1; i >= 0; i -= 1) {
    colors.push(colors[i]);
}


function connect(event) {
    username = document.querySelector('#name').value.trim();

    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        const socket = new SockJS(`${CONTEXT_PATH}/ws`);
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        const chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    console.log(`onMessageReceived: ${payload.body}`)

    const messageElement = document.createElement('li');

    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        const avatarElement = document.createElement('i');
        const avatarText = document.createTextNode(message.sender);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        const usernameElement = document.createElement('span');
        const usernameText = document.createTextNode(message.sender);
        const timeSpan = document.createElement("span");
        const portSpan = document.createElement("span");
        let now = Date.now() % 1000;
        now = now < 10 ? `00${now}` : (now < 100 ? `0${now}` : now);
        const timeText = document.createTextNode(new Date().toLocaleString() + `.${now}`);
        timeSpan.appendChild(timeText);
        timeSpan.className = "item-time";
        const portText = document.createTextNode(`[${message.port || 0}]`);
        portSpan.appendChild(portText);
        portSpan.className = 'port';
        usernameElement.appendChild(usernameText);
        usernameElement.appendChild(portSpan)
        usernameElement.appendChild(timeSpan)
        messageElement.appendChild(usernameElement);
    }

    const textElement = document.createElement('p');
    const messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


function getAvatarColor(messageSender) {
    return colors[messageSender.indexForArray(colors.length)];
}

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)

get(`${CONTEXT_PATH}/getPort`, (data) => {
    const nameDom = document.getElementById("name");
    nameDom.value = data.port;
});