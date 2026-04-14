import http from '@/utils/http';

export function login(params) {
  return http.post('/login', params);
}

export function logout(token) {
  return http.get('/exit', { params: { token } });
}
export const exit = logout;

export function getLoginUser(token) {
  return http.get('/info', { params: { token } });
}

export function updateLoginUserInfo(params) {
  return http.post('/info', params);
}
export const updLoginUserInfo = updateLoginUserInfo;

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
export const updLoginUserPwd = updateLoginUserPwd;

export function getSysNoticeList(token) {
  return http.get('/sys/notices', { params: { token } });
}

export function getDemoAccounts() {
  return http.get('/demoAccounts');
}

export function getPageUsers(pageIndex, pageSize, userName, name, phone, type) {
  return http.get('/users/page', {
    params: { pageIndex, pageSize, userName, name, phone, type },
  });
}

export function addUsers(params) {
  return http.post('/users/add', params);
}
export function getManagers() {
  return http.get('/users/managers');
}

export function updateUsers(params) {
  return http.post('/users/upd', params);
}
export const updUsers = updateUsers;

export function deleteUsers(id) {
  return http.post('/users/del', { id });
}
export const delUsers = deleteUsers;

export function getAllTypes() {
  return http.get('/teamTypes/all');
}

export function getPageTeamTypes(pageIndex, pageSize, name) {
  return http.get('/teamTypes/page', {
    params: { pageIndex, pageSize, name },
  });
}

export function addTeamTypes(params) {
  return http.post('/teamTypes/add', params);
}

export function updateTeamTypes(params) {
  return http.post('/teamTypes/upd', params);
}
export const updTeamTypes = updateTeamTypes;

export function deleteTeamTypes(id) {
  return http.post('/teamTypes/del', { id });
}
export const delTeamTypes = deleteTeamTypes;

export function getAllTeamList() {
  return http.get('/teams/all');
}

export function getManTeamList(manId) {
  return http.get('/teams/man', { params: { manId } });
}

export function getPageTeams(pageIndex, pageSize, token, name, typeId) {
  return http.get('/teams/page', {
    params: { pageIndex, pageSize, token, name, typeId },
  });
}

export function addTeams(params) {
  return http.post('/teams/add', params);
}
export function assignTeamManager(token, teamId, managerId) {
  return http.post('/teams/assignManager', { token, teamId, managerId });
}

export function updateTeams(params) {
  return http.post('/teams/upd', params);
}
export const updTeams = updateTeams;

export function deleteTeams(token, id) {
  return http.post('/teams/del', { token, id });
}
export const delTeams = deleteTeams;

export function getPageActivities(pageIndex, pageSize, token, teamName, activeName) {
  return http.get('/activities/page', {
    params: { pageIndex, pageSize, token, teamName, activeName },
  });
}

export function addActivities(params) {
  return http.post('/activities/add', params);
}

export function updateActivities(params) {
  return http.post('/activities/upd', params);
}
export const updActivities = updateActivities;

export function deleteActivities(token, id) {
  return http.post('/activities/del', { token, id });
}
export const delActivities = deleteActivities;

export function getActiveLogs(activeId) {
  return http.get('/activeLogs/list', { params: { activeId } });
}

export function getMyActiveIds(token) {
  return http.get('/activeLogs/myActiveIds', { params: { token } });
}

export function getMyActiveStatuses(token) {
  return http.get('/activeLogs/myStatuses', { params: { token } });
}

export function addActiveLogs(params) {
  return http.post('/activeLogs/add', params);
}

export function deleteActiveLogs(token, id) {
  return http.post('/activeLogs/del', { token, id });
}
export const delActiveLogs = deleteActiveLogs;
export function updateActiveLogs(params) {
  return http.post('/activeLogs/upd', params);
}
export const updActiveLogs = updateActiveLogs;
export function getPageActiveLogs(pageIndex, pageSize, token, teamName, activeName, userName, status) {
  return http.get('/activeLogs/page', {
    params: { pageIndex, pageSize, token, teamName, activeName, userName, status },
  });
}

export function getPageApplyLogs(pageIndex, pageSize, token, teamName, userName) {
  return http.get('/applyLogs/page', {
    params: { pageIndex, pageSize, token, teamName, userName },
  });
}

export function addApplyLogs(params) {
  return http.post('/applyLogs/add', params);
}

export function getMyPendingApplyTeamIds(token) {
  return http.get('/applyLogs/myPendingTeamIds', {
    params: { token },
  });
}

export function updateApplyLogs(params) {
  return http.post('/applyLogs/upd', params);
}
export const updApplyLogs = updateApplyLogs;
export function deleteApplyLogs(id) {
  return http.post('/applyLogs/del', { id });
}
export const delApplyLogs = deleteApplyLogs;

export function getPageMembers(pageIndex, pageSize, token, teamName, userName) {
  return http.get('/members/page', {
    params: { pageIndex, pageSize, token, teamName, userName },
  });
}

export function getMyMemberTeamIds(token) {
  return http.get('/members/myTeamIds', {
    params: { token },
  });
}

export function getMemberOptions(token, teamId) {
  return http.get('/members/options', {
    params: { token, teamId },
  });
}

export function deleteMembers(id) {
  return http.post('/members/del', { id });
}
export const delMembers = deleteMembers;
export function addMembers(params) {
  return http.post('/members/add', params);
}
export function updateMembers(params) {
  return http.post('/members/upd', params);
}
export const updMembers = updateMembers;

export function getPagePayLogs(pageIndex, pageSize, token, teamName, userName) {
  return http.get('/payLogs/page', {
    params: { pageIndex, pageSize, token, teamName, userName },
  });
}

export function getPaySummary(token, teamId) {
  return http.get('/payLogs/summary', {
    params: { token, teamId },
  });
}

export function addPayLogs(params) {
  return http.post('/payLogs/add', params);
}

export function updatePayLogs(params) {
  return http.post('/payLogs/upd', params);
}
export const updPayLogs = updatePayLogs;

export function deletePayLogs(token, id) {
  return http.post('/payLogs/del', { token, id });
}
export const delPayLogs = deletePayLogs;

export function getPagePayExpenses(pageIndex, pageSize, token, teamId, title) {
  return http.get('/payExpenses/page', {
    params: { pageIndex, pageSize, token, teamId, title },
  });
}

export function addPayExpenses(params) {
  return http.post('/payExpenses/add', params);
}

export function updatePayExpenses(params) {
  return http.post('/payExpenses/upd', params);
}
export const updPayExpenses = updatePayExpenses;

export function deletePayExpenses(token, id) {
  return http.post('/payExpenses/del', { token, id });
}
export const delPayExpenses = deletePayExpenses;

export function getPageNotices(pageIndex, pageSize, token, title, teamName, teamId, systemOnly) {
  return http.get('/notices/page', {
    params: { pageIndex, pageSize, token, title, teamName, teamId, systemOnly },
  });
}

export function addNotices(params) {
  return http.post('/notices/add', params);
}

export function updateNotices(params) {
  return http.post('/notices/upd', params);
}
export const updNotices = updateNotices;

export function toggleNoticeTop(token, id, isTop) {
  return http.post('/notices/top', { token, id, isTop });
}

export function deleteNotices(token, id) {
  return http.post('/notices/del', { token, id });
}
export const delNotices = deleteNotices;

export function getPageRules(pageIndex, pageSize, token, title, teamName) {
  return http.get('/rules/page', {
    params: { pageIndex, pageSize, token, title, teamName },
  });
}
export function addRule(params) {
  return http.post('/rules/add', params);
}
export function updateRule(params) {
  return http.post('/rules/upd', params);
}
export function deleteRule(token, id) {
  return http.post('/rules/del', { token, id });
}

export function getPageInteractions(pageIndex, pageSize, token, keyword, teamName) {
  return http.get('/interactions/page', {
    params: { pageIndex, pageSize, token, keyword, teamName },
  });
}
export function addInteraction(params) {
  return http.post('/interactions/add', params);
}
export function deleteInteraction(token, id) {
  return http.post('/interactions/del', { token, id });
}

export function getInteractionComments(token, interactionId) {
  return http.get('/interactionComments/list', { params: { token, interactionId } });
}

export function addInteractionComment(params) {
  return http.post('/interactionComments/add', params);
}

export function deleteInteractionComment(token, id) {
  return http.post('/interactionComments/del', { token, id });
}
