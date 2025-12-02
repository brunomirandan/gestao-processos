export function loginSuccess(token, username, roles) {
  localStorage.setItem("token", token);
  localStorage.setItem("username", username);
  localStorage.setItem("roles", JSON.stringify(roles));
}

export function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("username");
  localStorage.removeItem("roles");
}

export function isAuthenticated() {
  return localStorage.getItem("token") !== null;
}

export function getToken() {
  return localStorage.getItem("token");
}

export function getUsername() {
  return localStorage.getItem("username");
}

export function hasRole(role) {
  const roles = JSON.parse(localStorage.getItem("roles") || "[]");
  return roles.includes(role);
}
