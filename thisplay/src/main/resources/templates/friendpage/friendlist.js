import api from "../../static/js/api/axiosInstance.js";
import { getToken } from "../../static/js/utils/auth.js";

const BASE_URL = "http://localhost:8080";
const API_ROOT = BASE_URL + "/api/friends";

// axios 기반 요청 유틸
async function request(url, options = {}) {
    const method = options.method || "GET";
    const body = options.body ? JSON.parse(options.body) : null;

    try {
        const res = await api({
            url: API_ROOT + url,
            method,
            data: body,
        });

        return res.data;  // axios는 res.data에 JSON이 들어있음
    } catch (err) {
        console.error("❌ API 요청 실패:", err.response?.data || err.message);
        throw err;
    }
}

/** --------------------
 *  1) 친구 목록 불러오기
 * --------------------*/
async function loadFriendList() {
    const el = document.getElementById('friendsList');
    el.innerHTML = '불러오는 중...';

    try {
        const data = await request('/list', { method: 'GET' });
        renderFriendList(data);
    } catch (err) {
        el.innerHTML = `<div style="color:red;">목록 불러오기 실패: ${err.message}</div>`;
    }
}

function renderFriendList(data) {
    const el = document.getElementById('friendsList');
    el.innerHTML = '';

    if (!data || (Array.isArray(data) && data.length === 0)) {
        el.innerHTML = '<div class="muted">친구가 없습니다.</div>';
        return;
    }

    let friends = [];
    if (Array.isArray(data)) {
        friends = data.filter(item => item.status === 'FRIEND' || !item.status);
    } else if (Array.isArray(data.friends)) {
        friends = data.friends;
    }

    if (!friends || friends.length === 0) {
        el.innerHTML = '<div class="muted">친구가 없습니다.</div>';
        return;
    }

    friends.forEach(f => {
        const fid = f.friendshipId || f.friendId || f.userId;
        const nickname = f.nickname || f.name;

        const div = document.createElement('div');
        div.className = 'item';
        div.innerHTML = `
                <div>
                    <strong>${escapeHtml(nickname)}</strong>
                    <span class="muted">#${f.userId || ''}</span>
                </div>
                <div>
                    <button class="btn-unfriend" data-id="${fid}">친구 삭제</button>
                </div>
            `;
        el.appendChild(div);
    });

    // 삭제 이벤트
    el.querySelectorAll('.btn-unfriend').forEach(btn => {
        btn.addEventListener('click', async e => {
            const id = btn.dataset.id;
            if (!id) return;

            if (!confirm('친구를 삭제하시겠습니까?')) return;

            try {
                await request(`/${id}/reject`, { method: 'DELETE' });
                showStatus('친구 삭제 완료');
                await loadAll();
            } catch (err) {
                showStatus('삭제 실패: ' + err.message, true);
            }
        });
    });
}

/** --------------------
 *  2) 요청 목록 불러오기
 * --------------------*/
async function loadRequests() {
    const el = document.getElementById('requestsList');
    el.innerHTML = '불러오는 중...';

    try {
        const data = await request('/list', { method: 'GET' });
        renderRequests(data);
    } catch (err) {
        el.innerHTML = `<div style="color:red;">요청 불러오기 실패: ${err.message}</div>`;
    }
}

function renderRequests(data) {
    const el = document.getElementById('requestsList');
    el.innerHTML = '';

    let pending = [];

    if (Array.isArray(data)) {
        pending = data.filter(i => i.status !== 'FRIEND');
    } else if (Array.isArray(data.pending)) {
        pending = data.pending;
    }

    if (pending.length === 0) {
        el.innerHTML = '<div class="muted">처리할 요청이 없습니다.</div>';
        return;
    }

    pending.forEach(p => {
        const fid = p.friendshipId || p.id || p.friendId;
        const nickname = p.nickname || p.senderNickname || p.receiverNickname;

        const received = !(p.isSender || p.type === "SENT");

        const div = document.createElement('div');
        div.className = 'item';
        div.innerHTML = `
                <div>
                    <strong>${escapeHtml(nickname)}</strong>
                    <span class="muted">#${p.userId || ''}</span>
                    <div class="muted">${received ? '받은 요청' : '보낸 요청'}</div>
                </div>
                <div>
                    ${received
                ? `<button class="btn-accept" data-id="${fid}">수락</button>
                               <button class="btn-reject" data-id="${fid}">거절</button>`
                : `<button class="btn-cancel" data-id="${fid}">취소</button>`
            }
                </div>
            `;
        el.appendChild(div);
    });

    // 이벤트 바인딩
    el.querySelectorAll('.btn-accept').forEach(btn => {
        btn.addEventListener('click', async e => {
            const id = btn.dataset.id;
            try {
                await request(`/${id}/accept`, { method: 'POST' });
                showStatus('요청 수락됨');
                await loadAll();
            } catch (err) {
                showStatus('수락 실패: ' + err.message, true);
            }
        });
    });

    el.querySelectorAll('.btn-reject').forEach(btn => {
        btn.addEventListener('click', async e => {
            const id = btn.dataset.id;
            try {
                await request(`/${id}/reject`, { method: 'DELETE' });
                showStatus('거절 완료');
                await loadAll();
            } catch (err) {
                showStatus('거절 실패: ' + err.message, true);
            }
        });
    });

    el.querySelectorAll('.btn-cancel').forEach(btn => {
        btn.addEventListener('click', async e => {
            const id = btn.dataset.id;
            try {
                await request(`/${id}/cancel`, { method: 'DELETE' });
                showStatus('요청 취소됨');
                await loadAll();
            } catch (err) {
                showStatus('취소 실패: ' + err.message, true);
            }
        });
    });
}

/** 공용 함수들 */
function showStatus(msg, isErr = false) {
    const s = document.getElementById('statusMsg');
    s.style.color = isErr ? 'red' : 'green';
    s.textContent = msg;
    setTimeout(() => {
        if (s.textContent === msg) s.textContent = '';
    }, 4000);
}

function escapeHtml(s) {
    if (!s) return '';
    return s.replace(/[&<>"']/g, c => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    }[c]));
}

async function loadAll() {
    await Promise.all([loadFriendList(), loadRequests()]);
}

document.getElementById('refreshBtn').addEventListener('click', loadAll);

let autoInterval = setInterval(loadAll, 10000);
window.addEventListener('beforeunload', () => clearInterval(autoInterval));

// 첫 로드
loadAll();