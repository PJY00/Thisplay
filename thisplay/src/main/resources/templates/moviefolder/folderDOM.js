import api from "../../static/js/api/axiosInstance.js";
import { getToken } from "../../static/js/utils/auth.js";


document.addEventListener("DOMContentLoaded", () => {
    const folderWrapper = document.getElementById("folder-wrapper");
    const modal = document.getElementById("folder-modal");
    const closeBtn = document.getElementById("close-modal");
    const modalName = document.getElementById("modal-folder-name");
    const modalList = document.getElementById("modal-movie-list");

    // 🔥 영화 카드 클릭 이벤트 (한번만 등록)
    modalList.addEventListener("click", (e) => {
        const card = e.target.closest(".movie-card");
        if (!card) return;

        const movieId = card.dataset.movieid; // ← 중요!
        location.href = `../moviepage/moviepage.html?movieId=${movieId}`;
    });

    folderWrapper.addEventListener("click", async (e) => {
        const card = e.target.closest(".folder-card");
        if (!card) return;

        const folderId = card.dataset.folderid;

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
                <div class="movie-card" data-movieid="${m.tmdbId}">
                    <img src="https://image.tmdb.org/t/p/w300${m.posterPath}" alt="${m.title}">
                    <h4>${m.title}</h4>
                </div>
            `).join("");

        } catch (err) {
            console.error("폴더 영화 불러오기 실패:", err);
            modalList.innerHTML = `<p style="color:red;">영화를 불러오지 못했습니다.</p>`;
        }
    });

    closeBtn.addEventListener("click", () => modal.classList.add("hidden"));
    modal.addEventListener("click", (e) => {
        if (e.target === modal) modal.classList.add("hidden");
    });
});