//token 권한 js
export function saveToken(token) {
    localStorage.setItem("token", token);
}

export function getToken() {
    return localStorage.getItem("token");
}

export function logout() {
    localStorage.removeItem("token");
    location.href = "/login";
}

export function isLoggedIn() {
    return !!getToken();
}