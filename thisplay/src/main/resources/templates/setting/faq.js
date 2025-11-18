import api from "../../static/js/api/axiosInstance.js";

document.addEventListener("DOMContentLoaded", async () => {
    const faqListEl = document.getElementById("faqList");

    // 초기 로딩 메시지
    faqListEl.innerHTML = `<p class="faq-loading">FAQ를 불러오는 중입니다...</p>`;

    try {
        // FAQ 전체 조회: GET /api/setting/faq
        const res = await api.get("/api/setting/faq");
        const faqs = res.data;

        if (!faqs || faqs.length === 0) {
            faqListEl.innerHTML = `<p class="faq-empty">등록된 FAQ가 없습니다.</p>`;
            return;
        }

        // 목록 렌더링
        faqListEl.innerHTML = faqs.map(faqToHTML).join("");

        // 아코디언 토글
        faqListEl.addEventListener("click", (e) => {
            const btn = e.target.closest(".faq-question");
            if (!btn) return;

            const item = btn.closest(".faq-item");
            if (!item) return;

            const isOpen = item.classList.contains("open");

            // 다른 것들 닫기
            document.querySelectorAll(".faq-item.open")
                .forEach(el => el.classList.remove("open"));

            // 내가 클릭한 것만 토글
            if (!isOpen) {
                item.classList.add("open");
            }
        });
    } catch (err) {
        console.error("FAQ 불러오기 실패:", err);
        faqListEl.innerHTML = `<p class="faq-error">FAQ를 불러오는 중 오류가 발생했습니다.</p>`;
    }
});

// FAQ 하나를 HTML 문자열로 변환
function faqToHTML(faq) {
    // DTO 이름에 맞게 필드 사용 (faqId / question / answer 가정)
    const { faqId, question, answer } = faq;

    return `
      <article class="faq-item" data-faqid="${faqId}">
        <button class="faq-question">
          <div class="faq-question-left">
            <span class="faq-q-label">Q</span>
            <span class="faq-question-text">${escapeHtml(question ?? "")}</span>
          </div>
          <span class="faq-arrow">▶</span>
        </button>
        <div class="faq-answer-wrapper">
          <div class="faq-answer">
            ${escapeHtml(answer ?? "").replace(/\n/g, "<br>")}
          </div>
        </div>
      </article>
    `;
}

// 간단 escape
function escapeHtml(str) {
    return String(str)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}