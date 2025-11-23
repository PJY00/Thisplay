import api from "../../static/js/api/axiosInstance.js";
import { getToken, isLoggedIn, logout } from "../../static/js/utils/auth.js";

console.log("✅ mypage.js 연결 완료");

document.addEventListener("DOMContentLoaded", async () => {
  if (!isLoggedIn()) {
    alert("로그인이 필요합니다.");
    location.href = "/login";
    return;
  }

  loadProfile();
  loadMyReviews();
});


// ==============================================
//  프로필 불러오기
// ==============================================
async function loadProfile() {
  try {
    // 서버에게 userId 요청
    const idRes = await api.get("/api/users/me");
    const userId = idRes.data;

    console.log("서버에서 받은 userId:", userId);

    // userId로 프로필 조회
    const res = await api.get(`/api/users/${userId}/profile`);
    document.getElementById("profileName").textContent = res.data.nickname;

  } catch (err) {
    console.error("프로필 로딩 실패:", err);
  }
}



// ==============================================
//  나만의 감상평 불러오기
// ==============================================
async function loadMyReviews() {
  const container = document.querySelector(".review-slider");

  container.innerHTML = "<p>📝 나만의 감상평을 불러오는 중...</p>";

  try {
    const res = await api.get("/api/reviews/me");
    const reviews = res.data;

    if (!reviews || reviews.length === 0) {
      container.innerHTML = "<p>❌ 작성한 감상평이 없습니다.</p>";
      return;
    }

    // ✨ 슬라이더 기본 구조 + 리뷰 카드 렌더링
    container.innerHTML = `
          <div class="slider">
            <div class="slider-wrap" id="wrap">
              <div class="slider-track" id="track">
                ${reviews.map(r => `
                  <article class="slider-card review-card" data-reviewid="${r.reviewId}">
                    <div class="review-card-content">
                      <h3 class="review-card-title">${r.reviewTitle}</h3>
                      <p class="review-card-oneline">${r.oneLineReview || "내용 없음"}</p>
                      <p class="review-card-star">
                        ${"★".repeat(r.star)}${"☆".repeat(5 - r.star)}
                      </p>
                    </div>
                  </article>
                `).join("")}
              </div>
            </div>

            <div class="slider-controls" id="controls">
              <button class="slider-btn" id="prev">◀</button>
              <div class="slider-dots" id="dots"></div>
              <button class="slider-btn" id="next">▶</button>
            </div>
          </div>
        `;

    initSlider();

  } catch (error) {
    console.error("❌ 리뷰 불러오기 실패:", error);
    container.innerHTML = `<p style="color:red;">리뷰 정보를 불러오지 못했습니다.</p>`;
  }
}

// ==============================================
//  리뷰 카드 클릭 → 리뷰 상세 페이지로 이동
// ==============================================
document.addEventListener("click", (e) => {
  const card = e.target.closest(".slider-card");
  if (!card) return;

  const reviewId = card.dataset.reviewid;
  if (!reviewId) return;

  // 리뷰 상세 페이지로 이동
  location.href = `../reviewpage/reviewlist.html?reviewId=${reviewId}`;
});


//----------------------카드 슬라이더-----------------------------
function initSlider() {
  const track = document.getElementById("track");
  const wrap = document.getElementById("wrap");
  const cards = Array.from(track.children);
  const prev = document.getElementById("prev");
  const next = document.getElementById("next");
  const dotsBox = document.getElementById("dots");

  // 점(dot) 초기화 (중복 방지)
  dotsBox.innerHTML = "";

  // 점(dot) 생성
  cards.forEach((_, i) => {
    const dot = document.createElement("span");
    dot.className = "dot";
    dot.onclick = () => activate(i, true);
    dotsBox.appendChild(dot);
  });

  const dots = Array.from(dotsBox.children);
  let current = 0;

  // 중앙 정렬
  function center(i) {
    const card = cards[i];
    wrap.scrollTo({
      left: card.offsetLeft - (wrap.clientWidth / 2 - card.clientWidth / 2),
      behavior: "smooth"
    });
  }

  // UI 업데이트
  function toggleUI(i) {
    cards.forEach((c, k) => c.toggleAttribute("active", k === i));
    dots.forEach((d, k) => d.classList.toggle("active", k === i));
    prev.disabled = i === 0;
    next.disabled = i === cards.length - 1;
  }

  // 카드 활성화
  function activate(i, scroll) {
    current = i;
    toggleUI(i);
    if (scroll) center(i);
  }

  // 이동
  function go(step) {
    const nextIndex = Math.min(Math.max(current + step, 0), cards.length - 1);
    activate(nextIndex, true);
  }

  prev.onclick = () => go(-1);
  next.onclick = () => go(1);

  cards.forEach((card, i) => {
    card.addEventListener("mouseenter", () => activate(i, true));
    card.addEventListener("click", () => activate(i, true));
  });

  addEventListener("resize", () => center(current));

  // 초기 상태
  toggleUI(0);
  center(0);
}