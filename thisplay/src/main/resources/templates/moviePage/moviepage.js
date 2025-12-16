// ==========================
// URL에서 영화 ID 추출
// ==========================
const params = new URLSearchParams(window.location.search);
let movieId = params.get("movieId") || 1218925;

// 현재 선택된 리뷰 ID (댓글용)
let currentReviewId = null;

// ==========================
// CSS 로드
// ==========================
const cssLink = document.createElement("link");
cssLink.rel = "stylesheet";
cssLink.href = "moviepage.css";
document.head.appendChild(cssLink);

// ==========================
// 영화 상세 정보
// ==========================
async function loadMovieDetail() {
  try {
    const res = await axios.get(
      `http://localhost:8080/api/movies/show/${movieId}`
    );
    const movie = res.data;

    document.getElementById("poster").src = movie.poster_path;
    document.getElementById("title").textContent = movie.title;
    document.getElementById("summary").textContent = movie.overview;
    document.getElementById("addinfo").innerHTML = `
      원제: ${movie.original_title}<br>
      개봉일: ${movie.release_date.substring(0, 10)}<br>
      러닝타임: ${movie.runtime}분<br>
      평점: ${movie.vote_average}점
    `;

    document.getElementById(
      "goReview"
    ).href = `../reviewpage/writereview.html?movieId=${movieId}`;
  } catch (err) {
    console.error("영화 데이터 로드 실패:", err);
  }
}

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

// ==========================
// 한줄 리뷰 목록
// ==========================
async function loadOneLineReviews(movieId, page = 0) {
  try {
    const res = await axios.get(
      `http://localhost:8080/api/reviews/${movieId}/oneline`,
      { params: { page, size: 10 } }
    );
    return res.data;
  } catch (err) {
    console.error("한줄 리뷰 로드 실패:", err);
  }
}

async function renderOneLineReviews() {
  const reviewsDiv = document.getElementById("reviews");
  const pageData = await loadOneLineReviews(movieId);

  if (!pageData || !pageData.content || pageData.content.length === 0) {
    reviewsDiv.innerHTML = "<p>등록된 한 줄 리뷰가 없습니다.</p>";
    return;
  }

  reviewsDiv.innerHTML = "";

  pageData.content.forEach((review) => {
    const item = document.createElement("div");
    item.className = "review-item";

    item.innerHTML = `
      <div class="profile">${review.nickname ?? "익명"}</div>
      <div class="review">
        ${review.oneLineReview}
        <a href="#" class="open-review-detail"
           data-reviewid="${review.reviewId}">
          자세히 보기
        </a>
      </div>
    `;
    reviewsDiv.appendChild(item);
  });
}

// ==========================
// 리뷰 상세 모달
// ==========================
document.addEventListener("click", (e) => {
  const link = e.target.closest(".open-review-detail");
  if (!link) return;

  e.preventDefault();
  openReviewDetailModal(link.dataset.reviewid);
});

async function openReviewDetailModal(reviewId) {
  currentReviewId = reviewId;

  const modal = document.getElementById("reviewDetailModal");
  modal.classList.remove("hidden");

  document.getElementById("detailReviewTitle").textContent = "로딩 중...";
  document.getElementById("detailMeta").innerHTML = "";
  document.getElementById("detailBody").innerHTML = "";
  document.getElementById("detailOneLine").innerHTML = "";

  try {
    const res = await axios.get(
      `http://localhost:8080/api/reviews/${reviewId}`
    );
    const r = res.data;

    document.getElementById("detailReviewTitle").textContent =
      r.reviewTitle ?? "(제목 없음)";

    document.getElementById("detailMeta").innerHTML = `
      ⭐ ${r.star ?? 0}
    `;

    document.getElementById("detailBody").innerHTML =
      r.reviewBody?.replace(/\n/g, "<br>") ?? "";

    document.getElementById(
      "detailOneLine"
    ).innerHTML = `<strong>한줄평:</strong> ${r.oneLineReview ?? ""}`;

    // 좋아요
    document.getElementById("detailLikeCount").textContent = r.likeCount ?? 0;

    const likeBtn = document.getElementById("detailLikeBtn");
    likeBtn.classList.remove("active");
    likeBtn.onclick = () => handleLike(reviewId);

    // 댓글 로드
    loadComments(reviewId);
  } catch (err) {
    alert("리뷰를 불러올 수 없습니다.");
    console.error(err);
  }
}

// ==========================
// 좋아요 처리 (쿠키 기반)
// ==========================
async function handleLike(reviewId) {
  const btn = document.getElementById("detailLikeBtn");

  try {
    if (btn.classList.contains("active")) {
      await axios.delete(`http://localhost:8080/api/likes/${reviewId}`, {
        withCredentials: true,
      });
      btn.classList.remove("active");
    } else {
      await axios.post(
        `http://localhost:8080/api/likes/${reviewId}`,
        {},
        { withCredentials: true }
      );
      btn.classList.add("active");
    }

    const res = await axios.get(`http://localhost:8080/api/likes/${reviewId}`);
    document.getElementById("detailLikeCount").textContent =
      res.data.likeCount ?? 0;
  } catch (err) {
    if (err.response?.status === 401) {
      alert("로그인이 필요합니다.");
    } else {
      console.error("좋아요 처리 실패:", err);
    }
  }
}

// ==========================
// 댓글
// ==========================
async function loadComments(reviewId) {
  try {
    const res = await axios.get(
      `http://localhost:8080/api/reviews/${reviewId}/comments`
    );

    const list = document.getElementById("commentList");
    list.innerHTML = "";

    if (!res.data || res.data.length === 0) {
      list.innerHTML = "<li style='color:gray'>댓글이 없습니다.</li>";
      return;
    }

    res.data.forEach((c) => {
      const li = document.createElement("li");
      li.className = "comment-item";
      li.innerHTML = `
        <div class="comment-meta">
          ${c.nickname ?? "익명"} · ${formatDate(c.createdAt)}
        </div>
        <div class="comment-content">
          ${escapeHtml(c.content)}
        </div>
      `;
      list.appendChild(li);
    });
  } catch (err) {
    console.error("댓글 로드 실패:", err);
  }
}

document
  .getElementById("commentSubmitBtn")
  ?.addEventListener("click", async () => {
    const input = document.getElementById("commentInput");
    const content = input.value.trim();
    if (!content) return alert("댓글을 입력하세요.");

    try {
      await axios.post(
        `http://localhost:8080/api/reviews/${currentReviewId}/comments`,
        { content },
        { withCredentials: true }
      );
      input.value = "";
      loadComments(currentReviewId);
    } catch (err) {
      if (err.response?.status === 401) {
        alert("로그인이 필요합니다.");
      } else {
        console.error("댓글 작성 실패:", err);
      }
    }
  });

// ==========================
// 유틸
// ==========================
function formatDate(str) {
  const d = new Date(str);
  return `${d.getFullYear()}-${d.getMonth() + 1}-${d.getDate()}`;
}

function escapeHtml(str) {
  return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
}

// ==========================
// 모달 닫기
// ==========================
document.getElementById("modalCloseBtn").addEventListener("click", () => {
  document.getElementById("reviewDetailModal").classList.add("hidden");
});

// ==========================
// 초기 실행
// ==========================
loadMovieDetail();
renderOneLineReviews();
