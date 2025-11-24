import { getToken } from "../../static/js/utils/auth.js";
import api from "../../static/js/api/axiosInstance.js";

const BASE_URL = "http://localhost:8080"; // 백엔드 주소
const API_ROOT = BASE_URL + "/api/friends"; // 모든 요청 기본 경로

const cssLink = document.createElement("link");
cssLink.rel = "stylesheet";
cssLink.href = "friend.css";
document.head.appendChild(cssLink);

// 공통 request() — axiosInstance 기반
async function request(path, options = {}) {
  const method = options.method || "GET";
  const body = options.body ? JSON.parse(options.body) : null;

  try {
    const res = await api({
      url: `/api/friends${path}`,
      method,
      data: body, // GET이면 자동 무시됨
    });

    return res.data; // axios는 JSON 자동 파싱되어 data에 저장됨
  } catch (err) {
    console.error("❌ API Request Error:", err.response?.data || err.message);
    throw new Error(err.response?.data?.message || err.message);
  }
}

// left container 시작

// 친구 검색
document.getElementById("searchBtn").addEventListener("click", doSearch);
document.getElementById("clearBtn").addEventListener("click", () => {
  document.getElementById("searchInput").value = "";
  document.getElementById("searchResults").innerHTML = "";
});
document.getElementById("searchInput").addEventListener("keyup", (e) => {
  if (e.key === "Enter") doSearch();
});

async function doSearch() {
  const nickname = document.getElementById("searchInput").value.trim();
  if (!nickname) return alert("검색할 닉네임을 입력하세요.");

  const box = document.getElementById("searchResults");
  box.innerHTML = "검색 중...";

  try {
    const result = await request("/search", {
      method: "POST",
      body: JSON.stringify({ nickname }),
    });

    renderSearchResults(result);
  } catch (err) {
    box.innerHTML = `<div style="color:red;">검색 실패: ${err.message}</div>`;
  }
}

function renderSearchResults(data) {
  const container = document.getElementById("searchResults");
  container.innerHTML = "";

  if (!data || (Array.isArray(data) && data.length === 0)) {
    container.innerHTML = '<div class="muted">검색 결과가 없습니다.</div>';
    return;
  }

  const list = Array.isArray(data) ? data : [data];

  // 현재 로그인한 내 정보 가져오기 (JWT에서 가져옴)
  const stored = localStorage.getItem("user");
  let myId = null;
  if (stored) {
    try {
      myId = JSON.parse(stored).userId;
    } catch (e) {}
  }

  list.forEach((u) => {
    const div = document.createElement("div");
    div.className = "user-card";

    const nickname = escapeHtml(u.nickname || u.name || "");
    const uid = u.userId;

    // 버튼 표시 여부 조건
    const isMe = myId && uid && myId === uid;
    const isFriend = u.isFriend === true || u.friend === true;

    const showAddBtn = !(isMe || isFriend);

    div.innerHTML = `
            <div>
                <strong>${nickname}</strong>
                <span class="muted">#${uid || ""}</span>
            </div>
            <div>
                ${
                  showAddBtn
                    ? `<button class="btn-add" data-nickname="${nickname}">친구 추가</button>`
                    : `<span class="muted">${
                        isMe ? "본인" : "이미 친구입니다"
                      }</span>`
                }
            </div>
        `;

    container.appendChild(div);
  });

  // 친구 추가 버튼 이벤트
  container.querySelectorAll(".btn-add").forEach((btn) => {
    btn.addEventListener("click", async (e) => {
      const receiverNickname = btn.dataset.nickname;

      btn.disabled = true;
      btn.textContent = "요청 중...";

      try {
        const res = await request("/request", {
          method: "POST",
          body: JSON.stringify({ receiverNickname }),
        });

        showMessage(res || "친구 요청 완료");
        window.parent.location.reload();

        btn.textContent = "요청 보냄";
      } catch (err) {
        showMessage("요청 실패: " + err.message, true);
        btn.disabled = false;
        btn.textContent = "친구 추가";
      }
    });
  });
}

async function loadRecommendations() {
  const el = document.getElementById("recommendations");
  el.innerHTML = "불러오는 중...";

  try {
    const recs = await request("/recommend", {
      method: "GET",
    });

    if (!recs || recs.length === 0) {
      el.innerHTML = '<div class="muted">추천 친구가 없습니다.</div>';
      return;
    }

    el.innerHTML = "";

    recs.forEach((r) => {
      const row = document.createElement("div");
      row.className = "user-card";
      row.innerHTML = `
                    <div>
                        <strong>${escapeHtml(r.nickname || r.name)}</strong>
                        <span class="muted">#${r.userId || ""}</span>
                    </div>
                    <div>
                        <button class="btn-add" data-nickname="${escapeHtml(
                          r.nickname || r.name
                        )}">친구 추가</button>
                    </div>
                `;
      el.appendChild(row);
    });

    el.querySelectorAll(".btn-add").forEach((btn) => {
      btn.addEventListener("click", async () => {
        const receiverNickname = btn.dataset.nickname;

        btn.disabled = true;
        btn.textContent = "요청 중...";

        try {
          const res = await request("/request", {
            method: "POST",
            body: JSON.stringify({ receiverNickname }),
          });

          showMessage(res || "친구 요청 완료");
          window.parent.location.reload();

          btn.textContent = "요청 보냄";
        } catch (err) {
          showMessage("요청 실패: " + err.message, true);
          btn.disabled = false;
          btn.textContent = "친구 추가";
        }
      });
    });
  } catch (err) {
    el.innerHTML = `<div style="color:red;">추천 실패: ${err.message}</div>`;
  }
}

// 메시지 표시
function showMessage(msg, isError = false) {
  const m = document.getElementById("message");
  m.style.color = isError ? "red" : "green";
  m.textContent = msg;

  setTimeout(() => {
    if (m.textContent === msg) m.textContent = "";
  }, 4000);
}

function escapeHtml(s) {
  return s
    ? String(s).replace(/[&<>"']/g, (c) => {
        return {
          "&": "&amp;",
          "<": "&lt;",
          ">": "&gt;",
          '"': "&quot;",
          "'": "&#39;",
        }[c];
      })
    : "";
}

// 시작 시 실행
loadRecommendations();

// right container 시작

/* 친구 목록 불러오기 */
async function loadFriendList() {
  const el = document.getElementById("friendsList");
  el.innerHTML = "불러오는 중...";

  try {
    const data = await request("/list", { method: "GET" });
    renderFriendList(data);
  } catch (err) {
    el.innerHTML = `<div style="color:red;">목록 불러오기 실패: ${err.message}</div>`;
  }
}

function renderFriendList(data) {
  const el = document.getElementById("friendsList");
  el.innerHTML = "";

  if (!data || data.length === 0) {
    el.innerHTML = '<div class="muted">친구가 없습니다.</div>';
    return;
  }

  // 현재 친구 필터링
  const friends = data.filter((f) => f.status === "ACCEPTED");

  if (friends.length === 0) {
    el.innerHTML = '<div class="muted">친구가 없습니다.</div>';
    return;
  }

  friends.forEach((f) => {
    const fid = f.friendshipId;
    const nickname = f.otherUserName;
    const userId = f.otherUserId;

    const div = document.createElement("div");
    div.className = "item";
    div.innerHTML = `
            <div>
                <strong>${escapeHtml(nickname)}</strong>
                <span class="muted">#${userId}</span>
            </div>
            
        `;
    el.appendChild(div);
  });

  //친구 삭제 버튼. 기능이없어 빼둠.
  // <div>
  //     <button class="btn-unfriend" data-id="${fid}">친구 삭제</button>
  // </div>
  // // 삭제 이벤트(근데 지금 기능 없음ㅋ)
  // el.querySelectorAll('.btn-unfriend').forEach(btn => {
  //     btn.addEventListener('click', async () => {
  //         const id = btn.dataset.id;
  //         if (!id) return;

  //         if (!confirm('친구를 삭제하시겠습니까?')) return;

  //         try {
  //             await request(`/${id}/reject`, { method: 'DELETE' });
  //             showStatus('친구 삭제 완료');
  //             window.parent.location.reload();
  //         } catch (err) {
  //             showStatus('삭제 실패: ' + err.message, true);
  //         }
  //     });
  // });
}

/* 요청 목록 불러오기 */
// 요청 전체 불러오기
async function loadRequests() {
  const receivedEl = document.getElementById("requestsList"); // 받은 요청
  const sentEl = document.getElementById("sendList"); // 보낸 요청

  receivedEl.innerHTML = "불러오는 중...";
  sentEl.innerHTML = "불러오는 중...";

  try {
    // 백엔드에서 전체 pending(보낸+받은) 요청이 모두 옴
    const data = await request("/received", { method: "GET" });

    renderRequests(data);
  } catch (err) {
    receivedEl.innerHTML = `<div style="color:red;">요청 불러오기 실패: ${err.message}</div>`;
    sentEl.innerHTML = `<div style="color:red;">요청 불러오기 실패: ${err.message}</div>`;
  }
}

// 받은 요청, 보낸 요청 필터링
function renderRequests(data) {
  const receivedEl = document.getElementById("requestsList");
  const sentEl = document.getElementById("sendList");

  receivedEl.innerHTML = "";
  sentEl.innerHTML = "";

  let pending = Array.isArray(data) ? data : [];

  // 내가 받은 요청
  const receivedList = pending.filter((p) => p.from === false);

  // 내가 보낸 요청
  const sentList = pending.filter((p) => p.from === true);

  // 받은 요청 렌더링
  if (receivedList.length === 0) {
    receivedEl.innerHTML = '<div class="muted">받은 요청이 없습니다.</div>';
  } else {
    receivedList.forEach((p) => {
      const fid = p.friendshipId;

      const nickname =
        p.otherUserName ||
        p.nickname ||
        p.senderNickname ||
        p.receiverNickname ||
        "(이름 없음)";

      const div = document.createElement("div");
      div.className = "item";
      div.innerHTML = `
        <div>
          <strong>${escapeHtml(nickname)}</strong>
          <span class="muted">${p.otherUserId || ""}</span>
        </div>
        <div>
            <button class="btn-accept" data-id="${fid}">수락</button>
            <button class="btn-reject" data-id="${fid}">거절</button>
        </div>
      `;
      receivedEl.appendChild(div);
    });
  }

  // 보낸 요청 렌더링
  if (sentList.length === 0) {
    sentEl.innerHTML = '<div class="muted">보낸 요청이 없습니다.</div>';
  } else {
    sentList.forEach((p) => {
      const fid = p.friendshipId;
      const nickname =
        p.otherUserName ||
        p.nickname ||
        p.senderNickname ||
        p.receiverNickname ||
        "(이름 없음)";

      const div = document.createElement("div");
      div.className = "item";
      div.innerHTML = `
        <div>
          <strong>${escapeHtml(nickname)}</strong>
          <span class="muted">${p.otherUserId || ""}</span>
        </div>
        <div>
            <button class="btn-cancel" data-id="${fid}">취소</button>
        </div>
      `;
      sentEl.appendChild(div);
    });
  }

  // 이벤트 바인딩
  receivedEl.querySelectorAll(".btn-accept").forEach((btn) => {
    btn.addEventListener("click", async () => {
      try {
        await request(`/${btn.dataset.id}/accept`, { method: "POST" });
        showStatus("요청 수락됨");
        await loadAll();
      } catch (err) {
        showStatus("수락 실패: " + err.message, true);
      }
    });
  });

  receivedEl.querySelectorAll(".btn-reject").forEach((btn) => {
    btn.addEventListener("click", async () => {
      try {
        await request(`/${btn.dataset.id}/reject`, { method: "DELETE" });
        showStatus("거절 완료");
        await loadAll();
      } catch (err) {
        showStatus("거절 실패: " + err.message, true);
      }
    });
  });

  sentEl.querySelectorAll(".btn-cancel").forEach((btn) => {
    btn.addEventListener("click", async () => {
      try {
        await request(`/${btn.dataset.id}/cancel`, { method: "DELETE" });
        showStatus("요청 취소됨");
        await loadAll();
      } catch (err) {
        showStatus("취소 실패: " + err.message, true);
      }
    });
  });
}

/* 공용 함수들 */
function showStatus(msg, isErr = false) {
  const s = document.getElementById("statusMsg");
  s.style.color = isErr ? "red" : "green";
  s.textContent = msg;
  setTimeout(() => {
    if (s.textContent === msg) s.textContent = "";
  }, 4000);
}

async function loadAll() {
  await Promise.all([loadFriendList(), loadRequests()]);
}

document.getElementById("refreshBtn").addEventListener("click", loadAll);

//자동 새로고침 없앰
// let autoInterval = setInterval(loadAll, 10000);
// window.addEventListener('beforeunload', () => clearInterval(autoInterval));

// 첫 로드
loadAll();
