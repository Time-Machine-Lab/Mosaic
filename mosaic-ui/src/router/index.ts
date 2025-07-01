
import { createRouter, createWebHashHistory } from 'vue-router/auto'
import routes from '~pages'

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

router.isReady().then(() => {
  console.log('Initial route:', routes)
})
export default router
