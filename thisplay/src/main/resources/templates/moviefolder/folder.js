import api from "../../static/js/api/axiosInstance.js";
import { getToken } from "../../static/js/utils/auth.js";

const BASE_URL = "http://localhost:8080";
console.log("✅ folder.js 연결 완료");

document.addEventListener("DOMContentLoaded", async () => {
    const folderWrapper = document.getElementById("folder-wrapper");
    const leftArrow = document.getElementById("left-arrow");
    const rightArrow = document.getElementById("right-arrow");

    /* 🔥 카드 크기 계산 */
    const CARD_WIDTH = 150;
    const GAP = 16;
    const ITEM_WIDTH = CARD_WIDTH + GAP; // 166px
    const MOVE_COUNT = 5;
    const MOVE_AMOUNT = ITEM_WIDTH * MOVE_COUNT; // 한 번 누르면 5칸 이동

    /* 📌 폴더 목록 가져오기 */
    async function loadMyFolders() {
        try {
            const response = await api.get("/api/folders/me");
            const folders = response.data;

            if (!folders || folders.length === 0) {
                folderWrapper.innerHTML = `<p style="color:#ccc;">등록된 폴더가 없습니다.</p>`;
                return;
            }

            folderWrapper.innerHTML = folders
                .map(
                    (f) => `
                <div class="folder-card" data-folder-id="${f.folderId}">
                    <div class="folder-thumbnail"></div>
                    <p class="folder-title">${f.folderName}</p>

                    <div class="folder-menu">
                        <button class="menu-btn">⋮</button>
                        <div class="menu-dropdown hidden">
                            <button class="delete-btn">삭제</button>
                        </div>
                    </div>
                </div>
            `
                )
                .join("");

            attachMenuEvents();
        } catch (err) {
            console.error("❌ 폴더 불러오기 실패:", err);
            folderWrapper.innerHTML = `<p style="color:red;">폴더 정보를 가져오지 못했습니다.</p>`;
        }
    }

    /* 📌 ⋮ 메뉴 및 삭제 기능 */
    function attachMenuEvents() {
        document.querySelectorAll(".menu-btn").forEach((btn) => {
            btn.addEventListener("click", (e) => {
                e.stopPropagation();
                const dropdown = e.currentTarget.nextElementSibling;
                dropdown.classList.toggle("hidden");
            });
        });

        document.querySelectorAll(".delete-btn").forEach((btn) => {
            btn.addEventListener("click", async (e) => {
                e.stopPropagation();
                const card = e.currentTarget.closest(".folder-card");
                const folderId = card.dataset.folderId;

                if (!confirm("정말 삭제할까요?")) return;

                try {
                    const res = await api.delete(`/api/folders/${folderId}`, {
                        headers: { Authorization: `Bearer ${getToken()}` },
                    });

                    if (res.status === 200) {
                        card.remove();
                    }
                } catch (err) {
                    console.error("❌ 폴더 삭제 실패:", err);
                }
            });
        });

        document.addEventListener("click", () => {
            document.querySelectorAll(".menu-dropdown").forEach((m) =>
                m.classList.add("hidden")
            );
        });
    }

    /* 🔥 좌우 화살표 이동 기능 */
    leftArrow.addEventListener("click", () => {
        folderWrapper.scrollBy({
            left: -MOVE_AMOUNT,
            behavior: "smooth",
        });
    });

    rightArrow.addEventListener("click", () => {
        folderWrapper.scrollBy({
            left: MOVE_AMOUNT,
            behavior: "smooth",
        });
    });

    /* 📌 폴더 생성 기능 */
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
                const response = await api.post(
                    `/api/folders/create?folderName=${encodeURIComponent(folderName)}&visibility=${visibility}`
                );

                resultText.textContent = `"${response.data.folderName}" 폴더 생성 완료!`;
                resultText.style.color = "green";

                await loadMyFolders();
                form.reset();
            } catch (err) {
                console.error("❌ 폴더 생성 실패:", err);
                resultText.textContent = "폴더 생성 실패";
                resultText.style.color = "red";
            }
        });
    }

    /* 초기 로딩 */
    await loadMyFolders();
});
