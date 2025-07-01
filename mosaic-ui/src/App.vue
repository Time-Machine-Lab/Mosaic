<template>
  <div class="default-app">
  <router-view></router-view>
  </div>
</template>
<script lang="ts" setup>
import router from "@/router";
onMounted(()=>{
  router.push('/dashboard')
})
</script>
<style scoped lang="scss">
.default-app{
  height: 100vh;
  width: 100vw;
  overflow: hidden;
}
</style>

