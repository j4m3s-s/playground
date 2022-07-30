import Vue from 'vue'
import VueRouter from 'vue-router'

import CardsView from '../views/CardsView.vue'
import CardEditView from '../views/CardEditView.vue'
import CardAddView from '../views/CardAddView.vue'
import TagsView from '../views/TagsView.vue'
import TagEditView from '../views/TagEditView.vue'
import TestWorkflowView from '../views/TestWorkflowView.vue'
import LoginView from '../views/LoginView.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/cards',
    name: 'Cards',
    component: CardsView
  },
  {
    path: '/card-edit/:id',
    name: 'CardEdit',
    component: CardEditView
  },
  {
    path: '/card-add/',
    name: 'CardAdd',
    component: CardAddView
  },
  {
    path: '/tags',
    name: 'Tags',
    component: TagsView
  },
  {
    path: '/tag-edit/:id',
    name: 'TagEdit',
    component: TagEditView
  },
  {
    path: '/testworkflow',
    name: 'TestWorkflow',
    component: TestWorkflowView
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginView,
    // Only Login path doesn't require to be authenticated. Defaults to true.
    meta: { requiresAuth: false }
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

/*
router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  // FIXME: Default to true. === undefined or smth?
  const isAuthenticated = store.getters.isAuthenticated
  if (requiresAuth && !isAuthenticated) {
    next({
      name: 'login-register',
      query: { redirect: to.fullPath }
    })
  } else if (requiresAuth && isAuthenticated) {
    next()
  } else {
    next()
  }
})
*/

export default router
