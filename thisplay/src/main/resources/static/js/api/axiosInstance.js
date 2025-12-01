// import axios from "axios";
import {
  getAccessToken,
  getRefreshToken,
  saveAccessToken,
  logout,
  refreshAccessToken,
} from "../utils/auth.js";

// base url 설정
const BASE_URL = "http://localhost:8080";

export const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  // withCredentials: true,
});

/* 요청 인터셉터*/
api.interceptors.request.use(
  async (config) => {
    const token = getAccessToken();
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
  },
  (error) => Promise.reject(error)
);

/* 응답 인터셉터 (자동 재발급) */
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    const status = error.response?.status;

    // Access Token 만료 → 401 Unauthorized
    if (status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      // Refresh Token을 가져옴
      const refreshToken = getRefreshToken();
      if (!refreshToken) {
        alert("로그인 세션이 만료되었습니다. 다시 로그인해주세요.");
        logout();
        return Promise.reject(error);
      }

      // refresh 로 access 재발급 시도
      try {
        const newAccessToken = await refreshAccessToken();
        if (newAccessToken) {
          saveAccessToken(newAccessToken);

          // Authorization 헤더 갱신 후 재요청
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
          return api(originalRequest);
        } else {
          alert("세션이 만료되었습니다. 다시 로그인해주세요.");
          logout();
        }
      } catch (refreshErr) {
        alert("세션이 만료되었습니다. 다시 로그인해주세요.");
        logout();
        return Promise.reject(refreshErr);
      }
    }

    // 기타 서버 오류 처리
    if (status >= 500) {
      alert("서버 오류가 발생했습니다.");
    } else {
      console.error("API Error:", error.message);
    }

    return Promise.reject(error);
  }
);

export { BASE_URL };
export default api;
