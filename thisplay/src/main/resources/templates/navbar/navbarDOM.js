import {
  logout,
  getAccessToken,
  getRefreshToken,
} from "../../static/js/utils/auth.js";

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
            <img id="profile-img" />
            <ul class="dropdown hidden">
              <li><a href="#" id="mypage-btn">마이페이지</a></li>
              <li><a href="#" id="friend-btn">친구페이지</a></li>
              <li><a href="#" id="setting-btn">설정</a></li>
              <li><a href="#" id="logout-btn">로그아웃</a></li>
            </ul>
          </li>
        </ul>
      </div>
    </nav>
  `;
  document.body.insertAdjacentHTML("afterbegin", navbarHTML);

  // DOM 요소
  const loginLi = document.getElementById("login-btn");
  const loginBtn = loginLi.querySelector("button");
  const profileMenu = document.getElementById("profile-menu");
  const dropdown = profileMenu.querySelector(".dropdown");
  const profileImg = document.getElementById("profile-img");

  const searchBox = document.querySelector(".search-box");
  const searchInput = document.getElementById("search-input");
  const searchBtn = document.getElementById("search-btn");

  // 로그인 여부 (accessToken 사용)
  const isLoggedIn = () => !!getAccessToken();
  const userId = localStorage.getItem("userId");

  // 로그인 UI 반영
  if (isLoggedIn()) {
    loginLi.classList.add("hidden");
    profileMenu.classList.remove("hidden");
  } else {
    loginLi.classList.remove("hidden");
    profileMenu.classList.add("hidden");
  }

  // 로그인 버튼 -> 로그인 페이지 이동
  loginBtn.addEventListener("click", () => {
    localStorage.setItem("previousPage", window.location.href);
    window.location.href = "../login_join/login.html";
  });

  // 로고 클릭
  document.querySelector(".logo").addEventListener("click", () => {
    window.location.href = "../mainpage/mainpage.html";
  });

  // Dropdown 메뉴
  profileMenu.addEventListener("mouseenter", () =>
    dropdown.classList.remove("hidden")
  );
  profileMenu.addEventListener("mouseleave", () =>
    dropdown.classList.add("hidden")
  );

  // 로그아웃 버튼
  document.getElementById("logout-btn").addEventListener("click", () => {
    logout();
  });

  // 마이페이지 이동
  document.getElementById("mypage-btn").addEventListener("click", (e) => {
    e.preventDefault();
    window.location.href = "../mypage/mypage.html";
  });

  // 친구페이지 이동
  document.getElementById("friend-btn").addEventListener("click", (e) => {
    e.preventDefault();
    window.location.href = "../friendpage/friend.html";
  });

  // 설정 이동
  document.getElementById("setting-btn").addEventListener("click", (e) => {
    e.preventDefault();
    window.location.href = "../settingpage/settingspage.html";
  });

  // 프로필 이미지 로드
  async function loadProfile() {
    if (!isLoggedIn() || !userId) return;

    try {
      const token = getAccessToken();

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

  //   검색창 자동완성 기능

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
        <img src="${movie.posterPath}" />
        <span>${movie.title}</span>
      `;

      li.addEventListener("click", () => {
        window.location.href = `../moviepage/moviepage.html?movieId=${movie.id}`;
      });

      autoBox.appendChild(li);
    });

    autoBox.classList.remove("hidden");
  }

  document.addEventListener("click", (e) => {
    if (!searchBox.contains(e.target)) {
      autoBox.classList.add("hidden");
    }
  });
}

document.addEventListener("DOMContentLoaded", createNavbar);
