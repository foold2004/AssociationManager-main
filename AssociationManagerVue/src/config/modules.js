import {
  IconApps,
  IconBook,
  IconCalendar,
  IconCheckCircle,
  IconDashboard,
  IconFile,
  IconIdcard,
  IconInteraction,
  IconNotification,
  IconSafe,
  IconStorage,
  IconUser,
  IconUserGroup,
} from '@arco-design/web-vue/es/icon';

export const moduleDefinitions = [
  { key: 'overview', title: '首页概览', label: '首页概览', icon: IconDashboard, roles: [0, 1, 2], implemented: true },
  { key: 'profile', title: '个人中心', label: '个人中心', icon: IconIdcard, roles: [0, 1, 2], implemented: true },
  { key: 'users', title: '用户管理', label: '用户管理', icon: IconUser, roles: [0], implemented: true },
  { key: 'team-types', title: '社团类型', label: '社团类型', icon: IconApps, roles: [0], implemented: true },
  { key: 'teams', title: '社团管理', label: '社团管理', icon: IconStorage, roles: [0, 1, 2], implemented: true },
  { key: 'members', title: '社团成员', label: '社团成员', icon: IconUserGroup, roles: [0, 1], implemented: true },
  { key: 'activities', title: '社团活动', label: '社团活动', icon: IconCalendar, roles: [0, 1, 2], implemented: true },
  { key: 'active-logs', title: '活动报名', label: '活动报名', icon: IconSafe, roles: [0, 1, 2], implemented: true },
  { key: 'notices', title: '通知公告', label: '通知公告', icon: IconNotification, roles: [0, 1, 2], implemented: true },
  { key: 'apply-logs', title: '入团申请', label: '入团申请', icon: IconCheckCircle, roles: [0, 1], implemented: true },
  { key: 'fees', title: '费用管理', label: '费用管理', icon: IconFile, roles: [0, 1, 2], implemented: true },
  { key: 'rules', title: '规则制度', label: '规则制度', icon: IconBook, roles: [0, 1, 2], implemented: true },
  { key: 'interactions', title: '交流互动', label: '交流互动', icon: IconInteraction, roles: [0, 1, 2], implemented: true },
];

export function getModulesByRole(userType) {
  return moduleDefinitions.filter((item) => item.roles.includes(Number(userType)));
}

export function findModule(moduleKey) {
  return moduleDefinitions.find((item) => item.key === moduleKey) || moduleDefinitions[0];
}
