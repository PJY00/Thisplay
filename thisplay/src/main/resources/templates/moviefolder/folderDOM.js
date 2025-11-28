import api from "../../static/js/api/axiosInstance.js";
import { getToken } from "../../static/js/utils/auth.js";


document.addEventListener("DOMContentLoaded", () => {
    const folderWrapper = document.getElementById("folder-wrapper");
    const modal = document.getElementById("folder-modal");
    const closeBtn = document.getElementById("close-modal");
    const modalName = document.getElementById("modal-folder-name");
    const modalList = document.getElementById("modal-movie-list");

    let currentFolderId = null;

    folderWrapper.addEventListener("click", async (e) => {
        const card = e.target.closest(".folder-card");
        if (!card) return;

        const folderId = card.dataset.folderId;
        currentFolderId = folderId;

        modal.classList.remove("hidden");
        modalName.textContent = card.querySelector(".folder-title").textContent;

        try {
            const res = await api.get(`/api/folders/${folderId}/movies`);
            const movies = res.data.movies;

            if (!movies || movies.length === 0) {
                modalList.innerHTML = `<p>이 폴더에는 영화가 없습니다.</p>`;
                return;
            }

            modalList.innerHTML = movies.map(m => `
                <div class="movie-card" data-movieid="${m.tmdbId}" data-folderid = "${folderId}">
                    <button class = "delete-movie-btn">✕</button>
                    <img src="https://image.tmdb.org/t/p/w300${m.posterPath}" alt="${m.title}">
                    <h4>${m.title}</h4>
                </div>
            `).join("");

        } catch (err) {
            console.error("폴더 영화 불러오기 실패:", err);
            modalList.innerHTML = `<p style="color:red;">영화를 불러오지 못했습니다.</p>`;
        }
    });


    // ============================
    // 📌 영화 삭제 + 카드 클릭 핸들러
    // ============================
    modalList.addEventListener("click", async (e) => {
        const deleteBtn = e.target.closest(".delete-movie-btn");
        const card = e.target.closest(".movie-card");

        // (1) 삭제 버튼 클릭
        if (deleteBtn && card) {
            const movieId = card.dataset.movieid;

            if (!currentFolderId) {
                console.error("❌ currentFolderId가 비어 있습니다.");
                return;
            }

            if (!confirm("이 영화를 폴더에서 삭제하시겠습니까?")) return;

            try {
                await api.delete(`/api/movies/delete/${currentFolderId}/${movieId}`);

                card.remove(); // 화면에서 즉시 제거
            } catch (err) {
                console.error("영화 삭제 실패:", err);
                alert("영화를 삭제하지 못했습니다.");
            }

            return; // 아래의 상세 페이지 이동 막기
        }

        // (2) 영화 카드 클릭 → 상세 페이지 이동
        if (card) {
            const movieId = card.dataset.movieid;
            location.href = `../moviepage/moviepage.html?movieId=${movieId}`;
        }
    });

    closeBtn.addEventListener("click", () => modal.classList.add("hidden"));
    modal.addEventListener("click", (e) => {
        if (e.target === modal) modal.classList.add("hidden");
    });
});