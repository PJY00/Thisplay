import axios from "https://cdn.jsdelivr.net/npm/axios@1.6.8/+esm";
import { getToken } from "../../static/js/utils/auth.js"; // JWT 토큰 불러오기 함수

const token = getToken();

document.getElementById("submitReviewBtn").addEventListener("click", async (e) => {
    e.preventDefault();

    const tmdbId = 12345; // ✅ 예시: 실제 페이지에서 해당 영화 id를 세팅해야 함
    const folderId = document.getElementById("folderSelect").value;
    const reviewTitle = document.getElementById("reviewTitle").value;
    const reviewBody = document.getElementById("reviewBody").value;
    const oneLineReview = document.getElementById("oneLineReview").value;
    const star = parseInt(document.getElementById("starRating").value);
    const visibility = document.querySelector("input[name='visibility']:checked").value;

    if (!reviewTitle || !reviewBody) {
        alert("제목과 감상평을 입력해주세요!");
        return;
    }

    const data = {
        reviewTitle,
        reviewBody,
        oneLineReview,
        star,
        folderId: folderId || null,
        visibility,
    };

    try {
        // ✅ 리뷰 등록 요청
        const res = await axios.post(`${BASE_URL}/movie/${tmdbId}`, data, {
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        });

        alert("리뷰가 등록되었습니다!");

        // ✅ 등록 후 내 리뷰 목록 다시 불러오기
        const listRes = await axios.get(`${BASE_URL}/me`, {
            headers: { Authorization: `Bearer ${token}` },
        });

        console.log("나의 리뷰 목록:", listRes.data);

        // 화면에 표시하는 로직 (예시)
        renderMyReviewList(listRes.data);

    } catch (err) {
        console.error("리뷰 등록 실패:", err);
        alert("리뷰 등록 중 오류가 발생했습니다.");
    }
});

function renderMyReviewList(reviews) {
    const container = document.getElementById("myReviewList");
    if (!container) return;

    container.innerHTML = ""; // 기존 목록 초기화

    reviews.forEach((review) => {
        const div = document.createElement("div");
        div.className = "review-card";
        div.innerHTML = `
      <h4>${review.reviewTitle}</h4>
      <p>⭐ ${review.star} / 5</p>
      <p>${review.oneLineReview}</p>
      <p><small>${review.movieTitle}</small></p>
    `;
        container.appendChild(div);
    });
}

console.log("✅ writereview.js 연결 완료");