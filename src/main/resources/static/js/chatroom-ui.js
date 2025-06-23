document.addEventListener('DOMContentLoaded', function () {
    const chatBox = document.getElementById("chatBox");
    if (chatBox) chatBox.scrollTop = chatBox.scrollHeight;

    const fileInput = document.querySelector('.chat-input-area input[type="file"]');
    const fileNameDisplay = document.getElementById('selectedFileName');

    fileInput.addEventListener('change', function () {
        fileNameDisplay.textContent = fileInput.files.length > 0 ? fileInput.files[0].name : "";
    });

    const form = document.querySelector('.chat-input-area');
    const textInput = form.querySelector('input[name="message"]');

    form.addEventListener('submit', async function (event) {
        event.preventDefault();

        const text = textInput.value.trim();
        const file = fileInput.files.length > 0 ? fileInput.files[0] : null;

        if (text.length === 0 && !file) return;

        await sendMessageWithFile(text, file);
        textInput.value = '';
        fileInput.value = '';
        fileNameDisplay.textContent = '';
    });
});

function showMessage(msg) {
    const currentUserName = document.getElementById('app-data').dataset.currentUserName;
    const isMe = msg.from === currentUserName;
    const chatBox = document.getElementById("chatBox");

    const messageRow = document.createElement("div");
    messageRow.className = "chat-message-row" + (isMe ? " me" : "");

    const avatar = isMe ? "" : `<img class="chat-avatar" src="/img/bear.png" alt="Avatar" />`;

    const fileLink = msg.fileId && msg.filename
        ? msg.filename.match(/\.(jpg|jpeg|png|gif)$/i)
            ? `<img class="image-preview" src="/chatrooms/files/${msg.fileId}/download" />`
            : `<a href="/chatrooms/files/${msg.fileId}/download" download>${msg.filename}</a>`
        : "";

    messageRow.innerHTML = `
        ${avatar}
        <div>
            <div class="chat-bubble">
                ${!isMe ? `<span class="sender-name">${msg.from}</span>` : ""}
                ${fileLink}
                <div>${msg.text}</div>
                <div class="chat-meta">${msg.time}</div>
            </div>
        </div>
    `;

    chatBox.appendChild(messageRow);
    chatBox.scrollTop = chatBox.scrollHeight;
}
