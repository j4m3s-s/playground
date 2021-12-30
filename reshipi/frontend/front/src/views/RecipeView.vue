<template>
  <div class="home">
  <Recipe :id="$route.params.id" />
  </div>
</template>

<script>
import Recipe from '@/components/Recipe.vue'

export default {
  name: 'RecipeView',
  components: {
      Recipe
  }
}
</script>
