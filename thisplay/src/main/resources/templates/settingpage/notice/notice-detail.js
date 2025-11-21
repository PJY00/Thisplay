import api from "../../static/js/api/axiosInstance.js";

// URL에서 ID 추출
const noticeId = window.location.pathname.split("/").pop();

// HTML 요소
const titleEl = document.querySelector(".detail-title");
const dateEl = document.querySelector(".detail-date");
const bodyEl = document.querySelector(".detail-body");

async function loadDetail() {
  try {
    const res = await api.get(`/notice/${noticeId}`);
    const n = res.data;

    titleEl.textContent = n.title;
    dateEl.textContent = n.createdAt;
    bodyEl.innerHTML = `<p>${n.content}</p>`;
  } catch (err) {
    console.error("공지 상세 불러오기 실패:", err);
  }
}

loadDetail();
