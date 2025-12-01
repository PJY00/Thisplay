export function saveAccessToken(token) {
  localStorage.setItem("accessToken", token);
}

export function saveRefreshToken(token) {
  localStorage.setItem("refreshToken", token);
}

export function getAccessToken() {
  return localStorage.getItem("accessToken");
}

export function getRefreshToken() {
  return localStorage.getItem("refreshToken");
}

// 하위 호환용
export function getToken() {
  return getAccessToken();
}

export function clearTokens() {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("refreshToken");
}

export function logout() {
  clearTokens();
  location.href = "../mainpage/mainpage.html";
}

// 자동 로그인 체크
export function isLoggedIn() {
  return !!getAccessToken();
}

export async function refreshAccessToken() {
  const refreshToken = getRefreshToken();
  if (!refreshToken) return null;

  try {
    const res = await fetch("http://localhost:8080/api/auth/refresh", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ refreshToken }),
    });

    if (!res.ok) return null;

    const data = await res.json();
    if (data.accessToken) {
      saveAccessToken(data.accessToken);
      return data.accessToken;
    }
    return null;
  } catch (err) {
    return null;
  }
}
