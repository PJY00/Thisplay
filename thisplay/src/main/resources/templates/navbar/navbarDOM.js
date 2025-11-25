import { logout } from "../../static/js/utils/auth.js";

function createNavbar() {
  // CSS 삽입
  const link = document.createElement("link");
  link.rel = "stylesheet";
  link.href = "../navbar/navbar.css";
  document.head.appendChild(link);

  // HTML 먼저 삽입 (순서 중요)
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
            <img  id="profile-img">
            <ul class="dropdown hidden">
              <li><a href="#" id ="mypage-btn">마이페이지</a></li>
              <li><a href="#" id = "setting-btn">설정</a></li>
              <li><a href="#" id="logout-btn">로그아웃</a></li>
            </ul>
          </li>
        </ul>
      </div>
    </nav>
  `;

  document.body.insertAdjacentHTML("afterbegin", navbarHTML);

  // DOM 요소 셀렉터
  const loginLi = document.getElementById("login-btn");
  const loginBtn = loginLi.querySelector("button");
  const profileMenu = document.getElementById("profile-menu");
  const profileImg = document.getElementById("profile-img");
  const dropdown = profileMenu.querySelector(".dropdown");

  const searchBox = document.querySelector(".search-box");
  const searchInput = document.getElementById("search-input");
  const searchBtn = document.getElementById("search-btn");

  // 로그인 여부 UI 처리
  const isLoggedIn = () => !!localStorage.getItem("token");
  const userId = localStorage.getItem("userId");

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

  // 로고 클릭: 메인 이동
  document.querySelector(".logo").addEventListener("click", () => {
    window.location.href = "../mainpage/mainpage.html";
  });

  // 프로필 드롭다운
  profileMenu.addEventListener("mouseenter", () =>
    dropdown.classList.remove("hidden")
  );
  profileMenu.addEventListener("mouseleave", () =>
    dropdown.classList.add("hidden")
  );

  document.getElementById("logout-btn").addEventListener("click", () => {
    logout();
  });


  // 마이페이지 이동
  document.getElementById("mypage-btn").addEventListener("click", (e) => {
    e.preventDefault();
    window.location.href = "../mypage/mypage.html";
  });

  // 설정 페이지 이동
  document.getElementById("setting-btn").addEventListener("click", (e) => {
    e.preventDefault();
    window.location.href = "../settingpage/settingspage.html";
  });

  // 로그인 상태면 프로필 이미지 API 호출
  async function loadProfile() {
    console.log("isLoggedIn():", isLoggedIn());
    console.log("userId:", userId);
    console.log("token:", localStorage.getItem("token"));
    if (!isLoggedIn() || !userId) return;

    try {
      const token = localStorage.getItem("token");

      const res = await axios.get(
        `http://localhost:8080/api/users/${userId}/profile`,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      const profile = res.data;

      profileImg.src = `http://localhost:8080${profile.profileImgUrl}`;
    } catch (err) {
      console.error("프로필 로드 실패:", err);
    }
  }

  loadProfile();

  // 검색 기능 + 자동완성

  // 자동완성 리스트 박스 생성
  const autoBox = document.createElement("ul");
  autoBox.id = "autocomplete-list";
  autoBox.classList.add("autocomplete", "hidden");
  searchBox.appendChild(autoBox);

  let timer = null;

  searchInput.addEventListener("input", () => {
    const query = searchInput.value.trim();

    clearTimeout(timer);

    if (query.length === 0) {
      autoBox.classList.add("hidden");
      autoBox.innerHTML = "";
      return;
    }

    timer = setTimeout(() => {
      fetch(
        `http://localhost:8080/api/movies/search?query=${encodeURIComponent(
          query
        )}`
      )
        .then((res) => res.json())
        .then((data) => {
          console.log("검색 API 응답:", data);
          const results = Array.isArray(data) ? data : data.results || [];
          renderAutoComplete(results);
        })
        .catch((err) => console.error("검색 오류:", err));
    }, 250);
  });

  function renderAutoComplete(list) {
    autoBox.innerHTML = "";

    if (list.length === 0) {
      autoBox.classList.add("hidden");
      return;
    }

    list.forEach((movie) => {
      const li = document.createElement("li");
      li.classList.add("autocomplete-item");

      li.innerHTML = `
        <img src= ${movie.posterPath} />
        <span>${movie.title}</span>
      `;
      // li.innerHTML = `
      //   <img src="https://image.tmdb.org/t/p/w45${movie.poster_path}" />
      //   <span>${movie.title}</span>
      // `;

      li.addEventListener("click", () => {
        window.location.href = `../moviepage/moviepage.html?movieId=${movie.id}`;
      });

      autoBox.appendChild(li);
    });

    autoBox.classList.remove("hidden");
  }

  // 외부 클릭 시 자동완성 숨기기
  document.addEventListener("click", (e) => {
    if (!searchBox.contains(e.target)) {
      autoBox.classList.add("hidden");
    }
  });
}

document.addEventListener("DOMContentLoaded", createNavbar);


// import { logout } from "../../static/js/utils/auth.js";

// function createNavbar() {
//   // CSS 삽입
//   const link = document.createElement("link");
//   link.rel = "stylesheet";
//   link.href = "../navbar/navbar.css";
//   document.head.appendChild(link);

//   // HTML 삽입
//   const navbarHTML = `
//     <nav class="navbar">
//       <div class="nav-left">
//         <div class="logo">THISPLAY</div>
//       </div>

//       <div class="nav-center">
//         <div class="search-box">
//           <input id="search-input" type="text" placeholder="검색어를 입력하세요..." />
//           <button id="search-btn"><i class="uil uil-search"></i></button>
//         </div>
//       </div>

//       <div class="nav-right">
//         <ul class="menu">
//           <li id="login-btn"><button type="button">LOG IN</button></li>

//           <li id="profile-menu" class="hidden">
//             <img src="/thisplay/src/main/resources/templates/navbar/profile.png"
//                  alt="프로필" id="profile-img" />
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

//   document.body.insertAdjacentHTML("afterbegin", navbarHTML);

//   // 요소 셀렉터
//   const loginLi = document.getElementById("login-btn");
//   const loginBtn = loginLi.querySelector("button");
//   const profileMenu = document.getElementById("profile-menu");
//   const dropdown = profileMenu.querySelector(".dropdown");

//   // 메인으로 이동
//   const logo = document.querySelector(".logo");
//   logo.addEventListener("click", () => {
//     window.location.href = "../mainpage/mainpage.html";
//   });

//   // 로그인 여부 확인
//   const isLoggedIn = () => !!localStorage.getItem("token");

//   if (isLoggedIn()) {
//     loginLi.classList.add("hidden");
//     profileMenu.classList.remove("hidden");
//   } else {
//     loginLi.classList.remove("hidden");
//     profileMenu.classList.add("hidden");
//   }

//   loginBtn.addEventListener("click", () => {
//     window.location.href = "../login_join/login.html";
//   });

//   // 프로필 드롭다운
//   profileMenu.addEventListener("mouseenter", () => {
//     dropdown.classList.remove("hidden");
//   });

//   profileMenu.addEventListener("mouseleave", () => {
//     dropdown.classList.add("hidden");
//   });

//   // 로그아웃
//   document.getElementById("logout-btn").addEventListener("click", () => {
//     logout();
//   });

//   // 검색창
//   const searchBox = document.querySelector(".search-box");
//   const searchInput = document.getElementById("search-input");
//   const searchBtn = document.getElementById("search-btn");

//   // 자동완성 리스트 박스 생성
//   const autoBox = document.createElement("ul");
//   autoBox.id = "autocomplete-list";
//   autoBox.classList.add("autocomplete", "hidden");
//   searchBox.appendChild(autoBox);

//   let timer = null;

//   searchInput.addEventListener("input", () => {
//     const query = searchInput.value.trim();

//     clearTimeout(timer);

//     if (query.length === 0) {
//       autoBox.classList.add("hidden");
//       autoBox.innerHTML = "";
//       return;
//     }

//     timer = setTimeout(() => {
//       fetch(
//         `http://localhost:8080/api/movies/search?query=${encodeURIComponent(
//           query
//         )}`
//       )
//         .then((res) => res.json())
//         .then((data) => {
//           console.log("검색 API 응답:", data); // 여기 추가!
//           const results = Array.isArray(data) ? data : data.results || [];
//           renderAutoComplete(results);
//         })
//         .catch((err) => console.error("검색 오류:", err));
//     }, 250);
//   });

//   // 자동완성 렌더링
//   function renderAutoComplete(list) {
//     autoBox.innerHTML = "";

//     if (list.length === 0) {
//       autoBox.classList.add("hidden");
//       return;
//     }

//     list.forEach((movie) => {
//       const li = document.createElement("li");
//       li.classList.add("autocomplete-item");

//       li.innerHTML = `
//         <img src="https://image.tmdb.org/t/p/w45${movie.poster_path}" />
//         <span>${movie.title}</span>
//       `;

//       li.addEventListener("click", () => {
//         window.location.href = `../moviepage/moviepage.html?movieId=${movie.id}`;
//       });

//       autoBox.appendChild(li);
//     });

//     autoBox.classList.remove("hidden");
//   }

//   // 외부 클릭 시 자동완성 숨기기
//   document.addEventListener("click", (e) => {
//     if (!searchBox.contains(e.target)) {
//       autoBox.classList.add("hidden");
//     }
//   });
// }

// // create navbar
// document.addEventListener("DOMContentLoaded", createNavbar);

// // import { logout } from "../../static/js/utils/auth.js";
// // function createNavbar() {
// //   // css 삽입
// //   const link = document.createElement("link");
// //   link.rel = "stylesheet";
// //   // link.href = "../navbar/navbar.css"; // 경로는 필요에 따라 수정
// //   link.href = "/thisplay/src/main/resources/templates/navbar/navbar.css"; // 경로는 필요에 따라 수정
// //   document.head.appendChild(link);

// //   // HTML 삽입
// //   const navbarHTML = `
// //     <nav class="navbar">
// //       <div class="nav-left">
// //         <div class="logo">THISPLAY</div>
// //       </div>

// //       <div class="nav-center">
// //         <div class="search-box">
// //           <input
// //             type="text"
// //             id="search-input"
// //             placeholder="검색어를 입력하세요..."
// //           />
// //           <button id="search-btn" aria-label="검색">
// //             <i class="uil uil-search"></i>
// //           </button>
// //         </div>
// //       </div>

// //       <div class="nav-right">
// //         <ul class="menu">
// //           <li id="login-btn"><button type="button">LOG IN</button></li>
// //           <li id="profile-menu" class="hidden">
// //             <img src="/thisplay/src/main/resources/templates/navbar/profile.png" alt="프로필" id="profile-img" />
// //             <ul class="dropdown hidden">
// //               <li><a href="#">마이페이지</a></li>
// //               <li><a href="#">설정</a></li>
// //               <li><a href="#" id="logout-btn">로그아웃</a></li>
// //             </ul>
// //           </li>
// //         </ul>
// //       </div>
// //     </nav>
// //   `;

// //   /// body에 navbar 추가
// //   document.body.insertAdjacentHTML("afterbegin", navbarHTML);
// //   const profileMenu = document.getElementById("profile-menu");
// //   const dropdown = profileMenu.querySelector(".dropdown");
// //   const profileImg = document.getElementById("profile-img");
// //   const logoutBtn = document.getElementById("logout-btn");

// //   // login 버튼 연결
// //   const loginBtn = document.querySelector("#login-btn button");
// //   if (loginBtn) {
// //     loginBtn.addEventListener("click", () => {
// //       window.location.href = "../login_join/login.html";
// //     });
// //   }

// //   const isLoggedIn = () => !!localStorage.getItem("token");
// //   console.log(loginBtn);

// //   if (isLoggedIn()) {
// //     loginBtn.classList.add("hidden");
// //     profileMenu.classList.remove("hidden");
// //   } else {
// //     loginBtn.classList.remove("hidden");
// //     profileMenu.classList.add("hidden");
// //   }

// //   document.addEventListener("DOMContentLoaded", () => {
// //     createNavbar();

// //     const loginBtn = document.querySelector("#login-btn");
// //     const profileMenu = document.getElementById("profile-menu");

// //     const isLoggedIn = () => !!localStorage.getItem("token");

// //     if (isLoggedIn()) {
// //       loginBtn?.classList.add("hidden");
// //       profileMenu?.classList.remove("hidden");
// //     } else {
// //       loginBtn?.classList.remove("hidden");
// //       profileMenu?.classList.add("hidden");
// //     }
// //   });

// //   // dropdown

// //   if (profileMenu && dropdown) {
// //     profileMenu.addEventListener("mouseenter", () => {
// //       dropdown.classList.remove("hidden");
// //     });

// //     profileMenu.addEventListener("mouseleave", () => {
// //       dropdown.classList.add("hidden");
// //     });
// //   }

// //   document.getElementById("logout-btn").addEventListener("click", () => {
// //     logout();
// //   });
// // }

// // document.addEventListener("DOMContentLoaded", createNavbar);

// // import { logout } from "../../static/js/utils/auth.js";

// // function createNavbar() {
// //   // CSS 삽입
// //   const link = document.createElement("link");
// //   link.rel = "stylesheet";
// //   link.href = "../navbar/navbar.css";
// //   document.head.appendChild(link);

// //   // HTML 삽입
// //   const navbarHTML = `
// //     <nav class="navbar">
// //       <div class="nav-left">
// //         <div class="logo">THISPLAY</div>
// //       </div>

// //       <div class="nav-center">
// //         <div class="search-box">
// //           <input id="search-input" type="text" placeholder="검색어를 입력하세요..." />
// //           <button id="search-btn"><i class="uil uil-search"></i></button>
// //         </div>
// //       </div>

// //       <div class="nav-right">
// //         <ul class="menu">
// //           <li id="login-btn"><button type="button">LOG IN</button></li>

// //           <li id="profile-menu" class="hidden">
// //             <img src="/thisplay/src/main/resources/templates/navbar/profile.png"
// //                  alt="프로필" id="profile-img" />
// //             <ul class="dropdown hidden">
// //               <li><a href="#">마이페이지</a></li>
// //               <li><a href="#">설정</a></li>
// //               <li><a href="#" id="logout-btn">로그아웃</a></li>
// //             </ul>
// //           </li>
// //         </ul>
// //       </div>
// //     </nav>
// //   `;

// //   document.body.insertAdjacentHTML("afterbegin", navbarHTML);

// //   // 요소 셀렉터
// //   const loginLi = document.getElementById("login-btn");
// //   const loginBtn = loginLi.querySelector("button");
// //   const profileMenu = document.getElementById("profile-menu");
// //   const dropdown = profileMenu.querySelector(".dropdown");

// //   //로고 클릭시 메인으로
// //   const logo = document.querySelector(".logo");
// //   logo.addEventListener("click", () => {
// //     window.location.href = "../mainpage/mainpage.html";
// //   });

// //   // 로그인 여부
// //   const isLoggedIn = () => !!localStorage.getItem("token");

// //   // 로그인/로그아웃 상태 반영
// //   if (isLoggedIn()) {
// //     loginLi.classList.add("hidden");
// //     profileMenu.classList.remove("hidden");
// //   } else {
// //     loginLi.classList.remove("hidden");
// //     profileMenu.classList.add("hidden");
// //   }

// //   // 로그인 버튼 클릭
// //   loginBtn.addEventListener("click", () => {
// //     window.location.href = "../login_join/login.html";
// //   });

// //   // dropdown
// //   profileMenu.addEventListener("mouseenter", () => {
// //     dropdown.classList.remove("hidden");
// //   });

// //   profileMenu.addEventListener("mouseleave", () => {
// //     dropdown.classList.add("hidden");
// //   });

// //   // 로그아웃
// //   document.getElementById("logout-btn").addEventListener("click", () => {
// //     logout();
// //   });
// // }

// // // 검색창 DOM 요소
// // const searchBox = document.querySelector(".search-box");
// // const searchInput = document.getElementById("search-input");
// // const searchBtn = document.getElementById("search-btn");

// // // 자동완성 리스트 박스 추가
// // const autoBox = document.createElement("ul");
// // autoBox.id = "autocomplete-list";
// // autoBox.classList.add("autocomplete", "hidden");
// // document.querySelector(".search-box").appendChild(autoBox);

// // // 디바운스 (너무 많이 호출하지 않도록)
// // let timer = null;

// // searchInput.addEventListener("input", () => {
// //   const query = searchInput.value.trim();

// //   clearTimeout(timer);

// //   if (query.length === 0) {
// //     autoBox.classList.add("hidden");
// //     autoBox.innerHTML = "";
// //     return;
// //   }

// //   timer = setTimeout(() => {
// //     fetch(
// //       `http://localhost:8080/api/movies/search?query=${encodeURIComponent(
// //         query
// //       )}`
// //     )
// //       .then((res) => res.json())
// //       .then((data) => {
// //         const results = data?.results || [];
// //         renderAutoComplete(results);
// //       })
// //       .catch((err) => console.error("검색 오류:", err));
// //   }, 250); // 디바운스 250ms
// // });

// // // 자동완성 렌더링
// // function renderAutoComplete(list) {
// //   autoBox.innerHTML = "";

// //   if (list.length === 0) {
// //     autoBox.classList.add("hidden");
// //     return;
// //   }

// //   list.forEach((movie) => {
// //     const li = document.createElement("li");
// //     li.classList.add("autocomplete-item");

// //     li.innerHTML = `
// //       <img src="https://image.tmdb.org/t/p/w45${movie.poster_path}" />
// //       <span>${movie.title}</span>
// //     `;

// //     li.addEventListener("click", () => {
// //       // 예: 상세 페이지 moviepage.html?id=xxxx 이동
// //       window.location.href = `../moviepage/moviepage.html?id=${movie.id}`;
// //     });

// //     autoBox.appendChild(li);
// //   });

// //   autoBox.classList.remove("hidden");
// // }

// // // 자동완성 영역 외부 클릭 시 숨기기
// // document.addEventListener("click", (e) => {
// //   if (!document.querySelector(".search-box").contains(e.target)) {
// //     autoBox.classList.add("hidden");
// //   }
// // });

// // // create navbar
// // document.addEventListener("DOMContentLoaded", createNavbar);
