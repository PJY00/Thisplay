// ==========================================================
//  axiosInstance + auth 유틸 불러오기
// ==========================================================
import api, { BASE_URL } from "../../static/js/api/axiosInstance.js";
import { getToken } from "../../static/js/utils/auth.js";

console.log("✅ writereview.js 연결 완료");


// ==========================================================
//  URL 파라미터로 수정 모드 여부 확인
//  예: /review/write?edit=true&reviewId=12
// ==========================================================
const params = new URLSearchParams(location.search);
const isEdit = params.get("edit") === "true";
const editReviewId = params.get("reviewId");
const movieId = new URLSearchParams(location.search).get("movieId");


// ==========================================================
//  페이지 로드 시 (1) 영화 정보 또는 (2) 기존 리뷰 데이터 불러오기
// ==========================================================
document.addEventListener("DOMContentLoaded", async () => {
    // ⭐ 수정 모드일 경우
    if (isEdit && editReviewId) {
        console.log("✏ 수정 모드 진입! reviewId =", editReviewId);

        try {
            const res = await api.get(`/api/reviews/${editReviewId}`, {
                headers: { Authorization: `Bearer ${getToken()}` }
            });

            const r = res.data;

            // ⭐ 폼에 기존 리뷰 데이터 자동 입력
            document.getElementById("movieTitle").value = r.movieTitle;
            document.getElementById("reviewTitle").value = r.reviewTitle;
            document.getElementById("reviewBody").value = r.reviewBody;
            document.getElementById("oneLineReview").value = r.oneLineReview ?? "";
            document.getElementById("starRating").value = r.star;

            // 업데이트용 TMDB ID (수정 시 필요)
            window.tmdbId = r.movieId;

            // 버튼 이름 변경
            document.getElementById("submitReviewBtn").textContent = "리뷰 수정 완료";

            console.log("✏ 기존 리뷰 데이터 로드 완료:", r);

        } catch (err) {
            console.error("❌ 수정 데이터 불러오기 실패:", err);
            alert("리뷰 정보를 불러오는 중 오류가 발생했습니다.");
        }

        return;
    }

    // ⭐ 신규 작성 모드일 경우
    console.log("🆕 신규 리뷰 작성 모드");

    window.tmdbId = movieId

    try {
        const res = await api.get(`/api/movies/show/${movieId}`);
        document.getElementById("movieTitle").value = res.data.title || "제목 없음";
    } catch (err) {
        console.error("❌ 영화 제목 불러오기 실패:", err);
        document.getElementById("movieTitle").value = "제목 없음";
    }
});


// ==========================================================
//  리뷰 제출 버튼 — 신규 작성(POST) + 수정(UPDATE) 모두 처리
// ==========================================================
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
        movieId: window.tmdbId
    };

    // ======================================================
    // ✏ [수정 모드]: PUT 요청 실행
    // ======================================================
    if (isEdit && editReviewId) {
        try {
            await api.patch(`/api/reviews/${editReviewId}`, data, {
                headers: { Authorization: `Bearer ${getToken()}` }
            });

            alert("리뷰가 수정되었습니다!");
            location.href = "../reviewpage/reviewlist.html"; // 수정 후 목록으로 이동

        } catch (err) {
            console.error("❌ 리뷰 수정 실패:", err);
            alert("리뷰 수정 중 오류가 발생했습니다.");
        }

        return;
    }


    // ======================================================
    // 🆕 [신규 작성 모드]: POST 요청 실행
    // ======================================================
    try {
        const res = await api.post(`/api/reviews/movie/${window.tmdbId}`, data);
        console.log("✅ 리뷰 등록 성공:", res.data);
        alert("리뷰가 등록되었습니다!");

        location.href = "../reviewpage/reviewlist.html"; // 리스트로 이동

    } catch (err) {
        console.error("❌ 리뷰 등록 실패:", err);
        alert("리뷰 등록 중 오류 발생 (콘솔 확인)");
    }
});


// ==========================================================
//  선택사항: 내 리뷰 목록 렌더링 함수
// ==========================================================
function renderMyReviewList(reviews) {
    const container = document.getElementById("myReviewList");
    if (!container) return;

    container.innerHTML = "";

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
