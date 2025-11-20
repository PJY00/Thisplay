import api from "../../static/js/api/axiosInstance.js";

// URL에서 ID 가져오기
const noticeId = window.location.pathname.split("/").pop();

const titleInput = document.querySelector("#notice-title");
const contentInput = document.querySelector("#notice-content");
const saveBtn = document.querySelector("#notice-save");
const deleteBtn = document.querySelector("#notice-delete");

// 수정 페이지 진입 시 기존 데이터 불러오기
async function loadNotice() {
  try {
    const res = await api.get(`/notice/${noticeId}`);
    const n = res.data;

    titleInput.value = n.title;
    contentInput.value = n.content;
  } catch (err) {
    console.error(err);
  }
}

saveBtn.addEventListener("click", async () => {
  const dto = {
    title: titleInput.value,
    content: contentInput.value,
  };

  try {
    await api.put(`/notice/${noticeId}`, dto);
    alert("수정 완료");
    window.location.href = `/notice/${noticeId}`;
  } catch (err) {
    console.error(err);
    alert("수정 실패");
  }
});

deleteBtn.addEventListener("click", async () => {
  if (!confirm("정말 삭제하시겠습니까?")) return;

  try {
    await api.delete(`/notice/${noticeId}`);
    alert("삭제 완료");
    window.location.href = "/notice/list";
  } catch (err) {
    alert("삭제 실패");
  }
});

loadNotice();
