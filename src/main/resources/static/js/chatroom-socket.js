let fileUploading = false;
let chatroomId = null;
let stompClient = null;

function disableSendButton(disabled) {
    const sendBtn = document.querySelector('.chat-input-area button[type="submit"]');
    if (sendBtn) sendBtn.disabled = disabled;
}

function connect() {
    chatroomId = document.getElementById('app-data').dataset.chatroomId;

    if (!chatroomId) {
        console.error("Chatroom ID not found.");
        return;
    }

    const socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function () {
        stompClient.subscribe(`/topic/messages/${chatroomId}`, function (messageOutput) {
            const msg = JSON.parse(messageOutput.body);
            showMessage(msg);
        });

        stompClient.subscribe(`/topic/presence/${chatroomId}`, function (presenceEvent) {
            const presence = JSON.parse(presenceEvent.body);
            showPresenceNotification(presence);
            fetchOnlineUsers();
        });

        stompClient.send("/app/join", {}, JSON.stringify({ chatroomId: chatroomId }));

        fetchOnlineUsers();
    });
}

function showPresenceNotification(presence) {
    const currentUserName = document.getElementById('app-data').dataset.currentUserName;
    const user = presence.username;
    const type = presence.type;

    if (user === currentUserName) return;

    const alertBox = document.createElement("div");
    alertBox.className = "alert alert-info text-center py-2";
    alertBox.textContent = `${user} has ${type === 'JOIN' ? 'joined' : 'left'} the chat`;

    const chatBox = document.getElementById("chatBox");
    chatBox.appendChild(alertBox);

    setTimeout(() => alertBox.remove(), 8000);
}

function fetchOnlineUsers() {
    fetch(`/presence/online/chatroom/${chatroomId}`)
        .then(response => response.json())
        .then(users => {
            const container = document.getElementById('onlineUsers');
            container.textContent = users.length === 0 ? "No one online" : users.join(", ");
        })
        .catch(err => console.error("Failed to load online users", err));
}

async function uploadFile(file) {
    fileUploading = true;
    disableSendButton(true);
    document.getElementById('uploadSpinner').style.display = 'inline';

    const formData = new FormData();
    formData.append("file", file);

    try {
        const response = await fetch(`/chatrooms/${chatroomId}/upload`, {
            method: "POST",
            body: formData
        });

        if (!response.ok) throw new Error("File upload failed");
        return await response.json();
    } finally {
        fileUploading = false;
        disableSendButton(false);
        document.getElementById('uploadSpinner').style.display = 'none';
    }
}

async function sendMessageWithFile(messageText, file) {
    const currentUserId = document.getElementById('app-data').dataset.currentUserId;
    const currentUserName = document.getElementById('app-data').dataset.currentUserName;
    if (fileUploading) return alert("Please wait until the file finishes uploading.");

    let fileData = null;
    if (file) {
        try {
            fileData = await uploadFile(file);
        } catch (err) {
            alert(err.message);
            return;
        }
    }

    const message = {
        fromId: currentUserId,
        from: currentUserName,
        text: messageText,
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        chatroomId: chatroomId,
        fileId: fileData?.fileId || null,
        filename: fileData?.filename || null
    };

    stompClient.send("/app/chat", {}, JSON.stringify(message));
}