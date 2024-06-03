document.addEventListener('DOMContentLoaded', function() {
    const btnSubmit = document.querySelector('.generateAsymmetric');

    // 비대칭 키 생성하기 버튼 클릭 시 동작
    btnSubmit.addEventListener('click', function() {
        const publicKeyFile = document.getElementById('publicKeyFile');
        const privateKeyFile = document.getElementById('privateKeyFile');

        if (publicKeyFile.value === "") {
            alert("공개키 파일명을 입력해주세요.");
            return false;
        }

        if(privateKeyFile.value === "") {
            alert("사설키 파일명을 입력해주세요.");
            return false;
        }

        return true;
    });
});