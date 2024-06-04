document.addEventListener('DOMContentLoaded', function() {
    const messageElement = document.getElementById('message');
    const message = messageElement.innerText.trim();

    // 서버에서 전달한 메시지가 존재하면 alert 창으로 표시
    if (message) {
        alert(message);
    }
});