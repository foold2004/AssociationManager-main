import http from '@/utils/http';

function objectOrLegacy(args, keys) {
  if (args.length === 1 && typeof args[0] === 'object' && args[0] !== null && !Array.isArray(args[0])) {
    return args[0];
  }

  return keys.reduce((result, key, index) => {
    result[key] = args[index];
    return result;
  }, {});
}

function getSessionToken() {
  try {
    return sessionStorage.getItem('token') || '';
  } catch (error) {
    return '';
  }
}

function resolveToken(candidate) {
  const sessionToken = getSessionToken();

  if (candidate && sessionToken && candidate !== sessionToken) {
    return sessionToken;
  }

  return candidate || sessionToken || '';
}

function withToken(params = {}, candidateToken) {
  const token = resolveToken(params.token ?? candidateToken);
  return token ? { ...params, token } : { ...params };
}

export function login(params) {
  return http.post('/login', params);
}

export function logout(token) {
  return http.get('/exit', { params: { token } });
}

export function getLoginUser(token) {
  return http.get('/info', { params: { token } });
}

export function updateLoginUserInfo(params) {
  return http.post('/info', params);
}

export function uploadAvatar(token, file) {
  const formData = new FormData();
  formData.append('token', token);
  formData.append('file', file);
  return http.post('/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}

export function checkUserPwd(token, oldPwd) {
  return http.get('/checkPwd', { params: { token, oldPwd } });
}

export function updateLoginUserPwd(token, password, oldPwd) {
  return http.post('/pwd', { token, password, oldPwd });
}

export function getDemoAccounts() {
  return http.get('/demoAccounts');
}

export function addUsers(params) {
  return http.post('/users/add', params);
}

export function getPageUsers(...args) {
  const params = objectOrLegacy(args, ['pageIndex', 'pageSize', 'userName', 'name', 'phone', 'type']);
  return http.get('/users/page', { params });
}

export function updateUsers(params) {
  return http.post('/users/upd', params);
}

export function deleteUsers(...args) {
  const params = objectOrLegacy(args, ['id']);
  return http.post('/users/del', params);
}

export function getManagers(params = {}) {
  return http.get('/users/managers', { params });
}

export function getAllTypes(params = {}) {
  return http.get('/teamTypes/all', { params });
}

export function getPageTeamTypes(...args) {
  const params = objectOrLegacy(args, ['pageIndex', 'pageSize', 'name']);
  return http.get('/teamTypes/page', { params });
}

export function addTeamTypes(params) {
  return http.post('/teamTypes/add', params);
}

export function updTeamTypes(params) {
  return http.post('/teamTypes/upd', params);
}

export function delTeamTypes(...args) {
  const params = objectOrLegacy(args, ['id']);
  return http.post('/teamTypes/del', params);
}

export function getAllTeamList(params = {}) {
  return http.get('/teams/all', { params });
}

export function getManTeamList(...args) {
  const rawParams = objectOrLegacy(args, ['manId']);
  const params = {
    ...rawParams,
    manId: rawParams.manId || rawParams.manager,
  };
  return http.get('/teams/man', { params });
}

export function getPageTeams(...args) {
  const rawParams = objectOrLegacy(args, ['pageIndex', 'pageSize', 'token', 'name', 'typeId']);
  const params = withToken(rawParams, args[2]);
  return http.get('/teams/page', { params });
}

export function addTeams(params) {
  return http.post('/teams/add', withToken(params));
}

export function updateTeams(params) {
  return http.post('/teams/upd', withToken(params));
}

export function uploadTeamImage(token, teamId, file) {
  const formData = new FormData();
  formData.append('token', resolveToken(token));
  formData.append('teamId', teamId);
  formData.append('file', file);
  return http.post('/teams/uploadImage', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}

export function deleteTeams(...args) {
  const rawParams = objectOrLegacy(args, ['token', 'id']);
  const params = withToken(rawParams, args[0]);
  return http.post('/teams/del', params);
}

export function addApplyLogs(params) {
  return http.post('/applyLogs/add', withToken(params));
}

export function getPageApplyLogs(...args) {
  const rawParams = objectOrLegacy(args, ['pageIndex', 'pageSize', 'token', 'teamName', 'userName']);
  const params = withToken(rawParams, args[2]);
  return http.get('/applyLogs/page', { params });
}

export function updApplyLogs(params) {
  return http.post('/applyLogs/upd', withToken(params));
}

export function getMyPendingApplyTeamIds(...args) {
  const rawParams =
    args.length === 1 && typeof args[0] === 'object' && args[0] !== null && !Array.isArray(args[0])
      ? args[0]
      : {};
  const params = withToken(rawParams, args[0]);
  return http.get('/applyLogs/myPendingTeamIds', { params });
}

export function getMyMemberTeamIds(...args) {
  const rawParams =
    args.length === 1 && typeof args[0] === 'object' && args[0] !== null && !Array.isArray(args[0])
      ? args[0]
      : {};
  const params = withToken(rawParams, args[0]);
  return http.get('/members/myTeamIds', { params });
}

export function getMemberOptions(...args) {
  const rawParams =
    args.length === 1 && typeof args[0] !== 'object'
      ? { teamId: args[0] }
      : objectOrLegacy(args, ['token', 'teamId']);
  const params = withToken(rawParams, args[0]);
  return http.get('/members/options', { params });
}

export function getPageMembers(...args) {
  const rawParams = objectOrLegacy(args, ['pageIndex', 'pageSize', 'token', 'teamName', 'userName']);
  const params = withToken(rawParams, args[2]);
  return http.get('/members/page', { params });
}

export function delMembers(...args) {
  const params = objectOrLegacy(args, ['id']);
  return http.post('/members/del', params);
}

export function getPageActivities(...args) {
  const rawParams = objectOrLegacy(args, ['pageIndex', 'pageSize', 'token', 'teamName', 'activeName']);
  const params = withToken(rawParams, args[2]);
  return http.get('/activities/page', { params });
}

export function addActivities(params) {
  return http.post('/activities/add', withToken(params));
}

export function updateActivities(params) {
  return http.post('/activities/upd', withToken(params));
}

export function deleteActivities(...args) {
  const rawParams = objectOrLegacy(args, ['token', 'id']);
  const params = withToken(rawParams, args[0]);
  return http.post('/activities/del', params);
}

export function getPageNotices(...args) {
  const rawParams = objectOrLegacy(args, ['pageIndex', 'pageSize', 'token', 'title', 'teamName', 'teamId', 'systemOnly']);
  const params = withToken(rawParams, args[2]);
  return http.get('/notices/page', { params });
}

export function addNotices(params) {
  return http.post('/notices/add', withToken(params));
}

export function updateNotices(params) {
  return http.post('/notices/upd', withToken(params));
}

export function deleteNotices(...args) {
  const rawParams = objectOrLegacy(args, ['token', 'id']);
  const params = withToken(rawParams, args[0]);
  return http.post('/notices/del', params);
}

export function toggleNoticeTop(...args) {
  const rawParams = objectOrLegacy(args, ['token', 'id', 'isTop']);
  const params = withToken(rawParams, args[0]);
  return http.post('/notices/top', params);
}

export function getPageRules(...args) {
  const rawParams = objectOrLegacy(args, ['pageIndex', 'pageSize', 'token', 'title', 'teamName']);
  const params = withToken(rawParams, args[2]);
  return http.get('/rules/page', { params });
}

export function addRule(params) {
  return http.post('/rules/add', withToken(params));
}

export function updateRule(params) {
  return http.post('/rules/upd', withToken(params));
}

export function deleteRule(...args) {
  const rawParams = objectOrLegacy(args, ['token', 'id']);
  const params = withToken(rawParams, args[0]);
  return http.post('/rules/del', params);
}

export function getPageActiveLogs(...args) {
  const rawParams = objectOrLegacy(args, ['pageIndex', 'pageSize', 'token', 'teamName', 'activeName', 'userName', 'status']);
  const params = withToken(rawParams, args[2]);
  return http.get('/activeLogs/page', { params });
}

export function getActiveLogs(...args) {
  const params = objectOrLegacy(args, ['activeId']);
  return http.get('/activeLogs/list', { params });
}

export function getMyActiveStatuses(...args) {
  const rawParams =
    args.length === 1 && typeof args[0] === 'object' && args[0] !== null && !Array.isArray(args[0])
      ? args[0]
      : {};
  const params = withToken(rawParams, args[0]);
  return http.get('/activeLogs/myStatuses', { params });
}

export function addActiveLogs(params) {
  return http.post('/activeLogs/add', withToken(params));
}

export function updateActiveLogs(params) {
  return http.post('/activeLogs/upd', withToken(params));
}

export function deleteActiveLogs(...args) {
  const rawParams = objectOrLegacy(args, ['token', 'id']);
  const params = withToken(rawParams, args[0]);
  return http.post('/activeLogs/del', params);
}

export function getPageInteractions(...args) {
  const rawParams = objectOrLegacy(args, ['pageIndex', 'pageSize', 'token', 'keyword', 'teamName']);
  const params = withToken(rawParams, args[2]);
  return http.get('/interactions/page', { params });
}

export function addInteraction(params) {
  return http.post('/interactions/add', withToken(params));
}

export function deleteInteraction(...args) {
  const rawParams = objectOrLegacy(args, ['token', 'id']);
  const params = withToken(rawParams, args[0]);
  return http.post('/interactions/del', params);
}

export function getInteractionComments(...args) {
  const rawParams = objectOrLegacy(args, ['token', 'interactionId']);
  const params = withToken(rawParams, args[0]);
  return http.get('/interactionComments/list', { params });
}

export function addInteractionComment(params) {
  return http.post('/interactionComments/add', withToken(params));
}

export function deleteInteractionComment(...args) {
  const rawParams = objectOrLegacy(args, ['token', 'id']);
  const params = withToken(rawParams, args[0]);
  return http.post('/interactionComments/del', params);
}

export function getPagePayLogs(...args) {
  const rawParams = objectOrLegacy(args, ['pageIndex', 'pageSize', 'token', 'teamName', 'userName']);
  const params = withToken(rawParams, args[2]);
  return http.get('/payLogs/page', { params });
}

export function getPaySummary(...args) {
  const rawParams = objectOrLegacy(args, ['token', 'teamId']);
  const params = withToken(rawParams, args[0]);
  return http.get('/payLogs/summary', { params });
}

export function addPayLogs(params) {
  return http.post('/payLogs/add', withToken(params));
}

export function updatePayLogs(params) {
  return http.post('/payLogs/upd', withToken(params));
}

export function deletePayLogs(...args) {
  const rawParams = objectOrLegacy(args, ['token', 'id']);
  const params = withToken(rawParams, args[0]);
  return http.post('/payLogs/del', params);
}

export function getPagePayExpenses(...args) {
  const rawParams = objectOrLegacy(args, ['pageIndex', 'pageSize', 'token', 'teamId', 'title']);
  const params = withToken(rawParams, args[2]);
  return http.get('/payExpenses/page', { params });
}

export function addPayExpenses(params) {
  return http.post('/payExpenses/add', withToken(params));
}

export function updatePayExpenses(params) {
  return http.post('/payExpenses/upd', withToken(params));
}

export function deletePayExpenses(...args) {
  const rawParams = objectOrLegacy(args, ['token', 'id']);
  const params = withToken(rawParams, args[0]);
  return http.post('/payExpenses/del', params);
}
