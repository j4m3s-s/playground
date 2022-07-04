import Vue from 'vue'
import VueRouter from 'vue-router'

import CardsView from '../views/CardsView.vue'
import CardEditView from '../views/CardEditView.vue'
import TagsView from '../views/TagsView.vue'
import TagEditView from '../views/TagEditView.vue'

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
    path: '/tags',
    name: 'Tags',
    component: TagsView
  },
  {
    path: '/tag-edit/:id',
    name: 'TagEdit',
    component: TagEditView
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
