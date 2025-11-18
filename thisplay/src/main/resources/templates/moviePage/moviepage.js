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

const cssLink = document.createElement("link");
cssLink.rel = "stylesheet";
cssLink.href = "moviepage.css"; // CSS 경로
document.head.appendChild(cssLink);

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

async function loadOneLineReviews(movieId, page = 0, sort = "latest") {
  try {
    const res = await axios.get(
      `http://localhost:8080/api/reviews/${movieId}/oneline`,
      {
        params: {
          sort: sort,
          page: page,
          size: 10,
        },
      }
    );

    console.log("한줄 리뷰 응답:", res.data);
    return res.data; // Page<DTO> 구조
  } catch (err) {
    console.error("한줄 리뷰 로드 실패:", err);
  }
}

async function renderOneLineReviews() {
  const reviewsDiv = document.getElementById("reviews");

  const pageData = await loadOneLineReviews(movieId);

  if (!pageData || pageData.content.length === 0) {
    reviewsDiv.innerHTML = "<p>등록된 한 줄 리뷰가 없습니다.</p>";
    return;
  }

  reviewsDiv.innerHTML = "";

  pageData.content.forEach((review) => {
    const item = document.createElement("div");
    item.classList.add("review-item");

    item.innerHTML = `
      <div class="profile">${review.username ?? "익명"}</div>
      <div class="review">
        ${review.content}
        <a href="/review/${review.reviewId}">자세히 보기</a>
      </div>
    `;
    reviewsDiv.appendChild(item);
  });
}

// const params = new URLSearchParams(window.location.search);
// const movieId = params.get("id"); // 또는 movieId= 로 보낸 경우 수정 필요

// 페이지 로드 시 실행
loadMovieDetail();
loadReviews();
