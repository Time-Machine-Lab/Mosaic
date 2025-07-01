
import { createRouter, createWebHistory } from 'vue-router/auto'
import routes from '~pages'

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.isReady().then(() => {
  console.log('Initial route:', routes)
})
export default router
