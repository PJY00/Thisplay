import api from "../../static/js/api/axiosInstance.js";
import { logout, isLoggedIn, getToken } from "../../static/js/utils/auth.js";

const BASE_URL = "http://localhost:8080";
console.log("✅ folder.js 연결 완료");

document.addEventListener("DOMContentLoaded", async () => {
    console.log("✅ DOMContentLoaded in folder.js"); // ⭐ 디버그 추가

    const folderWrapper = document.getElementById("folder-wrapper");
    const leftArrow = document.getElementById("left-arrow");
    const rightArrow = document.getElementById("right-arrow");
    const scrollAmount = 300;

    // 🔹 폴더 목록 가져오기
    async function loadMyFolders() {
        try {
            console.log("🔁 loadMyFolders 호출"); // ⭐ 디버그 추가
            const response = await api.get("/api/folders/me");
            const folders = response.data;

            console.log("📦 내 폴더 목록:", folders);

            if (!folders || folders.length === 0) {
                folderWrapper.innerHTML = `<p style="color: #ccc;">등록된 폴더가 없습니다.</p>`;
                return;
            }

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

            attachMenuEvents();
        } catch (err) {
            console.error("❌ 폴더 목록 불러오기 실패:", err);
            folderWrapper.innerHTML = `<p style="color:red;">폴더를 불러오는 중 오류가 발생했습니다.</p>`;
        }
    }

    // ================================
    // ⭐ 친구 폴더 불러오기
    // ================================
    async function loadFriendFolders(nickname) {
        console.log("🔁 loadFriendFolders 호출, nickname =", nickname); // ⭐ 디버그 추가

        try {
            const response = await api.get(`/api/folders/${nickname}`);
            const folders = response.data;
            console.log("📦 친구 폴더 응답:", folders); // ⭐ 디버그 추가

            const friendContainer = document.getElementById("friend-folder-container");
            const msg = document.getElementById("friend-folder-result");

            console.log("🧩 friendContainer =", friendContainer); // ⭐ 디버그 추가
            console.log("🧩 msg =", msg);                         // ⭐ 디버그 추가

            if (!friendContainer) {
                console.warn("⚠ friend-folder-container 요소를 찾지 못했습니다."); // ⚠ 의심 포인트
                return;
            }

            friendContainer.innerHTML = "";
            if (msg) msg.textContent = "";

            if (!folders || folders.length === 0) {
                friendContainer.innerHTML = `<p style="color:#ccc;">폴더가 없습니다.</p>`;
                if (msg) {
                    msg.textContent = `${nickname}님의 공개 폴더가 없습니다.`;
                    msg.style.color = "white";
                }
                return;
            }

            friendContainer.innerHTML = folders
                .map(
                    (f) => `
                <div class="folder-card" data-folderid="${f.folderId}">
                    <div class="folder-thumbnail"></div>
                    <p class="folder-title">${f.folderName}</p>

                    <div class="folder-menu">
                        <button class="menu-btn hidden">⋮</button>
                        <div class="menu-dropdown hidden">
                            <button class="delete-btn hidden">삭제</button>
                        </div>
                    </div>
                </div>
            `
                )
                .join("");

            attachMenuEvents(false);

            if (msg) {
                msg.textContent = `${nickname}님의 폴더 ${folders.length}개를 불러왔습니다.`;
                msg.style.color = "green";
            }
        } catch (err) {
            console.error("❌ loadFriendFolders 오류:", err);
            const msg = document.getElementById("friend-folder-result");
            if (msg) {
                msg.textContent = "친구 폴더를 불러오는 데 실패했습니다.";
                msg.style.color = "red";
            }
        }
    }

    // ⭐ 친구 폴더 검색 버튼 이벤트
    const searchBtn = document.getElementById("search-friend-folder-btn");
    console.log("🔍 searchBtn =", searchBtn); // ⭐ 디버그 추가

    if (searchBtn) {
        searchBtn.addEventListener("click", async () => {
            console.log("✅ 검색 버튼 클릭 이벤트 진입"); // ⭐ 디버그 추가

            const input = document.getElementById("friend-nickname");
            console.log("🔍 friend-nickname input =", input); // ⭐ 디버그 추가

            const nickname = input?.value.trim();
            console.log("🔍 입력된 nickname =", nickname);     // ⭐ 디버그 추가

            if (!nickname) {
                const msg = document.getElementById("friend-folder-result");
                if (msg) {
                    msg.textContent = "닉네임을 입력해주세요.";
                    msg.style.color = "red";
                }
                console.log("⚠ 닉네임이 비어 있음"); // ⭐ 디버그 추가
                return;
            }

            await loadFriendFolders(nickname);
        });
    } else {
        console.warn("⚠ search-friend-folder-btn 요소를 찾지 못했습니다."); // ⚠ 의심 포인트
    }

    // ✅ ⋮ 버튼 및 삭제 버튼 이벤트 연결 함수
    function attachMenuEvents(isMyFolder = true) {

        if (!isMyFolder) {
            document.querySelectorAll(".delete-btn").forEach(btn => btn.classList.add("hidden"));
        }

        document.querySelectorAll(".menu-btn").forEach((btn) => {
            btn.addEventListener("click", (e) => {
                if (!isMyFolder) return;
                e.stopPropagation();
                const dropdown = e.currentTarget.nextElementSibling;
                dropdown.classList.toggle("hidden");
            });
        });

        if (isMyFolder) {
            document.querySelectorAll(".delete-btn").forEach((btn) => {
                btn.addEventListener("click", async (e) => {
                    e.stopPropagation();
                    const card = e.currentTarget.closest(".folder-card");
                    const folderId = card.dataset.folderid;
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
        }

        document.addEventListener("click", (e) => {
            if (!e.target.closest(".folder-menu")) {
                document.querySelectorAll(".menu-dropdown").forEach((menu) => menu.classList.add("hidden"));
            }
        });
    }

    // ✅ 좌우 스크롤
    leftArrow?.addEventListener("click", () => {
        folderWrapper.scrollBy({
            left: -scrollAmount,
            behavior: "smooth",
        });
    });

    rightArrow?.addEventListener("click", () => {
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
                const response = await api.post(
                    `/api/folders/create?folderName=${encodeURIComponent(folderName)}&visibility=${visibility}`
                );

                const data = response.data;
                console.log("✅ 폴더 생성 성공:", data);

                resultText.textContent = `"${data.folderName}" 폴더가 생성되었습니다!`;
                resultText.style.color = "green";

                setTimeout(() => {
                    resultText.textContent = "";
                }, 5000);

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
