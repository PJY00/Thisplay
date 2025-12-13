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
    document
      .getElementById("goReviewBtn")
      .addEventListener("click", function (e) {
        e.preventDefault();
        location.href = `../reviewpage/writereview.html?movieId=${movieId}`;
      });

    // document.getElementById("likes-count").textContent = movie.likes || 0;
    // document.getElementById("rating-score").textContent =
    //   movie.vote_average || "0.0";
  } catch (err) {
    console.error("영화 데이터 로드 실패:", err);
  }
}

// 리뷰 목록 불러오기
// async function loadReviews() {
//   try {
//     const res = await axios.get(
//       `http://localhost:8080/api/reviews/movie/${movieId}`
//     );

//     const reviews = res.data.reviews;

//     const list = document.querySelector("#reviews");
//     list.innerHTML = "";

//     if (!reviews || reviews.length === 0) {
//       list.innerHTML = "<p>아직 등록된 감상평이 없습니다.</p>";
//       return;
//     }
//     console.log(reviews);

//     reviews.forEach((r) => {
//       const div = document.createElement("div");
//       div.classList.add("review-item");
//       div.textContent = r.content;
//       list.appendChild(div);
//     });
//   } catch (err) {
//     console.error("리뷰 로드 실패:", err);
//   }
// }

const cssLink = document.createElement("link");
cssLink.rel = "stylesheet";
cssLink.href = "moviepage.css";
document.head.appendChild(cssLink);

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

// async function renderOneLineReviews() {
//   const reviewsDiv = document.getElementById("reviews");

//   const pageData = await loadOneLineReviews(movieId);

//   if (!pageData || pageData.content.length === 0) {
//     reviewsDiv.innerHTML = "<p>등록된 한 줄 리뷰가 없습니다.</p>";
//     return;
//   }

//   reviewsDiv.innerHTML = "";

//   pageData.content.forEach((review) => {
//     const item = document.createElement("div");
//     item.classList.add("review-item");

//     item.innerHTML = `
//       <div class="profile">${review.username ?? "익명"}</div>
//       <div class="review">
//         ${review.content}
//         <a href="/review/${review.reviewId}">자세히 보기</a>
//       </div>
//     `;
//     reviewsDiv.appendChild(item);
//   });
// }

// const params = new URLSearchParams(window.location.search);
// const movieId = params.get("id"); // 또는 movieId= 로 보낸 경우 수정 필요

async function renderOneLineReviews() {
  const reviewsDiv = document.getElementById("reviews");

  if (!reviewsDiv) {
    console.error("reviews 요소를 찾을 수 없습니다.");
    return;
  }

  const pageData = await loadOneLineReviews(movieId);

  console.log("한줄 리뷰 응답:", pageData);

  if (!pageData || !pageData.content || pageData.content.length === 0) {
    reviewsDiv.innerHTML = `<p>등록된 한 줄 리뷰가 없습니다.</p>`;
    return;
  }

  reviewsDiv.innerHTML = "";

  const escapeHTML = (str) => {
    if (!str) return "";
    return str.replace(/</g, "&lt;").replace(/>/g, "&gt;");
  };

  pageData.content.forEach((review) => {
    const nickname = review.nickname ?? "익명";
    const oneLineReview = escapeHTML(review.oneLineReview);
    const reviewId = review.reviewId;
    const profileImage = review.profileImageUrl
      ? `<img src="${review.profileImageUrl}" class="profile-img">`
      : `<div class="profile-img empty"></div>`;

    const item = document.createElement("div");
    item.classList.add("review-item");

    item.innerHTML = `
      <div class="profile_img">${profileImage}</div>
      <div class="profile">${nickname}</div>
      <div class="review">
        ${oneLineReview}
        <a 
          href="/review?id=${reviewId}" 
          class="open-review-detail"
          data-reviewid="${reviewId}"
        >자세히 보기</a>
      </div>
    `;

    reviewsDiv.appendChild(item);
  });
}

// 리뷰 상세보기 모달 열기
document.addEventListener("click", async (e) => {
  const link = e.target.closest(".open-review-detail");
  if (!link) return;

  e.preventDefault();

  const reviewId = link.dataset.reviewid;
  openReviewDetailModal(reviewId);
});

async function openReviewDetailModal(reviewId) {
  const modal = document.getElementById("reviewDetailModal");

  document.getElementById("detailReviewTitle").textContent = "로딩 중...";
  document.getElementById("detailMeta").innerHTML = "";
  document.getElementById("detailBody").innerHTML = "";
  document.getElementById("detailOneLine").innerHTML = "";

  modal.classList.remove("hidden");

  function formatDate(str) {
    const fixed = str.replace(/\+00:00$/, "Z");

    const date = new Date(fixed);

    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, "0");
    const d = String(date.getDate()).padStart(2, "0");

    const hh = String(date.getHours()).padStart(2, "0");
    const mm = String(date.getMinutes()).padStart(2, "0");

    return `${y}-${m}-${d} ${hh}:${mm}`;
  }

  try {
    const res = await axios.get(
      `http://localhost:8080/api/reviews/${reviewId}`,
      { headers: { Authorization: `Bearer ${localStorage.getItem("token")}` } }
    );

    const r = res.data;
    document.getElementById("detailReviewTitle").textContent =
      r.reviewTitle ?? "(제목 없음)";

    document.getElementById("detailMeta").innerHTML = `
      ⭐ ${r.star ?? 0} · 작성일 ${formatDate(r.createdAt)}
      · 👍 ${r.likeCount ?? 0} · 👁 ${r.viewCount ?? 0}
    `;

    document.getElementById("detailBody").innerHTML =
      r.reviewBody?.replace(/\n/g, "<br>") ?? "본문 없음";

    document.getElementById("detailOneLine").innerHTML = `
      <strong>한줄평:</strong> ${r.oneLineReview ?? "(없음)"}
    `;
  } catch (err) {
    console.error("리뷰 상세 조회 실패:", err);
    alert("리뷰 정보를 불러올 수 없습니다.");
  }
}

// 모달 닫기
document.getElementById("modalCloseBtn").addEventListener("click", () => {
  document.getElementById("reviewDetailModal").classList.add("hidden");
});

// 페이지 로드 시 실행
loadMovieDetail();
// renderOneLineReviews();
renderOneLineReviews();
// loadReviews();

// 리뷰 작성 링크
document.getElementById(
  "goReview"
).href = `../reviewpage/writereview.html?movieId=${movieId}`;

// 폴더 팝업
// 폴더 팝업 요소
const folderPopup = document.getElementById("folderPopup");

// 선택 모드
const selectMode = document.getElementById("fp-select-mode");
const folderListUI = document.getElementById("fp-folder-list");
const openCreateModeBtn = document.getElementById("fp-create-folder");

// 생성 모드
const createMode = document.getElementById("fp-create-mode");
const backBtn = document.getElementById("fp-back");
const newFolderNameInput = document.getElementById("fp-new-folder-name");
const visibilitySelect = document.getElementById("fp-visibility-select");
const createFolderBtn = document.getElementById("fp-create-folder-btn");

// 닫기 버튼
const closeBtn = document.getElementById("fp-close");

// 팝업 열기
document.getElementById("addfolder").addEventListener("click", openFolderPopup);

async function openFolderPopup() {
  folderListUI.innerHTML = "";

  // 기본 모드는 폴더 선택 모드
  selectMode.classList.remove("hidden");
  createMode.classList.add("hidden");

  try {
    const res = await axios.get("http://localhost:8080/api/folders/me", {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });

    const folders = res.data || [];

    if (folders.length === 0) {
      folderListUI.innerHTML = `<li>폴더가 없습니다.</li>`;
    } else {
      folders.forEach((folder) => {
        const li = document.createElement("li");
        li.textContent = folder.folderName;
        li.addEventListener("click", () => saveMovieToFolder(folder.folderId));
        folderListUI.appendChild(li);
      });
    }

    folderPopup.classList.remove("hidden");
  } catch (err) {
    console.error("폴더 목록 불러오기 실패:", err);
    alert("폴더 목록을 불러오는 중 오류 발생");
  }
}

// 팝업 닫기
closeBtn.addEventListener("click", () => {
  folderPopup.classList.add("hidden");
});

// 검은 배경 클릭 시 닫기
folderPopup.addEventListener("click", (e) => {
  if (e.target === folderPopup) folderPopup.classList.add("hidden");
});

// 새 폴더 만들기 전환
openCreateModeBtn.addEventListener("click", () => {
  selectMode.classList.add("hidden");
  createMode.classList.remove("hidden");
});

// 돌아가기 버튼
backBtn.addEventListener("click", () => {
  createMode.classList.add("hidden");
  selectMode.classList.remove("hidden");
});

// 새 폴더 생성
createFolderBtn.addEventListener("click", createNewFolder);

async function createNewFolder() {
  const folderName = newFolderNameInput.value.trim();
  const visibility = visibilitySelect.value;

  if (!folderName) {
    alert("폴더 이름을 입력하세요.");
    return;
  }

  try {
    await axios.post(
      `http://localhost:8080/api/folders/create?folderName=${encodeURIComponent(
        folderName
      )}&visibility=${visibility}`,
      {},
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      }
    );

    alert("폴더가 생성되었습니다!");
    newFolderNameInput.value = "";
    openFolderPopup();
  } catch (error) {
    console.error("폴더 생성 실패:", error);
    alert("폴더 생성 중 오류가 발생했습니다.");
  }
}

// 영화 저장
async function saveMovieToFolder(folderId) {
  try {
    await axios.post(
      `http://localhost:8080/api/movies/save/${folderId}/${movieId}`,
      {},
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      }
    );

    alert("영화가 폴더에 추가되었습니다!");
    folderPopup.classList.add("hidden");
  } catch (err) {
    console.error("영화 저장 실패:", err);
    alert("영화를 폴더에 저장하지 못했습니다.");
  }
}
