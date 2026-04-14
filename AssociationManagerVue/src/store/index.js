import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

const token = sessionStorage.getItem('token') || '';
const user = sessionStorage.getItem('user');

export default new Vuex.Store({
  state: {
    token,
    user: user ? JSON.parse(user) : null,
  },
  getters: {
    token: (state) => state.token,
    user: (state) => state.user,
    userType: (state) => (state.user ? state.user.type : null),
  },
  mutations: {
    setToken(state, value) {
      state.token = value || '';
      if (value) {
        sessionStorage.setItem('token', value);
      } else {
        sessionStorage.removeItem('token');
      }
    },
    setUser(state, value) {
      state.user = value || null;
      if (value) {
        sessionStorage.setItem('user', JSON.stringify(value));
      } else {
        sessionStorage.removeItem('user');
      }
    },
    clearSession(state) {
      state.token = '';
      state.user = null;
      sessionStorage.removeItem('token');
      sessionStorage.removeItem('user');
    },
  },
});
