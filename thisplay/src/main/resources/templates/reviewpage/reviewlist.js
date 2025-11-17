import api from "../../static/js/api/axiosInstance.js";
import { getToken, isLoggedIn, logout } from "../../static/js/utils/auth.js";

document.addEventListener("DOMContentLoaded", async () => {
    const rightContainer = document.querySelector(".review-items");
    const leftContainer = document.querySelector(".reviewlist-class ul");

    if (!isLoggedIn()) {
        alert("로그인이 필요합니다.");
        location.href = "/login";
        return;
    }

    rightContainer.innerHTML = "<p>불러오는 중...</p>";
    if (leftContainer) {
        leftContainer.innerHTML = "<li>불러오는 중...</li>";
    }

    try {
        const res = await api.get("/api/reviews/me", {
            headers: { Authorization: `Bearer ${getToken()}` }
        });

        const reviews = res.data;

        if (!reviews || reviews.length === 0) {
            if (leftContainer) leftContainer.innerHTML = "<li>작성한 리뷰가 없습니다.</li>";
            rightContainer.innerHTML = "<p>아직 작성한 리뷰가 없습니다.</p>";
            return;
        }

        // ============================================
        // 1️⃣ 왼쪽: 중복 제거한 영화 제목 렌더링
        // ============================================
        const uniqueTitles = [...new Set(reviews.map(r => r.movieTitle))];

        if (leftContainer) {
            leftContainer.innerHTML = uniqueTitles
                .map(title => `<li class="movie-title-item" data-title="${title}">🎬 ${title}</li>`)
                .join("");
        }

        // ============================================
        // 2️⃣ 오른쪽: 전체 리뷰제목 표시 (초기)
        // ============================================
        renderReviewTitles(reviews);

        // ============================================
        // ⭐ 3️⃣ 왼쪽 영화 제목 클릭 → 해당 리뷰만 표시
        // ============================================
        leftContainer.addEventListener("click", (e) => {
            const item = e.target.closest(".movie-title-item");
            if (!item) return;

            const selectedTitle = item.dataset.title;

            // 선택된 영화 제목에 해당하는 리뷰만 필터링
            const filtered = reviews.filter(r => r.movieTitle === selectedTitle);

            renderReviewTitles(filtered);
        });

    } catch (err) {
        console.error("리뷰 불러오기 실패:", err);
        rightContainer.innerHTML = "<p>리뷰를 불러오는 중 오류가 발생했습니다.</p>";

        if (leftContainer) leftContainer.innerHTML = "<li>오류 발생</li>";

        if (err.response?.status === 401) {
            alert("세션이 만료되었습니다. 다시 로그인해주세요.");
            logout();
        }
    }
});


// =====================================================
// 🧩 리뷰 제목 목록을 렌더링하는 함수 (재사용 가능)
// =====================================================
function renderReviewTitles(list) {
    const rightContainer = document.querySelector(".review-items");

    rightContainer.innerHTML = `
        <h3>리뷰 제목</h3>
        <ul class="review-body-list">
            ${list
            .map(
                (r) => `
                <li class="review-body-item" data-reviewid="${r.reviewId}">
                    <h4>${r.reviewTitle || "(제목 없음)"}</h4>
                </li>
            `
            )
            .join("")}
        </ul>
    `;
}



// =====================================================
// ⭐ 4️⃣ 리뷰 제목 클릭 → 상세페이지로 표시
// =====================================================
document.addEventListener("click", async (e) => {
    const clicked = e.target.closest(".review-body-item");
    if (!clicked) return;

    const reviewId = clicked.dataset.reviewid;
    const mainContent = document.querySelector(".review-content");

    mainContent.innerHTML = "<p>리뷰 불러오는 중...</p>";

    try {
        const res = await api.get(`/api/reviews/${reviewId}`, {
            headers: { Authorization: `Bearer ${getToken()}` }
        });

        const r = res.data;

        mainContent.innerHTML = `
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

                <button class="back-to-list">← 목록으로 돌아가기</button>
            </article>
        `;
    } catch (err) {
        console.error("리뷰 상세 조회 실패:", err);
        mainContent.innerHTML = "<p>리뷰를 불러오는 중 오류가 발생했습니다.</p>";
    }
});


// =====================================================
// ⭐ 5️⃣ 목록으로 돌아가기 (새로고침 방식)
// =====================================================
document.addEventListener("click", (e) => {
    if (e.target.classList.contains("back-to-list")) {
        location.reload();
    }
});
