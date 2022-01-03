import Vue from 'vue'
import VueRouter from 'vue-router'
import GlossaryView from '../views/GlossaryView.vue'
import RecipeView from '../views/RecipeView.vue'
import UstensilView from '../views/UstensilView.vue'
import IngredientsView from '../views/IngredientsView.vue'
import IngredientEditionView from '../views/IngredientEditionView.vue'
import UstensilEditionView from '../views/UstensilEditionView.vue'

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
  },
  {
    path: '/ingredient-edit',
    name: 'IngredientEditionView',
    component: IngredientEditionView
  },
  {
    path: '/ustensil-edit',
    name: 'UstensilEditionView',
    component: UstensilEditionView
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
