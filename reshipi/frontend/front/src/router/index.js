import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import RecipeView from '../views/RecipeView.vue'
import IngredientsView from '../views/IngredientsView.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/recipe/:id',
    name: 'Recipe',
    component: RecipeView
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
