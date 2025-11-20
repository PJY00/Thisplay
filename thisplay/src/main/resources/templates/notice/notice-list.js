import api from "../../static/js/api/axiosInstance.js";

// HTML 요소
const listContainer = document.querySelector(".notice-list");

// 공지사항 목록 불러오기
async function loadNotices() {
  try {
    const res = await api.get("/notice");
    const notices = res.data;

    listContainer.innerHTML = "";

    notices.forEach((n) => {
      const row = document.createElement("li");
      row.classList.add("notice-row");

      row.innerHTML = `
                <a href="/notice/${n.noticeId}" class="col-title">${n.title}</a>
                <span class="col-writer">관리자</span>
                <span class="col-date">${n.createdAt}</span>
                <span class="col-view">0</span>
            `;

      listContainer.appendChild(row);
    });
  } catch (err) {
    console.error("공지사항 불러오기 실패:", err);
  }
}

loadNotices();
