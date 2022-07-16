import Vue from 'vue'
import VueRouter from 'vue-router'

import CardsView from '../views/CardsView.vue'
import CardEditView from '../views/CardEditView.vue'
import CardAddView from '../views/CardAddView.vue'
import TagsView from '../views/TagsView.vue'
import TagEditView from '../views/TagEditView.vue'
import TestWorkflowView from '../views/TestWorkflowView.vue'

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
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
