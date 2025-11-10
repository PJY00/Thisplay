// join.js
document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("signup-form");

  form.addEventListener("submit", async (e) => {
    e.preventDefault(); // 페이지 리로드 차단

    const nickname = document.getElementById("sign-username").value.trim();
    const password = document.getElementById("sign-password").value.trim();
    const passwordCheck = document
      .getElementById("sign-password-check")
      .value.trim();

    if (!nickname || !password || !passwordCheck) {
      alert("모든 필드를 입력해주세요.");
      return;
    }
    if (password !== passwordCheck) {
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }

    try {
      // 서버로 POST 요청 (JSON)
      const res = await axios.post("/signup", {
        nickname: nickname,
        password: password,
      });

      const data = res.data;
      console.log("서버 응답:", data);

      if (data.valid) {
        alert("회원가입 성공");
        // 로그인 페이지 또는 직전 페이지로 이동
        window.location.href = "/login";
      } else {
        alert("회원가입 실패: " + data.message);
      }
    } catch (err) {
      console.error("회원가입 요청 중 오류 발생:", err);
      alert("서버와 통신 중 문제가 발생했습니다.");
    }
  });
});
