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

            // 🔹 폴더 카드 렌더링 (⋮ 메뉴 + 삭제 버튼 포함)
            folderWrapper.innerHTML = folders
                .map(
                    (f) => `
        <div class="folder-card" data-folderid="${f.folderId}">
            <div class="folder-thumbnail"></div>
            <p class="folder-title">${f.folderName}</p>

            <!-- 오른쪽 하단 ⋮ 메뉴 -->
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

            // ✅ 카드 렌더링 후 이벤트 연결
            attachMenuEvents();
        } catch (err) {
            console.error("❌ 폴더 목록 불러오기 실패:", err);
            folderWrapper.innerHTML = `<p style="color:red;">폴더를 불러오는 중 오류가 발생했습니다.</p>`;
        }
    }

    // ✅ ⋮ 버튼 및 삭제 버튼 이벤트 연결 함수
    function attachMenuEvents() {
        // ⋮ 버튼 클릭 시 메뉴 열기/닫기
        document.querySelectorAll(".menu-btn").forEach((btn) => {
            btn.addEventListener("click", (e) => {
                e.stopPropagation(); // 이벤트 버블링 방지
                const dropdown = e.currentTarget.nextElementSibling;
                dropdown.classList.toggle("hidden");
            });
        });

        // 삭제 버튼 클릭 시 API 호출
        document.querySelectorAll(".delete-btn").forEach((btn) => {
            btn.addEventListener("click", async (e) => {
                e.stopPropagation();
                const card = e.currentTarget.closest(".folder-card");
                const folderId = card.dataset.folderId;
                const folderName = card.querySelector(".folder-title").textContent;

                if (confirm(`'${folderName}' 폴더를 삭제하시겠습니까?`)) {
                    try {
                        const res = await api.delete(`/api/folders/${folderId}`, {
                            headers: {
                                Authorization: `Bearer ${getToken()}`,
                            },
                        });

                        if (res.status === 200) {
                            alert("폴더가 성공적으로 삭제되었습니다.");
                            card.remove();
                        } else {
                            alert("폴더 삭제 실패: " + res.statusText);
                        }
                    } catch (err) {
                        console.error("❌ 폴더 삭제 중 오류:", err);
                        alert("서버 오류로 폴더를 삭제할 수 없습니다.");
                    }
                }
            });
        });

        // 다른 영역 클릭 시 모든 드롭다운 닫기
        document.addEventListener("click", (e) => {
            if (!e.target.closest(".folder-menu")) {
                document.querySelectorAll(".menu-dropdown").forEach((menu) => menu.classList.add("hidden"));
            }
        });
    }

    // ✅ 좌우 스크롤
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
                setTimeout(() => {
                    resultText.textContent = "";
                }, 5000);
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

    // ✅ 초기 폴더 목록 로드
    await loadMyFolders();
});
