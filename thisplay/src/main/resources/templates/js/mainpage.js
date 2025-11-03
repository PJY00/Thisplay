console.log("✅ mainpage.js 연결 완료");

const BASE_URL = "http://localhost:8080/api/main";

document.addEventListener("DOMContentLoaded", () => {
  const select = document.getElementById("genre-select");
  const container = document.getElementById("movie-container");
  const logoLink = document.querySelector('.nav-item[href="/mainpage"]');

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
      container.innerHTML = movies
        .map(
          (m) => `
          <div class="card">
            <img src="https://image.tmdb.org/t/p/w500${m.poster_path}" alt="${m.title}">
            <p>${m.title}</p>
          </div>
        `
        )
        .join("");
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

