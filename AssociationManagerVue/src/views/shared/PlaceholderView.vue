<template>
  <div class="page-block">
    <div class="page-toolbar">
      <h2 class="page-section-title">{{ title }}</h2>
    </div>

    <div class="page-card">
      <div class="page-card__body placeholder-panel">
        <a-empty description="模块建设中" />
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true,
  },
});
</script>

<style scoped>
.placeholder-panel {
  min-height: 260px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
