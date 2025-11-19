
import { getToken } from "../../static/js/utils/auth.js";
import api from "../../static/js/api/axiosInstance.js";

const BASE_URL = "http://localhost:8080";   // 백엔드 주소
const API_ROOT = BASE_URL + "/api/friends"; // 모든 요청 기본 경로
// -------------------------
// 1) 공통 request() — axiosInstance 기반
// -------------------------
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

// -------------------------
// 3) 검색 기능
// -------------------------
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
            body: JSON.stringify({ nickname })
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

    list.forEach((u) => {
        const div = document.createElement("div");
        div.className = "user-card";
        div.innerHTML = `
                <div>
                    <strong>${escapeHtml(u.nickname || u.name || '')}</strong>
                    <span class="muted">#${u.userId || ""}</span>
                </div>
                <div>
                    <button class="btn-add" data-nickname="${escapeHtml(
            u.nickname || u.name || ""
        )}">친구 추가</button>
                </div>
            `;
        container.appendChild(div);
    });

    container.querySelectorAll(".btn-add").forEach((btn) => {
        btn.addEventListener("click", async (e) => {
            const receiverNickname = btn.dataset.nickname;

            btn.disabled = true;
            btn.textContent = "요청 중...";

            try {
                const res = await request("/request", {
                    method: "POST",
                    body: JSON.stringify({ receiverNickname })
                });

                showMessage(res || "친구 요청 완료");
                window.parent.postMessage({ type: "refresh-manage" }, "*");

                btn.textContent = "요청 보냄";
            } catch (err) {
                showMessage("요청 실패: " + err.message, true);
                btn.disabled = false;
                btn.textContent = "친구 추가";
            }
        });
    });
}

// -------------------------
// 4) 추천 친구 불러오기
// -------------------------
async function loadRecommendations() {
    const el = document.getElementById("recommendations");
    el.innerHTML = "불러오는 중...";

    try {
        const recs = await request("/recommend", {
            method: "GET"
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
                        body: JSON.stringify({ receiverNickname })
                    });

                    showMessage(res || "친구 요청 완료");
                    window.parent.postMessage({ type: "refresh-manage" }, "*");

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

// -------------------------
// 5) 메시지 표시
// -------------------------
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
                "'": "&#39;"
            }[c];
        })
        : "";
}

// -------------------------
// 6) 시작 시 실행
// -------------------------
loadRecommendations();