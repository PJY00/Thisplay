// ✅ axiosInstance + auth 유틸 불러오기
import api, { BASE_URL } from "../../static/js/api/axiosInstance.js";
import { getToken } from "../../static/js/utils/auth.js";

console.log("✅ writereview.js 연결 완료");

// ✅ 임시로 TMDB 영화 ID 지정
const tmdbId = 1022789; // 인사이드 아웃 2 예시

// ✅ 페이지 로드시 영화 제목 불러오기
document.addEventListener("DOMContentLoaded", async () => {
    try {
        const res = await api.get(`/api/movies/show/${tmdbId}`);
        const title = res.data.title || "제목 없음";
        document.getElementById("movieTitle").value = title;
        console.log("🎬 영화 제목 로드 완료:", title);
    } catch (err) {
        console.error("❌ 영화 제목 불러오기 실패:", err);
        document.getElementById("movieTitle").value = "제목 없음";
    }
});

// ✅ 리뷰 등록 버튼 클릭 이벤트
document.getElementById("submitReviewBtn").addEventListener("click", async (e) => {
    e.preventDefault();

    const folderId = document.getElementById("folderSelect").value;
    const reviewTitle = document.getElementById("reviewTitle").value.trim();
    const reviewBody = document.getElementById("reviewBody").value.trim();
    const oneLineReview = document.getElementById("oneLineReview").value.trim();
    const star = parseInt(document.getElementById("starRating").value);
    const visibility = document.querySelector("input[name='visibility']:checked")?.value;

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
        // ✅ 리뷰 등록 (api 사용 → 자동으로 Authorization 헤더 포함)
        const res = await api.post(`/api/reviews/movie/${tmdbId}`, data);
        console.log("✅ 리뷰 등록 성공:", res.data);
        alert("리뷰가 등록되었습니다!");

        // ✅ 내 리뷰 목록 불러오기
        const listRes = await api.get(`/api/reviews/me`);
        console.log("📋 나의 리뷰 목록:", listRes.data);
        renderMyReviewList(listRes.data);

    } catch (err) {
        console.error("❌ 리뷰 등록 실패:", err);
        alert("리뷰 등록 중 오류가 발생했습니다.\n(콘솔을 확인하세요)");
    }
});

// ✅ 리뷰 목록 렌더링 함수
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

console.log("🌐 BASE_URL:", BASE_URL);
