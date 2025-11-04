console.log("✅ mainpage.js 연결 완료");

const BASE_URL = "http://localhost:8080/api/main";

document.addEventListener("DOMContentLoaded", () => {
  const select = document.getElementById("genre-select");
  const container = document.getElementById("movie-container");
  const logoLink = document.querySelector('.nav-item[href="/mainpage"]');

  // ✅ 로고 클릭 시 mainpage.html로 이동
  logoLink.addEventListener("click", (e) => {
    e.preventDefault(); // 기본 링크 이동 막기 (선택)
    window.location.href = "mainpage.html"; // 이동할 경로
  });

  // ✅ 1. 장르 목록 가져오기
  async function loadGenres() {
    try {
      const res = await fetch(`${BASE_URL}/genres`);
      if (!res.ok) throw new Error(`HTTP 오류: ${res.status}`);

      const data = await res.json();
      const genres = data.genres || [];

      // 드롭다운 생성
      select.innerHTML = genres
        .map((g) => `<option value="${g.id}">${g.name}</option>`)
        .join("");

      if (genres.length > 0) {
        // 첫 번째 장르로 기본 영화 목록 불러오기
        loadMovies(genres[0].id);
      }
    } catch (error) {
      console.error("❌ 장르 불러오기 실패:", error);
      container.innerHTML = `<p style="color:red;">장르 정보를 불러오지 못했습니다.</p>`;
    }
  }

  // ✅ 2. 특정 장르의 영화 목록 가져오기
  async function loadMovies(genreId) {
    container.innerHTML = "<p>🎞 영화 불러오는 중...</p>";

    try {
      const res = await fetch(`${BASE_URL}/genres/${genreId}/top20`);
      if (!res.ok) throw new Error(`HTTP 오류: ${res.status}`);

      const data = await res.json();
      const movies = Array.isArray(data) ? data : data.results || [];


      if (movies.length === 0) {
        container.innerHTML = "<p>❌ 해당 장르의 영화가 없습니다.</p>";
        return;
      }

      // 카드 렌더링
      container.innerHTML = `
      <div class="slider">
        <div id="wrap">
          <div id="track">
            ${movies
          .map(
            (m) => `
                <article class="project-card">
                  <img class="project-card__bg" src="https://image.tmdb.org/t/p/w500${m.poster_path}" alt="">
                  <div class="project-card__content">
                    <img class="project-card__thumb" src="https://image.tmdb.org/t/p/w500${m.poster_path}" alt="">
                    <div>
                      <h3 class="project-card__title">${m.title}</h3>
                    </div>
                  </div>
                </article>
              `
          )
          .join("")}
          </div>
        </div>
        <div id="controls">
          <button id="prev">◀</button>
          <div id="dots"></div>
          <button id="next">▶</button>
        </div>
      </div>
    `;

      initSlider();
    } catch (error) {
      console.error("❌ 영화 불러오기 실패:", error);
      container.innerHTML = `<p style="color:red;">영화 정보를 불러오지 못했습니다.</p>`;
    }
  }

  // ✅ 3. 장르 선택 시 영화 갱신
  select.addEventListener("change", (e) => {
    loadMovies(e.target.value);
  });

  // ✅ 초기 실행
  loadGenres();
});

//--------------------------카드 슬라이더------------------------

function initSlider() {
  const track = document.getElementById("track");
  const wrap = track.parentElement;
  const cards = Array.from(track.children);
  const prev = document.getElementById("prev");
  const next = document.getElementById("next");
  const dotsBox = document.getElementById("dots");

  // 점(dot) 생성
  cards.forEach((_, i) => {
    const dot = document.createElement("span");
    dot.className = "dot";
    dot.onclick = () => activate(i, true);
    dotsBox.appendChild(dot);
  });
  const dots = Array.from(dotsBox.children);

  let current = 0;

  // 중앙 정렬 (가로 슬라이드 기준)
  function center(i) {
    const card = cards[i];
    const start = card.offsetLeft;
    wrap.scrollTo({
      left: start - (wrap.clientWidth / 2 - card.clientWidth / 2),
      behavior: "smooth"
    });
  }

  // UI 상태 업데이트
  function toggleUI(i) {
    cards.forEach((c, k) => c.toggleAttribute("active", k === i));
    dots.forEach((d, k) => d.classList.toggle("active", k === i));
    prev.disabled = i === 0;
    next.disabled = i === cards.length - 1;
  }

  // 특정 카드 활성화
  function activate(i, scroll) {
    if (i === current) return;
    current = i;
    toggleUI(i);
    if (scroll) center(i);
  }

  // 이동
  function go(step) {
    activate(Math.min(Math.max(current + step, 0), cards.length - 1), true);
  }

  // 버튼 이벤트
  prev.onclick = () => go(-1);
  next.onclick = () => go(1);

  // 키보드 방향키로 이동
  addEventListener(
    "keydown",
    (e) => {
      if (["ArrowRight"].includes(e.key)) go(1);
      if (["ArrowLeft"].includes(e.key)) go(-1);
    },
    { passive: true }
  );

  // 마우스 호버 / 클릭으로 이동
  cards.forEach((card, i) => {
    card.addEventListener("mouseenter", () => activate(i, true));
    card.addEventListener("click", () => activate(i, true));
  });

  // 창 크기 변경 시 중앙 유지
  addEventListener("resize", () => center(current));

  // 초기화
  toggleUI(0);
  center(0);
};
