// settingspage.js
import api, { BASE_URL } from "../../static/js/api/axiosInstance.js";
import { getToken, isLoggedIn, logout } from "../../static/js/utils/auth.js";

document.addEventListener("DOMContentLoaded", async () => {
    const profileBtn = document.getElementById("profileBtn");
    const profileImg = profileBtn.querySelector("img");
    const profileName = document.getElementById("profilename");
    const logoutBtn = document.getElementById("logout");

    if (!isLoggedIn()) {
        alert("로그인이 필요합니다.");
        location.href = "/login";
        return;
    }

    try {
        const res = await api.get("/api/users/me/profile", {
            headers: { Authorization: `Bearer ${getToken()}` },
        });

        const user = res.data;

        // 이름 / 닉네임 세팅
        if (user.nickname) {
            profileName.textContent = user.nickname;
        } else if (user.name) {
            profileName.textContent = user.name;
        }

        // 프로필 이미지 세팅
        if (user.profileImgUrl) {
            profileImg.src = user.profileImgUrl;
        }

    } catch (err) {
        console.error("프로필 불러오기 실패:", err.response || err);
        // 실패해도 기본 이미지 + "이름"으로 놔둬도 됨
    }

    // 3️⃣ 프로필 카드 클릭 → 회원정보 수정 페이지 이동
    profileBtn.addEventListener("click", () => {
        location.href = "/edit-profile";
    });

    // 4️⃣ 로그아웃 버튼
    logoutBtn.addEventListener("click", async () => {
        const ok = confirm("정말 로그아웃 하시겠습니까?");
        if (!ok) return;

        try {
            await api.post("/api/auth/logout", null, {
                headers: { Authorization: `Bearer ${getToken()}` },
            });
        } catch (err) {
            console.warn("서버 로그아웃 실패(무시 가능):", err.response || err);
        } finally {
            logout();
            location.href = "/login";
        }
    });
});