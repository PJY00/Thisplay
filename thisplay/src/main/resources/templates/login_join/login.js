// document.addEventListener("DOMContentLoaded", () => {
//   const form = document.getElementById("login-form");

//   // 이전 페이지로 돌아가긴
//   const previousPage = document.referrer; // 바로 직전 페이지 URL
//   console.log("이전 페이지:", previousPage);

//   form.addEventListener("submit", async (e) => {
//     e.preventDefault();

//     const username = document.getElementById("log-username").value;
//     const password = document.getElementById("log-password").value;

//     try {
//       const res = await axios.post("/login", {
//         username: username,
//         password: password,
//       });

//       const data = res.data;
//       if (data.valid) {
//         alert("로그인 성공!");

//         // 로그인 시 페이지 이동
//         if (previousPage && !previousPage.includes("/login")) {
//           window.location.href = previousPage;
//         } else {
//           window.location.href = "/main";
//         }
//       } else {
//         alert("로그인 실패: " + data.message);
//       }
//     } catch (err) {
//       alert("로그인 중 오류 발생");
//       console.error(err);
//     }
//   });
// });
// import { api } from "../../static/js/api/axiosInstance.js";

// document.addEventListener("DOMContentLoaded", () => {
//   const form = document.getElementById("login-form");
//   const googleBtn = document.getElementById("google-login-btn");

//   // 일반 로그인
//   form.addEventListener("submit", async (e) => {
//     e.preventDefault();
//     const username = document.getElementById("log-username").value;
//     const password = document.getElementById("log-password").value;

//     // axios POST 요청
//     const res = await axios.post("http://localhost:8080/login", {
//       nickname: username,
//       password: password,
//     });

//     if (res.data.message === "로그인 성공") {
//       alert("로그인 성공!");
//       // window.location.href = "mainpage.html"; // redirect
//       window.location.href = "../mainpage/mainpage.html";
//     } else {
//       alert("로그인 실패: " + res.data.message);
//     }
//   });

//   // Google 로그인 버튼
//   googleBtn.addEventListener("click", () => {
//     window.location.href = "http://localhost:8080/oauth2/authorization/google";
//   });
// });

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
      // axiosInstance 사용
      const res = await api.post("/login", {
        nickname: username,
        password: password,
      });

      console.log("로그인 응답:", res.data);

      if (res.data.message === "로그인 성공") {
        const accessToken = res.data.data.accessToken;

        // token 저장
        localStorage.setItem("token", accessToken);

        alert("로그인 성공!");
        window.location.href = "../mainpage/mainpage.html";
      } else {
        alert("로그인 실패: " + res.data.message);
      }
    } catch (err) {
      console.error("로그인 오류:", err);
      alert("로그인 중 오류가 발생했습니다.");
    }
  });

  // Google 로그인
  googleBtn.addEventListener("click", () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  });
});
