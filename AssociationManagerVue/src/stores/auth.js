import { computed, ref } from 'vue';
import { defineStore } from 'pinia';

const TOKEN_KEY = 'token';
const USER_KEY = 'user';

function readUser() {
  const raw = sessionStorage.getItem(USER_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw);
  } catch (error) {
    sessionStorage.removeItem(USER_KEY);
    return null;
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(sessionStorage.getItem(TOKEN_KEY) || '');
  const user = ref(readUser());

  const isAuthenticated = computed(() => !!token.value);
  const userType = computed(() => (user.value ? Number(user.value.type) : null));

  function setToken(nextToken) {
    token.value = nextToken || '';
    if (token.value) {
      sessionStorage.setItem(TOKEN_KEY, token.value);
    } else {
      sessionStorage.removeItem(TOKEN_KEY);
    }
  }

  function setUser(nextUser) {
    user.value = nextUser || null;
    if (user.value) {
      sessionStorage.setItem(USER_KEY, JSON.stringify(user.value));
    } else {
      sessionStorage.removeItem(USER_KEY);
    }
  }

  function clearSession() {
    setToken('');
    setUser(null);
  }

  return {
    token,
    user,
    isAuthenticated,
    userType,
    setToken,
    setUser,
    clearSession,
  };
});
