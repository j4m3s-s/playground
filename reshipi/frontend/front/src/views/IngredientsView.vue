<template>
  <div class="home">
  <Ingredients />
  </div>
</template>

<script>
import Ingredients from '@/components/Ingredients.vue'

export default {
  name: 'IngredientsView',
  components: {
      Ingredients
  }
}
</script>
