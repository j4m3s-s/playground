import Vue from 'vue'
import VueRouter from 'vue-router'
import GlossaryView from '../views/GlossaryView.vue'
import RecipeView from '../views/RecipeView.vue'
import UstensilView from '../views/UstensilView.vue'
import IngredientsView from '../views/IngredientsView.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'GlossaryView',
    component: GlossaryView
  },
  {
    path: '/recipe/:id',
    name: 'Recipe',
    component: RecipeView
  },
  {
    path: '/ustensils',
    name: 'Ustensil',
    component: UstensilView
  },
  {
    path: '/ingredients',
    name: 'Ingredients',
    component: IngredientsView
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
