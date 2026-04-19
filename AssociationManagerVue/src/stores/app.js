import { computed, ref } from 'vue';
import { defineStore } from 'pinia';

export const useAppStore = defineStore('app', () => {
  const navExpanded = ref(false);
  const navPinned = ref(false);
  const mobileNavVisible = ref(false);
  const pageLoading = ref(false);

  const navOpen = computed(() => navPinned.value || navExpanded.value);

  function expandNav() {
    navExpanded.value = true;
  }

  function collapseNav() {
    if (!navPinned.value) {
      navExpanded.value = false;
    }
  }

  function togglePinnedNav() {
    navPinned.value = !navPinned.value;
    navExpanded.value = navPinned.value;
  }

  function toggleMobileNav(nextState) {
    mobileNavVisible.value = typeof nextState === 'boolean' ? nextState : !mobileNavVisible.value;
  }

  function hideMobileNav() {
    mobileNavVisible.value = false;
  }

  return {
    navExpanded,
    navPinned,
    mobileNavVisible,
    pageLoading,
    navOpen,
    expandNav,
    collapseNav,
    togglePinnedNav,
    toggleMobileNav,
    hideMobileNav,
  };
});
