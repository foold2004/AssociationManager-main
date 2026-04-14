# Frontend Scan Recommendations (Admin UI)

This document is based on scanning the current frontend implementation and highlights layout, visual, and interaction issues that reduce a “professional, restrained, data‑first” admin experience. Each item includes the file location for direct follow‑up.

## High‑Impact Fixes (Do First)

- **Rules content block is oversized and visually heavy.** The rules detail occupies a large padded box (`rule-content`) inside a large card with heavy shadow, which makes long text dominate the page. Recommendation: collapse long rule content (3–5 lines + “expand”), reduce padding, and use a subtle left border instead of a full tinted block. Consider moving metadata into a compact row to reduce vertical height.【F:/D:/ccodex/AssociationManager-main/AssociationManagerVue/src/views/pages/RulesPage.vue†L1-L72】【F:/D:/ccodex/AssociationManager-main/AssociationManagerVue/src/views/pages/RulesPage.vue†L240-L300】
- **Card radius/shadow is too strong and inconsistent with the “restrained” direction.** Several pages use large radius (24px) and strong shadows. Recommendation: standardize to smaller radius (12–16px) and remove strong shadows; rely on borders and spacing instead.【F:/D:/ccodex/AssociationManager-main/AssociationManagerVue/src/views/pages/RulesPage.vue†L235-L253】
- **Too many summary blocks on some pages (density without hierarchy).** Example: Fees page stacks multiple summary rows and a ratio block. Recommendation: keep a single summary row (3–4 core metrics) and move the ratio bar into the same row or a compact inline bar.【F:/D:/ccodex/AssociationManager-main/AssociationManagerVue/src/views/pages/PayLogs.vue†L50-L160】

## Medium‑Impact Fixes

- **Dashboard “hero” area is visually large for admin usage.** It still uses a large hero card with a separate visual block and floating stat cards, which is more marketing‑style than admin. Recommendation: reduce hero height, remove the right visual block entirely, and make the hero a single compact stats row + title.【F:/D:/ccodex/AssociationManager-main/AssociationManagerVue/src/views/DashboardPage.vue†L70-L120】【F:/D:/ccodex/AssociationManager-main/AssociationManagerVue/src/views/DashboardPage.vue†L732-L905】
- **Repeated summary rows (Users/Teams) should collapse into a single line or header badges.** Recommendation: keep “Total” only, or convert to small header chips (e.g., total, role).【F:/D:/ccodex/AssociationManager-main/AssociationManagerVue/src/views/pages/Users.vue†L29-L80】【F:/D:/ccodex/AssociationManager-main/AssociationManagerVue/src/views/pages/Teams.vue†L23-L76】
- **Rules list item layout is vertically heavy.** Recommendation: align title + metadata in one row, reduce line-height, and make content preview smaller. This will reduce scroll fatigue.【F:/D:/ccodex/AssociationManager-main/AssociationManagerVue/src/views/pages/RulesPage.vue†L20-L55】

## Low‑Impact Fixes

- **Login page still uses marketing hero/gallery.** For a management system, consider removing the hero gallery and keep a plain login card to reduce distraction and load time.【F:/D:/ccodex/AssociationManager-main/AssociationManagerVue/src/views/LoginPage.vue†L1-L40】
- **Global theme tokens exist but are not consistently used.** Many page styles still hardcode colors and shadows. Recommendation: replace hardcoded values with CSS variables from `app.css` to ensure consistent theme control.【F:/D:/ccodex/AssociationManager-main/AssociationManagerVue/src/assets/app.css†L1-L40】

## Suggested Quick Actions

- Convert `rule-content` to a collapsed preview with an “expand” toggle.
- Reduce card radius and shadows across all list/tool cards to a single standard.
- Reduce summary cards to a single row on each page.
- Remove marketing-style hero visuals from dashboard and login.
- Replace hardcoded colors in page styles with CSS variables.

---

If you want, I can turn these into specific code changes (component by component) and include a before/after visual checklist.
