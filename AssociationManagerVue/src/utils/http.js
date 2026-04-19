import axios from 'axios';
import qs from 'qs';
import { Message } from '@arco-design/web-vue';
import { useAuthStore } from '@/stores/auth';

const service = axios.create({
  baseURL: '/association',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
  },
});

service.interceptors.request.use(
  (config) => {
    if (config.method === 'post' && config.data && !(config.data instanceof FormData)) {
      config.data = qs.stringify(config.data, { indices: false });
    }
    return config;
  },
  (error) => Promise.reject(error)
);

service.interceptors.response.use(
  (response) => {
    const payload = response.data || {};

    if (payload.code === 2) {
      Message.error(payload.msg || '请求失败');
      return Promise.reject(payload);
    }

    if (payload.code === 1 && payload.msg) {
      Message.warning(payload.msg);
    }

    return payload;
  },
  (error) => {
    const msgText =
      (error.response && error.response.data && error.response.data.msg) ||
      error.message ||
      '网络异常，请稍后重试';

    Message.error(msgText);

    if (msgText.includes('登录')) {
      try {
        useAuthStore().clearSession();
      } catch (storeError) {
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('user');
      }
    }

    return Promise.reject(error);
  }
);

export default service;
