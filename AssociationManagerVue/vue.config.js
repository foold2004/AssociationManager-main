module.exports = {
  parallel: false,
  devServer: {
    port: 9212,
    proxy: {
      '/association': {
        target: 'http://localhost:9211',
        changeOrigin: true,
      },
    },
  },
  lintOnSave: false,
};
