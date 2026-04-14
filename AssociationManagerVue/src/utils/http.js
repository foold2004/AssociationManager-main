import axios from 'axios';
import qs from 'qs';
import { message } from 'ant-design-vue';
import store from '@/store';

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
      message.error(payload.msg || '请求失败');
      return Promise.reject(payload);
    }

    if (payload.code === 1) {
      message.warning(payload.msg || '操作提醒');
    }

    return payload;
  },
  (error) => {
    const msgText =
      (error.response && error.response.data && error.response.data.msg) ||
      error.message ||
      '网络异常';

    message.error(msgText);

    if (msgText.indexOf('登录') >= 0) {
      store.commit('clearSession');
    }

    return Promise.reject(error);
  }
);

export default service;
