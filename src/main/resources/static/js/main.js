'use strict';

const joinPage = document.querySelector('#join-page');
const chatPage = document.querySelector('#chat-page');
const joinFrom = document.querySelector('#joinFrom');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');
const fileInput = document.getElementById('file-input');
const promptMessage = document.getElementById('promptMessage');
const uploadButton = document.getElementById('upload-button');


let stompClient = null;
let username = null;

const colors = [
    '#DBA979', '#C7B7A3', '#EF9595', '#7469B6',
    '#A87676', '#A0937D', '#B4B4B8', '#B8B2A6'
];

document.addEventListener('DOMContentLoaded', function () {
    fetchMessages();
});

// 消息提示函数
function showPromptMessage(message, duration = 2000) {
    promptMessage.style.display = 'block';
    promptMessage.innerText = message;
    setTimeout(() => {
        promptMessage.style.display = 'none';
    }, duration);
}

function fetchMessages() {
    fetch('/api/messages')
        .then(response => response.json())
        .then(messages => {
            messages.forEach(message => {
                renderMessage(message); // 调用共享的消息展示函数
            });
        })
        .catch(error => {
            console.error('Error fetching messages:', error);
        });
}

function renderMessage(message) {
    let messageElement = document.createElement('li');

    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        let avatarElement = document.createElement('i');
        let avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        let usernameElement = document.createElement('span');
        let usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    let contentElement;
    if (message.type === 'FILE') {
        contentElement = document.createElement('a');
        contentElement.textContent = message.content;
        contentElement.id = 'file-down';
        contentElement.addEventListener('click', function(event) {
            event.preventDefault();
            downloadFile(message.content);
        });
    } else {
        contentElement = document.createElement('p');
        contentElement.textContent = message.content;
    }

    messageElement.appendChild(contentElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function onMessageReceived(payload) {
    let message = JSON.parse(payload.body);
    renderMessage(message);
}

function connect(event) {
    username = document.querySelector('#name').value.trim();

    if (username) {
        joinPage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        let socket = new SockJS('/ws');
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
    connectingElement.textContent = 'Connection failed, please refresh the page';
    connectingElement.style.color = '#F40002';
}

function sendMessage(event) {
    let messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        let chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


// 上传文件按钮点击事件
function handleFileUpload() {
    const fileInput = document.getElementById('file-input');
    fileInput.click();

    fileInput.onchange = function() {
        const file = fileInput.files[0];
        if (!file) {
            showPromptMessage('Please upload files');
            return;
        }

        uploadFile(file);
    }
}

// 上传文件
function uploadFile(file) {
    const formData = new FormData();
    formData.append('file', file);

    showPromptMessage('uploading');

    fetch('/api/upload', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (response.ok) {
                return response.text();
            }
            throw new Error('Upload failed');
        })
        .then(fileName => {
            let chatMessage = {
                sender: username,
                content: fileName,
                type: 'FILE'
            };
            stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
            fileInput.value = '';

            showPromptMessage('Upload completed');
        })
        .catch(error => {
            console.error('Error:', error);
            showPromptMessage('Upload failed, file size should not exceed 16MB');
        });
}


// 下载文件
function downloadFile(fileName) {
    showPromptMessage('downloading');

    fetch(`/api/download/${encodeURIComponent(fileName)}`, {
        method: 'GET',
    })
        .then(response => {
            if (response.ok) {
                return response.blob();
            }
            throw new Error('Network response was not ok.');
        })
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.style.display = 'none';
            a.href = url;
            a.download = fileName;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            showPromptMessage('Download completed');
        })
        .catch(error => {
            console.error('Download error:', error);
            showPromptMessage('Download failed please check network connection');
        });
}

function getAvatarColor(messageSender) {
    let hash = 0;
    for (let i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    let index = Math.abs(hash % colors.length);
    return colors[index];
}

joinFrom.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)
uploadButton.addEventListener('click', handleFileUpload);