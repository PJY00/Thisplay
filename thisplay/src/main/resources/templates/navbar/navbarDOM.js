// import { logout } from "../../static/js/utils/auth.js";
// function createNavbar() {
//   // css 삽입
//   const link = document.createElement("link");
//   link.rel = "stylesheet";
//   // link.href = "../navbar/navbar.css"; // 경로는 필요에 따라 수정
//   link.href = "/thisplay/src/main/resources/templates/navbar/navbar.css"; // 경로는 필요에 따라 수정
//   document.head.appendChild(link);

//   // HTML 삽입
//   const navbarHTML = `
//     <nav class="navbar">
//       <div class="nav-left">
//         <div class="logo">THISPLAY</div>
//       </div>

//       <div class="nav-center">
//         <div class="search-box">
//           <input
//             type="text"
//             id="search-input"
//             placeholder="검색어를 입력하세요..."
//           />
//           <button id="search-btn" aria-label="검색">
//             <i class="uil uil-search"></i>
//           </button>
//         </div>
//       </div>

//       <div class="nav-right">
//         <ul class="menu">
//           <li id="login-btn"><button type="button">LOG IN</button></li>
//           <li id="profile-menu" class="hidden">
//             <img src="/thisplay/src/main/resources/templates/navbar/profile.png" alt="프로필" id="profile-img" />
//             <ul class="dropdown hidden">
//               <li><a href="#">마이페이지</a></li>
//               <li><a href="#">설정</a></li>
//               <li><a href="#" id="logout-btn">로그아웃</a></li>
//             </ul>
//           </li>
//         </ul>
//       </div>
//     </nav>
//   `;

//   /// body에 navbar 추가
//   document.body.insertAdjacentHTML("afterbegin", navbarHTML);
//   const profileMenu = document.getElementById("profile-menu");
//   const dropdown = profileMenu.querySelector(".dropdown");
//   const profileImg = document.getElementById("profile-img");
//   const logoutBtn = document.getElementById("logout-btn");

//   // login 버튼 연결
//   const loginBtn = document.querySelector("#login-btn button");
//   if (loginBtn) {
//     loginBtn.addEventListener("click", () => {
//       window.location.href = "../login_join/login.html";
//     });
//   }

//   const isLoggedIn = () => !!localStorage.getItem("token");
//   console.log(loginBtn);

//   if (isLoggedIn()) {
//     loginBtn.classList.add("hidden");
//     profileMenu.classList.remove("hidden");
//   } else {
//     loginBtn.classList.remove("hidden");
//     profileMenu.classList.add("hidden");
//   }

//   document.addEventListener("DOMContentLoaded", () => {
//     createNavbar();

//     const loginBtn = document.querySelector("#login-btn");
//     const profileMenu = document.getElementById("profile-menu");

//     const isLoggedIn = () => !!localStorage.getItem("token");

//     if (isLoggedIn()) {
//       loginBtn?.classList.add("hidden");
//       profileMenu?.classList.remove("hidden");
//     } else {
//       loginBtn?.classList.remove("hidden");
//       profileMenu?.classList.add("hidden");
//     }
//   });

//   // dropdown

//   if (profileMenu && dropdown) {
//     profileMenu.addEventListener("mouseenter", () => {
//       dropdown.classList.remove("hidden");
//     });

//     profileMenu.addEventListener("mouseleave", () => {
//       dropdown.classList.add("hidden");
//     });
//   }

//   document.getElementById("logout-btn").addEventListener("click", () => {
//     logout();
//   });
// }

// document.addEventListener("DOMContentLoaded", createNavbar);

import { logout } from "../../static/js/utils/auth.js";

function createNavbar() {
  // CSS 삽입
  const link = document.createElement("link");
  link.rel = "stylesheet";
  link.href = "../navbar/navbar.css";
  document.head.appendChild(link);

  // HTML 삽입
  const navbarHTML = `
    <nav class="navbar">
      <div class="nav-left">
        <div class="logo">THISPLAY</div>
      </div>

      <div class="nav-center">
        <div class="search-box">
          <input id="search-input" type="text" placeholder="검색어를 입력하세요..." />
          <button id="search-btn"><i class="uil uil-search"></i></button>
        </div>
      </div>

      <div class="nav-right">
        <ul class="menu">
          <li id="login-btn"><button type="button">LOG IN</button></li>

          <li id="profile-menu" class="hidden">
            <img src="/thisplay/src/main/resources/templates/navbar/profile.png" 
                 alt="프로필" id="profile-img" />
            <ul class="dropdown hidden">
              <li><a href="#">마이페이지</a></li>
              <li><a href="#">설정</a></li>
              <li><a href="#" id="logout-btn">로그아웃</a></li>
            </ul>
          </li>
        </ul>
      </div>
    </nav>
  `;

  document.body.insertAdjacentHTML("afterbegin", navbarHTML);

  // 요소 셀렉터
  const loginLi = document.getElementById("login-btn");
  const loginBtn = loginLi.querySelector("button");
  const profileMenu = document.getElementById("profile-menu");
  const dropdown = profileMenu.querySelector(".dropdown");

  // 로그인 여부
  const isLoggedIn = () => !!localStorage.getItem("token");

  // 로그인/로그아웃 상태 반영
  if (isLoggedIn()) {
    loginLi.classList.add("hidden");
    profileMenu.classList.remove("hidden");
  } else {
    loginLi.classList.remove("hidden");
    profileMenu.classList.add("hidden");
  }

  // 로그인 버튼 클릭
  loginBtn.addEventListener("click", () => {
    window.location.href = "../login_join/login.html";
  });

  // dropdown
  profileMenu.addEventListener("mouseenter", () => {
    dropdown.classList.remove("hidden");
  });

  profileMenu.addEventListener("mouseleave", () => {
    dropdown.classList.add("hidden");
  });

  // 로그아웃
  document.getElementById("logout-btn").addEventListener("click", () => {
    logout();
  });
}

// create navbar
document.addEventListener("DOMContentLoaded", createNavbar);
