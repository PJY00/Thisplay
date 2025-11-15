// URL에서 영화 ID 추출
const params = new URLSearchParams(window.location.search);
// const movieId = params.get("id");
let movieId = params.get("movieId");

if (!movieId) {
  movieId = 1218925; // 여기 원하는 ID 넣기 (Fight Club 예시)
}

// 영화 상세정보 불러오기
async function loadMovieDetail() {
  try {
    const res = await axios.get(
      `http://localhost:8080/api/movies/show/${movieId}`
    );
    const movie = res.data;

    // HTML에 데이터 삽입
    document.getElementById("poster").src = movie.poster_path;
    document.getElementById("title").textContent = movie.title;
    document.getElementById("summary").textContent = movie.overview;
    document.querySelector("#addinfo").innerHTML = `
        원제: ${movie.original_title} <br>
        개봉일: ${movie.release_date.substring(0, 10)} <br>
        러닝타임: ${movie.runtime}분 <br>
        평점: ${movie.vote_average}점
    `;
    // document.getElementById("likes-count").textContent = movie.likes || 0;
    // document.getElementById("rating-score").textContent =
    //   movie.vote_average || "0.0";
  } catch (err) {
    console.error("영화 데이터 로드 실패:", err);
  }
}

// 리뷰 목록 불러오기
async function loadReviews() {
  try {
    const res = await axios.get(
      `http://localhost:8080/api/reviews/movied/${movieId}`
    );

    const reviews = res.data.reviews;

    const list = document.querySelector("#reviews");
    list.innerHTML = "";

    if (!reviews || reviews.length === 0) {
      list.innerHTML = "<p>아직 등록된 감상평이 없습니다.</p>";
      return;
    }

    reviews.forEach((r) => {
      const div = document.createElement("div");
      div.classList.add("review-item");
      div.textContent = r.content;
      list.appendChild(div);
    });
  } catch (err) {
    console.error("리뷰 로드 실패:", err);
  }
}

// 리뷰 등록하기
// document.getElementById("submit-review").addEventListener("click", async () => {
//   const text = document.getElementById("review-text").value.trim();
//   if (!text) return alert("리뷰를 입력하세요!");

//   try {
//     await axios.post(`http://localhost:8080/api/movies/${movieId}/reviews`, {
//       content: text,
//     });
//     document.getElementById("review-text").value = "";
//     loadReviews(); // 새 리뷰 목록 갱신
//   } catch (err) {
//     console.error("리뷰 등록 실패:", err);
//   }
// });

// 좋아요 버튼
// document.getElementById("like-btn").addEventListener("click", async () => {
//   try {
//     await axios.post(`http://localhost:8080/api/movies/${movieId}/like`);
//     loadMovieDetail(); // 좋아요 수 갱신
//   } catch (err) {
//     console.error("좋아요 실패:", err);
//   }
// });

// 페이지 로드 시 실행
loadMovieDetail();
loadReviews();
