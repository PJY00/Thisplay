import api from "../api/axiosInstance.js";

const listContainer = document.querySelector(".notice-list");

async function loadNotices() {
  try {
    let notices = [];

    // 1) 백엔드에서 실제 공지 가져오기
    try {
      const res = await api.get("/notice");
      notices = res.data;
    } catch (e) {
      console.warn(
        "백엔드에서 데이터를 가져오지 못했습니다. 더미 데이터로 표시합니다."
      );
    }

    // 2) 공지가 0개일 경우 → 기본 공지 1개 추가
    if (notices.length === 0) {
      notices.push({
        noticeId: 1,
        title: "Thisplay 공지사항",
        content: "테스트 공지입니다.",
        createdAt: "2024-12-10",
        updatedAt: null,
        writer: "관리자",
        viewCount: 0,
      });
    }

    // 3) 화면 렌더링
    listContainer.innerHTML = "";

    notices.forEach((n) => {
      const row = document.createElement("li");
      row.classList.add("notice-row");

      row.innerHTML = `
                <span class="col-id">${n.noticeId}</span>
                <span class="col-title"><a href="/notice/${n.noticeId}">${
        n.title
      }</a></span>
                <span class="col-writer">${n.writer ?? "관리자"}</span>
                <span class="col-date">${n.createdAt}</span>
                <span class="col-view">${n.viewCount ?? 0}</span>
            `;

      listContainer.appendChild(row);
    });
  } catch (err) {
    console.error(err);
  }
}

loadNotices();
