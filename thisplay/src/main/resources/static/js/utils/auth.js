//token 권한 js
export function saveToken(token) {
  localStorage.setItem("token", token);
}

export function getToken() {
  return localStorage.getItem("token");
}

export function logout() {
  localStorage.removeItem("token");
  location.href = "../mainpage/mainpage.html";
}

export function isLoggedIn() {
  // return !!getToken();
  // 강제로 허용
  return true;
}
