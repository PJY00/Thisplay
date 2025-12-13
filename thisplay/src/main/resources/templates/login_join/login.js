import {
  saveAccessToken,
  saveRefreshToken,
} from "../../static/js/utils/auth.js";
import api from "../../static/js/api/axiosInstance.js";

document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("login-form");
  const googleBtn = document.getElementById("google-login-btn");

  // 일반 로그인
  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const username = document.getElementById("log-username").value;
    const password = document.getElementById("log-password").value;

    try {
      // /login 요청
      const res = await api.post("/login", {
        nickname: username,
        password: password,
      });

      console.log("로그인 응답:", res.data);

      if (res.data.message === "로그인 성공") {
        const { accessToken, refreshToken, userId } = res.data.data;

        // 토큰 저장
        saveAccessToken(accessToken);
        saveRefreshToken(refreshToken);

        // userId 저장
        localStorage.setItem("userId", userId);

        alert("로그인 성공!");

        // 이전 페이지로 돌아가기
        const previousPage = localStorage.getItem("previousPage");

        if (previousPage && !previousPage.includes("/login")) {
          window.location.href = previousPage;
        } else {
          window.location.href = "../mainpage/mainpage.html";
        }

        localStorage.removeItem("previousPage");
      } else {
        alert("로그인 실패: " + res.data.message);
      }
    } catch (err) {
      console.error("로그인 오류:", err);

      if (err.response?.status === 401) {
        alert("아이디 또는 비밀번호가 올바르지 않습니다.");
      } else {
        alert("로그인 중 서버 오류가 발생했습니다.");
      }
    }
  });

  // Google 로그인
  googleBtn.addEventListener("click", () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  });
});
