// import { isLoggedIn, logout, saveToken } from "./auth.js";

// // [테스트용] 로그인 상태 강제 유지
// if (!localStorage.getItem("token")) {
//   saveToken("dummy_token");
// }

// document.getElementById("dev-login").addEventListener("click", () => {
//   saveToken("dummy_token");
//   alert("로그인된 상태로 전환되었습니다.");
//   location.reload();
// });

// window.addEventListener("DOMContentLoaded", () => {
//   const loginBtn = document.getElementById("login-btn");
//   const profileMenu = document.getElementById("profile-menu");
//   const profileImg = document.getElementById("profile-img");
//   const dropdown = document.querySelector(".dropdown");
//   const logoutBtn = document.getElementById("logout-btn");

//   // 로그인 여부에 따라 전환
//   if (isLoggedIn()) {
//     loginBtn.classList.add("hidden");
//     profileMenu.classList.remove("hidden");
//   } else {
//     loginBtn.classList.remove("hidden");
//     profileMenu.classList.add("hidden");
//   }

//   // 프로필 클릭 시 드롭다운 열기/닫기
//   profileImg.addEventListener("click", () => {
//     dropdown.classList.toggle("hidden");
//   });

//   // 로그아웃 클릭 시
//   logoutBtn.addEventListener("click", (e) => {
//     e.preventDefault();
//     logout();
//   });
// });

// import { isLoggedIn, logout, saveToken } from "./utils/auth.js";

// // [테스트용] 로그인 상태 강제 유지
// if (!localStorage.getItem("token")) {
//   saveToken("dummy_token");
// }

// window.addEventListener("DOMContentLoaded", () => {
//   const devLogin = document.getElementById("dev-login");
//   if (devLogin) {
//     devLogin.addEventListener("click", () => {
//       saveToken("dummy_token");
//       alert("로그인된 상태로 전환되었습니다.");
//       location.reload();
//     });
//   }

// const loginBtn = document.getElementById("login-btn");
// const profileMenu = document.getElementById("profile-menu");
// const profileImg = document.getElementById("profile-img");
// const dropdown = document.querySelector(".dropdown");
// const logoutBtn = document.getElementById("logout-btn");

//   // 로그인 여부에 따라 전환
//   if (isLoggedIn()) {
//     loginBtn.classList.add("hidden");
//     profileMenu.classList.remove("hidden");
//   } else {
//     loginBtn.classList.remove("hidden");
//     profileMenu.classList.add("hidden");
//   }

// 프로필 클릭 시 드롭다운 열기/닫기
// profileImg?.addEventListener("click", () => {
//   dropdown.classList.toggle("hidden");
// });

//   // 로그아웃 클릭 시
//   logoutBtn?.addEventListener("click", (e) => {
//     e.preventDefault();
//     logout();
//   });
// });

// document.addEventListener("DOMContentLoaded", () => {
//   const profileImg = document.querySelector("#profile-menu img");
//   const dropdown = document.querySelector(".dropdown");

//   profileImg.addEventListener("click", () => {
//     dropdown.classList.toggle("hidden");
//   });
// });

// window.addEventListener("DOMContentLoaded", () => {
//   const profileImg = document.getElementById("profile-img");
//   const dropdown = document.querySelector("#profile-menu .dropdown");

//   if (!profileImg || !dropdown) return;

//   // 1️⃣ 프로필 클릭 → 열기/닫기
//   profileImg.addEventListener("click", (e) => {
//     e.stopPropagation(); // 문서 전체 클릭 이벤트로 전파되지 않게 막기
//     dropdown.classList.toggle("hidden");
//   });

//   // 2️⃣ 문서 아무 곳이나 클릭하면 닫힘
//   document.addEventListener("click", (e) => {
//     // 클릭한 요소가 dropdown 내부나 profile 이미지가 아니면 닫기
//     if (
//       !dropdown.classList.contains("hidden") &&
//       !dropdown.contains(e.target) &&
//       e.target !== profileImg
//     ) {
//       dropdown.classList.add("hidden");
//     }
//   });
// });

window.addEventListener("DOMContentLoaded", () => {
  const profileMenu = document.getElementById("profile-menu");
  const dropdown = profileMenu.querySelector(".dropdown");

  if (!profileMenu || !dropdown) return;

  // 마우스 들어오면 열기
  profileMenu.addEventListener("mouseenter", () => {
    dropdown.classList.remove("hidden");
  });

  // 마우스 나가면 닫기
  profileMenu.addEventListener("mouseleave", () => {
    dropdown.classList.add("hidden");
  });
});
