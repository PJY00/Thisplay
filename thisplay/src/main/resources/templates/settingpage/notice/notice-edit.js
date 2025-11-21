import api from "../api/axiosInstance.js";

// URL에서 ID 가져오기
const pathParts = window.location.pathname.split("/");
const noticeId = pathParts[pathParts.length - 1];

const titleInput = document.querySelector("#editTitle");
const contentInput = document.querySelector("#editContent");
const submitBtn = document.querySelector("#editSubmit");

// 1) 기존 데이터 불러오기
async function loadNotice() {
  try {
    const res = await api.get(`/notice/${noticeId}`);
    const data = res.data;

    titleInput.value = data.title;
    contentInput.value = data.content;
  } catch (err) {
    console.error(err);
    alert("공지사항 정보를 불러오지 못했습니다.");
  }
}

loadNotice();

// 2) 수정 요청
submitBtn.addEventListener("click", async () => {
  const title = titleInput.value.trim();
  const content = contentInput.value.trim();

  if (!title) return alert("제목을 입력해주세요.");
  if (!content) return alert("내용을 입력해주세요.");

  try {
    await api.put(`/notice/${noticeId}`, {
      title,
      content,
    });

    alert("수정이 완료되었습니다.");
    window.location.href = `/notice/${noticeId}`;
  } catch (err) {
    console.error(err);
    alert("수정 중 오류가 발생했습니다.");
  }
});
