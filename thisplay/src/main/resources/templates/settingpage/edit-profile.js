import api from "../../static/js/api/axiosInstance.js";
import { getToken, isLoggedIn, logout } from "../../static/js/utils/auth.js";

document.addEventListener("DOMContentLoaded", async () => {
    const container = document.querySelector(".profile-card");

    if (!isLoggedIn()) {
        alert("로그인이 필요합니다.");
        location.href = "/login";
        return;
    }

    container.innerHTML = "<p>불러오는 중...</p>";

    try {
        const res = await api.get("/api/users/me", {
            headers: { Authorization: `Bearer ${getToken()}` }
        });

        const user = res.data;

        container.innerHTML = `
            <h2>회원정보 수정</h2>

            <div class="profile-img-box">
                <img id="profilePreview" src="${user.profileImgUrl}" alt="프로필">
                <br>
                <label class="profile-label">프로필 사진 변경</label>
                <input type="file" id="profileImage" accept="image/*">
            </div>

            <div class="profile-row">
                <span class="profile-label">가입일</span><br>
                <span class="profile-value">${user.createdAt}</span>
            </div>

            <div class="profile-row">
                <label class="profile-label" for="nicknameInput">닉네임</label><br>
                <input type="text" id="nicknameInput" value="${user.nickname}">
            </div>

            <button class="save-btn">저장하기</button>
        `;
    } catch (err) {
        console.error("회원정보 불러오기 실패:", err);
        container.innerHTML = "<p>오류가 발생했습니다.</p>";
    }
});

// 사진 미리보기
document.addEventListener("change", (e) => {
    if (e.target.id !== "profileImage") return;

    const file = e.target.files[0];
    if (!file) return;

    const preview = document.getElementById("profilePreview");
    preview.src = URL.createObjectURL(file);
});

// 저장하기 버튼
document.addEventListener("click", async (e) => {
    if (!e.target.classList.contains("save-btn")) return;

    const nickname = document.getElementById("nicknameInput").value;
    const fileInput = document.getElementById("profileImage");

    const formData = new FormData();
    formData.append("nickname", nickname);

    if (fileInput.files[0]) {
        formData.append("profileImage", fileInput.files[0]);
    }

    try {
        await api.put("/api/users/me/profile", formData, {
            headers: {
                Authorization: `Bearer ${getToken()}`,
                "Content-Type": "multipart/form-data"
            }
        });

        alert("회원정보가 수정되었습니다!");
        location.reload();

    } catch (err) {
        console.error("수정 실패:", err);
        alert("수정 중 오류 발생");
    }
});