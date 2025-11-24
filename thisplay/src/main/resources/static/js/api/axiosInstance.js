// import axios from 'axios';
import axios from "https://cdn.jsdelivr.net/npm/axios@1.6.8/+esm";
import { getToken, logout } from "../utils/auth.js";
// 상단 import는 권한(토큰) 관련 페이지이므로 주석처리 해둠
// login page와 연결시킬 예정

//가져올 base url 나중에 주소 생기면 none 부분 그걸로 수정


// const BASE_URL = "http://localhost:8080";
// // window.location.hostname === "localhost" ? "http://localhost:8080" : "none";

// export const api = axios.create({
//   baseURL: BASE_URL,
//   headers: {
//     "Content-Type": "application/json",
//   },
//   withCredentials: true, // 쿠키 포함 요청 허용


const TEST_TOKEN = "eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsIm5pY2tuYW1lIjoi7J207IOI7JqwIiwicm9sZSI6IlJPTEVfQURNSU4iLCJpYXQiOjE3NjM3MjE0MDMsImV4cCI6MTc2MzcyMjAwM30.jO2I98X0Rx16rDizliviTe6mBPVhMlopnA2899nfmTOuqE0jA3iOFuL9f5EqNdZN";
const BASE_URL = "http://localhost:8080"
// window.location.hostname === "localhost"
//     ? "http://localhost:8080"
//     : "none";

const api = axios.create({
    baseURL: BASE_URL,
    headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${TEST_TOKEN}`,
    },
    withCredentials: true, // 쿠키 포함 요청 허용
});

api.interceptors.request.use(
    (config) => {
        const token = getToken();
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;  //토큰 자동 첨부
        } else {
            config.headers.Authorization = `Bearer ${TEST_TOKEN}`; // ✅ 임시 테스트 토큰 (없을 때만)
        }
        console.log("🎯 실제 요청 Authorization:", api.defaults.headers.Authorization);

        return config;
    },
    // (error) => Promise.reject(error)
);

// 전역 에러
api.interceptors.response.use(
    (response) => response,
    (error) => {
        const status = error.response?.status;
        if (status === 401) {
            alert("세션이 만료되었습니다. 다시 로그인해주세요.");
            logout();
        } else if (status >= 500) {
            alert("서버 오류");
        } else {
            console.error("API Error:", error.message);
        }
        return Promise.reject(error);
    }
);

export { BASE_URL };
export default api;

console.log("✅ axiosInstance 연결 완료");
console.log("🧾 Authorization 헤더:", api.defaults.headers.Authorization);
console.log("🌐 BASE_URL:", BASE_URL);

// 주석 처리 된 부분은 추후 토큰 문제 해결하고 나서 주석 해제
// nodejs 패스 문제로 import가 안돼서 html에 직접적으로 import 하겠음
// 추후 이 문제도 수정할 예정