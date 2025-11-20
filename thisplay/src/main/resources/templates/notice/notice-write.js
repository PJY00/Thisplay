import api from "../../static/js/api/axiosInstance.js";

const titleInput = document.querySelector("#notice-title");
const contentInput = document.querySelector("#notice-content");
const submitBtn = document.querySelector("#notice-submit");

submitBtn.addEventListener("click", async () => {
  const dto = {
    title: titleInput.value,
    content: contentInput.value,
  };

  try {
    await api.post("/notice", dto);
    alert("등록되었습니다.");
    window.location.href = "/notice/list";
  } catch (err) {
    console.error(err);
    alert("공지 등록 실패");
  }
});
