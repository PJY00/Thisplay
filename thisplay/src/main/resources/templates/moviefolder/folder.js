import api from "../../static/js/api/axiosInstance.js";
import { getToken } from "../../static/js/utils/auth.js";

const BASE_URL = "http://localhost:8080";
console.log("✅ folder.js 연결 완료");

document.addEventListener("DOMContentLoaded", async () => {
    console.log("✅ DOMContentLoaded in folder.js");

    const folderWrapper = document.getElementById("folder-wrapper");
    const leftArrow = document.getElementById("left-arrow");
    const rightArrow = document.getElementById("right-arrow");

    const friendFolderWrapper = document.getElementById("friend-folder-container");
    const friendLeftArrow = document.getElementById("friend-left-arrow");
    const friendRightArrow = document.getElementById("friend-right-arrow");
    const friendInput = document.getElementById("friend-nickname");

    const CARD_WIDTH = 360;
    const GAP = 8;
    const ITEM_WIDTH = CARD_WIDTH + GAP;
    const MOVE_COUNT = 5;
    const MOVE_AMOUNT = ITEM_WIDTH * MOVE_COUNT;

    // ✅ (중요) SVG defs는 문서에 1번만 있어야 함 (id 충돌 방지)
    function injectFolderSpriteOnce() {
        if (document.getElementById("folderSprite")) return;

        const sprite = document.createElement("div");
        sprite.id = "folderSprite";
        sprite.style.position = "absolute";
        sprite.style.width = "0";
        sprite.style.height = "0";
        sprite.style.overflow = "hidden";

        sprite.innerHTML = `
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1080 1080">
  <defs>
    <filter id="glow" x="-100%" y="-100%" width="250%" height="250%">
      <feGaussianBlur stdDeviation="7" result="coloredBlur" />
      <feOffset dx="0" dy="0" result="offsetblur"></feOffset>
      <feFlood flood-color="black" flood-opacity="0.4"></feFlood>
      <feComposite in2="offsetblur" operator="in"></feComposite>
      <feMerge>
        <feMergeNode />
        <feMergeNode in="SourceGraphic"></feMergeNode>
      </feMerge>
    </filter>

    <clipPath id="mainMask">
      <path d="M864.51,787.3H210.18c-36.45,0-66-29.55-66-66V192.12c0-34.15,27.69-61.84,61.84-61.84h164.94c7.37,0,14.57,2.24,20.63,6.43l52.03,38.35c15.42,11.37,34.08,17.5,53.24,17.5h371.38c30.52,0,55.26,24.74,55.26,55.26v480.47c0,32.58-26.42,59-59,59Z"/>
    </clipPath>

    <linearGradient id="backGrad" x1="533.84" y1="50" x2="533.84" y2="269.59" gradientUnits="userSpaceOnUse">
      <stop offset="0" stop-color="#fff" />
      <stop offset="1" stop-color="#000" />
    </linearGradient>

    <linearGradient id="frontGrad" x1="128.32" y1="514.49" x2="933.02" y2="514.49" gradientUnits="userSpaceOnUse">
      <stop offset="0" stop-color="#000" />
      <stop offset=".05" stop-color="#787878" stop-opacity=".53" />
      <stop offset=".32" stop-color="#fff" stop-opacity="0" />
      <stop offset=".68" stop-color="#fff" stop-opacity="0" />
      <stop offset=".95" stop-color="#878787" stop-opacity=".47" />
      <stop offset="1" stop-color="#000" />
    </linearGradient>

    <linearGradient id="barGrad" x1="532.72" y1="699.13" x2="532.72" y2="771.46" gradientUnits="userSpaceOnUse">
      <stop offset=".35" stop-color="#000" stop-opacity="0" />
      <stop offset=".52" stop-color="#fff" stop-opacity=".2" />
      <stop offset=".7" stop-color="#000" stop-opacity="0" />
    </linearGradient>
    <linearGradient id="barGrad-2" y1="673.38" y2="745.7" xlink:href="#barGrad" />

    <!-- ✅ 카드에서 재사용할 폴더 아이콘 -->
    <symbol id="folderIcon" viewBox="0 0 1080 1080">
      <g filter="url(#glow)">
        <g clip-path="url(#mainMask)">
          <path class="back" d="M864.51,787.3H210.18c-36.45,0-66-29.55-66-66V192.12c0-34.15,27.69-61.84,61.84-61.84h164.94c7.37,0,14.57,2.24,20.63,6.43l52.03,38.35c15.42,11.37,34.08,17.5,53.24,17.5h371.38c30.52,0,55.26,24.74,55.26,55.26v480.47c0,32.58-26.42,59-59,59Z" fill="#f85f60"/>
          <path class="gradOverlay" d="M864.51,787.3H210.18c-36.45,0-66-29.55-66-66V192.12c0-34.15,27.69-61.84,61.84-61.84h164.94c7.37,0,14.57,2.24,20.63,6.43l52.03,38.35c15.42,11.37,34.08,17.5,53.24,17.5h371.38c30.52,0,55.26,24.74,55.26,55.26v480.47c0,32.58-26.42,59-59,59Z" fill="url(#backGrad)" opacity="0.97"/>
          <path class="front" d="M200.95,241.68h660.72c34.13,0,61.84,27.71,61.84,61.84v424.77c0,32.56-26.44,59-59,59H210.18c-36.43,0-66-29.57-66-66v-422.84c0-31.33,25.44-56.77,56.77-56.77Z" fill="#f85f60"/>
          <path class="gradOverlay" d="M200.95,241.68h660.72c34.13,0,61.84,27.71,61.84,61.84v424.77c0,32.56-26.44,59-59,59H210.18c-36.43,0-66-29.57-66-66v-422.84c0-31.33,25.44-56.77,56.77-56.77Z" fill="url(#frontGrad)" opacity="1"/>
          <g opacity="0.3">
            <rect x="136" y="724.45" width="800" height="21.8" fill="url(#barGrad)" />
            <rect x="136" y="698.69" width="800" height="21.8" fill="url(#barGrad-2)" />
          </g>
        </g>
      </g>
    </symbol>
  </defs>
</svg>
    `;
        document.body.appendChild(sprite);
    }

    // ✅ 폴더 열기(프로젝트에 맞게 라우트만 바꾸면 됨)
    function handleFolderOpen(folderId, isMyFolder) {
        // 1) 혹시 기존에 모달/상세열기 함수가 전역으로 있으면 그걸 우선 사용
        const fn =
            window.openFolderModal ||
            window.openFolder ||
            window.showFolderMovies;

        if (typeof fn === "function") {
            return fn(folderId, isMyFolder);
        }

        // 2) 없으면 기본 이동 (여기 경로만 네 프로젝트 라우트에 맞게 수정)
        location.href = `/folders/${folder.folderId}`;
    }

    // ✅ "카드"가 아니라 "폴더"만 클릭되게: 컨테이너에 이벤트 1번만 등록
    function bindFolderOpenDelegation(container, isMyFolder) {
        if (!container) return;

        // 중복 바인딩 방지
        if (container.dataset.folderOpenBound === "1") return;
        container.dataset.folderOpenBound = "1";

        container.addEventListener("click", (e) => {
            // ⋮ 메뉴/드롭다운 영역 클릭은 무시
            if (e.target.closest(".folder-menu")) return;

            // ✅ 폴더 그림(SVG) 또는 제목(탭) 클릭일 때만 열기
            const onSvg = e.target.closest(".folder-svg");
            const onTitle = e.target.closest(".folder-title");
            if (!onSvg && !onTitle) return;

            const card = e.target.closest(".folder-card");
            if (!card) return;

            const folderId = card.dataset.folderId;
            if (!folderId) return;

            handleFolderOpen(folderId, isMyFolder);
        });
    }


    // ✅ 클릭 위임 바인딩(딱 1번만)
    bindFolderOpenDelegation(folderWrapper, true);
    bindFolderOpenDelegation(friendFolderWrapper, false);

    injectFolderSpriteOnce();

    function getVisibilityClass(visibility) {
        if (visibility === "PUBLIC") return "folder-public";
        if (visibility === "FRIENDS") return "folder-friends";
        if (visibility === "PRIVATE") return "folder-private";
        return "";
    }

    // ✅ 카드 HTML (폴더 모양 = SVG)
    function createFolderCardHTML(folder, isMyFolder) {
        const visibilityClass = getVisibilityClass(folder.visibility);

        return `
    <div class="folder-card ${visibilityClass}" data-folder-id="${folder.folderId}">
      
      <div class="folder-visual">
        <svg class="folder-svg" viewBox="0 0 1080 1080" aria-hidden="true">
          <use href="#folderIcon" xlink:href="#folderIcon"></use>
        </svg>

        <div class="folder-menu">
          <button class="menu-btn${isMyFolder ? "" : " hidden"}" type="button">⋮</button>
          <div class="menu-dropdown hidden">
            <button class="delete-btn${isMyFolder ? "" : " hidden"}" type="button">삭제</button>
          </div>
        </div>
      </div>

      <p class="folder-title folder-title-below">${folder.folderName}</p>
    </div>
  `;
    }

    /* 📌 내 폴더 목록 가져오기 */
    async function loadMyFolders() {
        try {
            console.log("🔁 loadMyFolders 호출");
            const response = await api.get("/api/folders/me");
            const folders = response.data;

            console.log("📦 내 폴더 목록:", folders);

            if (!folders || folders.length === 0) {
                folderWrapper.innerHTML = `<p style="color:#ccc;">등록된 폴더가 없습니다.</p>`;
                return;
            }

            folderWrapper.innerHTML = folders.map((f) => createFolderCardHTML(f, true)).join("");
            attachMenuEvents(true);
        } catch (err) {
            console.error("❌ 폴더 불러오기 실패:", err);
            folderWrapper.innerHTML = `<p style="color:red;">폴더 정보를 가져오지 못했습니다.</p>`;
        }
    }

    // ================================
    // ⭐ 친구 폴더 불러오기
    // ================================
    async function loadFriendFolders(nickname) {
        console.log("🔁 loadFriendFolders 호출, nickname =", nickname);

        try {
            const response = await api.get(`/api/folders/${nickname}`);
            const folders = response.data;
            console.log("📦 친구 폴더 응답:", folders);

            const friendContainer = document.getElementById("friend-folder-container");
            const msg = document.getElementById("friend-folder-result");

            if (!friendContainer) {
                console.warn("⚠ friend-folder-container 요소를 찾지 못했습니다.");
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

            friendContainer.innerHTML = folders.map((f) => createFolderCardHTML(f, false)).join("");
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

    // 🔍 친구 폴더 검색 공통 함수
    async function runFriendSearch() {
        const input = document.getElementById("friend-nickname");
        const nickname = input?.value.trim();

        if (!nickname) {
            const msg = document.getElementById("friend-folder-result");
            if (msg) {
                msg.textContent = "닉네임을 입력해주세요.";
                msg.style.color = "red";
            }
            return;
        }

        await loadFriendFolders(nickname);
    }

    // ⭐ 친구 폴더 검색 버튼 이벤트
    const searchBtn = document.getElementById("search-friend-folder-btn");
    if (searchBtn) {
        searchBtn.addEventListener("click", async () => {
            await runFriendSearch();
        });
    }

    // 🔹 Enter 키로도 검색
    if (friendInput) {
        friendInput.addEventListener("keydown", async (e) => {
            if (e.key === "Enter") {
                e.preventDefault();
                await runFriendSearch();
            }
        });
    }

    // ✅ 바깥 클릭 이벤트 중복 등록 방지
    let outsideBound = false;

    function attachMenuEvents(isMyFolder = true) {

        document.querySelectorAll(".menu-btn").forEach((btn) => {
            btn.addEventListener("click", (e) => {
                if (!isMyFolder) return;
                e.stopPropagation();

                // 다른 메뉴 닫기
                document.querySelectorAll(".menu-dropdown").forEach((m) => m.classList.add("hidden"));

                const menu = e.currentTarget.closest(".folder-menu");
                const dropdown = menu?.querySelector(".menu-dropdown");
                dropdown?.classList.toggle("hidden");
            });
        });

        if (isMyFolder) {
            document.querySelectorAll(".delete-btn").forEach((btn) => {
                btn.addEventListener("click", async (e) => {
                    e.stopPropagation();
                    const card = e.currentTarget.closest(".folder-card");
                    const folderId = card.dataset.folderId;
                    const folderName = card.querySelector(".folder-title").textContent;

                    if (confirm(`'${folderName}' 폴더를 삭제하시겠습니까?`)) {
                        try {
                            const res = await api.delete(`/api/folders/${folderId}`, {
                                headers: { Authorization: `Bearer ${getToken()}` },
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

        if (!outsideBound) {
            outsideBound = true;
            document.addEventListener("click", (e) => {
                if (!e.target.closest(".folder-menu")) {
                    document.querySelectorAll(".menu-dropdown").forEach((menu) => menu.classList.add("hidden"));
                }
            });
        }
    }

    // ✅ 좌우 스크롤
    leftArrow?.addEventListener("click", () => {
        folderWrapper.scrollBy({ left: -MOVE_AMOUNT, behavior: "smooth" });
    });

    rightArrow?.addEventListener("click", () => {
        folderWrapper.scrollBy({ left: MOVE_AMOUNT, behavior: "smooth" });
    });

    // ✅ 친구 폴더 좌우 스크롤
    if (friendLeftArrow && friendFolderWrapper) {
        friendLeftArrow.addEventListener("click", () => {
            friendFolderWrapper.scrollBy({ left: -MOVE_AMOUNT, behavior: "smooth" });
        });
    }

    if (friendRightArrow && friendFolderWrapper) {
        friendRightArrow.addEventListener("click", () => {
            friendFolderWrapper.scrollBy({ left: MOVE_AMOUNT, behavior: "smooth" });
        });
    }

    /* 폴더 생성 기능 */
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
                resultText.textContent = `"${data.folderName}" 폴더가 생성되었습니다!`;
                resultText.style.color = "green";

                setTimeout(() => (resultText.textContent = ""), 5000);

                await loadMyFolders();
                form.reset();
            } catch (err) {
                console.error("폴더 생성 실패:", err);
                resultText.textContent = "폴더 생성 실패";
                resultText.style.color = "red";
            }
        });
    }

    /* 초기 로딩 */
    await loadMyFolders();
});
