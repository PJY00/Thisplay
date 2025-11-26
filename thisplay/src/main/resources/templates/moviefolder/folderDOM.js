import api from "../../static/js/api/axiosInstance.js";
import { getToken } from "../../static/js/utils/auth.js";

document.addEventListener("DOMContentLoaded", () => {
    const folderWrapper = document.getElementById("folder-wrapper");
    const modal = document.getElementById("folder-modal");
    const closeBtn = document.getElementById("close-modal");
    const modalName = document.getElementById("modal-folder-name");
    const modalList = document.getElementById("modal-movie-list");

    // 🎯 이벤트 위임
    // folderWrapper.addEventListener("click", async (e) => {
    //     const card = e.target.closest(".folder-card");
    //     if (!card) return;

    //     const folderId = card.dataset.folderId;
    //     const folderName = card.querySelector(".folder-title").textContent;
    //     modal.classList.remove("hidden");
    //     modalName.textContent = folderName;

    //     try {
    //         const res = await api.get(`/api/folders/${folderId}/movies`);
    //         const data = res.data;

    //         console.log("📦 서버 응답:", data);

    //         // movies가 배열인지 확인 후 추출
    //         const movies = Array.isArray(data) ? data : data.movies;

    //         if (!Array.isArray(movies)) {
    //             throw new Error("서버 응답 형식이 예상과 다릅니다.");
    //         }

    //         modalList.innerHTML = movies
    //             .map(
    //                 (m) => `
    //   <div class="movie-card">
    //     <img src="${m.posterUrl}" alt="${m.title}">
    //     <h4>${m.title}</h4>
    //   </div>`
    //             )
    //             .join("");
    //     } catch (err) {
    //         console.error("❌ 폴더 불러오기 오류:", err);
    //         modalList.innerHTML = `<p style="color:red;">불러오기 실패: ${err.message}</p>`;
    //     }

    // });

    folderWrapper.addEventListener("click", async (e) => {
        const card = e.target.closest(".folder-card");
        if (!card) return;

        const folderName = card.querySelector(".folder-title").textContent;
        modal.classList.remove("hidden");
        modalName.textContent = folderName;

        // 🎨 임의의 데이터 1개 넣기 (디자인 테스트용)
        const movies = [
            {
                title: "테스트 영화",
                posterUrl: "https://image.tmdb.org/t/p/w300/8Y1AJCNZQFzSjSbkC6귀하임의.jpg"
            }
        ];

        modalList.innerHTML = movies
            .map(
                (m) => `
        <div class="movie-card">
            <img src="${m.posterUrl}" alt="${m.title}">
            <h4>${m.title}</h4>
        </div>`
            )
            .join("");
    });

    // 닫기 버튼
    closeBtn.addEventListener("click", () => modal.classList.add("hidden"));
    modal.addEventListener("click", (e) => {
        if (e.target === modal) modal.classList.add("hidden");
    });
});
