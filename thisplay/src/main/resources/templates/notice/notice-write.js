import api from "../api/axiosInstance.js";

const titleInput = document.querySelector("#writeTitle");
const contentInput = document.querySelector("#writeContent");
const submitBtn = document.querySelector("#writeSubmit");

submitBtn.addEventListener("click", async () => {
  const title = titleInput.value.trim();
  const content = contentInput.value.trim();

  if (!title) {
    alert("제목을 입력해주세요.");
    return;
  }
  if (!content) {
    alert("내용을 입력해주세요.");
    return;
  }

  try {
    const res = await api.post("/notice", {
      title,
      content,
    });

    alert("공지사항이 등록되었습니다.");
    window.location.href = "/notice/list";
  } catch (err) {
    console.error(err);
    alert("등록 중 오류가 발생했습니다.");
  }
});
