import Vue from 'vue'
import VueRouter from 'vue-router'
import CardsView from '../views/CardsView.vue'
import TagsView from '../views/TagsView.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/cards',
    name: 'Cards',
    component: CardsView
  },
  {
    path: '/tags',
    name: 'Tags',
    component: TagsView
  },
  {
    path: '/cards',
    name: 'Cards',
    component: CardsView
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
