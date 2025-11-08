import api from "../../static/js/api/axiosInstance.js";
import { logout, isLoggedIn, getToken } from "../../static/js/utils/auth.js";

const BASE_URL = "http://localhost:8080";
console.log("✅ folder.js 연결 완료");


document.addEventListener("DOMContentLoaded", async () => {
    const folderWrapper = document.getElementById("folder-wrapper");
    const leftArrow = document.getElementById("left-arrow");
    const rightArrow = document.getElementById("right-arrow");
    const scrollAmount = 300;

    // 🔹 폴더 목록 가져오기
    async function loadMyFolders() {
        try {
            const response = await api.get("/api/folders/me");
            const folders = response.data;


            console.log(folders);

            if (!folders || folders.length === 0) {
                folderWrapper.innerHTML = `<p style="color: #ccc;">등록된 폴더가 없습니다.</p>`;
                return;
            }

            // 🔹 innerHTML로 카드 렌더링
            folderWrapper.innerHTML = folders
                .map(
                    (f) => `
        <div class="folder-card">
            <div class="folder-thumbnail"></div>
            <p class="folder-title">${f.folderName}</p>
        </div>
        `
                )
                .join("");
        } catch (err) {
            console.error("❌ 폴더 목록 불러오기 실패:", err);
            folderWrapper.innerHTML = `<p style="color:red;">폴더를 불러오는 중 오류가 발생했습니다.</p>`;
        }
    }

    leftArrow.addEventListener("click", () => {
        folderWrapper.scrollBy({
            left: -scrollAmount,
            behavior: "smooth",
        });
    });

    rightArrow.addEventListener("click", () => {
        folderWrapper.scrollBy({
            left: scrollAmount,
            behavior: "smooth",
        });
    });
    // ✅ 폴더 생성 기능
    const form = document.getElementById("create-folder-form");
    const resultText = document.getElementById("folder-result");

    if (form) {
        form.addEventListener("submit", async (e) => {
            e.preventDefault();

            const folderName = document.getElementById("folderName").value.trim();
            const visibility = document.getElementById("visibility").value;

            if (!folderName) {
                resultText.textContent = "폴더 이름을 입력해주세요.";
                resultText.style.color = "red";
                return;
            }

            try {
                // ✅ axiosInstance 사용 → TEST_TOKEN 자동 첨부됨
                const response = await api.post(
                    `/api/folders/create?folderName=${encodeURIComponent(folderName)}&visibility=${visibility}`
                );

                const data = response.data;
                console.log("✅ 폴더 생성 성공:", data);

                resultText.textContent = `"${data.folderName}" 폴더가 생성되었습니다!`;
                resultText.style.color = "green";

                // 새로고침 없이 바로 폴더 목록 갱신
                await loadMyFolders();
                form.reset();
            } catch (err) {
                console.error("❌ 폴더 생성 실패:", err);
                resultText.textContent = "폴더 생성에 실패했습니다.";
                resultText.style.color = "red";
            }
        });
    }
    await loadMyFolders();
});
