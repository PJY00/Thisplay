import api from "../../static/js/api/axiosInstance.js";
import { getToken, isLoggedIn, logout } from "../../static/js/utils/auth.js";

document.addEventListener("DOMContentLoaded", async () => {
    const listContainer = document.querySelector(".review-items"); // 리뷰 목록
    const leftContainer = document.querySelector(".reviewlist-class ul");

    // ⭐ 상세보기 DOM 생성
    const detailContainer = document.createElement("div");
    detailContainer.classList.add("review-detail");
    detailContainer.style.display = "none";   // 처음에는 숨김
    document.querySelector(".review-content").appendChild(detailContainer);

    if (!isLoggedIn()) {
        alert("로그인이 필요합니다.");
        location.href = "/login";
        return;
    }

    listContainer.innerHTML = "<p>불러오는 중...</p>";
    leftContainer.innerHTML = "<li>불러오는 중...</li>";

    try {
        const res = await api.get("/api/reviews/me", {
            headers: { Authorization: `Bearer ${getToken()}` }
        });

        const reviews = res.data;

        if (!reviews || reviews.length === 0) {
            leftContainer.innerHTML = "<li>작성한 리뷰가 없습니다.</li>";
            listContainer.innerHTML = "<p>아직 작성한 리뷰가 없습니다.</p>";
            return;
        }

        // 왼쪽 영화 목록
        const uniqueTitles = [...new Set(reviews.map(r => r.movieTitle))];
        leftContainer.innerHTML = uniqueTitles
            .map(title => `<li class="movie-title-item" data-title="${title}">🎬 ${title}</li>`)
            .join("");

        // 오른쪽 리뷰 제목 목록 표시
        renderReviewTitles(reviews);

        // 왼쪽 영화 제목 클릭 시 필터링
        leftContainer.addEventListener("click", (e) => {
            const item = e.target.closest(".movie-title-item");
            if (!item) return;

            const selectedTitle = item.dataset.title;
            const filtered = reviews.filter(r => r.movieTitle === selectedTitle);

            // 목록 업데이트
            renderReviewTitles(filtered);

            // 상세보기 닫기
            detailContainer.style.display = "none";
            listContainer.style.display = "block";
        });

    } catch (err) {
        console.error("리뷰 불러오기 실패:", err);
        listContainer.innerHTML = "<p>리뷰를 불러오는 중 오류가 발생했습니다.</p>";
        leftContainer.innerHTML = "<li>오류 발생</li>";

        if (err.response?.status === 401) {
            alert("세션이 만료되었습니다. 다시 로그인해주세요.");
            logout();
        }
    }
});


// =====================================================
// 🧩 리뷰 제목 목록 렌더링
// =====================================================
function renderReviewTitles(list) {
    const listContainer = document.querySelector(".review-items");

    listContainer.innerHTML = `
        <h3>리뷰 제목</h3>
        <ul class="review-body-list">
            ${list.map(r => `
                <li class="review-body-item" data-reviewid="${r.reviewId}">
                    <h4>${r.reviewTitle || "(제목 없음)"}</h4>
                </li>
            `).join("")}
        </ul>
    `;
}


// =====================================================
// ⭐ 리뷰 제목 클릭 → 리뷰 내용 보기.
// =====================================================
document.addEventListener("click", async (e) => {
    const clicked = e.target.closest(".review-body-item");
    if (!clicked) return;

    const reviewId = clicked.dataset.reviewid;

    const listContainer = document.querySelector(".review-items");
    const detailContainer = document.querySelector(".review-detail");

    // 목록 숨기기
    listContainer.style.display = "none";
    detailContainer.style.display = "block";

    detailContainer.innerHTML = "<p>리뷰 불러오는 중...</p>";

    try {
        const res = await api.get(`/api/reviews/${reviewId}`, {
            headers: { Authorization: `Bearer ${getToken()}` }
        });

        const r = res.data;

        detailContainer.innerHTML = `
            <article class="review-fullpage">
                <h2 class="review-title">${r.reviewTitle || "(제목 없음)"}</h2>
                
                <div class="review-meta">
                    <span>⭐ ${r.star}</span>
                    <span>작성일: ${r.createdAt}</span>
                </div>

                <hr>

                <section class="review-body">
                    <p>${r.reviewBody.replace(/\n/g, "<br>")}</p>
                </section>

                <section class="review-oneline">

                <hr>
                <h4>한줄평</h4>
                <br>
                <p>${r.oneLineReview ? r.oneLineReview : "(등록된 한줄평이 없습니다)"}</p>
                </section>

                <button class="back-to-list">← 목록으로 돌아가기</button>
            </article>
        `;
    } catch (err) {
        console.error("리뷰 상세 조회 실패:", err);
        detailContainer.innerHTML = "<p>리뷰를 불러오는 중 오류가 발생했습니다.</p>";
    }
});


// =====================================================
// ⭐ 목록으로 돌아가기 버튼
// =====================================================
document.addEventListener("click", (e) => {
    if (!e.target.classList.contains("back-to-list")) return;

    const listContainer = document.querySelector(".review-items");
    const detailContainer = document.querySelector(".review-detail");

    // 상세보기 숨기고 목록 다시 표시
    detailContainer.style.display = "none";
    listContainer.style.display = "block";
});
