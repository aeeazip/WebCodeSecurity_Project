document.addEventListener('DOMContentLoaded', function() {
    const errorMessageElement = document.getElementById('errorMessage');
    const errorMessage = errorMessageElement.innerText.trim();

    // 서버에서 전달한 에러 메시지가 존재하면 alert 창으로 표시
    if (errorMessage) {
        alert(errorMessage);
    }
});